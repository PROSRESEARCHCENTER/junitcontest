/**
  * Copyright (c) 2017 Universitat Politècnica de València (UPV)

  * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

  * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

  * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

  * 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  **/
package sbst.benchmark.pitest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.pitest.mutationtest.engine.MutationIdentifier;

import sbst.benchmark.Main;
import sbst.benchmark.coverage.TestUtil;

public class TestExec4MutationTask  implements Callable<MutationResults>{

	private URL[] urls;
	private List<String> testClasses;
	private MutationResults results;
	private Set<TestInfo> flakyTests;

	public TestExec4MutationTask(String cp, List<String> pTestClasses, Set<TestInfo> pFlakyTests, MutationIdentifier id){
		try {
			// Load the jar
			urls = TestUtil.createURLs(cp);
			testClasses = pTestClasses;
			this.flakyTests = pFlakyTests;
			results = new MutationResults(new ArrayList<Result>(), id);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public MutationResults call() throws ClassNotFoundException, IOException {

			URLClassLoader cl = URLClassLoader.newInstance(urls, this.getClass().getClassLoader());

			Main.debug("Running the tests: "+testClasses);
			for (String test : testClasses){

				if  (test.contains("_scaffolding")) {
					Main.debug("Skipped scaffolding test "+test);
					continue;
				}

				// remove the prefix "testcases." added by our tool
				if (test.startsWith("testcases."))
					test = test.replaceFirst("testcases.", "");

				try {
					// load the test case
					final Class<?> testClass = cl.loadClass(test);

					// Here we execute our test target class through its Runnable interface:
					JUnitCore junit = new JUnitCore();
					Result result = junit.run(testClass);

					results.addJUnitResult(result);

					// a failure here means that the mutation in the CUT
					// is covered. Then, we don't need to run the remaining tests
					if (result.getFailures().size() > 0) {
						for (Failure fail : result.getFailures()){
							String header = fail.getTestHeader();

							if (header.contains("(")){
								String testMethod = header.substring(0, header.indexOf('('));
								String tc = header.substring(header.indexOf('(')+1, header.length());
								TestInfo info = new TestInfo(tc, testMethod);
								if (!this.flakyTests.contains(info))
									break;
							}
						}
					}

					//Main.debug("Failure: "+result.getFailures());
					//for (Failure fail : result.getFailures()){
					//	Main.debug("Failing Tests: "+fail.getTestHeader()+"\n"+fail.getException()+"\n"+fail.getDescription()+"\n"+fail.getMessage());
					//}
				} catch (Exception e){
					Main.debug("Exception running mutation task with test class: " + test);
					e.printStackTrace(Main.debugStr);
				}				
			}
			Main.debug("Executions terminated");
			cl.close();
			return results;
	}

	/**
	 * Count the number of failing test methods
	 * @return number of failing test methods
	 */
	public int countFailingTests(){
		int count = 0;

		for (Result result : this.results.getJUnitResults()){
			count += result.getFailureCount();
		}
		return count;
	}

	public List<Result> getExecutionResults(){
		return this.results.getJUnitResults();
	}

}
