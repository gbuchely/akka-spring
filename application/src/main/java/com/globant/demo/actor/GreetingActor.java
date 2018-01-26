package com.globant.demo.actor;

import akka.actor.AbstractActor;
import com.globant.demo.config.Actor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.request.async.DeferredResult;

@Actor
@Profile("greeting")
public class GreetingActor extends AbstractActor {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DeferredResult<String> deferredResult;

    public GreetingActor(DeferredResult<String> deferredResult) {
        this.deferredResult = deferredResult;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(this::unhandled)
                .build();
    }

}
