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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Benchmark implements IBenchmarkTask{
	private final File sourceDirectory;
	private final File binDirectory;
	private final List<File> classPath;
	private final List<String> classNames;

	public Benchmark(File source, File bin, List<File> cp, List<String> classes) {
		sourceDirectory = source;
		binDirectory = bin;
		classPath = new ArrayList<File>(cp);
		classNames = new ArrayList<String>(classes);
	    Collections.sort(classNames);
	}

	@Override
	public File getSourceDirectory(){ return sourceDirectory; }

	@Override
	public File getBinDirectory(){ return binDirectory;	}

	@Override
	public List<File> getClassPath() { return classPath; }

	@Override
	public List<String> getClassNames() { return classNames; }
}
