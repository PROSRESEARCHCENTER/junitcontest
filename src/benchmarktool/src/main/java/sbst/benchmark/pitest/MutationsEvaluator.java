/**
  * Copyright (c) 2017 Universitat Politècnica de València (UPV)

  * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

  * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

  * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

  * 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  **/
package sbst.benchmark.pitest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.pitest.mutationtest.engine.MutationIdentifier;

import sbst.benchmark.Main;
import sbst.benchmark.TestSuite;
import sbst.benchmark.coverage.JacocoResult;

public class MutationsEvaluator {

	private static final int MAX_THREAD = 1;

	private static final long GLOBAL_TIMEOUT = 300000; // global timeout for mutation analysis

	/** Folder where to save the mutated SUT **/
	private String tempFolder;

	private String classPath;

	private String classToMutate;

	private List<String> targetTest;

	private MutationAnalysis mutationResults;

	private Set<TestInfo> flakyTests;

	private boolean timeoutReached = false;

	/**
	 * Build the infrastructure to run the generated tests against the mutations
	 * @param pClassPath classpath with all required libraries to run the tests
	 * @param pClassToMutate name of the class to mutate
	 * @param pTargetTest test to run against the mutations
	 */
	public MutationsEvaluator(String pClassPath, String pClassToMutate, List<String> pTargetTest, Set<TestInfo> pFlakyTests){
		this.classPath = pClassPath;
		this.classToMutate = pClassToMutate;
		this.targetTest = pTargetTest;
		this.flakyTests = pFlakyTests;
	}

	public void computeCoveredMutants(MutationSet set, JacocoResult jacoco){
		mutationResults = new MutationAnalysis(set, jacoco);
	}

