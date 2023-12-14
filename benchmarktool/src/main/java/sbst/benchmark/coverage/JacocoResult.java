package sbst.benchmark.coverage;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to store the coverage results from Jacoco
 *
 * @author annibale.panichella
 */
public class JacocoResult {

    private int linesTotal;
    private int linesCovered;
    private int branchesTotal;
    private int branchesCovered;
    private int instructionsTotal;
    private int instructionsCovered;
    private int methodsTotal;
    private int methodsCovered;
    private int complexityTotal;
    private int complexityCovered;
    private Set<Integer> coveredLines;
    private Set<Integer> uncoveredLines;

    public JacocoResult(IClassCoverage cc) {
        linesTotal = cc.getLineCounter().getTotalCount();
        linesCovered = cc.getLineCounter().getCoveredCount();
        branchesTotal = cc.getBranchCounter().getTotalCount();
        branchesCovered = cc.getBranchCounter().getCoveredCount();
        instructionsTotal = cc.getInstructionCounter().getTotalCount();
        instructionsCovered = cc.getInstructionCounter().getCoveredCount();
        methodsTotal = cc.getMethodCounter().getTotalCount();
        methodsCovered = cc.getMethodCounter().getCoveredCount();
        complexityTotal = cc.getComplexityCounter().getTotalCount();
        complexityCovered = cc.getComplexityCounter().getCoveredCount();

        //let's derive the list uncovered lines
        coveredLines = new HashSet<Integer>();
        uncoveredLines = new HashSet<Integer>();
        for (int line = cc.getFirstLine(); line <= cc.getLastLine(); line++) {
            if (cc.getLine(line).getStatus() == ICounter.FULLY_COVERED || cc.getLine(line).getStatus() == ICounter.PARTLY_COVERED) {
                this.coveredLines.add(line);
            } else if (cc.getLine(line).getStatus() == ICounter.NOT_COVERED) {
                this.uncoveredLines.add(line);
            }
        }
    }

    public int getLinesTotal() {
        return linesTotal;
    }

    public int getLinesCovered() {
        return linesCovered;
    }

    public int getBranchesTotal() {
        return branchesTotal;
    }

    public int getBranchesCovered() {
        return branchesCovered;
    }

    public int getInstructionsTotal() {
        return instructionsTotal;
    }

    public int getInstructionsCovered() {
        return instructionsCovered;
    }

    public int getMethodsTotal() {
        return methodsTotal;
    }

    public int getMethodsCovered() {
        return methodsCovered;
    }

    public int getComplexityTotal() {
        return complexityTotal;
    }

    public int getComplexityCovered() {
        return complexityCovered;
    }

    public Set<Integer> getUncoveredLines() {
        return this.uncoveredLines;
    }

    public Set<Integer> getCoveredLines() {
        return this.coveredLines;
    }

    public void printResults() {
        System.out.println("Method coverage = " + ((double) this.methodsCovered) / ((double) this.methodsTotal));
        System.out.println("Line coverage = " + ((double) this.linesCovered) / ((double) this.linesTotal));
        System.out.println("Branch coverage = " + ((double) this.branchesCovered) / ((double) this.branchesTotal));
        System.out.println("Complexity coverage = " + ((double) this.complexityCovered) / ((double) this.complexityTotal));
    }

}
