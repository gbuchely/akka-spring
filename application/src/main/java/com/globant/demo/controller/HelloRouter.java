package com.globant.demo.controller;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import com.globant.demo.akka.actor.GreetingActor;
import com.globant.demo.akka.actor.HelloActor;
import com.globant.demo.akka.config.SpringExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static akka.actor.ActorRef.noSender;

@Component
@Profile("hello")
public class HelloRouter extends AllDirectives implements Router{

  private final ActorSystem system;
  private final ActorRef runtimeActor;

  public HelloRouter(ActorSystem actorSystem, SpringExtension springExtension) {
    this.system = actorSystem;
      //runtimeActor = system.actorOf(springExtension.props("greetingActor"));
      runtimeActor = system.actorOf(springExtension.props("helloActor"));
  }

    public Route createRoute() {
        // This handler generates responses to `/hello?name=XXX` requests
        Route greetings =
                parameterOptional("name", optName -> {
                    runtimeActor.tell(new HelloActor.Greet(optName.get()), noSender());
                    String name = optName.orElse("Mister X");
                    return complete("Hi " + name + " !!!!");
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
