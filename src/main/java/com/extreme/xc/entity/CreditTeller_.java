package com.extreme.xc.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-04-10T19:50:09.156+0300")
@StaticMetamodel(CreditTeller.class)
public class CreditTeller_ {
	public static volatile SingularAttribute<CreditTeller, Integer> id;
	public static volatile SingularAttribute<CreditTeller, String> name;
	public static volatile SingularAttribute<CreditTeller, String> type;
	public static volatile SingularAttribute<CreditTeller, String> status;
	public static volatile SetAttribute<CreditTeller, CreditTellerUser> creditTellerUsers;
}
