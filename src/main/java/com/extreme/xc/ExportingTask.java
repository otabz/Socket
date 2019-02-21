package com.extreme.xc;

import java.io.IOException;

public class ExportingTask implements Runnable {
	private SocketPooler pool;
	private int threadNo;
	private boolean retried = false;

	public ExportingTask(SocketPooler pool, int threadNo) {
		this.pool = pool;
		this.threadNo = threadNo;
	}

	public void run() {
		DigicoSocket socket = pool.borrowSocket();
		try {
			// get an object from the pool
			System.out.println("Thread " + threadNo
					+ ": Object with process no. " + socket.getProcessNo()
					+ " was borrowed");
			
			// process
			process(socket);
			
			// return ExportingProcess instance back to the pool
			pool.returnSocket(socket);
			System.out.println("Thread " + threadNo
					+ ": Object with process no. " + socket.getProcessNo()
					+ " was returned");
		} catch (SocketConnectTimeout e) {
			System.out.println("Thread " + threadNo
					+ ": Object with process no. " + socket.getProcessNo()
					+ " in couldn't connect");
		} catch (IOException e) {
			System.out.println("Thread " + threadNo
					+ ": Object with process no. " + socket.getProcessNo()
					+ " in couldn't write/read");
		}
	}

	private void process(DigicoSocket socket) throws SocketConnectTimeout,
			IOException {
		try {
			// make purchase
			String response = socket.makePurchase(this.threadNo, (String.valueOf(threadNo).concat("/")
					.concat(String.valueOf(socket.getProcessNo()))));
			System.out.println(response +" Thread "+ threadNo
					+ ": Object with process no. " + socket.getProcessNo()
					+ " output");
			if (socket.getProcessNo() != 2) {
				socket.askServerToClose(this.threadNo);
			}
		} catch (IOException e) {
			socket.retryPurchase(this.threadNo, (String.valueOf(threadNo).concat("/")
					.concat(String.valueOf(socket.getProcessNo()))));
		}
	}

}// End of the
