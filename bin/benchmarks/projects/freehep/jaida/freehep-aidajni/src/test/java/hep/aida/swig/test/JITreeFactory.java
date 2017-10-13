package hep.aida.swig.test;

import java.io.IOException;

import hep.aida.ITree;
import hep.aida.ITreeFactory;

public class JITreeFactory implements ITreeFactory {

	public ITree create() {
		System.err.println("JITree created");
		return new JITree();
	}

	public ITree create(String arg0) throws IllegalArgumentException,
			IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public ITree create(String arg0, String arg1)
			throws IllegalArgumentException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public ITree create(String arg0, String arg1, boolean arg2)
			throws IllegalArgumentException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public ITree create(String arg0, String arg1, boolean arg2, boolean arg3)
			throws IllegalArgumentException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public ITree create(String arg0, String arg1, boolean arg2, boolean arg3,
			String arg4) throws IllegalArgumentException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
