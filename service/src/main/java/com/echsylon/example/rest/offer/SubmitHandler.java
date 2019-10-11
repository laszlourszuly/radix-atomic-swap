package com.echsylon.example.rest.offer;

import com.echsylon.example.ledger.LedgerService;
import com.echsylon.example.rest.TokenRequestHandler;
import io.reactivex.Single;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.http.Response;
import ratpack.http.TypedData;
import ratpack.rx2.RxRatpack;

import static java.lang.Boolean.TRUE;
import static ratpack.http.Status.OK;

public class SubmitHandler extends TokenRequestHandler {

	@Override
	public void onTokenParsed(Context context, String token) {
		context.getRequest()
			.getBody()
			.flatMap(body -> parseRequestBody(body))
			.flatMap(data -> acceptOffer(context, token, data))
			.then(status -> sendSuccess(context, status));
	}

	private Promise<String> parseRequestBody(TypedData body) {
		return RxRatpack.promise(Single.just(body.getText()));
	}

	private Promise<Boolean> acceptOffer(Context context, String token, String offer) {
		return RxRatpack.promise(context
			.get(LedgerService.class)
			.acceptExchange(token, offer)
			.toSingleDefault(TRUE));
	}

	private void sendSuccess(Context context, Boolean status) {
		Response response = context.getResponse();
		response.getHeaders()
			.set("Cache-Control", "no-cache, no-store");
		response.status(OK)
			.send();
	}

}
