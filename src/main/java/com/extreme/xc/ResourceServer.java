package com.extreme.xc;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.ejb.CreateException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.spi.DirStateFactory.Result;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.DatatypeConverter;
import javax.xml.crypto.dsig.keyinfo.KeyValue;

import com.extreme.xc.Purchase.Request;
import com.extreme.xc.Purchase.Voucher;
import com.extreme.xc.entity.CreditTellerUser;

@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public class ResourceServer {

	@Inject
	@Named("ProductionDB")
	private DBManager db;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/purchase")
	public javax.ws.rs.core.Response purchase(@Context HttpHeaders headers,
			Purchase.Request request) {
		javax.ws.rs.core.Response response = null;
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
						.dispatch(new NoSocketPooler(),
								Thread.currentThread().getId()).invoice();
				response = Response.createOkResponse(result);
			} catch (OAuthException ex) {
				response = Response.createOAuthExceptionResponse(ex);
			} catch (ResourceException rex) {
				response = Response.createResourceExceptionResponse(rex);
			} catch (Exception e) {
				response = Response.createInternalExceptionResponse(String
						.format(Response.INTERNAL_SERVER_ERROR,
								"Oops! internal exception",
								Calendar.getInstance(), Calendar.getInstance(),
								request.toString()));
				e.printStackTrace();
			}
		} else {
			response = Response
					.createBadRequestResponse(Response.UNSUPPORTED_MEDIA_TYPE);
		}
		return response;
	}
	
	@GET
	@Path("/service")
	@Produces({MediaType.APPLICATION_XML})
	public javax.ws.rs.core.Response service() {
		javax.ws.rs.core.Response response = null;
		try {
			Service service = new Service();
			String result = service.createOrder();
			String formattedResult = service.formatPretty(result);
			response = Response.createOkResponse(formattedResult);
		} catch(Exception e) {
			response = Response.createInternalExceptionResponse(String
					.format(Response.INTERNAL_SERVER_ERROR,
							"Oops! internal exception",
							Calendar.getInstance(), Calendar.getInstance(),
							e.getMessage()));
		}
		return response;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/ack")
	public javax.ws.rs.core.Response ack(@Context HttpHeaders headers,
			PurchaseAck.Request request) {
		javax.ws.rs.core.Response response = null;
		String contentType = headers.getHeaderString(HttpHeaders.CONTENT_TYPE);
		if (contentType != null
				&& contentType.contains(MediaType.APPLICATION_JSON)) {
			try {
				CreditTellerUser client = new AuthServer(db).auth(headers);
				int clientID = client.getCreditTeller().getId();
				String userID = client.getId();
				PurchaseAck.Response result = new PurchaseAck.Builder(userID, clientID)
				.with(db)
				.build(request)
				.endorse();
				response = Response.createOkResponse(result);
			} catch (OAuthException ex) {
				response = Response.createOAuthExceptionResponse(ex);
			} catch (ResourceException rex) {
				response = Response.createResourceExceptionResponse(rex);
			} catch (Exception e) {
				response = Response.createInternalExceptionResponse(String
						.format(Response.INTERNAL_SERVER_ERROR,
								"Oops! internal exception",
								Calendar.getInstance(), Calendar.getInstance(),
								e.getMessage()));
			}
		} else {
			response = Response
					.createBadRequestResponse(Response.UNSUPPORTED_MEDIA_TYPE);
		}
		return response;
	}
	

	public void testObjectPool() throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(8);

		// execute 8 tasks in separate threads

		executor.execute(new Task(1, create("1000")));
		//Thread.sleep(30000);
		executor.execute(new Task(2, create("2000")));
		//Thread.sleep(30000);
		executor.execute(new Task(3, create("3000")));
		//Thread.sleep(30000);
		executor.execute(new Task(4, create("4000")));
		//Thread.sleep(30000);
		executor.execute(new Task(5, create("5000")));
		//Thread.sleep(30000);
		executor.execute(new Task(6, create("6000")));
		//Thread.sleep(30000);
		executor.execute(new Task(7, create("7000")));
		Thread.sleep(30000);
		executor.execute(new Task(8, create("8000")));

		executor.shutdown();
		// executoeFixed.shutdown();
		try {
			// executoeFixed.awaitTermination(60, TimeUnit.SECONDS);
			executor.awaitTermination(60, TimeUnit.SECONDS);
			SocketPooler.shutdown();
		} catch (InterruptedException e)

		{
			e.printStackTrace();
		}
	}
	
	public Request create(String terminalID) {
		Request req = new Request();
		req.setClientID("420");
		req.setClientTransactionID("789870");
		req.setTerminalID(terminalID);
		req.setVendorCode("MOBILY");
		req.setProductCode("SR10");
		req.setQuantity(1.0);
		return req;
	}
	
	 public static void main(String[] args) throws InterruptedException {
		 ResourceServer pooler = new ResourceServer();
		 pooler.testObjectPool();
	 }
	
}
