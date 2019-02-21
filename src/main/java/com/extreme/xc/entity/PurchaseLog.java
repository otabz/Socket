package com.extreme.xc.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * PurchaseLog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "purchase_log", catalog = "xcharge")
public class PurchaseLog implements java.io.Serializable {

	// Fields

	private Integer purchaseLogId;
	private Integer transactionId;
	private String userId;
	private Integer clientId;
	private String terminalId;
	private String clientTransactionId;
	private String operatorTransactionId;
	private String operatorAgentCode;
	private String operatorAgentTransactionId;
	private String operatorRequestTime;
	private String operatorResponseTime;
	private String timestamp;
	private String ackTimestamp;
	private String verificationError;
	private Set<VoucherLog> voucherLogs = new HashSet<VoucherLog>(0);

	// Constructors

	/** default constructor */
	public PurchaseLog() {
	}

	/** full constructor */
	public PurchaseLog(Integer transactionId, String userId, Integer clientId,
			String terminalId, String clientTransactionId,
			String operatorTransactionId, String operatorAgentCode,
			String operatorAgentTransactionId, String operatorRequestTime,
			String operatorResponseTime, String timestamp, String ackTimestamp,
			String verificationError, Set<VoucherLog> voucherLogs) {
		this.transactionId = transactionId;
		this.userId = userId;
		this.clientId = clientId;
		this.terminalId = terminalId;
		this.clientTransactionId = clientTransactionId;
		this.operatorTransactionId = operatorTransactionId;
		this.operatorAgentCode = operatorAgentCode;
		this.operatorAgentTransactionId = operatorAgentTransactionId;
		this.operatorRequestTime = operatorRequestTime;
		this.operatorResponseTime = operatorResponseTime;
		this.timestamp = timestamp;
		this.ackTimestamp = ackTimestamp;
		this.verificationError = verificationError;
		this.voucherLogs = voucherLogs;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "purchase_log_id", unique = true, nullable = false)
	public Integer getPurchaseLogId() {
		return this.purchaseLogId;
	}

	public void setPurchaseLogId(Integer purchaseLogId) {
		this.purchaseLogId = purchaseLogId;
	}

	@Column(name = "transaction_id")
	public Integer getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	@Column(name = "user_id", length = 45)
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

	@Column(name = "terminal_id", length = 45)
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

	@Column(name = "operator_transaction_id", length = 100)
	public String getOperatorTransactionId() {
		return this.operatorTransactionId;
	}

	public void setOperatorTransactionId(String operatorTransactionId) {
		this.operatorTransactionId = operatorTransactionId;
	}

	@Column(name = "operator_agent_code", length = 100)
	public String getOperatorAgentCode() {
		return this.operatorAgentCode;
	}

	public void setOperatorAgentCode(String operatorAgentCode) {
		this.operatorAgentCode = operatorAgentCode;
	}

	@Column(name = "operator_agent_transaction_id", length = 100)
	public String getOperatorAgentTransactionId() {
		return this.operatorAgentTransactionId;
	}

	public void setOperatorAgentTransactionId(String operatorAgentTransactionId) {
		this.operatorAgentTransactionId = operatorAgentTransactionId;
	}

	@Column(name = "operator_request_time", length = 100)
	public String getOperatorRequestTime() {
		return this.operatorRequestTime;
	}

	public void setOperatorRequestTime(String operatorRequestTime) {
		this.operatorRequestTime = operatorRequestTime;
	}

	@Column(name = "operator_response_time", length = 100)
	public String getOperatorResponseTime() {
		return this.operatorResponseTime;
	}

	public void setOperatorResponseTime(String operatorResponseTime) {
		this.operatorResponseTime = operatorResponseTime;
	}

	@Column(name = "timestamp", length = 100)
	public String getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Column(name = "ack_timestamp", length = 100)
	public String getAckTimestamp() {
		return this.ackTimestamp;
	}

	public void setAckTimestamp(String ackTimestamp) {
		this.ackTimestamp = ackTimestamp;
	}

	@Column(name = "verification_error", length = 500)
	public String getVerificationError() {
		return this.verificationError;
	}

	public void setVerificationError(String verificationError) {
		this.verificationError = verificationError;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "purchaseLog")
	public Set<VoucherLog> getVoucherLogs() {
		return this.voucherLogs;
	}

	public void setVoucherLogs(Set<VoucherLog> voucherLogs) {
		this.voucherLogs = voucherLogs;
	}

}