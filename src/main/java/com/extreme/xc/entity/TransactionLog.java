package com.extreme.xc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TransactionLog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "transaction_log", catalog = "xcharge")
public class TransactionLog implements java.io.Serializable {

	// Fields

	private Integer transactionId;
	private String userId;
	private Integer clientId;
	private String terminalId;
	private String clientTransactionId;
	private String vendorCode;
	private String productCode;
	private Float qty;
	private String timestamp;

	// Constructors

	/** default constructor */
	public TransactionLog() {
	}

	/** full constructor */
	public TransactionLog(String userId, Integer clientId, String terminalId,
			String clientTransactionId, String vendorCode, String productCode,
			Float qty, String timestamp) {
		this.userId = userId;
		this.clientId = clientId;
		this.terminalId = terminalId;
		this.clientTransactionId = clientTransactionId;
		this.vendorCode = vendorCode;
		this.productCode = productCode;
		this.qty = qty;
		this.timestamp = timestamp;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "transaction_id", unique = true, nullable = false)
	public Integer getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	@Column(name = "user_id", length = 100)
	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Column(name = "client_id")
	public Integer getClientId() {
		return this.clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	@Column(name = "terminal_id", length = 100)
	public String getTerminalId() {
		return this.terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	@Column(name = "client_transaction_id", length = 100)
	public String getClientTransactionId() {
		return this.clientTransactionId;
	}

	public void setClientTransactionId(String clientTransactionId) {
		this.clientTransactionId = clientTransactionId;
	}

	@Column(name = "vendor_code", length = 100)
	public String getVendorCode() {
		return this.vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	@Column(name = "product_code", length = 100)
	public String getProductCode() {
		return this.productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	@Column(name = "qty", precision = 12, scale = 0)
	public Float getQty() {
		return this.qty;
	}

	public void setQty(Float qty) {
		this.qty = qty;
	}

	@Column(name = "timestamp", length = 100)
	public String getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}