	/**
	 * This method run all covered mutations. It makes a copy of the SUT in the
	 * @param tempFolder folder where the SUT will be copied for the mutation analysis
	 * @param path2SUT path of the "original" SUT
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	public void runMutations(String tempFolder, String path2SUT) throws ClassNotFoundException, IOException, InterruptedException, ExecutionException {
		// create a temporary copy of the SUT for mutation analysis
		this.tempFolder = tempFolder;
		if (tempFolder.equals(path2SUT))
			throw new IllegalArgumentException("Source and target directories should be different: \n "
					+ "source directory = "+path2SUT
					+ "target directory = "+ tempFolder);

		// create a copy of the SUT
		String newSUT = this.tempFolder+"/SUT";

		createSUTCopy(path2SUT, newSUT);
		// remove the original CUT
		String CUT =  newSUT + "/" + classToMutate.replace('.','/')+".class";
		File fcut = new File(CUT);
		if (fcut.exists())
			FileUtils.deleteQuietly(fcut);

		// Prepare the ExecutorService
		ExecutorService service = Executors.newScheduledThreadPool(MAX_THREAD);

		// start to measure the time
		long start_time = System.currentTimeMillis();

		// iterate over all mutations
		MutationSet coveredMutants = mutationResults.getCoveredMutants();
		Set<MutationIdentifier> mutations = coveredMutants.getMutationIDs();

		LinkedList<TestExec4MutationTask> task_list = new LinkedList<TestExec4MutationTask>();

		int mutation_counter = -1;
		for (MutationIdentifier id : mutations){
			//System.out.println(coveredMutants.getMutantionDetails(id).toString());
			//System.out.println(">> "+ coveredMutants.getMutantionDetails(id).getClassName());
			//System.out.println(">> "+ coveredMutants.getMutantionDetails(id).getMethod());

			mutation_counter++;

			// get the mutated bytecode of the CUT
			byte[] mu = coveredMutants.getMutantion(id).getBytes();

			// save the mutated bytecode of the CUT
			String newCUT =  this.tempFolder + "/CUT"+mutation_counter;
			writeMutationOnDisk(mu, newCUT);

			// change the classpath to consider the new copy of the SUT
			String newCP = this.classPath.replace(path2SUT, newSUT);
			// add the mutated CUT to the classpath
			newCP = newCP+":"+newCUT;

			// run the test against the mutated CUT
			List<String> testClasses = new ArrayList<String>();
			testClasses.addAll(this.targetTest);

			//Main.debug("## Run test with CP = "+ newCP);
			TestExec4MutationTask executor = new TestExec4MutationTask(newCP, testClasses, this.flakyTests, id);
			task_list.addLast(executor);
		}
		List<Future<MutationResults>> all = service.invokeAll(task_list,GLOBAL_TIMEOUT, TimeUnit.MILLISECONDS);

		for (Future<MutationResults> future : all){
			try {
				// if canceled let's notify printing a file in the result directory
				if (future.isCancelled()){
					this.timeoutReached = true;
					continue;
				}

				MutationResults mutation_result = future.get(TestSuite.TEST_TIMEOUT,TimeUnit.MILLISECONDS);

				List<Result> executionResults = mutation_result.getJUnitResults();
				MutationIdentifier id = mutation_result.getMutation_id();

				if (executionResults == null){
					mutationResults.addAliveMutant(coveredMutants.getMutantionDetails(id));
					continue;
				}

				for (Result result : executionResults){
					// if no failure, it means that the test did not kill the mutation
					if (result.getFailures().size() == 0)
						mutationResults.addAliveMutant(coveredMutants.getMutantionDetails(id));
					else {
						for (Failure fail : result.getFailures()){
							if (!fail.getTrace().contains("java.io.FileNotFoundException")){
								String header = fail.getTestHeader();
								//Main.debug("Failure: "+fail.getTestHeader()+"\n"+fail.getTrace());
								if (header.contains("(")){
									String testMethod = header.substring(0, header.indexOf('('));
									String testClass = header.substring(header.indexOf('(')+1, header.length());
									TestInfo info = new TestInfo(testClass, testMethod);
									mutationResults.addKilledMutant(coveredMutants.getMutantionDetails(id), info);
								} else {
									//Main.debug(" \n #### \n "+fail.getTestHeader()+"\n"+fail.getMessage()+"\n"+fail.getTrace()+" \n #### \n ");
								}
							}
						}
					}
				}
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				if (e instanceof TimeoutException) {
					Main.debug("Evaluation of the mutant stopped: it took more than "+TestSuite.TEST_TIMEOUT+" milliseconds");
					//MutationIdentifier id = mutationIndexes.get(future);
					//TestInfo info = new TestInfo("Timeout", "Timeout");
					//mutationResults.addKilledMutant(coveredMutants.getMutantionDetails(id), info);
				}
				e.printStackTrace(Main.debugStr);
				continue;
			}
		}
		service.shutdown();

		// remove temporary directory
		File dire2remove = new File(this.tempFolder);
		if (dire2remove.exists() && dire2remove.isDirectory())
			FileUtils.deleteDirectory(dire2remove);
	}

	/**
	 * Create a copy of the SUT for mutation analysis
	 * @param path2SUT path to the SUT
	 * @param tempFolder temporary file used for mutation analysis
	 */
	public static void createSUTCopy(String path2SUT, String tempFolder){
		File source = new File(path2SUT);
		File target = new File(tempFolder);

		try {
			if (path2SUT.contains(":")){
				String[] libraries = path2SUT.split(":");
				for (String lib : libraries){
					lib = lib.replace(":", "");
					if (lib.length()>0){
						File fileLib= new File(lib);
						if (fileLib.isDirectory())
							FileUtils.copyDirectory(fileLib, target);
						else
							FileUtils.copyFileToDirectory(fileLib, target);
					}
				}
			} else {
				if (!source.exists())
					throw new FileNotFoundException();
				FileUtils.copyDirectory(source, target);
			}


		} catch (IOException e) {
			Main.debug("Could not make a copy of the SUT "+path2SUT);
			e.printStackTrace(Main.debugStr);
		}
	}

	/**
	 * Method to write the mutated CUT on disk (in the folder specified by the attribute tempFolder)
	 * @param mu bytecode of the mutated CUT (generated by PIT)
	 * @param location temporary directory where to save the mutated CUT
	 */
	public void writeMutationOnDisk(byte[] mu, String location){

		String newCUT = location + "/" + classToMutate.replace('.','/')+".class";

		File changed_code = new File(newCUT);
		changed_code.getParentFile().mkdirs(); // create required folders

		FileOutputStream output;
		try {
			output = new FileOutputStream(changed_code);
			output.write(mu);
			output.flush();
			output.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MutationAnalysis getMutationCoverage(){
		return this.mutationResults;
	}

	public boolean isTimeoutReached(){
		return timeoutReached;
	}
}
