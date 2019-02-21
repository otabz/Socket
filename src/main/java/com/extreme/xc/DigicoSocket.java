package com.extreme.xc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.extreme.xc.Purchase.Order;
import com.sun.mail.util.SocketConnectException;

public class DigicoSocket {

	private final String host = ProviderProperties.getInstance().getIp();
	private final int port = ProviderProperties.getInstance().getPort();
	private final int timeout = ProviderProperties.getInstance()
			.getTimeOut1Minute(); // 60 seconds
	private static Logger log = Logger.getLogger(DigicoSocket.class.getName());
	// private String clientID;
	// private String userID;
	// private String terminalID;
	// private Long threadNo;
	private String order;
	private long proccessNo;
	private PrintWriter out;
	private BufferedReader in;
	private Socket socket;

	public DigicoSocket(long processNo) {
		this.proccessNo = processNo;
		this.socket = new Socket();
	}

	public String makePurchase(long threadNo, String order)
			throws SocketConnectTimeout, IOException {
		// this.clientID = clientID;
		// this.userID = userID;
		// this.terminalID = terminalID;
		this.order = order;
		connect(threadNo);
		write(threadNo, this.order);
		return read(threadNo);
	}

	public void askServerToClose(long threadNo) {
		// if ((testOn == proccessNo)) {
		write(threadNo, ".");
		// }
	}

	public String retryPurchase(long threadNo, String order)
			throws SocketConnectTimeout, IOException {
		/** silently close socket **/
		log.log(Level.INFO, "retrying . . . process no. " + proccessNo
				+ " thread no. " + threadNo);
		try {
			socket.close();
		} catch (Exception e) {
			log.log(Level.INFO, "socket couldn't close . . . process no. "
					+ proccessNo + " thread no. " + threadNo);
		}
		socket = new Socket();
		connect(threadNo);
		write(threadNo, this.order);
		return read(threadNo);
	}

	private void write(long threadNo, String request) {
		log.log(Level.INFO, "writing . . . process no. " + proccessNo
				+ " thread no. " + threadNo);
		out.println(request);
	}

	private String read(long threadNo) throws IOException {
		String response;
		log.log(Level.INFO, "reading . . . process no. " + proccessNo
				+ " thread no. " + threadNo);
		if ((response = in.readLine()) != null) {
			return response;
		}
		throw new IOException("socket returned null . . . process no. "
				+ proccessNo + " thread no. " + threadNo);
	}

	private void connect(long threadNo) throws SocketConnectTimeout {
		try {
			if (!socket.isConnected()) {
				log.log(Level.INFO, "connecting ["+host+"] . . . process no. "
						+ proccessNo + " thread no. " + threadNo);
				socket.connect((new InetSocketAddress(host, port)), timeout);
				socket.setSoTimeout(timeout);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
			}
			log.log(Level.INFO, "connected . . . process no. " + proccessNo
					+ " thread no. " + threadNo);
		} catch (IOException e) {
			throw new SocketConnectTimeout(e.getLocalizedMessage(), e);
		}
	}

	public void kill() throws IOException {
		log.log(Level.INFO, "killing . . . process no. " + proccessNo);
		// + " thread no. " + threadNo);
		// if (abandoned) {
		socket.close();
		log.log(Level.INFO, "killed . . . process no. " + proccessNo);
		// + " thread no. " + threadNo);
		// }
	}

	public long getProcessNo() {
		return this.proccessNo;
	}
}
