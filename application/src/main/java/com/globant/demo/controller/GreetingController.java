package com.globant.demo.controller;

import java.util.concurrent.atomic.AtomicLong;

import com.globant.demo.model.Greeting;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;

//@RestController
public class GreetingController {

    //@Value("${SPRING_PROFILES_ACTIVE}")
    @Value("${app.profile.static}")
    private String staticProfile;

    @Value("${app.profile.dynamic}")
    private String dynamicProfile;

    private static final String template =  "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    /*
    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        System.out.println("PARAMETER-> Static: " + staticProfile + " Dinamic: " + dynamicProfile);
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }
    */
}
