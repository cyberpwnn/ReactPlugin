package org.cyberpwn.react.updater;

public class EmptyCredentialsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "Username or Password not entered!";
	}

}
