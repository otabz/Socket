package com.extreme.xc.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-04-17T13:57:32.802+0300")
@StaticMetamodel(CommandLog.class)
public class CommandLog_ {
	public static volatile SingularAttribute<CommandLog, Integer> commandLogId;
	public static volatile SingularAttribute<CommandLog, Integer> transactionId;
	public static volatile SingularAttribute<CommandLog, String> command;
	public static volatile SingularAttribute<CommandLog, String> outcome;
	public static volatile SingularAttribute<CommandLog, String> userId;
	public static volatile SingularAttribute<CommandLog, Integer> clientId;
	public static volatile SingularAttribute<CommandLog, String> terminalId;
	public static volatile SingularAttribute<CommandLog, String> clientTransactionId;
	public static volatile SingularAttribute<CommandLog, String> timestamp;
}
