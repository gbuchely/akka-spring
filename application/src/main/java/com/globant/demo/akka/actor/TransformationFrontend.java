package com.globant.demo.akka.actor;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.AbstractActor;
import com.globant.demo.akka.message.TransformationMessages;

import static com.globant.demo.akka.message.TransformationMessages.BACKEND_REGISTRATION;

public class TransformationFrontend extends AbstractActor {

    List<ActorRef> backends = new ArrayList<>();
    int jobCounter = 0;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TransformationMessages.TransformationJob.class, job -> backends.isEmpty(), job -> {
                    sender().tell(new TransformationMessages.JobFailed("Service unavailable, try again later", job),
                            sender());
                })
                .match(TransformationMessages.TransformationJob.class, job -> {
                    jobCounter++;
                    backends.get(jobCounter % backends.size())
                            .forward(job, getContext());
                })
                .matchEquals(BACKEND_REGISTRATION, message -> {
                    getContext().watch(sender());
                    backends.add(sender());
                })
                .match(Terminated.class, terminated -> {
                    backends.remove(terminated.getActor());
                })
                .build();
    }
}
