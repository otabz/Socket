package com.extreme.xc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * VoucherLog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "voucher_log", catalog = "xcharge")
public class VoucherLog implements java.io.Serializable {

	// Fields

	private Integer voucherLogId;
	private PurchaseLog purchaseLog;
	private Integer transactionId;
	private String voucherPin;
	private String voucherSerial;
	private String voucherExpiry;

	// Constructors

	/** default constructor */
	public VoucherLog() {
	}

	/** full constructor */
	public VoucherLog(PurchaseLog purchaseLog, Integer transactionId,
			String voucherPin, String voucherSerial, String voucherExpiry) {
		this.purchaseLog = purchaseLog;
		this.transactionId = transactionId;
		this.voucherPin = voucherPin;
		this.voucherSerial = voucherSerial;
		this.voucherExpiry = voucherExpiry;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "voucher_log_id", unique = true, nullable = false)
	public Integer getVoucherLogId() {
		return this.voucherLogId;
	}

	public void setVoucherLogId(Integer voucherLogId) {
		this.voucherLogId = voucherLogId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "purchase_log_id")
	public PurchaseLog getPurchaseLog() {
		return this.purchaseLog;
	}

	public void setPurchaseLog(PurchaseLog purchaseLog) {
		this.purchaseLog = purchaseLog;
	}

	@Column(name = "transaction_id")
	public Integer getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	@Column(name = "voucher_pin", length = 200)
	public String getVoucherPin() {
		return this.voucherPin;
	}

	public void setVoucherPin(String voucherPin) {
		this.voucherPin = voucherPin;
	}

	@Column(name = "voucher_serial", length = 200)
	public String getVoucherSerial() {
		return this.voucherSerial;
	}

	public void setVoucherSerial(String voucherSerial) {
		this.voucherSerial = voucherSerial;
	}

	@Column(name = "voucher_expiry", length = 100)
	public String getVoucherExpiry() {
		return this.voucherExpiry;
	}

	public void setVoucherExpiry(String voucherExpiry) {
		this.voucherExpiry = voucherExpiry;
	}

}