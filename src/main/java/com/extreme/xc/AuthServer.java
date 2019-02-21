package com.extreme.xc;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.DatatypeConverter;

import com.extreme.xc.entity.CreditTellerUser;

public class AuthServer {

	private static final String AUTHORIZATION_PROPERTY = "Authorization";
	private static final String AUTHENTICATION_SCHEME_BASIC = "Basic";
	private static Logger log = Logger.getLogger(AuthServer.class.getName());
	private DBManager db;
	
	
	public AuthServer(DBManager db) {
		this.db = db;
	}

	public CreditTellerUser auth(@Context HttpHeaders headers)
			throws OAuthException {
		String[] credentials = AuthServer
				.getBasicAuthorizationClientCredentials(headers);
		return find(credentials[0], credentials[1]);
	}

	private CreditTellerUser find(String id, String password)
			throws OAuthException {
		try {
			return db.find(id, password);
		} catch (NoResultException e) {
			throw new OAuthException(Response.INVALID_USERNAME_PASSWORD,
					Status.BAD_REQUEST);
		}
	}

	public static String[] getBasicAuthorizationClientCredentials(
			HttpHeaders header) throws OAuthException {
		String[] clientCredentials = new String[2];
		// Fetch authorization header
		final List<String> authorization = header
				.getRequestHeader(AUTHORIZATION_PROPERTY);

		// If no authorization information present; block access
		if (authorization == null || authorization.isEmpty()) {
			throw new OAuthException(String.format(
					Response.MANDATORY_PARAM_MISSING, AUTHORIZATION_PROPERTY),
					Status.BAD_REQUEST);
		}

		// Get encoded username and password
		final String encodedUserPassword = authorization.get(0).replaceFirst(
				AUTHENTICATION_SCHEME_BASIC + " ", "");

		// Decode username and password
		String clientIdAndSecret = new String(
				DatatypeConverter.parseBase64Binary(encodedUserPassword));

		// Split username and password tokens
		final StringTokenizer tokenizer = new StringTokenizer(
				clientIdAndSecret, ":");

		try {
			final String id = tokenizer.nextToken();
			final String secret = tokenizer.nextToken();

			clientCredentials[0] = id;
			clientCredentials[1] = secret;

		} catch (NoSuchElementException e) {
			log.log(Level.SEVERE, String.format(
					Response.MANDATORY_PARAM_MISSING, AUTHORIZATION_PROPERTY));
			throw new OAuthException(String.format(
					Response.MANDATORY_PARAM_MISSING, AUTHORIZATION_PROPERTY),
					Status.BAD_REQUEST);
		} catch (NullPointerException e) {
			log.log(Level.SEVERE, String.format(
					Response.MANDATORY_PARAM_MISSING, AUTHORIZATION_PROPERTY));
			throw new OAuthException(String.format(
					Response.MANDATORY_PARAM_MISSING, AUTHORIZATION_PROPERTY),
					Status.BAD_REQUEST);
		}

		return clientCredentials;
	}
}
