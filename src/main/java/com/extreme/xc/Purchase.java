package com.extreme.xc;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.internet.HeaderTokenizer;
import javax.persistence.Transient;
import javax.security.auth.message.callback.PrivateKeyCallback.IssuerSerialNumRequest;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.extreme.xc.Purchase.Estel.Response.Records.Record;

public class Purchase extends Transaction<Purchase.Request, Purchase.Order> {

	private final static String REQUEST_RECEIVED_LOG = "Request received {user_id: %s, %s, at: %s}";
	private final static String DIGICO_COMMAND = "<estel><header><requesttype>PURCHASE</requesttype></header><request><agentcode>%s</agentcode><pin>%s</pin><terminalid>%s</terminalid><vendorcode>%s</vendorcode><clienttype>POS</clienttype><agenttransid>%d</agenttransid><productcode>%s</productcode><qty>%d</qty></request></estel>";
	private static Logger log = Logger.getLogger(Purchase.class.getName());
	private static Logger olog = Logger.getLogger(Order.class.getName());

	private DBManager db;
	private final String userID;
	private final int clientID;
	private final String requestedVendorCode;
	private final String requestedProductCode;
	private final Double requestedQty;
	private final String requestedAt = Purchase.getDateTimeNow();
	private final String clientTransID;
	private String terminalID;
	private long transID;

	private static String getDateTimeNow() {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a zzz");
		return ft.format(dNow);
	}

	public Purchase(String userID, int clientID, String terminalID,
			String clientTransID, String requestedVendorCode,
			String requestedProductCode, Double requestedQty, DBManager db) {
		this.clientID = clientID;
		this.userID = userID;
		this.terminalID = terminalID;
		this.clientTransID = clientTransID;
		this.requestedVendorCode = requestedVendorCode;
		this.requestedProductCode = requestedProductCode;
		this.requestedQty = requestedQty;
		this.db = db;
	}

	@Override
	public Order createOrder() throws ResourceException {
		// create request registry
		this.transID = db.createTransactionRegistry(clientID, userID,
				terminalID, clientTransID, requestedVendorCode,
				requestedProductCode, requestedQty, requestedAt);
		// create purchase order
		return initiateOrder();
	}

	private Order initiateOrder() {
		final String command = String.format(DIGICO_COMMAND, ProviderProperties
				.getInstance().getAgent(), ProviderProperties.getInstance()
				.getPin(), ProviderProperties.getInstance().getTerminal(),
				requestedVendorCode, transID, requestedProductCode,
				requestedQty.intValue());
		return new Order(command);
	}

	static class Builder {
		private int clientID;
		private String userID;
		private DBManager db;

		public Builder(String userID, int clientID) {
			this.clientID = clientID;
			this.userID = userID;
		}

		public Builder with(DBManager db) {
			this.db = db;
			return this;
		}

		public Purchase build(Request request) throws ResourceException {
			request.validate(this.clientID);
			log.log(Level.INFO,
					String.format(REQUEST_RECEIVED_LOG, this.userID,
							request.toString(), Purchase.getDateTimeNow()));
			Purchase purchase = new Purchase(this.userID, this.clientID,
					request.terminalID, request.clientTransactionID,
					request.vendorCode, request.productCode, request.qty,
					this.db);
			return purchase;
		}

	}

	class Order {
		private final String command;
		private String outcome;

		public Order(final String command) {
			this.command = command;
		}

		public Response invoice() throws ResourceException {
			Voucher voucher = new Voucher(outcome, null);
			if (voucher.isValidPurchase()) {
				voucher.result.requestTime = requestedAt;
				voucher.result.respondTime = getDateTimeNow();
				String error = voucher.verify();
				db.createPurchaseRegistry(transID, voucher.result.userID,
						clientID, voucher.result.terminalID,
						voucher.result.clientTransactionID,
						voucher.result.operatorTransactionID,
						voucher.result.agentCode, voucher.result.agentTransID,
						voucher.result.operatorRequestTime,
						voucher.result.operatorResponseTime,
						voucher.result.vendorCode, voucher.result.productCode,
						voucher.result.quantity, voucher.result.vouchers,
						voucher.result.respondTime, error);
				if (!error.isEmpty()) {
					throw new ResourceException(String.format(
							com.extreme.xc.Response.MESSAGE_VERIFY_FAILED,
							userID, clientID, terminalID, transID, error),
							Status.CONFLICT);
				}
			}
			return voucher.result;
		}

