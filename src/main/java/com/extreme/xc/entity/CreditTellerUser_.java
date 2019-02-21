package com.extreme.xc.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-04-10T19:50:09.172+0300")
@StaticMetamodel(CreditTellerUser.class)
public class CreditTellerUser_ {
	public static volatile SingularAttribute<CreditTellerUser, String> id;
	public static volatile SingularAttribute<CreditTellerUser, CreditTeller> creditTeller;
	public static volatile SingularAttribute<CreditTellerUser, String> password;
	public static volatile SingularAttribute<CreditTellerUser, String> role;
	public static volatile SingularAttribute<CreditTellerUser, String> status;
}
