package sbst.benchmark;

class ExecutionResult {
    public static final int FIELD_LENGTH = 25;

    String tool;
    String benchmark;
    String className;
    int runId;
    int preparationTime;
    int generationTime;
    int executionTime;
    int testcaseNumber;
    int uncompilableNumber;
    int brokenTests;
    int failTests;
    int linesTotal;
    int linesCovered;
    double linesCoverageRatio;
    int conditionsTotal;
    int conditionsCovered;
    double conditionsCoverageRatio;
    int mutantsTotal;
    int mutantsCovered;
    double mutantsCoverageRatio;
    int mutantsKilled;
    double mutantsKillRatio;
    int mutantsAlive;
    int timeBudget;
    int totalTestClasses;
}