		public Order dispatch(AbstractSocketPooler pool, long threadNo)
				throws ResourceException {
			DigicoSocket socket = pool.borrowSocket();
			// String response = null;
			try {
				// get an object from the pool
				olog.log(Level.INFO, "Thread " + threadNo
						+ ": Object with process no. " + socket.getProcessNo()
						+ " was borrowed");

				// process
				process(socket, threadNo);

				// return socket instance back to the pool
				pool.returnSocket(socket);
				olog.log(Level.INFO, "Thread " + threadNo
						+ ": Object with process no. " + socket.getProcessNo()
						+ " was returned");
				return this;
			} catch (SocketConnectTimeout e) {
				olog.log(Level.INFO, "Thread " + threadNo
						+ ": Object with process no. " + socket.getProcessNo()
						+ " in couldn't connect");
				outcome = String.format(
						com.extreme.xc.Response.GATEWAY_TIMEOUT, userID,
						clientID, terminalID, transID, Calendar.getInstance(),
						Calendar.getInstance());
				throw new ResourceException(outcome, Status.GATEWAY_TIMEOUT);
			} catch (IOException e) {
				olog.log(Level.INFO, "Thread " + threadNo
						+ ": Object with process no. " + socket.getProcessNo()
						+ " in couldn't write/read");
				outcome = String.format(
						com.extreme.xc.Response.GATEWAY_TIMEOUT, userID,
						clientID, terminalID, transID, Calendar.getInstance(),
						Calendar.getInstance());
				throw new ResourceException(outcome, Status.GATEWAY_TIMEOUT);
			} finally {
				// log command and outcome
				log();
			}
		}

		private void log() {
			db.createCommandRegistry(transID, userID, clientID, terminalID,
					clientTransID, command, outcome, getDateTimeNow());
		}

		private void process(DigicoSocket socket, long threadNo)
				throws SocketConnectTimeout, IOException {
			try {
				// make purchase
				outcome = socket.makePurchase(threadNo, command);
				log.log(Level.INFO, " Thread " + threadNo
						+ ": Object with process no. " + socket.getProcessNo()
						+ " output is " + outcome);
				/*
				 * if (socket.getProcessNo() != 2) {
				 * socket.askServerToClose(threadNo); }
				 */
			} catch (IOException e) {
				outcome = socket.retryPurchase(threadNo, command);
			}
		}
	}

	class Voucher {
		private Response result;
		private JAXBContext jaxbContext;

		public Voucher(String outcome, JAXBContext jaxbContext)
				throws ResourceException {
			this.jaxbContext = jaxbContext;
			// unmarshall
			Estel operatorResponse = unmarshall(outcome);
			// transform
			transform(operatorResponse);
		}

		private Estel unmarshall(String outcome) throws ResourceException {
			try {
				if (null == jaxbContext) {
					jaxbContext = JAXBContext.newInstance(Estel.class);
				}
				Unmarshaller jaxbUnmarshaller = jaxbContext
						.createUnmarshaller();
				return (Estel) jaxbUnmarshaller.unmarshal(new StringReader(
						outcome));

			} catch (Exception e) {
				log.log(Level.INFO, "UserID: " + userID + " ClientID: "
						+ clientID + " TerminalID: " + terminalID
						+ " couldn't unmarshall");
				e.printStackTrace();
				throw new ResourceException(String.format(
						com.extreme.xc.Response.MESSAGE_UNMARSHAL_FAILED,
						userID, clientID, terminalID, transID), Status.CONFLICT);
			}
		}

		private void transform(Estel operatorResponse) throws ResourceException {
			try {
				result = new Response(clientTransID, userID, terminalID,
						transID);
				result.transform(operatorResponse);
			} catch (Exception e) {
				throw new ResourceException(String.format(
						com.extreme.xc.Response.MESSAGE_TRANSFORM_FAILED,
						userID, clientID, terminalID, transID), Status.CONFLICT);
			}
		}

