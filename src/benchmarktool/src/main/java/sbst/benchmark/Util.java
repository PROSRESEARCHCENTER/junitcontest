/**
  * Copyright (c) 2017 Universitat Politècnica de València (UPV)

  * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

  * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

  * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

  * 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  **/
package sbst.benchmark;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.nio.MappedByteBuffer;
import java.nio.charset.Charset;

public class Util {

    public static class CPBuilder{
    	private final StringBuilder sb = new StringBuilder();
    	private final String seperator;

    	public CPBuilder(){ this(":"); }
    	public CPBuilder(String seperator){
    		this.seperator = seperator;
    	}


    	private CPBuilder append(String s){
    		if(sb.length() > 0)
    			sb.append(seperator);
    		sb.append(s);
    		return this;
    	}

    	public CPBuilder and(String f){ return append(f); }
    	public CPBuilder and(File f){ return append(f.getAbsolutePath()); }
    	public CPBuilder and(Collection<File> lf){
    		for(File f : lf)
    			append(f.getAbsolutePath());
    		return this;
    	}
    	public CPBuilder andStrings(Collection<String> lf){
    		for(String f : lf)
    			append(f);
    		return this;
    	}
    	public String build(){ return sb.toString(); }
    }

    public static String readFile(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.defaultCharset().decode(bb).toString();
        } finally {
            stream.close();
        }
    }


	public static void cleanDirectory(File dir) throws IOException{
		if(dir.exists())
			delete(dir);

		Main.debug("Creating directory " + dir);
		if (!dir.mkdir())
			throw new IOException("Could not create directory: "+dir);
	}

	private static void delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}
		Main.debug("Deleting file or directory " + f);
		if (!f.delete())
			throw new IOException("Failed to delete file: " + f);
	}

    public static void CopyToDirectory(File fileOrDirectory, File destDir, String targetName) throws IOException{
        Main.debug("Copying '" + fileOrDirectory + "' to '" + destDir + "'");

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

    public static void pause(double time){
        try{
        	Thread.sleep((int) (time * 1000));
        }catch(Throwable t){}
    }
 }
