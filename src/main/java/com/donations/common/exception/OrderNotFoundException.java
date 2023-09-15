package com.donations.common.exception;

public class OrderNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5046088972489765593L;

	public OrderNotFoundException(String message) {
		super(message);
	}
}