		public boolean isValidPurchase() throws ResourceException {
			if (result.isValidPurchase()) {
				// verify();
				return true;
			} else {
				// error/malformed response returned by operator
				String error = result.getError();
				if (!error.isEmpty()) {
					throw new ResourceException(String.format(
							com.extreme.xc.Response.MESSAGE_VALIDATE_FAILED,
							userID, clientID, terminalID, transID, error),
							Status.CONFLICT);
				}
				return false;
			}
		}

		private String verify() throws ResourceException {
			if (!result.verify(requestedVendorCode, requestedProductCode,
					requestedQty)) {
				return result.getError();
			}
			return "";
		}
	}

	@XmlRootElement
	static class Estel {
		@XmlElement
		private Header header;
		@XmlElement
		private Response response;

		static class Response {
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
			@XmlElement(name = "prewalletbalance")
			private Double preWalletBalance;
			@XmlElement(name = "resultcode")
			private Integer resultCode;
			@XmlElement(name = "resultdescription")
			private String resultDescription;
			@XmlElement(name = "recordcount")
			private Integer recordCount;
			@XmlElement(name = "transid")
			private String transID;
			@XmlElement(name = "records")
			private Records records;
			@XmlElement(name = "requestcts")
			private String requestCts;
			@XmlElement(name = "responsects")
			private String responseCts;
			@XmlElement(name = "clienttype")
			private String clientType;
			@XmlElement(name = "walletbalance")
			private Double walletBalance;
			@XmlElement(name = "service")
			private String service;
			@XmlElement(name = "tax")
			private Double tax;

			static class Records {

				@XmlElement(name = "record")
				private List<Record> record;

				@XmlRootElement(name = "record")
				static class Record {
					@XmlElement(name = "pinno")
					private String pinNo;
					@XmlElement(name = "serialno")
					private String serialNo;
					@XmlElement(name = "expirydate")
					private String expiryDate;

					public String getPinNo() {
						return pinNo;
					}

					public String getSerialNo() {
						return serialNo;
					}

					public String getExpiryDate() {
						return expiryDate;
					}
				}

				public List<Record> getRecord() {
					return record;
				}
			}

			public String getAgentCode() {
				return agentCode;
			}

			public String getAgentTransID() {
				return agentTransID;
			}

			public String getVendorCode() {
				return vendorCode;
			}

			public String getProductCode() {
				return productCode;
			}

			public Double getQty() {
				return qty;
			}

			public Double getAmount() {
				return amount;
			}

			public Double getPreWalletBalance() {
				return preWalletBalance;
			}

			public Integer getResultCode() {
				return resultCode;
			}

			public String getResultDescription() {
				return resultDescription;
			}

			public Integer getRecordCount() {
				return recordCount;
			}

			public String getTransID() {
				return transID;
			}

			public Records getRecords() {
				return records;
			}

			public String getRequestCts() {
				return requestCts;
			}

			public String getResponseCts() {
				return responseCts;
			}

			public String getClientType() {
				return clientType;
			}

			public Double getWalletBalance() {
				return walletBalance;
			}

			public String getService() {
				return service;
			}

			public Double getTax() {
				return tax;
			}
		}

		static class Header {
			//
			@XmlElement(name = "responsetype")
			private String responseType;
		}

		public Header getHeader() {
			return header;
		}

		public Response getResponse() {
			return response;
		}
	}

	public static class Response {
		@XmlElement(name="result_code")
		private Integer resultCode;
		@XmlElement(name="result_description")
		private String resultDescription;
		@XmlElement(name="user_id")
		private String userID;
		@XmlElement(name="terminal_id")
		private String terminalID;
		@XmlElement(name="quantity")
		private Double quantity;
		@XmlElement(name="client_trans_id")
		private String clientTransactionID;
		@XmlElement(name="operator_trans_id")
		private String operatorTransactionID;
		@XmlElement(name="purchase_id")
		private Long purchaseID;
		private String agentTransID;
		private String agentCode;
		@XmlElement(name="request_time")
		private String requestTime;
		@XmlElement(name="response_time")
		private String respondTime;
		@XmlElement(name="operator_request_time")
		private String operatorRequestTime;
		@XmlElement(name="operator_response_time")
		private String operatorResponseTime;
		@XmlElement(name="vendor_code")
		private String vendorCode;
		@XmlElement(name="product_code")
		private String productCode;
		@XmlElement(name = "vouchers")
		private List<Voucher> vouchers;
		private String msg = "";

