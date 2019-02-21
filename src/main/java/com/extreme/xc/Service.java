package com.extreme.xc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.enterprise.inject.ResolutionException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

public class Service extends Transaction<String, String>{

	private final String request = "<estel><header><requesttype>SERVICE</requesttype></header><request><agentcode>%s</agentcode><pin>%s</pin><terminalid>%s</terminalid><vendorcode>DIGICO</vendorcode><clienttype>POS</clienttype><agenttransid>0</agenttransid><comments>SERVICE API</comments></request></estel>";
	private final String host = ProviderProperties.getInstance().getIp();
	private final int port = ProviderProperties.getInstance().getPort();
	private final int timeout = ProviderProperties.getInstance()
			.getTimeOut1Minute();
	private PrintWriter out;
	private BufferedReader in;
	
	@Override
	public String createOrder() throws ResourceException {
		try {
			return connect(String.format(request, ProviderProperties.getInstance().getAgent(), ProviderProperties.getInstance().getPin(),
					ProviderProperties.getInstance().getTerminal()));
		} catch (SocketConnectTimeout e) {
			throw new ResolutionException(e.getMessage());
		}
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
	
	public String formatPretty(String arg0) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(arg0.getBytes(StandardCharsets.UTF_8)));
		return formatXml(doc);
	}
	
	 private final String formatXml(Document xml) throws Exception {
		 Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(2));
		Writer out = new StringWriter();
		tf.transform(new DOMSource(xml), new StreamResult(out));
		return out.toString();
	}

	
	public static void main(String[] args) throws ResourceException {
		System.out.println(new Service().createOrder());
	}

}
