/**
 * Copyright (c) 2017 Universitat Politècnica de València (UPV)
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * <p>
 * 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package sbst.runtool;

import java.io.*;
import java.util.List;

public class Randoop implements ITestingTool {

    List<File> classPath;
    static final String randoopJar = System.getProperty("sbst.benchmark.randoop");

    public List<File> getExtraClassPath() {
        return null;
    }

    public void initialize(File src, File bin, List<File> classPath) {
        this.classPath = classPath;
    }

    public void run(String cName) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < classPath.size(); i++) {
                if (i > 0) {
                    sb.append(":");
                }
                sb.append(classPath.get(i));
            }
            randoop(sb.toString(), cName);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException("Failed to run Randoop");
        }
    }


    private boolean randoop(String cp, String cut) throws IOException {
        final String javaCmd = "java";
        ProcessBuilder pbuilder = new ProcessBuilder(javaCmd, "-Xmx512m", "-classpath", randoopJar + ":" + cp,
                "randoop.main.Main", "gentests", "--testclass=" + cut,
                /*"--timelimit=120", "--outputlimit=100", "--output-tests=all",*/ "--junit-output-dir=./temp/testcases/");

        pbuilder.redirectErrorStream(true);
        Process process = null;
        InputStreamReader stdout = null;
        InputStream stderr = null;
        OutputStreamWriter stdin = null;
        boolean mutationExitStatus = false;

        process = pbuilder.start();
        stderr = process.getErrorStream();
        stdout = new InputStreamReader(process.getInputStream());
        stdin = new OutputStreamWriter(process.getOutputStream());

        BufferedReader reader = new BufferedReader(stdout);
        String line = null;
        while ((line = reader.readLine()) != null) {
            //System.out.println(line);
        }
        reader.close();

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int exitStatus = process.exitValue();

        //System.out.println("exit status = " + exitStatus);

        mutationExitStatus = exitStatus == 0;

        if (stdout != null) {
            stdout.close();
        }
        if (stdin != null) {
            stdin.close();
        }
        if (stderr != null) {
            stderr.close();
        }
        if (process != null) {
            process.destroy();
        }

        new File("./temp/testcases/RandoopTest.java").delete();
        return mutationExitStatus;
    }
}
