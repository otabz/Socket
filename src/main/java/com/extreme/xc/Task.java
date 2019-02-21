package com.extreme.xc;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import test.extreme.xc.AuthServer;
import test.extreme.xc.DBManager;
import test.extreme.xc.AuthServer.Headers;

import com.extreme.xc.Purchase.Request;
import com.extreme.xc.entity.CreditTellerUser;

public class Task extends ResourceServer implements Runnable {
	
	private static Logger log = Logger.getLogger(ResourceServer.class.getName());
	
	private long threadNo;
	private Purchase.Request request;
	private HttpHeaders headers;
	private DBManager db;
	
	public Task(long threadNo, Request req) {
		this.threadNo = threadNo;
		this.request = req;
		this.headers = new AuthServer.Headers();
		this.db = new DBManager();
		
	}
	
	@Override
	public void run() {
			//javax.ws.rs.core.Response response = null;
			String contentType = headers.getHeaderString(HttpHeaders.CONTENT_TYPE);
			if (contentType != null
					&& contentType.contains(MediaType.APPLICATION_JSON)) {
				try {
					CreditTellerUser client = new AuthServer(db).auth(headers);
					int clientID = client.getCreditTeller().getId();
					String userID = client.getId();
					com.extreme.xc.Purchase.Response result = new Purchase.Builder(
							userID, clientID)
							.with(db)
							.build(request)
							.createOrder()
							.dispatch(SocketPooler.getInstance(),
									threadNo).invoice();
					log.log(Level.INFO, "Done . . .");
				} catch (OAuthException ex) {
					//response = Response.createOAuthExceptionResponse(ex);
					log.log(Level.INFO, ex.getLocalizedMessage());
				} catch (ResourceException rex) {
					//response = Response.createResourceExceptionResponse(rex);
					log.log(Level.INFO, rex.getLocalizedMessage());
				} catch (Exception e) {
					/*response = Response.createInternalExceptionResponse(String
							.format(Response.INTERNAL_SERVER_ERROR,
									"Oops! internal exception",
									Calendar.getInstance(), Calendar.getInstance(),
									request.toString()));*/
					log.log(Level.INFO, e.getLocalizedMessage());
					e.printStackTrace();
				}
			} else {
				/*response = Response
						.createBadRequestResponse(Response.UNSUPPORTED_MEDIA_TYPE);*/
			}
			//log.log(Level.INFO, response.toString());
	}

}
