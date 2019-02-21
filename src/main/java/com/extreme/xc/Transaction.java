package com.extreme.xc;

public abstract class Transaction<X, Y> {

	protected static final String pin = "1120557217105980";
	protected static final String agentCode = "287";
	protected static final String terminalID = "11111111";
	public abstract Y createOrder() throws ResourceException;
}
 