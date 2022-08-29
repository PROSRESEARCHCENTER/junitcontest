

Install the EvoSuite BBC dependency in your local Maven repository using the command line: 

```shell
mvn install:install-file \
   -Dfile=tools/bbc/lib/evosuite-bbc.jar \
   -DgroupId=nl.tudelft \
   -DartifactId=evosuite-bbc \
   -Dversion=0.0.1 \
   -Dpackaging=jar \
   -DgeneratePom=true
```