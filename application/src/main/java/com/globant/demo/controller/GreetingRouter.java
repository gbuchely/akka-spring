package com.globant.demo.controller;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.server.*;
import com.globant.demo.akka.actor.GreetingActor;
import com.globant.demo.akka.config.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static akka.actor.ActorRef.noSender;

@Component
@Profile("greeting")
public class GreetingRouter extends AllDirectives implements Router{

  private final ActorSystem system;
  private final ActorRef runtimeActor;

  public GreetingRouter(ActorSystem actorSystem, SpringExtension springExtension) {
    this.system = actorSystem;
      runtimeActor = system.actorOf(springExtension.props("greetingActor"));
  }

    public Route createRoute() {
        // This handler generates responses to `/hello?name=XXX` requests
        Route greetings =
                parameterOptional("name", optName -> {
                    runtimeActor.tell(new GreetingActor.Greet(optName.get()), noSender());
                    String name = optName.orElse("Mister X");
                    return complete("Hello " + name + "!");
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
