package com.extreme.xc.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-05-11T12:31:02.035+0300")
@StaticMetamodel(VoucherLog.class)
public class VoucherLog_ {
	public static volatile SingularAttribute<VoucherLog, Integer> voucherLogId;
	public static volatile SingularAttribute<VoucherLog, PurchaseLog> purchaseLog;
	public static volatile SingularAttribute<VoucherLog, Integer> transactionId;
	public static volatile SingularAttribute<VoucherLog, String> voucherPin;
	public static volatile SingularAttribute<VoucherLog, String> voucherSerial;
	public static volatile SingularAttribute<VoucherLog, String> voucherExpiry;
}
