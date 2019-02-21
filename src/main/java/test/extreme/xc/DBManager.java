package test.extreme.xc;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import com.extreme.xc.entity.CommandLog;
import com.extreme.xc.entity.CreditTeller;
import com.extreme.xc.entity.CreditTellerUser;
import com.extreme.xc.entity.PurchaseLog;
import com.extreme.xc.entity.TransactionLog;
import com.extreme.xc.entity.VoucherLog;
import com.extreme.xc.Purchase.Response.Voucher;

public class DBManager extends com.extreme.xc.DBManager {

	private static Logger log = Logger.getLogger(DBManager.class.getName());
	private final static String TRANSACTION_REGISTRY_LOG = "Request registry created {trans_id=%d} {user_id: %s, client_id: %d, terminal_id: %s, client_trans_id: %s, vendor_code: %s, product_code: %s, qty: %s, at: %s}";
	private final static String COMMAND_REGISTRY_LOG = "Command registry created {trans_id=%d} {user_id: %s, client_id: %d, terminal_id: %s, client_trans_id: %s, command: %s, outcome: %s, at: %s}";
	private final static String PURCHASE_REGISTRY_LOG = "Purchase registry created {trans_id=%s} {user_id: %s, client_id: %d, terminal_id: %s, client_trans_id: %s, operator_trans_id: %s, vendor_code: %s, product_code: %s, qty: %f, vouchers_quantity: %d, at: %s}";
	//private static Random random = new SecureRandom();

	@Override
	public CreditTellerUser find(String id, String password) {
		return new CreditTellerUser("Tayyab", new CreditTeller(420, "Nahdi", "Pharmacy", "1"), "", "1");
	}

	public void createCommandRegistry(Long transID, String userID,
			int clientID, String terminalID, String clientTransID,
			String command, String outcome, String timestamp) {

		log.log(Level.INFO, String.format(COMMAND_REGISTRY_LOG, transID,
				userID, clientID, terminalID, clientTransID, command, outcome,
				timestamp));
		
	}

	@Override
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
		return new Random().nextInt();
	}

	@Override
	public Long createTransactionRegistry(int clientID, String userID,
			String terminalID, String clientTransID,
			String requestedVendorCode, String requestedProductCode,
			Double requestedQty, String requestedAt) {
		// log
		Long transID = new Random().nextLong();
		
		log.log(Level.INFO, String.format(TRANSACTION_REGISTRY_LOG,
			transID, userID, clientID, terminalID,
				clientTransID, requestedVendorCode, requestedProductCode,
				requestedQty.toString(), requestedAt));
		return transID;
	}
}
