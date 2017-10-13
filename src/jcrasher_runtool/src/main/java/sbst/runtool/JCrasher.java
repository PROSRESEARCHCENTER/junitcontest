/**
  * Copyright (c) 2017 Universitat Politècnica de València (UPV)

  * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

  * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

  * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

  * 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  **/
package sbst.runtool;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;

public class JCrasher implements ITestingTool{

    List<File> classPath;

	public List<File> getExtraClassPath(){
        List<File> ret = new ArrayList<File>();
        ret.add(new File(Main.JCRASHER_JAR));
        return ret;
    }

	public void initialize(File src, File bin, List<File> classPath){
        this.classPath = classPath;
    }

	public void run(String cName){
        try{
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < classPath.size(); i++){
                if(i > 0)
                    sb.append(":");
                sb.append(classPath.get(i));
            }
            jcrasher_run(sb.toString(), cName);
        }catch(IOException ioe){
            ioe.printStackTrace();
            throw new RuntimeException("Failed to run jCrasher");
        }
    }

	private boolean jcrasher_run(String cp, String cut) throws IOException {
        PrintStream logging = new PrintStream(new FileOutputStream(new File("./jcrasher_log.txt"), false));

        final String javaCmd = "/usr/bin/java";

		ProcessBuilder pbuilder = new ProcessBuilder(javaCmd,
                                                     "-classpath", Main.JCRASHER_JAR + ":" + cp,
                                                      "edu.gatech.cc.jcrasher.JCrasher", "-o", "./temp/testcases/",
                                                      "-f", "10", cut);

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
        while((line = reader.readLine()) != null) {
            logging.println(line);
        }
        reader.close();
        logging.flush();
        logging.close();

		try {
            process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int exitStatus = process.exitValue();

		mutationExitStatus = exitStatus == 0;

		if(stdout !=null) stdout.close();
		if(stdin !=null) stdin.close();
		if(stderr !=null) stderr.close();
		if(process !=null) process.destroy();
        try{
            process.waitFor();
        }catch(InterruptedException ie){}


        new File("./temp/testcases/JUnitAll.java").delete();
		return mutationExitStatus;
	}
}
