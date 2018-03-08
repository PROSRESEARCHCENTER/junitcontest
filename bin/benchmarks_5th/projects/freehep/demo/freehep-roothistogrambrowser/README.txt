* the following warning appears when running mvn

[WARNING] Something failed while checking if the main class contains the main() method. 
This is probably due to the limited classpath we have provided to the class loader. 
The specified main class (hep.io.root.util.RootHistogramBrowser) found in the jar is 
*assumed* to contain a main method... jas/hist/JASHist

* you need to run

sh signjar.sh 

to publish the jars and jnlp into src/site/resources and to sign them.

