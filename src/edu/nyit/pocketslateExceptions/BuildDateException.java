/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslateExceptions;
/**
 * <p>Title: BuildDateException.java</p>
 * <p>Description: Exception class for handling a matching build date of XML data.</p>
 * @author jasonscott
 *
 */
public class BuildDateException extends Exception{


	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the exception.
	 * @param message - appropriate message for exception thrown.
	 */
	public BuildDateException(String message) {
		super(message);
	}
}
