package com.extreme.xc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Transient;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.extreme.xc.entity.PurchaseLog;

public class PurchaseAck {
	private final static String ACK_RECEIVED_LOG = "Ack received {user_id: %s, %s, at: %s}";
	private static Logger log = Logger.getLogger(PurchaseAck.class.getName());

	private DBManager db;
	private final String userID;
	private final int clientID;
	private final String ackdAt = PurchaseAck.getDateTimeNow();
	private final String clientTransID;
	private String terminalID;
	private int purchaseID;

	public PurchaseAck(String userID, int clientID, String terminalID,
			String clientTransID, Integer purchaseID, DBManager db) {
		this.clientID = clientID;
		this.userID = userID;
		this.terminalID = terminalID;
		this.clientTransID = clientTransID;
		this.purchaseID = purchaseID;
		this.db = db;
	}

	public Response endorse() throws ResourceException {
		PurchaseLog entry = db.findPurchase(this.purchaseID);
		String error = "";
		// back end verification
		Response result = new Response();
		result.setPurchaseID(this.purchaseID);
		result.setResultCode(0);
		if (null == entry) {
			error = String.format(com.extreme.xc.Response.PURCHASE_NOT_FOUND,
					"purchase_id doesn't match", userID, clientID, terminalID,
					clientTransID, purchaseID, Calendar.getInstance(),
					Calendar.getInstance());
			result.setResultCode(-1);
			result.setResultDescription(error);
		} else if (!entry.getUserId().equals(this.userID)) {
			error = String.format(com.extreme.xc.Response.PURCHASE_NOT_FOUND,
					"user_id doesn't match", userID, clientID, terminalID,
					clientTransID, purchaseID, Calendar.getInstance(),
					Calendar.getInstance());
			result.setResultCode(-1);
			result.setResultDescription(error);
		} else if (!entry.getClientId().equals(this.clientID)) {
			error = String.format(com.extreme.xc.Response.PURCHASE_NOT_FOUND,
					"client_id doesn't match", userID, clientID, terminalID,
					clientTransID, purchaseID, Calendar.getInstance(),
					Calendar.getInstance());
			result.setResultCode(-1);
			result.setResultDescription(error);
		} else if (!entry.getClientTransactionId().equals(this.clientTransID)) {
			error = String.format(com.extreme.xc.Response.PURCHASE_NOT_FOUND,
					"client_trans_id doesn't match", userID, clientID,
					terminalID, clientTransID, purchaseID,
					Calendar.getInstance(), Calendar.getInstance());
			result.setResultCode(-1);
			result.setResultDescription(error);
		} else if (!entry.getTerminalId().equals(this.terminalID)) {
			error = String.format(com.extreme.xc.Response.PURCHASE_NOT_FOUND,
					"terminal_id doesn't match", userID, clientID, terminalID,
					clientTransID, purchaseID, Calendar.getInstance(),
					Calendar.getInstance());
			result.setResultCode(-1);
			result.setResultDescription(error);
		}
		// throw violation
		//if (!error.isEmpty()) {
		//	throw new ResourceException(error, Status.CONFLICT);
		//}
		
		// register acknowledgement
		if (result.getResultCode().equals(0)) {
			db.regitserAck(this.purchaseID, this.ackdAt);
			result.setAckTime(this.ackdAt);
			result.setResultDescription("Ack received successfully");
		}
		return result;
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

		public PurchaseAck build(Request request) throws ResourceException {
			request.validate(this.clientID);
			log.log(Level.INFO,
					String.format(ACK_RECEIVED_LOG, this.userID,
							request.toString(), PurchaseAck.getDateTimeNow()));
			PurchaseAck ack = new PurchaseAck(this.userID, this.clientID,
					request.terminalID, request.clientTransactionID,
					request.purchaseID, this.db);
			return ack;
		}

	}

	@XmlRootElement
	static class Request {
		@XmlElement(name = "client_id")
		private String clientID;
		@XmlElement(name = "terminal_id")
		private String terminalID;
		@XmlElement(name = "client_trans_id")
		private String clientTransactionID;
		@XmlElement(name = "purchase_id")
		private Integer purchaseID;

		private final static String MANDATORY_ID = "client_id";
		private final static String MANDATORY_TERMINAL = "terminal_id";
		private final static String MANDATORY_TRANS = "client_trans_id";
		private final static String MANDATORY_PURCHASE = "purchase_id";

		@Transient
		private String error = "";

		public void validate(int clientID) throws ResourceException {
			validate(this.clientID, MANDATORY_ID);
			validate(this.terminalID, MANDATORY_TERMINAL);
			validate(this.clientTransactionID, MANDATORY_TRANS);
			validate(this.purchaseID, MANDATORY_PURCHASE);

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

		private void validate(Integer field, String message) {
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

		public String getTerminalID() {
			return terminalID;
		}

		public void setTerminalID(String terminalID) {
			this.terminalID = terminalID == null ? terminalID : terminalID
					.trim();
			// validate(this.terminalID, MANDATORY_TERMINAL);
		}

		public String getClientTransactionID() {
			return clientTransactionID;
		}

		public void setClientTransactionID(String clientTransactionID) {
			this.clientTransactionID = clientTransactionID == null ? clientTransactionID
					: clientTransactionID.trim();
			validate(this.clientTransactionID, MANDATORY_TRANS);
		}

		public Integer getPurchaseID() {
			return purchaseID;
		}

		public void setPurchaseID(Integer purchaseID) {
			this.purchaseID = purchaseID;
		}

		@Override
		public String toString() {
			return MANDATORY_ID.concat(": ").concat(this.clientID).concat(", ")
					.concat(MANDATORY_TERMINAL).concat(": ")
					.concat(this.terminalID).concat(", ")
					.concat(MANDATORY_TRANS).concat(": ")
					.concat(this.clientTransactionID).concat(", ")
					.concat(MANDATORY_PURCHASE).concat(": ")
					.concat(String.valueOf(this.purchaseID));
		}

	}

	@XmlRootElement
	static class Response {
		@XmlElement(name="purchase_id")
		private Integer purchaseID;
		@XmlElement(name="result_code")
		private Integer resultCode;
		@XmlElement(name="result_description")
		private String resultDescription;
		@XmlElement(name="ack_time")
		private String ackTime;

		public Integer getPurchaseID() {
			return purchaseID;
		}

		public void setPurchaseID(Integer purchaseID) {
			this.purchaseID = purchaseID;
		}

		public Integer getResultCode() {
			return resultCode;
		}

		public void setResultCode(Integer resultCode) {
			this.resultCode = resultCode;
		}

		public String getResultDescription() {
			return resultDescription;
		}

		public void setResultDescription(String resultDescription) {
			this.resultDescription = resultDescription;
		}

		public String getAckTime() {
			return ackTime;
		}

		public void setAckTime(String ackTime) {
			this.ackTime = ackTime;
		}

	}

	private static String getDateTimeNow() {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a zzz");
		return ft.format(dNow);
	}
}
