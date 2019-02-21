package com.extreme.xc;

public abstract class AbstractSocketPooler {

	public abstract DigicoSocket borrowSocket();
	public abstract void returnSocket(DigicoSocket socket);
	
}
