package sbst.benchmark.pitest;

import org.junit.runner.Result;
import org.pitest.mutationtest.engine.MutationIdentifier;

import java.util.ArrayList;
import java.util.List;

public class MutationResults {

    public enum State {SURVIVED, KILLED, IGNORED, NEVER_RUN}

    /**
     * junit results
     */
    private List<Result> results = new ArrayList<Result>();

    /**
     * ID of the mutation
     */
    private MutationIdentifier mutation_id;

    private State state;

    public MutationResults(List<Result> pResults, MutationIdentifier id) {
        this.mutation_id = id;
        this.results = pResults;
        this.state = State.NEVER_RUN;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public List<Result> getJUnitResults() {
        return results;
    }

    public MutationIdentifier getMutation_id() {
        return mutation_id;
    }

    public void addJUnitResult(Result r) {
        this.results.add(r);
    }


}
