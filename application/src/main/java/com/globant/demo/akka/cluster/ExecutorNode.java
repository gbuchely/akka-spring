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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static akka.actor.ActorRef.noSender;
import static akka.pattern.Patterns.ask;

@Component
@Profile("executor")
public class ExecutorNode extends AllDirectives {

  private final ActorSystem system;
  private final ActorRef runtimeActor;

  public ExecutorNode(ActorSystem actorSystem, SpringExtension springExtension) {
    this.system = actorSystem;
      runtimeActor = null;
  }

  public void transform(String port, String role, int poolSize) {
    final Config config = doConfig(port, role);
    ActorSystem system = ActorSystem.create("ClusterSystem", config);
    while (poolSize > 0) {
        system.actorOf(Props.create(TransformationBackend.class), "backend_" + poolSize);
        poolSize--;
    }
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
}