		public Response(String clientTransactionID, String userID,
				String terminalID, Long purchaseID) {
			this.clientTransactionID = clientTransactionID;
			this.userID = userID;
			this.terminalID = terminalID;
			this.purchaseID = purchaseID;
		}

		public String getError() {
			return this.msg;
		}

		private boolean isValid() {
			if (this.resultCode == null) {
				this.msg = "resultCode not returned by operator";
				return false;
			}
			return true;
		}

		public boolean verify(String requestedVendorCode,
				String requestedProductCode, Double requestedQty) {
			if (this.agentTransID == null
					|| !this.agentTransID.equals((String
							.valueOf(this.purchaseID)))) {
				this.msg = "agentTransID doesn't match";
				return false;
			}
			/*if (this.vendorCode == null
					|| !this.vendorCode.equalsIgnoreCase(requestedVendorCode)) {
				this.msg = "vendorCode doen't match";
				return false;
			}*/
			if (this.productCode == null
					|| !this.productCode.equalsIgnoreCase(requestedProductCode)) {
				this.msg = "productCode doesn't match";
				return false;
			}
			if (this.quantity == null || !this.quantity.equals(requestedQty)) {
				this.msg = "quantity doesn't match";
				return false;
			}
			if (this.quantity.intValue() != vouchers.size()) {
				this.msg = "received vouchers are not equal to requested quantity";
				return false;
			}
			return true;
		}

		public boolean isValidPurchase() {
			if (!isValid()) {
				return false;
			}
			if (this.resultCode != 0) {
				return false;
			}
			if (this.vouchers == null || this.vouchers.size() == 0) {
				this.msg = "Empty vouchers list";
				return false;
			}
			for (Voucher voucher : this.vouchers) {
				if (voucher.expiryDate == null || voucher.pinNo == null
						|| voucher.serialNo == null) {
					this.msg = "Invalid voucher received";
					return false;
				}
			}
			return true;
		}

		public void transform(Estel from) {
			Estel.Response response = from.getResponse();
			this.resultCode = response.getResultCode();
			this.resultDescription = response.getResultDescription();
			this.agentTransID = response.getAgentTransID();
			this.quantity = response.getQty();
			this.operatorRequestTime = response.getRequestCts();
			this.operatorResponseTime = response.getResponseCts();
			this.operatorTransactionID = response.getTransID();
			this.productCode = response.getProductCode();
			this.vendorCode = response.getVendorCode();
			if (response.getRecords() != null
					&& response.getRecords().getRecord() != null) {
				this.vouchers = new LinkedList<Purchase.Response.Voucher>();
				for (Record record : response.getRecords().getRecord()) {
					Voucher voucher = new Voucher();
					voucher.expiryDate = record.getExpiryDate();
					voucher.pinNo = record.getPinNo();
					voucher.serialNo = record.getSerialNo();

					vouchers.add(voucher);
				}
			}
		}

		@XmlRootElement(name = "voucher")
		public
		static class Voucher {
			@XmlElement(name="pin_no")
			private String pinNo;
			@XmlElement(name="serial_no")
			private String serialNo;
			@XmlElement(name="expiry_date")
			private String expiryDate;

			public String getPinNo() {
				return pinNo;
			}

			public String getSerialNo() {
				return serialNo;
			}

			public String getExpiryDate() {
				return expiryDate;
			}
		}

	}

	@XmlRootElement
	static class Request {
		@XmlElement(name = "client_id")
		private String clientID;
		@XmlElement(name = "terminal_id")
		private String terminalID;
		@XmlElement(name = "vendor_code")
		private String vendorCode;
		@XmlElement(name = "product_code")
		private String productCode;
		@XmlElement(name = "qty")
		private Double qty;
		@XmlElement(name = "client_trans_id")
		private String clientTransactionID;

