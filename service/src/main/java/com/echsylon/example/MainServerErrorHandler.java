package com.echsylon.example;


import com.echsylon.example.rest.InvalidAuthenticationException;
import com.echsylon.example.rest.InvalidCredentialsException;
import com.google.gson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.error.ServerErrorHandler;
import ratpack.handling.Context;
import ratpack.http.Response;

import static ratpack.http.Status.BAD_REQUEST;
import static ratpack.http.Status.FORBIDDEN;
import static ratpack.http.Status.INTERNAL_SERVER_ERROR;
import static ratpack.http.Status.UNAUTHORIZED;

/**
 * Handles any runtime exceptions and ensures the client is served
 * a corresponding error response.
 */
class MainServerErrorHandler implements ServerErrorHandler {
	private final Logger logger;

	MainServerErrorHandler() {
		logger = LoggerFactory.getLogger("com.echsylon.example.atomic");
	}

	@Override
	public void error(Context context, Throwable throwable) {
		if (throwable instanceof InvalidCredentialsException) {
			sendUnauthorizedResponse(context, throwable, "Invalid credentials");
		} else if (throwable instanceof InvalidAuthenticationException) {
			sendForbiddenErrorResponse(context, throwable, "Unexpected authentication method");
		} else if (throwable instanceof JsonParseException) {
			sendBadRequestResponse(context, throwable, "Invalid request data");
		} else {
			sendInternalErrorResponse(context, throwable, "Unexpected internal error");
		}
	}

	@SuppressWarnings("SameParameterValue")
	private void sendUnauthorizedResponse(Context context, Throwable cause, String message) {
		logger.info(message + ", serving UNAUTHORIZED response", cause);
		Response response = context.getResponse();
		response.getHeaders()
			.set("WWW-Authenticate", "Bearer realm=\"Access to protected API\"");
		response.status(UNAUTHORIZED)
			.send();
	}

	@SuppressWarnings("SameParameterValue")
	private void sendForbiddenErrorResponse(Context context, Throwable cause, String message) {
		logger.info(message + ", serving FORBIDDEN response", cause);
		context.getResponse()
			.status(FORBIDDEN)
			.send();
	}

	@SuppressWarnings("SameParameterValue")
	private void sendBadRequestResponse(Context context, Throwable cause, String message) {
		logger.info(message + ", serving BAD REQUEST response", cause);
		context.getResponse()
			.status(BAD_REQUEST)
			.send();
	}

	@SuppressWarnings("SameParameterValue")
	private void sendInternalErrorResponse(Context context, Throwable cause, String message) {
		logger.info(message + ", serving INTERNAL ERROR response", cause);
		context.getResponse()
			.status(INTERNAL_SERVER_ERROR)
			.send();
	}

}
