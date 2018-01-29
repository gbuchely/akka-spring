package com.globant.demo.actor.publisher;

import akka.actor.AbstractActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import com.globant.demo.config.Actor;
import com.globant.demo.model.Work;
import com.globant.demo.service.PublisherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Actor
public class PublisherActor extends AbstractActor {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Integer publisherId;

    @Autowired
    private PublisherService publisherService;

    public PublisherActor(Integer publisherId) {
        System.out.println("............................... INIT PUBLISHER...............................");
        this.publisherId = publisherId;
        DistributedPubSub.get(getContext().system())
                .mediator()
                .tell(new DistributedPubSubMediator.Subscribe(
                        publisherId.toString(), getSelf()), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TextMessage.class, this::processMessageFromRobot)
                .match(Integer.class, this::sendDataToRobot)
                .match(Work.class, this::sendDataToRobot)
                .match(DistributedPubSubMediator.SubscribeAck.class, this::processSubscription)
                .matchAny(this::unhandled)
                .build();
    }

    private void processMessageFromRobot(TextMessage message) {
        log.info("received message from robot {}: {}", publisherId, message);
    }

    private void sendDataToRobot(Integer data) throws IOException {
        log.info("sending message to robot {}: {}", publisherId, data);
        //session.sendMessage(new TextMessage(data.toString()));
    }

    private void sendDataToRobot(Work data) throws IOException {
        data.setResult("Published by publisher # " + publisherId);
        log.info("sending message to robot {}: {}", publisherId, data);
        publisherService.addNewWork(data);
        //session.sendMessage(new TextMessage(data.toString()));
    }

    private void processSubscription(DistributedPubSubMediator.SubscribeAck ack) {
        log.info("robot #{} sucessfully subscribed", publisherId);
    }
}
