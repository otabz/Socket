package com.extreme.xc;

import javax.ws.rs.core.Response.Status;

public final class Response {

	public static final String GATEWAY_TIMEOUT = "{\"error\": \"operator not available, user_id: %s, client_id: %s, terminal_id: %s, trans_id: %d, at: %tD %tT\"}";
	public static final String MESSAGE_UNMARSHAL_FAILED = "{\"error\": \"malformed operator response, user_id: %s, client_id: %s, terminal_id: %s, trans_id: %d\"}";
	public static final String MESSAGE_VALIDATE_FAILED = "{\"error\": \"malformed operator response, user_id: %s, client_id: %s, terminal_id: %s, trans_id: %d, message: %s\"}";
	public static final String MESSAGE_VERIFY_FAILED = "{\"error\": \"operator response is conflicting with request, user_id: %s, client_id: %s, terminal_id: %s, trans_id: %d, message: %s\"}";
	public static final String MESSAGE_TRANSFORM_FAILED = "{\"error\": \"operator response couldn't be transformed, user_id: %s, client_id: %s, terminal_id: %s, trans_id: %d\"}";
	public static final String MANDATORY_PARAM_MISSING = "{\"error\": \"mandatory parameter %s is missing\"}";
	public static final String INTERNAL_SERVER_ERROR = "{\"error\": \" %s at: %tD %tT, %s\"}";
	public static final String CANNOT_ISSUE_TOKEN = "{\"error\": \"cannot issue token\"}";
	public static final String CANNOT_UPDATE_TOKEN = "{\"error\": \"cannot update token\"}";
	public static final String UNSUPPORTED_MEDIA_TYPE = "{\"error\":\"unsupported media type\"}";
	public static final String INVALID_USERNAME_PASSWORD = "{\"error\": \"invalid username/password\"}";
	public static final String INVALID_AUTHENTICATION_CREDENTIALS = "{\"error\": \"invalid username/password/client_id\"}";
	public static final String INVALID_ACCESS_TOKEN = "{\"error\":\"invalid access token\"}";
	public static final String INVALID_REFRESH_TOKEN = "{\"error\":\"invalid refresh token\"}";
	public static final String CREDIT_NOT_AVAILABLE = "{\"error\": \"credit not available\"}";
	public static final String PURCHASE_NOT_FOUND = "{\"error\": \"message: %s, user_id: %s, client_id: %d, terminal_id: %s, client_trans_id: %s, purchase_id: %d, at: %tD %tT\"}";

	public static javax.ws.rs.core.Response createBadRequestResponse(
			String message) {
		return createResponse(javax.ws.rs.core.Response.Status.BAD_REQUEST,
				message);
	}

	public static javax.ws.rs.core.Response createOAuthExceptionResponse(
			OAuthException ex) {
		return createResponse(ex.getHttpStatus(), ex.getMessage());
	}

	public static javax.ws.rs.core.Response createResourceExceptionResponse(
			ResourceException rex) {
		return createResponse(rex.getHttpStatus(), rex.getMessage());
	}

	public static javax.ws.rs.core.Response createInternalExceptionResponse(
			String msg) {
		return createResponse(Status.INTERNAL_SERVER_ERROR, msg);
	}

	public static javax.ws.rs.core.Response createOkResponse(Object json) {
		return createResponse(javax.ws.rs.core.Response.Status.OK, json);
	}

	public static javax.ws.rs.core.Response createResponse(
			javax.ws.rs.core.Response.Status status, Object message) {
		return javax.ws.rs.core.Response.status(status).entity(message).build();
	}

}
