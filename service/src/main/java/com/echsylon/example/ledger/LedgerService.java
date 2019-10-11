package com.echsylon.example.ledger;

import com.echsylon.example.RadixClient;
import com.echsylon.example.rest.account.AccountData;
import com.echsylon.example.rest.offer.AtomData;
import com.echsylon.example.rest.offer.OfferData;
import io.reactivex.Completable;
import io.reactivex.Single;
import ratpack.service.Service;
import ratpack.service.StopEvent;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class LedgerService implements Service {

	@Override
	public void onStop(StopEvent event) {
		RadixClient.dropAllClients();
	}

	public Single<AccountData> getAccountData(String token) {
		return RadixClient
			.getClientForUser(token)
			.map(client -> {
				String address = client.getAddress();
				BigDecimal balance = client.getTokenBalance();
				return new AccountData(address, balance);
			});
	}

	public Single<AtomData> getAtomData(String token, String atomBase58) {
		return RadixClient
			.getClientForUser(token)
			.map(client -> {
				Map<String, BigDecimal> transfers = client.parseTransfers(atomBase58);
				Set<String> signatures = client.parseSignatures(atomBase58);
				return new AtomData(transfers, signatures);
			});
	}

	public Single<String> prepareExchange(String token, OfferData offer) {
		return RadixClient
			.getClientForUser(token)
			.map(client -> client.prepareExchange(offer));
	}

	public Completable acceptExchange(String token, String atomBase58) {
		return RadixClient
			.getClientForUser(token)
			.doOnSuccess(client -> client.acceptExchange(atomBase58))
			.ignoreElement();
	}

}
