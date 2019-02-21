package com.extreme.xc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * CreditTellerUser entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "credit_teller_user", catalog = "xcharge")
public class CreditTellerUser implements java.io.Serializable {

	// Fields

	private String id;
	private CreditTeller creditTeller;
	private String password;
	private String role;
	private String status;

	// Constructors

	/** default constructor */
	public CreditTellerUser() {
	}

	/** minimal constructor */
	public CreditTellerUser(String id, CreditTeller creditTeller, String role,
			String status) {
		this.id = id;
		this.creditTeller = creditTeller;
		this.role = role;
		this.status = status;
	}

	/** full constructor */
	public CreditTellerUser(String id, CreditTeller creditTeller,
			String password, String role, String status) {
		this.id = id;
		this.creditTeller = creditTeller;
		this.password = password;
		this.role = role;
		this.status = status;
	}

	// Property accessors
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 45)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "credit_teller_id", nullable = false)
	public CreditTeller getCreditTeller() {
		return this.creditTeller;
	}

	public void setCreditTeller(CreditTeller creditTeller) {
		this.creditTeller = creditTeller;
	}

	@Column(name = "password", length = 250)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "role", nullable = false, length = 45)
	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Column(name = "status", nullable = false, length = 45)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}