		private final static String MANDATORY_ID = "client_id";
		// private final static String MANDATORY_PASSWORD = "client_password";
		private final static String MANDATORY_TERMINAL = "terminal_id";
		private final static String MANDATORY_VENDOR = "vendor_code";
		private final static String MANDATORY_PRODUCT = "product_code";
		private final static String MANDATORY_QUANTITY = "qty";
		private final static String MANDATORY_TRANS = "client_trans_id";

		@Transient
		private String error = "";

		public void validate(int clientID) throws ResourceException {
			validate(this.clientID, MANDATORY_ID);
			validate(this.terminalID, MANDATORY_TERMINAL);
			validate(this.vendorCode, MANDATORY_VENDOR);
			validate(this.productCode, MANDATORY_PRODUCT);
			validate(this.qty, MANDATORY_QUANTITY);
			validate(this.clientTransactionID, MANDATORY_TRANS);

			if (!error.isEmpty()) {
				throw new ResourceException(
						String.format(
								com.extreme.xc.Response.MANDATORY_PARAM_MISSING,
								error), Status.BAD_REQUEST);
			}

			if (!this.clientID.equals(String.valueOf(clientID))) {
				throw new ResourceException(
						com.extreme.xc.Response.INVALID_AUTHENTICATION_CREDENTIALS,
						Status.BAD_REQUEST);
			}
		}

		private void validate(String field, String message) {
			if (field == null || field.isEmpty()) {
				error += message + ", ";
			}
		}

		private void validate(Double field, String message) {
			if (field == null || field <= 0) {
				error += message + ", ";
			}
		}

		/** getters & setters **/
		public String getClientID() {
			return clientID;
		}

		public void setClientID(String clientID) {
			this.clientID = clientID == null ? clientID : clientID.trim();
			// validate(this.clientID, MANDATORY_ID);
		}

		/*
		 * public String getClientPassword() { return clientPassword; }
		 * 
		 * public void setClientPassword(String clientPassword) {
		 * this.clientPassword = clientPassword == null ? clientPassword :
		 * clientPassword.trim(); validate(this.clientPassword,
		 * MANDATORY_PASSWORD); }
		 */
		public String getTerminalID() {
			return terminalID;
		}

		public void setTerminalID(String terminalID) {
			this.terminalID = terminalID == null ? terminalID : terminalID
					.trim();
			// validate(this.terminalID, MANDATORY_TERMINAL);
		}

		public String getVendorCode() {
			return vendorCode;
		}

		public void setVendorCode(String vendorCode) {
			this.vendorCode = vendorCode == null ? vendorCode : vendorCode
					.trim();
			// validate(this.vendorCode, MANDATORY_VENDOR);
		}

		public String getProductCode() {
			return productCode;
		}

		public void setProductCode(String productCode) {
			this.productCode = productCode == null ? productCode : productCode
					.trim();
			// validate(this.productCode, MANDATORY_PRODUCT);
		}

		public Double getQuantity() {
			return this.qty;
		}

		public void setQuantity(Double qty) {
			this.qty = qty;
		}

		public String getClientTransactionID() {
			return clientTransactionID;
		}

		public void setClientTransactionID(String clientTransactionID) {
			this.clientTransactionID = clientTransactionID == null ? clientTransactionID
					: clientTransactionID.trim();
			validate(this.clientTransactionID, MANDATORY_TRANS);
		}

		@Override
		public String toString() {
			return MANDATORY_ID.concat(": ").concat(this.clientID).concat(", ")
					.concat(MANDATORY_TERMINAL).concat(": ")
					.concat(this.terminalID).concat(", ")
					.concat(MANDATORY_TRANS).concat(": ")
					.concat(this.clientTransactionID).concat(", ")
					.concat(MANDATORY_VENDOR).concat(": ")
					.concat(this.vendorCode).concat(", ")
					.concat(MANDATORY_PRODUCT).concat(": ")
					.concat(this.productCode).concat(", ")
					.concat(MANDATORY_QUANTITY).concat(": ")
					.concat(String.valueOf(this.qty));
		}

	}

	public static void main(String[] args) throws JAXBException {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a zzz");

		System.out.println("Current Date: " + ft.format(dNow));
	}

}
