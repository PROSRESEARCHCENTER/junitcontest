/**
  * Copyright (c) 2017 Universitat Politècnica de València (UPV)

  * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

  * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

  * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

  * 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  **/
package sbst.benchmark.pitest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;

import sbst.benchmark.Main;
import sbst.benchmark.coverage.JacocoResult;

public class MutationAnalysis {

	/** Map to keep track of the details for the generated Mutation **/
	private MutationSet generatedMutants;

	/** Map to keep track of uncovered mutations **/
	private MutationSet uncoveredMutants;

	/** Map to keep track of covered mutations **/
	private MutationSet coveredMutants;

	/** Map to keep track of the mutation being killed **/
	private Map<MutationIdentifier, Boolean> killData;

	/** Map to keep track of tests killing/covering mutations **/
	private Map<MutationIdentifier, List<TestInfo>> coveringTests;

	/**
	 * Simple constructor to initiate the coverage information
	 */
	public MutationAnalysis(MutationSet set, JacocoResult result) {
		this.generatedMutants = set;
		// take the coverage results from Jacoco
		Set<Integer> coveredLines = result.getCoveredLines();

		Main.debug("Matching Jacoco with PIT: find covered mutations");
		coveredMutants = new MutationSet();
		uncoveredMutants = new MutationSet();
		for (MutationIdentifier id : this.generatedMutants.getMutationIDs()){
			MutationDetails detail = this.generatedMutants.getMutantionDetails(id);
			if (coveredLines.contains(detail.getClassLine().getLineNumber()))
				coveredMutants.addMutant(id, this.generatedMutants.getMutantion(id), this.generatedMutants.getMutantionDetails(id));
			else
				uncoveredMutants.addMutant(id, this.generatedMutants.getMutantion(id), this.generatedMutants.getMutantionDetails(id));
		}

		this.killData = new HashMap<MutationIdentifier, Boolean>();
		this.coveringTests = new HashMap<MutationIdentifier, List<TestInfo>>();
	}

	/**
	 * Add killed/covered mutation
	 * @param mutant mutation being covered
	 */
	public void addKilledMutant(MutationDetails mutant, TestInfo info){
		this.killData.put(mutant.getId(), true);
		if (this.coveringTests.containsKey(mutant.getId())){
			// if the list already exists
			this.coveringTests.get(mutant.getId()).add(info);
		} else {
			// if the list doesn't exist
			List<TestInfo> list = new ArrayList<TestInfo>();
			list.add(info);
			this.coveringTests.put(mutant.getId(), list);
		}
	}

	/**
	 * Add mutation as uncovered/non-killed
	 * @param mutant mutation being covered
	 */
	public void addAliveMutant(MutationDetails mutant){		
                Boolean killed = this.killData.get(mutant.getId());
                if (killed == null || !killed) // if it was killed => ignore
                        this.killData.put(mutant.getId(), false);		
	}

	public int getNumberOfMutations(){
		return this.generatedMutants.getNumberOfMutations();
	}

	public int numberOfKilledMutation(){
		int count = 0;
		for (MutationIdentifier id : this.generatedMutants.getMutationIDs()){
			if (killData.containsKey(id)){
				if (this.killData.get(id))
					count++;
			}
		}
		return count;
	}

	public int numberOfUncoveredMutation(){
		return this.uncoveredMutants.getNumberOfMutations();
	}

	public int numberOfALiveMutation(){
		int count = 0;
		for (MutationIdentifier id : this.coveredMutants.getMutationIDs()){
			if (!this.killData.get(id))
				count++;
		}
		return count;
	}

	public void printCoverageInfo(){
		System.out.println(toString());
	}

	@Override
	public String toString(){
		String info = "N. of generated mutants "+this.getNumberOfMutations()+"\n";
		info = info + "N. of covered mutants "+this.getNumberOfCoveredMutants()+"\n";
		info = info + "N. of killed mutants "+this.numberOfKilledMutation()+"\n";
		String mutation_list = "";
		String test_info = "";
		int count = 0;
		for (MutationIdentifier id : this.coveringTests.keySet()){
			mutation_list = mutation_list + "Mutant: "+count+", killed by tests "+this.coveringTests.get(id)+"\n";
			test_info = test_info +  "Mutant: "+count+"\t ---> "+this.generatedMutants.getMutantionDetails(id)+"\n";
			count++;
		}
		info = info + "\n\n --- Tests killing mutants --- \n ";
		info = info + test_info;
		info = info + "\n\n --- Mutants details--- \n ";
		info = info + mutation_list;
		return info;
	}

	public boolean isMutantKilled(MutationIdentifier id){
		if (this.killData.containsKey(id)){
			return this.killData.get(id);
		}
		return false;
	}

	public void deleteFlakyTest(Set<TestInfo> flaky){
		for (MutationIdentifier id : this.coveringTests.keySet()){
			List<TestInfo> killingTests = this.coveringTests.get(id);

			// from the set of Killing tests, we remove remove flaky tests
			// i.e., tests that already fail in the original SUT
			for (TestInfo flaky_test : flaky){
				if (killingTests.contains(flaky_test))
					killingTests.remove(flaky_test);
			}

                        if (killingTests.size() == 0){
                                //this.killData.put(id, false);
                                MutationDetails detail = this.generatedMutants.getMutantionDetails(id);
                                this.addAliveMutant(detail);
                        }
			
		}
	}

	public MutationSet getCoveredMutants() {
		return coveredMutants;
	}

	public int getNumberOfCoveredMutants() {
		return coveredMutants.getNumberOfMutations();
	}

}
