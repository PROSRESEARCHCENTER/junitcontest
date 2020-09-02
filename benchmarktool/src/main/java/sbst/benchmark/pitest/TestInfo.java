/**
  * Copyright (c) 2017 Universitat Politècnica de València (UPV)

  * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

  * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

  * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

  * 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  **/
package sbst.benchmark.pitest;

public class TestInfo {

	public String testClass;
	public String testMethod;

	public TestInfo(String tClass, String tMethod){
		this.testClass = tClass;
		this.testMethod = tMethod;
	}

	public String getTestClass() {
		return testClass;
	}

	public String getTestMethod() {
		return testMethod;
	}

	@Override
	public String toString(){
		return "("+testClass+","+testMethod+")";
	}

	@Override
	public boolean equals(Object o){
		if (o == null)
			return false;

		if (o instanceof TestInfo){
			TestInfo other = (TestInfo) o;
			if (this.testClass.equals(other.testClass) && this.testMethod.equals(other.testMethod))
				return true;
			else
				return false;
		}
		return false;
	}

	@Override
	public int hashCode() {
		 int result = 17;
	        result = 31 * result + testClass.hashCode();
	        result = 31 * result + testMethod.hashCode();
	        return result;
	  }

}
