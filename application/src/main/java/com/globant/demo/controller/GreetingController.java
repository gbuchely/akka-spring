package com.globant.demo.controller;

import java.util.concurrent.atomic.AtomicLong;

import com.globant.demo.model.Greeting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("greeting")
public class GreetingController {

    @Value("${spring.profiles.active}")
    private String actorProfile;

    private static final String template =  "Hello, %s! -> Role: %s";
    private final AtomicLong counter = new AtomicLong();


    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        System.out.println("PARAMETER -> Actor Profile : " + actorProfile );
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name, actorProfile));
    }

}
