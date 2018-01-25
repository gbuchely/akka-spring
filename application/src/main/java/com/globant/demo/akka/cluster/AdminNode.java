package com.globant.demo.akka.cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnSuccess;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.util.Timeout;
import com.globant.demo.akka.actor.GreetingActor;
import com.globant.demo.akka.actor.TransformationBackend;
import com.globant.demo.akka.actor.TransformationFrontend;
import com.globant.demo.akka.config.SpringExtension;
import com.globant.demo.akka.message.TransformationMessages;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static akka.actor.ActorRef.noSender;
import static akka.pattern.Patterns.ask;

@Component
@Profile("admin")
public class AdminNode extends AllDirectives {

  private final ActorSystem system;
  private final ActorRef runtimeActor;

  public AdminNode(ActorSystem actorSystem, SpringExtension springExtension) {
    this.system = actorSystem;
      runtimeActor = null;
  }

  @PostConstruct
  private void init() {
      transform("0", "[frontend]");
  }

  public void transform(String port, String role) {
    final Config config = doConfig(port, role);
    ActorSystem system = ActorSystem.create("ClusterSystem", config);

    final ActorRef frontend = system.actorOf(
            Props.create(TransformationFrontend.class), "frontend");


    final FiniteDuration interval = Duration.create(2, TimeUnit.SECONDS);
    final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
    final ExecutionContext ec = system.dispatcher();
    final AtomicInteger counter = new AtomicInteger();
    system.scheduler().schedule(interval, interval, new Runnable() {
        public void run() {
            ask(frontend,
                    new TransformationMessages.TransformationJob("hello-" + counter.incrementAndGet()),
                    timeout).onSuccess(new OnSuccess<Object>() {
                public void onSuccess(Object result) {
                    System.out.println(result);
                }
            }, ec);
        }

    }, ec);

  }

  private Config doConfig(String port, String role) {
      Config config =
              ConfigFactory.parseString(
                      "akka.remote.netty.tcp.port=" + port + "\n" +
                              "akka.remote.artery.canonical.port=" + port)
                      .withFallback(ConfigFactory.parseString("akka.cluster.roles=" + role))
                      .withFallback(ConfigFactory.load());
      return config;
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
