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
import com.globant.demo.processor.service.ProcessorService;
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
    private BigInteger robotsCounter = BigInteger.ZERO;

    //@Autowired
    //private ProcessorService processorService;

    public WorkerActor() {
        DistributedData.get(getContext().getSystem()).replicator()
                .tell(new Replicator.Subscribe<>(PNCounterKey.create(Counters.SUBSCRIBED_ROBOTS.name()), getSelf()), self());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::process)
                .match(Replicator.Changed.class, a -> a.key().id().equals(Counters.SUBSCRIBED_ROBOTS.name()),
                        change -> robotsCounter = ((Replicator.Changed<PNCounter>) change).dataValue().getValue())
                .matchAny(this::unhandled)
                .build();
    }

    private void process(String sensorData) {
        log.info("processor working on data: {}", sensorData);

        //int computedValue = processorService.compute(sensorData);
        //int targetRobot = random.nextInt(robotsCounter.intValue()) + 1;

        //mediator.tell(new DistributedPubSubMediator.Publish(String.valueOf(targetRobot), computedValue), self());
        //sender().tell("The task sent to robot #" + targetRobot, self());
    }
}