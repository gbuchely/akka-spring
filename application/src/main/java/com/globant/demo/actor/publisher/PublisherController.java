package com.globant.demo.actor.publisher;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.globant.demo.actor.consumer.ConsumerActor;
import com.globant.demo.config.spring.SpringProps;
import com.globant.demo.model.Work;
import com.globant.demo.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@RestController
@Profile("publisher")
public class PublisherController {

    @Autowired
    private PublisherService service;

    @RequestMapping(value = "/output/data", method = RequestMethod.GET)
    public List<Work> getWorkList() {
        return service.getWorkList();
    }
}
