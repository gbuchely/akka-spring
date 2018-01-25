package com.globant.demo;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.metrics.AdaptiveLoadBalancingGroup;
import akka.cluster.metrics.CpuMetricsSelector;
import akka.cluster.routing.ClusterRouterGroup;
import akka.cluster.routing.ClusterRouterGroupSettings;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.routing.RoundRobinPool;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.globant.demo.akka.actor.TransformationBackend;
import com.globant.demo.akka.cluster.AdminNode;
import com.globant.demo.akka.cluster.ExecutorNode;
import com.globant.demo.config.spring.SpringExtension;
import com.globant.demo.config.spring.SpringProps;
import com.globant.demo.controller.Router;
import com.globant.demo.processor.ProcessorActor;
import com.globant.demo.transmitter.WebsocketHandler;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;
import static java.util.Collections.singletonList;

@SpringBootApplication
public class DemoApplication {

	@Autowired
	private ActorSystem system;

	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) throws IOException {
		// REST SERVICE
		SpringApplication.run(DemoApplication.class, args);

		//AKKA HTTP
	    /*
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

	@Bean
	public ActorSystem actorSystem(ApplicationContext context) {

		ActorSystem system = ActorSystem.create("robotsystem", ConfigFactory.load());
		SpringExtension.getInstance().get(system).initialize(context);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
					Cluster cluster = Cluster.get(system);
					cluster.leave(cluster.selfAddress());
				})
		);

		return system;
	}

	@Bean("clusterProcessorRouter")
	@Profile("receiver")
	public ActorRef clusterProcessorRouter() {
		List<String> path = singletonList("/user/localProcessorRouter");
		return system.actorOf(new ClusterRouterGroup(new AdaptiveLoadBalancingGroup(CpuMetricsSelector.getInstance(), path),
				new ClusterRouterGroupSettings(100, path, false, "processor")).props(), "clusterProcessorRouter");
	}

	@Bean("localProcessorRouter")
	@Profile("processor")
	public ActorRef localProcessorRouter() {
		return system.actorOf(SpringProps.create(system, ProcessorActor.class)
				.withDispatcher("processor-dispatcher")
				.withRouter(new RoundRobinPool(10)), "localProcessorRouter");
	}

	@EnableWebSocket
	@Profile("transmitter")
	public class WebSocketConfiguration implements WebSocketConfigurer {

		@Autowired
		private WebsocketHandler handler;

		@Override
		public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
			registry.addHandler(handler, "/robots/socket").setAllowedOrigins("*");
		}
	}
}