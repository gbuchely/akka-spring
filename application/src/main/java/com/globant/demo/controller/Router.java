package com.globant.demo.controller;

import akka.http.javadsl.server.Route;

public interface  Router {

    public Route createRoute();
}
