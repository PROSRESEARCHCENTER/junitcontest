package jas.util;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * A FileFilter which accepts files with a given file extension
 */
public class FileTypeFileFilter extends FileFilter
{
	/**
	 * @param extension The file extension (without the ., e.g "gif")
	 * @param description The file description.
	 */
	public FileTypeFileFilter(String extension, String description)
	{
		this.ext = extension;
		this.desc = description;
	}
    public boolean accept(File f) 
	{
 	   if (f.isDirectory()) return true;
 	   
 	   String s = f.getName();
 	   int i = s.lastIndexOf('.');
 	   if(i > 0 &&  i < s.length() - 1)
	   {
 		   String extension = s.substring(i+1).toLowerCase();
 		   if (extension.equals(ext)) return true;
	   }
	   return false;
    }
	/**
	 * The extension corresponding to this file filter
	 */
	public String getExtension()
	{
		return ext;
	}
    // The description of this filter
    public String getDescription() 
	{
 	   return desc;
    }
	private String ext;
	private String desc;
}
