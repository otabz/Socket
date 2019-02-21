package com.extreme.xc;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class NoSocketPooler extends AbstractSocketPooler {

	private static AtomicLong processNo=new AtomicLong(0); 
	private DigicoSocket socket;
	
	@Override
	public DigicoSocket borrowSocket() {
		return new DigicoSocket(processNo.incrementAndGet());
	}

	@Override
	public void returnSocket(DigicoSocket socket) {
		try {
			socket.kill();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
