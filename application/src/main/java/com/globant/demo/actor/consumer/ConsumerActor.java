package com.globant.demo.actor.consumer;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.globant.demo.config.Actor;
import com.globant.demo.model.Work;
import com.globant.demo.service.ConsumerService;
import com.globant.demo.service.PublisherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.request.async.DeferredResult;

@Actor
public class ConsumerActor extends AbstractActor {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DeferredResult<String> deferredResult;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    @Qualifier("clusterDemoRouter")
    private ActorRef router;

    public ConsumerActor(DeferredResult<String> deferredResult) {
        this.deferredResult = deferredResult;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Work.class, this::dispatch)
                .match(String.class, this::complete)
                .matchAny(this::unhandled)
                .build();
    }

    private void dispatch(Work data) {
        data.setId(consumerService.getWorkId());
        log.info("receiver dispatching the data: {}", data.getId());
        router.tell(data, self());
    }

    private void complete(String result) {
        log.info("receiver responding to sender: {}", result);
        router.tell(result, self());
        deferredResult.setResult(result);
        getContext().stop(self());
    }
}
