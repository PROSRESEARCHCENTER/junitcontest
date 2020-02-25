/**
  * Copyright (c) 2017 Universitat Politècnica de València (UPV)

  * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

  * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

  * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

  * 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  **/
package sbst.benchmark.coverage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TestUtil {

	public static URL[] createURLs(String cp) throws MalformedURLException{

		LinkedList<String> required_libraries = new LinkedList<String>();

		String[] libraries = cp.split(":");
		for (String s : libraries){
			s = s.replace(":", "");
			if (s.length() > 0)
				required_libraries.addLast(s);
		}

		URL[] url = new URL[required_libraries.size()];

		for (int index = 0; index < required_libraries.size(); index++){
			if (required_libraries.get(index).endsWith(".jar")) {
				url[index] = new URL("jar:file:" + required_libraries.get(index)+"!/");
			} else {
				url[index] = new File(required_libraries.get(index)).toURI().toURL();
			}
		}

		//for (URL u : url){
		//	Main.debug("url "+u.getFile());
		//}
		return url;

	}

	public static List<File> getCompiledFileList(File directory){
		List<File> list = new ArrayList<File>();
		if (directory.isDirectory()){
			for (File f : directory.listFiles()){
					list.addAll(getCompiledFileList(f));
			}
		}  else {
			if (directory.getName().endsWith(".class"))
				list.add(directory);
		}

		return list;
	}
}
