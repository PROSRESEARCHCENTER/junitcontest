package jas.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.BoundedRangeModel;
/**
 * This class should be used when you want to have a BoundedRangeModel
 * display the progress as a file is being opened.  The model is updated
 * with each call to <code>read</code>, so it is ideally used in a
 * BufferedReader where the stream is read in buffer-sized amounts.
 * That way, the model updates only when the buffer is filled.
 * For example, you might use it like this:
 * <br><code>BufferedReader input = new BufferedReader(new InputStreamReader(new FileReaderWithProgressBar(name, model)));</code>
 *  @see javax.swing.BoundedRangeModel
 *  @see java.io.BufferedReader
 *  @see java.io.InputStreamReader
 *  @author Jonas Gifford
 */
public class FileReaderWithProgressBar extends FileInputStream
{
	/**
	 * Creates a new FileReaderWithProgressBar.
	 *  @param fileName the file to read
	 *  @param model the BoundedRangeModel to update
	 *  @see javax.swing.BoundedRangeModel
	 *  @exception FileNotFoundException thrown from the constructor of FileInputStream
	 */
	public FileReaderWithProgressBar(String fileName, BoundedRangeModel model)
		throws FileNotFoundException
	{
		super(fileName);
		m_fileLength = new File(fileName).length();
		if (m_fileLength == 0) m_fileLength = 1; // just to be on the safe side (we will be dividing by this number)
		m_model = model;
		m_modelMin = model.getMinimum();
		m_modelRange = model.getMaximum() - m_modelMin;
	}

	/**
	 * Works just like the equivalent method in FileInputStream, except that
	 * it updates the model to reflect the progress in reading the file.
	 *  @see java.io.FileInputStream
	 *  @see java.io.FileInputStream#read()
	 */
	public int read() throws IOException
	{
		int i = super.read(); 
		if (i != -1)
			m_bytesRead++;
		updateModel();
		return i;
	}

	/**
	 * Works just like the equivalent method in FileInputStream, except that
	 * it updates the model to reflect the progress in reading the file.
	 *  @see java.io.FileInputStream
	 *  @see java.io.FileInputStream#read(byte[])
	 */
	public int read(byte[] cbuf) throws IOException
	{
		int i = super.read(cbuf); 
		if (i > 0)
			m_bytesRead += i;
		updateModel();
		return i;
	}

	/**
	 * Works just like the equivalent method in FileInputStream, except that
	 * it updates the model to reflect the progress in reading the file.
	 *  @see java.io.FileInputStream
	 *  @see java.io.FileInputStream#read(byte[], int, int)
	 */
	public int read(byte[] cbuf, int off, int len) throws IOException
	{
		int i = super.read(cbuf, off, len); 
		if (i > 0)
			m_bytesRead += i;
		updateModel();
		return i;
	}

	/**
	 * Works just like the equivalent method in FileInputStream, except that
	 * it updates the model to reflect the progress in reading the file.
	 *  @see java.io.FileInputStream
	 *  @see java.io.FileInputStream#skip(long)
	 */
	public long skip(long n) throws IOException
	{
		long rc = super.skip(n);
		m_bytesRead += n;
		updateModel();
		return rc;
	}
	private void updateModel()
	{
		long l = (m_modelRange * m_bytesRead) / m_fileLength;
		m_model.setValue(m_modelMin + (int) l); // l in range 0:100, so cast to int is safe
	}
	private BoundedRangeModel m_model;
	private long m_bytesRead = 0;
	private long m_fileLength;
	private long m_modelRange;
	private int m_modelMin;
}
