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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.plist.PropertyListConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

public class BenchmarkCollection
{
	Map<String,IBenchmarkTask> benchmarks;

	public BenchmarkCollection(File file) throws ConfigurationException
	{
		PropertyListConfiguration benchmarkList = new PropertyListConfiguration();
		benchmarkList.load(file);
		benchmarks = new HashMap<String, IBenchmarkTask>();
		for(ConfigurationNode child : benchmarkList.getRoot().getChildren())
		{
			String key = child.getName();
			SubnodeConfiguration conf = benchmarkList.configurationAt(key);

			String src = conf.getString("src");
			if(src == null || src.isEmpty())
				throw new ConfigurationException("Missing field: src");
			String bin = conf.getString("bin");
			if(bin == null || bin.isEmpty())
				throw new ConfigurationException("Missing field: bin");
			String[] classpath = conf.getStringArray("classpath");
			if(classpath == null)
				classpath = new String[0];
			String[] classes = conf.getStringArray("classes");
			if(classes == null || classes.length == 0)
				throw new ConfigurationException("Missing or empty field: classes");
			List<File> cp = new ArrayList<File>(classpath.length);
			for (String path : classpath)
			{
				cp.add(new File(path));
			}
			benchmarks.put(key, new Benchmark(new File(src), new File(bin), cp, Arrays.asList(classes)));
		}
	}

	public IBenchmarkTask forName(String benchmark)
	{
		return benchmarks.get(benchmark);
	}
	public IBenchmarkTask forName(String benchmark, int size, long seed) throws Exception
	{
		Random rand = new Random(seed);
		IBenchmarkTask result = forName(benchmark);
		if(result != null)
		{
			List<String> names = result.getClassNames();
			if(size > 0 && size < names.size())
			{
				List<String> newnames = new ArrayList<String>(names);
				Collections.shuffle(newnames,rand);
				newnames = new ArrayList<String>(newnames.subList(0, size));
				result = new Benchmark(result.getSourceDirectory(), result.getBinDirectory()
						, result.getClassPath(), names);
			}
		}
		return result;
	}

	public Set<String> getBenchmarks()
	{
		return benchmarks.keySet();
	}
}
