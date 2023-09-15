package com.donations.common.exception;

public class CategoryNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -769584053168473470L;

	public CategoryNotFoundException (String message) {
		super(message);
	}
}
