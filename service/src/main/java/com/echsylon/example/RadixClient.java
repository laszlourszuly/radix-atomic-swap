package com.echsylon.example;

import com.echsylon.example.rest.offer.OfferData;
import com.radixdlt.client.application.RadixApplicationAPI;
import com.radixdlt.client.application.identity.RadixIdentities;
import com.radixdlt.client.application.identity.RadixIdentity;
import com.radixdlt.client.application.translate.Action;
import com.radixdlt.client.application.translate.tokens.CreateTokenAction;
import com.radixdlt.client.application.translate.tokens.MintTokensAction;
import com.radixdlt.client.application.translate.tokens.TokenUnitConversions;
import com.radixdlt.client.application.translate.tokens.TransferTokensAction;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.core.BootstrapConfig;
import com.radixdlt.client.core.RadixEnv;
import com.radixdlt.client.core.atoms.Atom;
import com.radixdlt.client.core.atoms.particles.RRI;
import io.reactivex.Single;
import org.radix.serialization2.DsonOutput;
import org.radix.serialization2.client.Serialize;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RadixClient {
	private static final Map<String, RadixClient> CLIENTS = new ConcurrentHashMap<>();
	private static final String TOKEN_NAME = "TOK";

	public static Single<RadixClient> getClientForUser(String userId) {
		return CLIENTS.containsKey(userId) ?
			Single.just(CLIENTS.get(userId)) :
			Single.fromCallable(() -> {
				CLIENTS.put(userId, new RadixClient());
				return CLIENTS.get(userId);
			});
	}

	public static void dropAllClients() {
		CLIENTS.clear();
	}

	private RadixApplicationAPI api;
	private RRI token;

	private RadixClient() {
		setupApi();
		ensureTokens();
	}

	public String getAddress() {
		return api.getAddress().toString();
	}

	public BigDecimal getTokenBalance() {
		return api.getBalances().getOrDefault(token, BigDecimal.ZERO);
	}

	public String prepareExchange(OfferData offer) {
		RadixAddress myAddress = api.getAddress();
		RRI tokensToSell = RRI.of(api.getAddress(), TOKEN_NAME);

		RadixAddress buyersAddress = RadixAddress.from(offer.getPartnerAddress());
		RRI tokensToBuy = RRI.of(buyersAddress, TOKEN_NAME);
		api.pullOnce(buyersAddress).blockingAwait();

		RadixApplicationAPI.Transaction transaction = api.createTransaction();
		transaction.stage(TransferTokensAction.create(tokensToSell, myAddress, buyersAddress, offer.getOffer()));
		transaction.stage(TransferTokensAction.create(tokensToBuy, buyersAddress, myAddress, offer.getRequest()));

		Atom atom = transaction.buildAtom();
		Atom signedAtom = signAtom(atom);

		return Serialize.getInstance().toJson(signedAtom, DsonOutput.Output.ALL);
	}

	public void acceptExchange(String atomJson) {
		Atom atom = parseAtom(atomJson);
		Atom signedAtom = signAtom(atom);
		api.submitAtom(signedAtom, true).blockUntilComplete();
	}

	public Map<String, BigDecimal> parseTransfers(String atomJson) {
		Atom atom = parseAtom(atomJson);
		throw new IllegalStateException("Not implemented");
	}

	public Set<String> parseSignatures(String atomJson) {
		Atom atom = parseAtom(atomJson);
		throw new IllegalStateException("Not implemented");
	}

	private void setupApi() {
		try {
			BootstrapConfig config = RadixEnv.getBootstrapConfig();
			RadixIdentity identity = RadixIdentities.createNew();

			api = RadixApplicationAPI.create(config, identity);
			api.pull();

			RadixAddress myAddress = api.getAddress();
			token = RRI.of(myAddress, TOKEN_NAME);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void ensureTokens() {
		if (api.getTokenDef(token) == null) {
			createToken(token);
		}

		Map<RRI, BigDecimal> balances = api.getBalances();
		if (!balances.containsKey(token) || balances.get(token).compareTo(BigDecimal.ZERO) == 0) {
			mintTokens(token, BigDecimal.TEN);
		}
	}

	private void createToken(RRI token) {
		Action createTokenAction = CreateTokenAction.create(
			token,
			"TOK Token",
			"Atomic swap test token",
			BigDecimal.ZERO,
			TokenUnitConversions.getMinimumGranularity(),
			CreateTokenAction.TokenSupplyType.MUTABLE);

		RadixApplicationAPI.Transaction transaction = api.createTransaction();
		transaction.stage(createTokenAction);
		transaction.commitAndPush().blockUntilComplete();
	}

	private void mintTokens(RRI token, BigDecimal amount) {
		Action mintTokensAction = MintTokensAction.create(
			token,
			api.getAddress(),
			amount);

		RadixApplicationAPI.Transaction transaction = api.createTransaction();
		transaction.stage(mintTokensAction);
		transaction.commitAndPush().blockUntilComplete();
	}

	private Atom signAtom(Atom atom) {
		RadixIdentity myIdentity = api.getIdentity();
		return myIdentity.addSignature(atom).blockingGet();
	}

	private Atom parseAtom(String atomJson) {
		return Serialize.getInstance().fromJson(atomJson, Atom.class);
	}
}
