package com.extreme.xc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * CommandLog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "command_log", catalog = "xcharge")
public class CommandLog implements java.io.Serializable {

	// Fields

	private Integer commandLogId;
	private Integer transactionId;
	private String command;
	private String outcome;
	private String userId;
	private Integer clientId;
	private String terminalId;
	private String clientTransactionId;
	private String timestamp;

	// Constructors

	/** default constructor */
	public CommandLog() {
	}

	/** full constructor */
	public CommandLog(Integer transactionId, String command, String outcome,
			String userId, Integer clientId, String terminalId,
			String clientTransactionId, String timestamp) {
		this.transactionId = transactionId;
		this.command = command;
		this.outcome = outcome;
		this.userId = userId;
		this.clientId = clientId;
		this.terminalId = terminalId;
		this.clientTransactionId = clientTransactionId;
		this.timestamp = timestamp;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "command_log_id", unique = true, nullable = false)
	public Integer getCommandLogId() {
		return this.commandLogId;
	}

	public void setCommandLogId(Integer commandLogId) {
		this.commandLogId = commandLogId;
	}

	@Column(name = "transaction_id")
	public Integer getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	@Column(name = "command", length = 10000)
	public String getCommand() {
		return this.command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	@Column(name = "outcome", length = 65535)
	public String getOutcome() {
		return this.outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
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

	@Column(name = "timestamp", length = 45)
	public String getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}