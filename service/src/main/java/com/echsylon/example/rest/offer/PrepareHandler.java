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

import static java.nio.charset.StandardCharsets.UTF_8;
import static ratpack.http.Status.OK;

public class PrepareHandler extends TokenRequestHandler {

	@Override
	public void onTokenParsed(Context context, String token) {
		context.getRequest()
			.getBody()
			.flatMap(body -> parseRequestBody(body))
			.flatMap(data -> prepareExchangeProposal(context, token, data))
			.then(serializedOffer -> sendSuccess(context, serializedOffer));
	}

	private Promise<OfferData> parseRequestBody(TypedData body) {
		return RxRatpack.promise(Single.fromCallable(() -> {
			String json = body.getText(UTF_8);
			return new Gson().fromJson(json, OfferData.class);
		}));
	}

	private Promise<String> prepareExchangeProposal(Context context, String token, OfferData data) {
		return RxRatpack.promise(context
			.get(LedgerService.class)
			.prepareExchange(token, data));
	}

	private void sendSuccess(Context context, String data) {
		Response response = context.getResponse();
		response.getHeaders()
			.set("Content-Type", "application/json")
			.set("Cache-Control", "no-cache, no-store");
		response.status(OK)
			.send(data);
	}

}
