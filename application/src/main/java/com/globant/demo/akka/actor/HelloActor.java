package com.globant.demo.akka.actor;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.globant.demo.service.GreetingService;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Profile("hello")
@Scope("prototype")
public class HelloActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private GreetingService greetingService;

    public HelloActor(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Greet) {
            String name = ((Greet) message).getName();
            log.info("-> " + getSelf());
            log.info("Helloooooo: {}", greetingService.greet(name));
        } else {
            unhandled(message);
        }
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
