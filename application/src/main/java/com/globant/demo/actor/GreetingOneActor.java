package com.globant.demo.actor;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.globant.demo.config.Actor;
import com.globant.demo.service.GreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.request.async.DeferredResult;

@Actor
@Profile({"greeting1"})
public class GreetingOneActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private final DeferredResult<String> deferredResult;

    @Autowired
    private GreetingService greetingService;

    public GreetingOneActor(DeferredResult<String> deferredResult) {
        this.deferredResult = deferredResult;
    }
    //public GreetingOneActor(GreetingService greetingService) {
    //    this.greetingService = greetingService;
    //}

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
