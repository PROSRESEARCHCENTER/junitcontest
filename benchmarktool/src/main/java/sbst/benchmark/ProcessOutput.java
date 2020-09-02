/**
  * Copyright (c) 2017 Universitat Politècnica de València (UPV)

  * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

  * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

  * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

  * 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  **/
package sbst.benchmark;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.exec.LogOutputStream;

public class ProcessOutput extends LogOutputStream {
    private final List<String> lines = new ArrayList<String>();
    boolean reroute;

    public ProcessOutput(boolean rerouteToStdOut){
    	reroute = rerouteToStdOut;
    }

    protected void processLine(String line, int level) {
        if(line != null){
            lines.add(line);
            if(reroute) System.out.println(line);
        }
    }

    public List<String> getLines() { return lines; }

    public String toString(){
    	StringBuilder sb = new StringBuilder();
    	for(String line : getLines())
    		sb.append(line).append("\n");
    	return sb.toString();
    }
}
