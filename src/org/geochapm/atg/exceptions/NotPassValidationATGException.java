package org.geochapm.atg.exceptions;

/**
 * 
 * @author geovanni.chapman
 *
 */
public class NotPassValidationATGException extends ATGException {

	public static String ATG_ROOT_REQUIRED = "The ATG Root path has not been configured yet.";
	
	public static String PATH_IS_NOT_VALID_ATG_ROOT = "The path \"%s\" is not a valid ATG Root location.";
	
	public static String PATH_IS_NOT_VALID_ATG_MODULE = "The path \"%s\" is not a valid ATG module location.";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NotPassValidationATGException(String message) {
		super(message);
	}

}
