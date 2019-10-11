package com.echsylon.example.rest.account;

import com.echsylon.example.ledger.LedgerService;
import com.echsylon.example.rest.TokenRequestHandler;
import com.google.gson.Gson;
import ratpack.handling.Context;
import ratpack.http.Response;
import ratpack.rx2.RxRatpack;

import static ratpack.http.Status.OK;

public class AccountHandler extends TokenRequestHandler {

	@Override
	public void onTokenParsed(Context context, String token) {
		RxRatpack.promise(context
			.get(LedgerService.class)
			.getAccountData(token))
			.then(data -> sendSuccess(context, data));
	}

	private void sendSuccess(Context context, AccountData data) {
		Response response = context.getResponse();
		response.getHeaders()
			.set("Content-Type", "application/json")
			.set("Cache-Control", "no-cache, no-store");
		response.status(OK)
			.send(new Gson().toJson(data));
	}

}
