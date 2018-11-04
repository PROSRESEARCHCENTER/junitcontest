# junitcontest
junit tools contest infrastructure

Details about the 2019 edition coming soon ... <br /><br />

# About

This is the source code for the contest infrastructure for junit testing tools.
The contest was initiated during the FITTEST European project no. 257574 (2010-2013)
and hence partly funded by the FP7 programme on ICT Software & Service Architectures and Infrastructures.

The [FITTEST](http://crest.cs.ucl.ac.uk/fittest/) project, which developed an integrated environment for the automated and continuous testing of Future Internet Applications, was coordinated by:<br />

  Tanja E. J. Vos (tvos@pros.upv.es)<br />
  Software Quality & Testing<br />
  Research Center on Software Production Methods ([PROS](http://www.pros.webs.upv.es/))<br />
  Department of Information Systems and Computation ([DSIC](http://www.upv.es/entidades/DSIC/index.html))<br />
  Universitat Politècnica de València ([UPV](http://www.upv.es/))<br />
  Camino de Vera s/n, 46022 Valencia (Spain)<br />

# Status

The contest infrastructure has been used in testing contests during yearly events from 2013 till 2017 (check the [Acknowledgements](ACKNOWLEDGEMENTS) and [Publications](PUBLICATIONS.md)).

The latest event happened in February 2018: the 6th round of the junit contest series, which will be celebrated in the 11th International SBST workshop [1] held in conjunction with ICSE conference [2]:
* [1] SBST2018 - 11th International Workshop on Search-Based Software Testing, May 28-29 2018
    * URL: http://software.imdea.org/sbst18/
    * Contest Committee
        * Urko Rueda Molina (Chair), Universitat Politècnica de València
        * Fitsum Kifetew (co-Chair), Fondazione Bruno Kessler
        * Annibale Panichella, University of Luxembourg and Delft University of Technology
    * Important dates:
        * Tool set up (runtool script and libs): ready due **26th January**
        * Competition: contest finished and data available due **9th February**
        * Contest reports: submissions due **16th February**
* [2] ICSE2018 - 40th International Conference on Software Engineering, May 27 - 3 June 2018, Gothenburg, Sweden https://www.icse2018.org/

The next contest event is scheduled to be organised for 2019, which will be chaired by Fitsum Kifetew from [Fondazione Bruno Kessler](https://www.fbk.eu/en/).

The junit tools competition is now **closed**, but do not hesitate making contact if you would like to contribute with a junit tool at any time. We are happy to assist you preparing your tool for the future contests. Any tool that automatically generates test cases (JUnit4 format) for Java programs at the class level. You can find details about the contest benchmark infrastructure, and also about the tool requirements, in this public repository.
	
# Target

Linux (Ubuntu x64)

# Folder contents

* [bin](/bin):   Contest infrastructure binaries
* [src](/src):   Contest infrastructure source code
* [tools](/tools): Contest tools

# Requirements

Java8 (JDK):
```shell-script
apt-get install default-jdk
```
# Further information

For more information see [DETAILS](/DETAILS) and [PUBLICATIONS](/PUBLICATIONS.md).
