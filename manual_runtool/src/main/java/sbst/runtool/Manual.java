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
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Manual implements ITestingTool{

    List<File> classPath;
    String extracp;

    public Manual(String extraCP){
        this.extracp = extraCP;
    }


	public List<File> getExtraClassPath(){
        List<File> ret = null;
        if(extracp != null){
            ret = new ArrayList<File>();
            ret.add(new File(extracp));
        }
        return ret;
    }

	public void initialize(File src, File bin, List<File> classPath){
        this.classPath = classPath;
    }

	public void run(String cName){
        try{
            File path = new File("./" + cName.replace('.', '/'));
            String pathPref = path.getParent();
            File testFile1 = new File(pathPref + "/" + "Test" + path.getName() + ".java");
            File testFile2 = new File(pathPref + "/" + path.getName() + "Test" + ".java");

            if(testFile1.exists())
                CopyToDirectory(testFile1, new File("./temp/testcases/" + pathPref), null);
            else if(testFile2.exists())
                CopyToDirectory(testFile2, new File("./temp/testcases/" + pathPref), null);
            else
                throw new IOException("No corresponding JUnit test found which could be copied!!");
        }catch(IOException ioe){
            ioe.printStackTrace();
            throw new RuntimeException("Failed to copy unit test!");
        }
    }

    public static void CopyToDirectory(File fileOrDirectory, File destDir, String targetName) throws IOException{

        if(targetName == null)
            targetName = fileOrDirectory.getName();

        if(!destDir.exists()){
            if(!destDir.mkdirs())
                throw new IOException("Unable to create directory " + destDir.getAbsolutePath());
        }

        if(fileOrDirectory.isFile()){
            File destFile = new File(destDir.getAbsolutePath() + File.separator + targetName);
            if(!destFile.exists()){
                if(!destFile.createNewFile())
                    throw new IOException("Unable to create file " + destFile.getAbsolutePath());
            }

            FileChannel source = null;
            FileChannel destination = null;

            try {
                source = new FileInputStream(fileOrDirectory).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            }finally {
                if(source != null)
                    source.close();
                if(destination != null)
                    destination.close();
            }
        }else if(fileOrDirectory.isDirectory()){
            File copyDir = new File(destDir.getAbsolutePath() + File.separator + targetName);

            if(!copyDir.exists()){
                if(!copyDir.mkdir())
                    throw new IOException("Unable to create directory " + copyDir.getAbsolutePath());
            }

            File[] files = fileOrDirectory.listFiles();
            if(files != null) {
                for(File f: files)
                    CopyToDirectory(f, copyDir, null);
            }
        }
    }

}
