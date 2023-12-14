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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class RunTool {
    private final ITestingTool tool;
    private final SBSTChannel channel;

    public RunTool(ITestingTool tool, Reader input, Writer output) {
        this.tool = tool;
        this.channel = new SBSTChannel(input, output);
    }

    public void run() throws IOException {
        channel.token("BENCHMARK");
        File src = channel.directory();
        File bin = channel.directory();
        int n = channel.number();
        List<File> classPath = new ArrayList<File>();
        for (int i = 0; i < n; i++) {
            classPath.add(channel.directory_jarfile());
        }
        tool.initialize(src, bin, classPath);

        int m = channel.number();
        if (tool.getExtraClassPath() != null) {
            channel.emit("CLASSPATH");
            List<File> extraCP = tool.getExtraClassPath();
            int k = extraCP.size();
            channel.emit(k);
            for (File file : extraCP) {
                channel.emit(file);
            }
        }
        channel.emit("READY");
        for (int i = 0; i < m; i++) {
            String cName = channel.className();
            tool.run(cName);
            channel.emit("READY");
        }
    }
}
