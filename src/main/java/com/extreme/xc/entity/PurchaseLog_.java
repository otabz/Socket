package com.extreme.xc.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-04-17T15:55:18.512+0300")
@StaticMetamodel(PurchaseLog.class)
public class PurchaseLog_ {
	public static volatile SingularAttribute<PurchaseLog, Integer> purchaseLogId;
	public static volatile SingularAttribute<PurchaseLog, Integer> transactionId;
	public static volatile SingularAttribute<PurchaseLog, String> userId;
	public static volatile SingularAttribute<PurchaseLog, Integer> clientId;
	public static volatile SingularAttribute<PurchaseLog, String> terminalId;
	public static volatile SingularAttribute<PurchaseLog, String> clientTransactionId;
	public static volatile SingularAttribute<PurchaseLog, String> operatorTransactionId;
	public static volatile SingularAttribute<PurchaseLog, String> operatorAgentCode;
	public static volatile SingularAttribute<PurchaseLog, String> operatorAgentTransactionId;
	public static volatile SingularAttribute<PurchaseLog, String> operatorRequestTime;
	public static volatile SingularAttribute<PurchaseLog, String> operatorResponseTime;
	public static volatile SingularAttribute<PurchaseLog, String> timestamp;
	public static volatile SingularAttribute<PurchaseLog, String> ackTimestamp;
	public static volatile SingularAttribute<PurchaseLog, String> verificationError;
	public static volatile SetAttribute<PurchaseLog, VoucherLog> voucherLogs;
}
