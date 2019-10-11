package com.echsylon.example.rest.offer;

import com.echsylon.example.ledger.LedgerService;
import com.echsylon.example.rest.TokenRequestHandler;
import com.google.gson.Gson;
import io.reactivex.Single;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.http.Response;
import ratpack.http.TypedData;
import ratpack.rx2.RxRatpack;

import static ratpack.http.Status.OK;

public class ReviewHandler extends TokenRequestHandler {

	@Override
	public void onTokenParsed(Context context, String token) {
		context.getRequest()
			.getBody()
			.flatMap(body -> parseRequestBody(body))
			.flatMap(data -> parseAtomData(context, token, data))
			.then(offerDetails -> sendSuccess(context, offerDetails));
	}

	private Promise<String> parseRequestBody(TypedData body) {
		return RxRatpack.promise(Single.just(body.getText()));
	}

	private Promise<AtomData> parseAtomData(Context context, String token, String offer) {
		return RxRatpack.promise(context
			.get(LedgerService.class)
			.getAtomData(token, offer));
	}

	private void sendSuccess(Context context, AtomData data) {
		Response response = context.getResponse();
		response.getHeaders()
			.set("Content-Type", "application/json")
			.set("Cache-Control", "no-cache, no-store");
		response.status(OK)
			.send(new Gson().toJson(data));
	}

}
