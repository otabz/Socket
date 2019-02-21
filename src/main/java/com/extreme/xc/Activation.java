package com.extreme.xc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Activation extends Transaction<String, String>{

	private final String request = "<estel><header><requesttype>ACTIVATION</requesttype></header><request><agentcode>%s</agentcode><pin>%s</pin><activationcode>%s</activationcode><terminalid>%s</terminalid><vendorcode>DIGICO</vendorcode></request></estel>";
	private final String host = ProviderProperties.getInstance().getIp();
	private final int port = ProviderProperties.getInstance().getPort();
	private final int timeout = ProviderProperties.getInstance()
			.getTimeOut1Minute();
	private PrintWriter out;
	private BufferedReader in;
	
	@Override
	public String createOrder() throws ResourceException {
		String agentCode = "50001";
		String pin = "4766435408911570";
		String activationCode = "4766435408911570";
		String terminalID = "11111111";
		try {
			return connect(String.format(request, agentCode, pin, activationCode, terminalID));
		} catch (SocketConnectTimeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "-1";
	}
	
	private String connect(String request) throws SocketConnectTimeout {
		try {
				Socket socket = new Socket();
				socket.connect((new InetSocketAddress(host, port)), timeout);
				socket.setSoTimeout(timeout);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				
				// activate
				out.println(request);
				return in.readLine();
		} catch (IOException e) {
			throw new SocketConnectTimeout(e.getLocalizedMessage(), e);
		}
	}
	
	public static void main(String[] args) throws ResourceException {
		System.out.println(new Activation().createOrder());
	}

}
