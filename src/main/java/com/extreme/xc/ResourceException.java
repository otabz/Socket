package com.extreme.xc;

import java.util.logging.Logger;
import javax.ws.rs.core.Response.Status;

public class ResourceException extends Exception {

	private static final long serialVersionUID = -3489020487691423112L;

	protected static Logger log = Logger.getLogger(OAuthException.class
			.getName());

	private String message;
	private Status httpStatus;

	public ResourceException(String message, Status httpStatus) {
		super();
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public ResourceException(Throwable e, String message, Status httpStatus) {
		super(e);
		this.message = message;
		this.httpStatus = httpStatus;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public Status getHttpStatus() {
		return httpStatus;
	}
}
