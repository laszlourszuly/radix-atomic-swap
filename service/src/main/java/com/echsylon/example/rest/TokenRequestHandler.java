package com.echsylon.example.rest;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Headers;
import ratpack.http.Request;

public abstract class TokenRequestHandler implements Handler {

	@Override
	public void handle(Context context) {
		Request request = context.getRequest();
		Headers headers = request.getHeaders();

		if (!headers.contains("Authorization")) {
			throw new InvalidCredentialsException("Missing 'Authorization' header");
		}

		String header = headers.get("Authorization");
		String[] authorization = header.split(" ", 2);
		String type = authorization[0];
		String token = authorization[1];

		if (!"Bearer".equalsIgnoreCase(type)) {
			throw new InvalidAuthenticationException("Unexpected authorization type: " + type);
		}

		onTokenParsed(context, token);
	}

	public abstract void onTokenParsed(Context context, String token);

}
