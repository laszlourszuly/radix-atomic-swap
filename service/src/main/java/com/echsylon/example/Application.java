package com.echsylon.example;

import com.echsylon.example.ledger.LedgerService;
import com.echsylon.example.rest.account.AccountHandler;
import com.echsylon.example.rest.offer.PrepareHandler;
import com.echsylon.example.rest.offer.ReviewHandler;
import com.echsylon.example.rest.offer.SubmitHandler;
import org.slf4j.LoggerFactory;
import ratpack.error.ClientErrorHandler;
import ratpack.error.ServerErrorHandler;
import ratpack.rx2.RxRatpack;
import ratpack.server.RatpackServer;

import java.io.File;

public class Application {

	public static void main(String... args) throws Exception {
		RxRatpack.initialize();
		RatpackServer.start(server -> server
			.serverConfig(config -> config
				.baseDir(new File("").getAbsoluteFile())
				.port(8084))
			.registryOf(action -> action
				.add(LedgerService.class, new LedgerService()))
			.handlers(chain -> chain
				.register(registry -> registry
					.add(ServerErrorHandler.class, new MainServerErrorHandler())
					.add(ClientErrorHandler.class, new MainClientErrorHandler()))
				.all(new CorsHandler()) // TODO: Remove for prod, handled by reverse proxy.
				.get("api/account", new AccountHandler())
				.post("api/offer/prepare", new PrepareHandler())
				.post("api/offer/review", new ReviewHandler())
				.post("api/offer/accept", new SubmitHandler())
				.files(files -> files.dir("www").indexFiles("index.html"))));

		LoggerFactory.getLogger("com.echsylon.example.atomic")
			.info("Wohoo! Atomic Exchange server is alive and kicking!");
	}

}
