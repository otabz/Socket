package com.extreme.xc.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-04-17T13:57:32.813+0300")
@StaticMetamodel(TransactionLog.class)
public class TransactionLog_ {
	public static volatile SingularAttribute<TransactionLog, Integer> transactionId;
	public static volatile SingularAttribute<TransactionLog, String> userId;
	public static volatile SingularAttribute<TransactionLog, Integer> clientId;
	public static volatile SingularAttribute<TransactionLog, String> terminalId;
	public static volatile SingularAttribute<TransactionLog, String> clientTransactionId;
	public static volatile SingularAttribute<TransactionLog, String> vendorCode;
	public static volatile SingularAttribute<TransactionLog, String> productCode;
	public static volatile SingularAttribute<TransactionLog, Float> qty;
	public static volatile SingularAttribute<TransactionLog, String> timestamp;
}
