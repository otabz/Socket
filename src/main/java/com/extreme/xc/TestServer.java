package com.extreme.xc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.List;

import javax.ejb.CreateException;
import javax.print.attribute.standard.DateTimeAtCompleted;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.extreme.xc.Purchase.Estel;
import com.extreme.xc.Purchase.Estel.Header;
import com.extreme.xc.Purchase.Estel.Response;
import com.extreme.xc.Purchase.Estel.Response.Records;
import com.extreme.xc.Purchase.Estel.Response.Records.Record;

public class TestServer {

	
	  public static void main(String[] args) throws Exception {
	        System.out.println("The capitalization server is running.");
	        int clientNumber = 0;
	        ServerSocket listener = new ServerSocket(9898, 50);
	   
	        try {
	            while (true) {
	            	//Thread.sleep(65000);
	                new Capitalizer(listener.accept(), clientNumber++).start();
	            }
	        } finally {
	            listener.close();
	        }
	    }
	 
	  
	  private static class Capitalizer extends Thread {
	        private Socket socket;
	        private int clientNumber;

	        public Capitalizer(Socket socket, int clientNumber) {
	            this.socket = socket;
	            this.clientNumber = clientNumber;
	            log("New connection with client# " + clientNumber + " at " + socket);
	        }

	        /**
	         * Services this thread's client by first sending the
	         * client a welcome message then repeatedly reading strings
	         * and sending back the capitalized version of the string.
	         */
	        public void run() {
	            try {

	                // Decorate the streams so we can send characters
	                // and not just bytes.  Ensure output is flushed
	                // after every newline.
	                BufferedReader in = new BufferedReader(
	                        new InputStreamReader(socket.getInputStream()));
	                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

	                // Send a welcome message to the client.
	               // out.println("Hello, you are client #" + clientNumber + ".");
	               // out.println("Enter a line with only a period to quit\n");

	                // Get messages from the client, line by line; return them
	                // capitalized
	                int count = 0 ;
	                while (true) {
	                	log("waiting for io :"+ count);
	                    String input = in.readLine();
	                    log("received io:"+ count);
	                    if (input == null || input.equals(".")) {
	                        break;
	                    }
	                    count++;
	                    System.out.println(input);
	                    String output = "";
	                    
	                    if ("MOBILY".equals(createRequest(input).request.vendorCode)) {
	                    	System.out.println("i am sleeping  for MOBILY");
	                    	output = "<estel><header><responsetype>PURCHASE</responsetype></header><response><resultcode>74</resultcode><resultdescription>Vendor Not Found</resultdescription><agentcode>1234</agentcode><transid>76841642</transid></response></estel>";
	                    	//output="8767868";
	                    	Thread.sleep(40000);
	                    	System.out.println("i am awaked for MOBILY");
	                    } else {
	                    	System.out.println("i am sleeping for else");
	                    	output = createResponse(input);
	                    	Thread.sleep(5000);
	                    }
	                    out.println(output);
	                }
	            } catch (IOException | InterruptedException | JAXBException e) {
	                log("Error handling client# " + clientNumber + ": " + e);
	            } finally {
	                try {
	                    socket.close();
	                } catch (IOException e) {
	                    log("Couldn't close a socket, what's going on?");
	                }
	                log("Connection with client# " + clientNumber + " closed");
	            }
	        }

	        /**
	         * Logs a simple message.  In this case we just write the
	         * message to the server applications standard output.
	         */
	        private void log(String message) {
	            System.out.println(message);
	        }
	    }
	  
	public static String createResponse(String message) throws JAXBException {
		//message = "<estel><header><requesttype>PURCHASE</requesttype></header><request><agentcode>1234</agentcode><pin>1234567890123456</pin><terminalid>11111111</terminalid><vendorcode>MOBILY</vendorcode><clienttype>POS</clienttype><agenttransid>18467</agenttransid><productcode>SR20</productcode><qty>1</qty></request></estel>";
				Estel request = unmarshall(message);
				return String.format("<estel><header><responsetype>PURCHASE</responsetype></header><response><agentcode>%s</agentcode><agenttransid>%s</agenttransid><vendorcode>%s</vendorcode><productcode>%s</productcode><qty>%f</qty><amount>20.00</amount><prewalletbalance>982.0 </prewalletbalance><resultcode>0</resultcode><resultdescription>Transaction Successful</resultdescription><recordcount>1</recordcount><transid>772804</transid><records><record><pinno>91023410855115330</pinno><serialno>1533158197</serialno><expirydate>12/31/2015 00:00:00</expirydate></record><record><pinno>91023410855115330</pinno><serialno>1533158197</serialno><expirydate>12/31/2015 00:00:00</expirydate></record></records><requestcts>03/30/2017 12:37:57 </requestcts><responsects>06/30/2012 12:37:58</responsects><clienttype>POS</clienttype><walletbalance>976.0</walletbalance><service>F</service><tax>0.00</tax></response></estel>",
						request.request.agentCode, request.request.agentTransID, request.request.vendorCode, request.request.productCode, request.request.qty);
	  }
	
	public static Estel createRequest(String message) throws JAXBException {
		return unmarshall(message);
	}
	  
	  private static Estel unmarshall(String message) throws JAXBException {
		  JAXBContext	jaxbContext = JAXBContext.newInstance(Estel.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext
					.createUnmarshaller();
			return (Estel) jaxbUnmarshaller.unmarshal(new StringReader(
					message));
	  }
	  
	  @XmlRootElement
		static class Estel {
			@XmlElement
			private Header header;
			@XmlElement
			private Request request;

			static class Request {
				@XmlElement(name = "agentcode")
				private String agentCode;
				@XmlElement(name = "agenttransid")
				private String agentTransID;
				@XmlElement(name = "vendorcode")
				private String vendorCode;
				@XmlElement(name = "productcode")
				private String productCode;
				@XmlElement(name = "qty")
				private Double qty;
				@XmlElement(name = "amount")
				private Double amount;
				@XmlElement(name = "clienttype")
				private String clientType;
				@XmlElement(name = "pinno")
				private String pinNo;
			}

			static class Header {
				//
				@XmlElement(name = "requestType")
				private String requestType;
			}
		}
}
