package com.globant.demo.router;

import akka.actor.ActorSystem;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.server.*;
import com.globant.demo.actor.GreetingOneActor;
import com.globant.demo.config.spring.SpringProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import static akka.actor.ActorRef.noSender;

@Component
@Profile("greeting1")
public class GreetingOneRouter extends AllDirectives implements Router {

    @Autowired
    private ActorSystem system;

    public Route createRoute() {
        Route greetings =
                parameterOptional("name", optName -> {
                    DeferredResult<String> result = new DeferredResult<>();
                    system.actorOf(SpringProps.create(system, GreetingOneActor.class, result))
                            .tell(new GreetingOneActor.Greet(optName.get()), noSender());
                    String name = optName.orElse("Mister X");
                    return complete("Hello " + name + ", from NODE 1!");
                });

        return
                // here the complete behavior for this server is defined

                // only handle GET requests
                get(() -> route(
                        // matches the empty path
                        pathSingleSlash(() ->
                                // return a constant string with a certain content type
                                complete(HttpEntities.create(ContentTypes.TEXT_HTML_UTF8, "<html><body>Hello world!</body></html>"))
                        ),
                        path("ping", () ->
                                // return a simple `text/plain` response
                                complete("PONG!")
                        ),
                        path("greet", () ->
                                // uses the route defined above
                                greetings
                        )
                ));
    }
}
