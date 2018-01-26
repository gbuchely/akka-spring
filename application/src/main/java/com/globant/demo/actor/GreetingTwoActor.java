package com.globant.demo.actor;

import akka.actor.AbstractActor;
import com.globant.demo.config.Actor;
import com.globant.demo.service.GreetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.request.async.DeferredResult;

@Actor
@Profile({"greeting2"})
public class GreetingTwoActor extends AbstractActor {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DeferredResult<String> deferredResult;

    @Autowired
    private GreetingService greetingService;

    public GreetingTwoActor(DeferredResult<String> deferredResult) {
        this.deferredResult = deferredResult;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Greet.class, this::greeting)
                .build();
    }

    private void greeting(Greet message) {
        String name = message.getName();
        log.info("-> " + getSelf());
        log.info("Message greet: {}", greetingService.greet(name));
    }

    public static class Greet {
        private String name;
        public Greet(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

}
