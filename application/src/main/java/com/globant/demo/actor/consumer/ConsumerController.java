package com.globant.demo.actor.consumer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.globant.demo.config.spring.SpringProps;
import com.globant.demo.receiver.ReceiverActor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@Profile("consumer")
public class ConsumerController {

    @Autowired
    private ActorSystem system;

    @RequestMapping(value = "/sensors/data", method = RequestMethod.POST)
    private DeferredResult<String> receiveSensorsData(@RequestBody String data) {
        DeferredResult<String> result = new DeferredResult<>();
        system.actorOf(SpringProps.create(system, ConsumerActor.class, result))
                .tell(data, ActorRef.noSender());
        return result;
    }
}
