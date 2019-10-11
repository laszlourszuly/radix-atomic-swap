package com.echsylon.example;

import ratpack.error.ClientErrorHandler;
import ratpack.handling.Context;

class MainClientErrorHandler implements ClientErrorHandler {

    @Override
    public void error(Context context, int statusCode) {
        context.getResponse()
                .status(statusCode)
                .send();
    }

}
