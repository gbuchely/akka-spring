package com.globant.demo.router;

import akka.http.javadsl.server.Route;

public interface  Router {
    public Route createRoute();
}
