package com.echsylon.example;

import ratpack.handling.Context;
import ratpack.handling.Handler;

public class CorsHandler implements Handler {

    @Override
    public void handle(Context context) {
        context.getResponse().getHeaders()
                .set("Access-Control-Allow-Origin", "*")
                .set("Access-Control-Allow-Methods", "GET,POST")
                .set("Access-Control-Allow-Headers", "Content-Type,Authorization");
        context.next();
    }

}
