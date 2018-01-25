package com.globant.demo;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.globant.demo.akka.actor.TransformationBackend;
import com.globant.demo.akka.cluster.AdminNode;
import com.globant.demo.akka.cluster.ExecutorNode;
import com.globant.demo.controller.Router;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;

@SpringBootApplication
public class DemoApplication {

	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) throws IOException {
		// REST SERVICE
		//SpringApplication.run(DemoApplication.class, args);

		//AKKA HTTP

		final ApplicationContext context = SpringApplication.run(DemoApplication.class, args);
		final ActorSystem system = context.getBean(ActorSystem.class);
		final Http http = Http.get(system);
		final ActorMaterializer materializer = ActorMaterializer.create(system);

		final Router router = context.getBean(Router.class);
		final Flow<HttpRequest, HttpResponse, NotUsed> flow = router.createRoute().flow(system, materializer);
		final CompletionStage<ServerBinding> binding = http
				.bindAndHandle(flow, ConnectHttp.toHost("0.0.0.0", 8080), materializer);

		log.info("Server online at http://localhost:8080/\nPress RETURN to stop...");

		/*
		System.in.read();

		binding
				.thenCompose(ServerBinding::unbind)
				.thenAccept(unbound -> system.terminate());
		*/

	}
}