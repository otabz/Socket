package com.extreme.xc.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * CreditTeller entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "credit_teller", catalog = "xcharge")
public class CreditTeller implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private String type;
	private String status;
	private Set<CreditTellerUser> creditTellerUsers = new HashSet<CreditTellerUser>(
			0);

	// Constructors

	/** default constructor */
	public CreditTeller() {
	}

	/** minimal constructor */
	public CreditTeller(Integer id, String name, String type, String status) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.status = status;
	}

	/** full constructor */
	public CreditTeller(Integer id, String name, String type, String status,
			Set<CreditTellerUser> creditTellerUsers) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.status = status;
		this.creditTellerUsers = creditTellerUsers;
	}

	// Property accessors
	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name", nullable = false, length = 150)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "type", nullable = false, length = 100)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "status", nullable = false, length = 45)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "creditTeller")
	public Set<CreditTellerUser> getCreditTellerUsers() {
		return this.creditTellerUsers;
	}

	public void setCreditTellerUsers(Set<CreditTellerUser> creditTellerUsers) {
		this.creditTellerUsers = creditTellerUsers;
	}

}