package sbst.benchmark.coverage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TestUtil {

    public static URL[] createURLs(String cp) throws MalformedURLException {

        LinkedList<String> required_libraries = new LinkedList<String>();

        String[] libraries = cp.split(":");
        for (String s : libraries) {
            s = s.replace(":", "");
            if (s.length() > 0) {
                required_libraries.addLast(s);
            }
        }

        URL[] url = new URL[required_libraries.size()];

        for (int index = 0; index < required_libraries.size(); index++) {
            if (required_libraries.get(index).endsWith(".jar")) {
                url[index] = new URL("jar:file:" + required_libraries.get(index) + "!/");
            } else {
                url[index] = new File(required_libraries.get(index)).toURI().toURL();
            }
        }

        //for (URL u : url){
        //	Main.debug("url "+u.getFile());
        //}
        return url;

    }

    public static List<File> getCompiledFileList(File directory) {
        List<File> list = new ArrayList<File>();
        if (directory.isDirectory()) {
            for (File f : directory.listFiles()) {
                list.addAll(getCompiledFileList(f));
            }
        } else {
            if (directory.getName().endsWith(".class")) {
                list.add(directory);
            }
        }

        return list;
    }
}
