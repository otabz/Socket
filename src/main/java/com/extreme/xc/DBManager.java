package com.extreme.xc;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.core.Response.Status;

import com.extreme.xc.Purchase.Response.Voucher;
import com.extreme.xc.entity.CommandLog;
import com.extreme.xc.entity.CreditTeller;
import com.extreme.xc.entity.CreditTellerUser;
import com.extreme.xc.entity.PurchaseLog;
import com.extreme.xc.entity.TransactionLog;
import com.extreme.xc.entity.VoucherLog;

@Named("ProductionDB")
public class DBManager {

	private static Logger log = Logger.getLogger(DBManager.class.getName());
	private final static String TRANSACTION_REGISTRY_LOG = "Request registry created {trans_id=%d} {user_id: %s, client_id: %d, terminal_id: %s, client_trans_id: %s, vendor_code: %s, product_code: %s, qty: %s, at: %s}";
	private final static String COMMAND_REGISTRY_LOG = "Command registry created {trans_id=%d} {user_id: %s, client_id: %d, terminal_id: %s, client_trans_id: %s, command: %s, outcome: %s, at: %s}";
	private final static String PURCHASE_REGISTRY_LOG = "Purchase registry created {trans_id=%s} {user_id: %s, client_id: %d, terminal_id: %s, client_trans_id: %s, operator_trans_id: %s, vendor_code: %s, product_code: %s, qty: %f, vouchers_quantity: %d, at: %s}";
	// private static Random random = new SecureRandom();

	@PersistenceContext
	private EntityManager em;

	public CreditTellerUser find(String id, String password) {
		Query query = em
				.createQuery("FROM CreditTellerUser WHERE id = :id AND password = :password");
		query.setParameter("id", id);
		query.setParameter("password", password);
		return (CreditTellerUser) query.getSingleResult();
	}

	public void createCommandRegistry(Long transID, String userID,
			int clientID, String terminalID, String clientTransID,
			String command, String outcome, String timestamp) {

		log.log(Level.INFO, String.format(COMMAND_REGISTRY_LOG, transID,
				userID, clientID, terminalID, clientTransID, command, outcome,
				timestamp));
		em.persist(new CommandLog(transID.intValue(), command, outcome, userID,
				clientID, terminalID, clientTransID, timestamp));
	}

	public int createPurchaseRegistry(Long transID, String userID,
			int clientID, String terminalID, String clientTransID,
			String operatorTransactionID, String operatorAgentCode,
			String operatorAgentTransID, String operatorRequestTime,
			String operatorResponseTime, String vendorCode, String productCode,
			Double qty, List<Voucher> vouchers, String timestamp, String error) {

		log.log(Level.INFO, String.format(PURCHASE_REGISTRY_LOG, transID,
				userID, clientID, terminalID, clientTransID,
				operatorTransactionID, vendorCode, productCode, qty,
				vouchers.size(), timestamp));
		PurchaseLog purchaseLog = new PurchaseLog(transID.intValue(), userID,
				clientID, terminalID, clientTransID, operatorTransactionID,
				operatorAgentCode, operatorAgentTransID, operatorRequestTime,
				operatorResponseTime, timestamp, null, error, null);
		purchaseLog.setVoucherLogs(convertVouchers(purchaseLog,
				transID.intValue(), vouchers));
		em.persist(purchaseLog);
		return purchaseLog.getPurchaseLogId();
	}

	private Set<VoucherLog> convertVouchers(PurchaseLog purchaseLog,
			Integer transID, List<Voucher> vouchers) {
		Set<VoucherLog> voucherLog = new HashSet<VoucherLog>();
		for (Voucher voucher : vouchers) {
			voucherLog
					.add(new VoucherLog(purchaseLog, transID, voucher
							.getPinNo(), voucher.getSerialNo(), voucher
							.getExpiryDate()));
		}
		return voucherLog;
	}

	public Long createTransactionRegistry(int clientID, String userID,
			String terminalID, String clientTransID,
			String requestedVendorCode, String requestedProductCode,
			Double requestedQty, String requestedAt) {
		// log
		// Long transID = random.nextLong();
		TransactionLog transaction = new TransactionLog(userID, clientID,
				terminalID, clientTransID, requestedVendorCode,
				requestedProductCode, requestedQty.floatValue(), requestedAt);
		em.persist(transaction);
		log.log(Level.INFO, String.format(TRANSACTION_REGISTRY_LOG,
				transaction.getTransactionId(), userID, clientID, terminalID,
				clientTransID, requestedVendorCode, requestedProductCode,
				requestedQty.toString(), requestedAt));
		return transaction.getTransactionId().longValue();
	}
	
	public void regitserAck(Integer purchaseID, String at) throws ResourceException {
		PurchaseLog entry = findPurchase(purchaseID);
		entry.setAckTimestamp(at);
		em.merge(entry);
	}
	
	public PurchaseLog findPurchase(Integer id) {
		return em.find(PurchaseLog.class, id);
	}

}
