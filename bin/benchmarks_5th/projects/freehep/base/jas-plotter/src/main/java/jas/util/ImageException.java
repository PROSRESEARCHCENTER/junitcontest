package jas.util;

/**
 * An Exception to be thrown in case of problems loading an image.
 * @see JASIcon
 */
public class ImageException extends Exception
{
	ImageException() { super(); }
	ImageException(String message) { super(message); }
}
