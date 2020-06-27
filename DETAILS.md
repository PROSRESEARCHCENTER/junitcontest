# The contest

The contest targets to evaluate tools that generate junit test cases for java programs at the class level.

Each tool is applied on a set of java classes (the benchmarks) taken from open-source projects,
which have been selected by the contest organisation without favouring any specific tool.
The participating tools have a fixed time budget to generate tests for java classes.
The score is calculated based on efficiency and effectiveness.

## Restrictions

Only tools that can run automatically without human intervention can enter the contest.

Target platform: Linux

## Instructions

Each participant can install a copy of their tool on the server (at $HOME) where the contest will be run.
To this end, each participant will have SSH access to the contest server.

Specific tool requirements:
- `$HOME/runtool` script/binary must be implemented folllowing the protocol descrived below
- `$HOME/temp/testcases/` must contain the generated unit tests in JUnit4 format
Optional:
- `$HOME/temp/data/` must keep any intermediate data, which will be useful for offline analyses (debugging)

Specific generated unit tests requirements:
- Must be stored as one or more java files containing JUnit4 tests
	- declare classes public
	- add zero-argument public constructor
	- annotate test methods with @Test
	- declare test methods public
- The generated test cases will be compiled against
	- JUnit 4.12
	- The benchmark SUT (System/Application Under Test)
	- Any dependencies of the benchmark SUT

Test generation time restrictions:
- Scoring for each CUT (Class Under Test) takes place after a fixed amount of time budget (B)
- Any test artefact generated after this time B is ignored by the contest infrastructure

# Protocol

The runtool script/binary is the interface between the contest infrastructure and the participating tools.
The communication between the runtool and the contest infrastructure is done through a very simple line based protocol
over the standard input and output channels. The following table describes the protocol, every step consisting of
a line of text received by the runtool program on STDIN or sent to STDOUT:

STEP  |	STDIN MESSAGES	      |	STDOUT MESSAGES	      |	DESCRIPTION
------|-----------------------|-----------------------|------------
   1  |             BENCHMARK |                       | Signals the start of a benchmark run; directory $HOME/temp is cleared
   2  |             directory |                       | Directory with the SUT' source code
   3  |             directory |                       | Directory with compiled class files of the SUT
   4  |                number |                       | Number of entries in the class path (N)
   5  | directory or jar file |                       | Class path entry (repeated N times)
   6  |                number |                       | Number of classes to be covered (M)
   7  |                       |             CLASSPATH | Signals that the testing tool requires additional classpath entries
   8  |                       |                number | Number of additional class path entries (K)
   9  |                       | directory or jar file | Class path entry (repeated K times)
  10  |                       |                 READY | Signals that the testing tool is ready to receive challenges
  11  |           time budget |                       | Expected test generation time, in seconds
  12  |            class name |                       | The name of the class for which unit tests must be generated
  13  |                       |                 READY | Signals that the testing tool is ready to receive more challenges

Once the participant tool signals the READY message at step 13, test cases in `$HOME/temp/testcases/` are analysed.
Subsequently, `$HOME/temp/testcases/` is cleared; then GOTO step 11 until M class names have been processed.

A sample implementation of this protocol can be found at `src/runtool/`
