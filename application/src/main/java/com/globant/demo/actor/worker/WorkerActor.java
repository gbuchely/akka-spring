package com.globant.demo.actor.worker;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.ddata.DistributedData;
import akka.cluster.ddata.PNCounter;
import akka.cluster.ddata.PNCounterKey;
import akka.cluster.ddata.Replicator;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import com.globant.demo.config.Actor;
import com.globant.demo.config.Counters;
import com.globant.demo.model.Work;
import com.globant.demo.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.Random;

@Actor
public class WorkerActor extends AbstractActor {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ActorRef mediator = DistributedPubSub.get(getContext().getSystem()).mediator();
    private final Random random = new Random();
    private BigInteger robotsCounter = BigInteger.valueOf(20);

    @Autowired
    private ProcessorService processorService;

    public WorkerActor() {
        DistributedData.get(getContext().getSystem()).replicator()
                .tell(new Replicator.Subscribe<>(PNCounterKey.create(Counters.SUBSCRIBED_ROBOTS.name()), getSelf()), self());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Receive createReceive() {
        return receiveBuilder()
                .match(Work.class, this::process)
                .match(Replicator.Changed.class, a -> a.key().id().equals(Counters.SUBSCRIBED_ROBOTS.name()),
                        change -> robotsCounter = ((Replicator.Changed<PNCounter>) change).dataValue().getValue())
                .matchAny(this::unhandled)
                .build();
    }

    private void process(Work work) {
        log.info("processor working on data: {}", work.getId() + " " + work.getKey());
        work.setWorkerId(getSelf().path().toString());
        mediator.tell(new DistributedPubSubMediator.Publish(String.valueOf("0"), work), self());
        sender().tell("The task sent to publisher  # " + "0", self());

        // EXTENDER PARA UN SERVICIO UTIL
        //int computedValue = processorService.compute(5);
        //int targetRobot = random.nextInt(robotsCounter.intValue()) + 1;

        //ActorRef mediator = DistributedPubSub.get(getContext().getSystem()).mediator();
        //mediator.tell(new DistributedPubSubMediator.Publish(String.valueOf("0"), computedValue), self());
        //sender().tell("The task sent to robot #" + targetRobot, self());
    }
}
