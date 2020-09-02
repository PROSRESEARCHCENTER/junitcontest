/**
  * Copyright (c) 2017 Universitat Politècnica de València (UPV)

  * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

  * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

  * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

  * 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  **/
package sbst.benchmark.coverage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.junit.runner.Result;

import sbst.benchmark.Main;
import sbst.benchmark.TestSuite;

public class JaCoCoLauncher {

	private String temp_folder;
	private String targetClass;
	private List<String> testCases;
	private String testFolder;

	private LinkedList<String> required_libraries; // it has to contain the CP required to run the tests

	private List<File> jar_to_instrument;
	private List<File> instrumented_jar;

	private JacocoResult results;

	public JaCoCoLauncher(String pTempFile){
		this.temp_folder = pTempFile;
	}

	public void setTestCase(List<String> testCases){
		this.testCases = testCases;
	}

	public void setTestFolder(String testFolder){
		this.testFolder = testFolder;
	}

	public void setTargetClass(String CUT){
		this.targetClass = CUT.replace('.', '/');
	}

	/**
	 * This classes store all the libraries required to run the test against the SUT
	 * @param classpath String containing all required jars separated by ":"
	 */
	public void setClassPath(String classpath){
		required_libraries = new LinkedList<String>();

		String[] libraries = classpath.split(":");
		for (String s : libraries){
			s = s.replace(":", "");
			if (s.length() > 0)
				required_libraries.addLast(s);
		}
	}

	/**
	 * Set the jar (SUT) to instrument with JaCoCo
	 * @param path of the SUTs to instrument
	 */
	public void setJarInstrument(List<File> jars){
		jar_to_instrument = new LinkedList<File>();

		instrumented_jar = new LinkedList<File>();

		for (File file : jars){

			if (!file.exists())
				try {
					throw new FileNotFoundException();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			jar_to_instrument.add(file);

			// name of the jar where to store the instrumented classes
			String jarName = file.getName();
			if (jarName.endsWith(".jar"))
				jarName = jarName.replaceAll(".jar", "_instrumented.jar");
			else
				jarName = jarName+"_instrumented";
			instrumented_jar.add(new File(this.temp_folder+"/"+jarName));
		}
	}


	private String generateClassPath() throws MalformedURLException{
		String cp ="";

		// first we add the instrumented jar
		for (int index = 0; index < instrumented_jar.size(); index++){
			cp = cp + instrumented_jar.get(index).getAbsolutePath()+":";
		}

		// then we add all the other required library
		for (int index = 0; index < required_libraries.size(); index++){
			cp = cp + required_libraries.get(index)+":";
		}

		cp = cp + testFolder;
		return cp;
	}

	public void runJaCoCo() throws Exception{
		// For instrumentation and runtime we need a IRuntime instance
		// to collect execution data:
		final IRuntime runtime = new LoggerRuntime();
		final Instrumenter instr = new Instrumenter(runtime);

		int tot_instrumented_class = 0;
		for (int index = 0; index<jar_to_instrument.size(); index++) {

			// if it is a directory, then we instrument all classes in it (including in sub-folders)
			if (jar_to_instrument.get(index).isDirectory()) {
				// create the directory for the instrumented classes
				instrumented_jar.get(index).mkdir();

				// copy all classes in the new directory temp_folder
				FileUtils.copyDirectory(jar_to_instrument.get(index), instrumented_jar.get(index));

				// instrument all file *.class
				List<File> files = (List<File>) FileUtils.listFiles(jar_to_instrument.get(index), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

				for (File f : files) {
					if (f.getName().endsWith(".class")){
						//System.out.println(">>>>>>>>>"+f.getAbsolutePath());

						String fileName = f.getAbsolutePath();
						fileName = fileName.replace(jar_to_instrument.get(index).getAbsolutePath(), instrumented_jar.get(index).getAbsolutePath());

						// jar of the SUT
						InputStream input = new FileInputStream(f.getAbsolutePath());
						// jar with instrumented classes

						OutputStream output = new FileOutputStream(fileName);
						tot_instrumented_class += instr.instrumentAll(input, output, "");
					}

				}
			} else{
				// jar of the SUT
				InputStream input = new FileInputStream(jar_to_instrument.get(index));
				// jar with instrumented classes
				OutputStream output = new FileOutputStream(instrumented_jar.get(index));
				tot_instrumented_class += instr.instrumentAll(input, output, "");
			}
		}
		Main.debug("Number of instrumented file = " + tot_instrumented_class);

		// Now we're ready to run our instrumented class and need to startup the
		// runtime first:
		final RuntimeData data = new RuntimeData();
		runtime.startup(data);

		String cp = generateClassPath();

		Main.debug("Running tests with the following classpath: \n"+cp);

		ExecutorService service = Executors.newFixedThreadPool(2);

		TestExecutionTask executor = new TestExecutionTask(cp, testCases);
		FutureTask<List<Result>> task = (FutureTask<List<Result>>) service.submit(executor);
		task.get(TestSuite.TEST_TIMEOUT, TimeUnit.MILLISECONDS); // run task

		service.shutdown();
		service.awaitTermination(5, TimeUnit.MINUTES);

		// At the end of test execution we collect execution data and shutdown
		// the runtime:
		final ExecutionDataStore executionData = new ExecutionDataStore();
		final SessionInfoStore sessionInfos = new SessionInfoStore();
		data.collect(executionData, sessionInfos, false);
		runtime.shutdown();

		for (int index = 0; index<jar_to_instrument.size(); index++) {
			// Together with the original class definition we can calculate coverage
			// information:
			final CoverageBuilder coverageBuilder = new CoverageBuilder();
			final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
			int n = analyzer.analyzeAll(jar_to_instrument.get(index));
			Main.debug("Number of file with coverage information = " + n);
			/**/
			// Let's dump some metrics and line coverage information:
			for (final IClassCoverage cc : coverageBuilder.getClasses()) {
				if (cc.getName().equals(targetClass)){
					Main.debug("Extracted coverage data for the class " + targetClass);
					results = new JacocoResult(cc);
				}
			}
		}
		// delete instrumented files
		for (File file : this.instrumented_jar){
			if (file.exists()){
				if (file.isDirectory())
					FileUtils.deleteDirectory(file);
				else
					file.deleteOnExit();
			}
		}
	}

	public JacocoResult getResults() {
		return results;
	}

}
