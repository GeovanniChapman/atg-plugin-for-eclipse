package org.geochapm.atg.exceptions;

/**
 * 
 * @author geovanni.chapman
 *
 */
public class NotClasspathEntryFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_MESSAGE = "Not ClasspathEntry Found.";
	
	public static final String NOT_CLASSPATH_ENTRY_FOUND_BY_ENTRY_KIND_MESSAGE = "Not ClasspathEntry Found By Entry Kind: %s.";
	
	public NotClasspathEntryFoundException(String message) {
		super(message);
	}

}
