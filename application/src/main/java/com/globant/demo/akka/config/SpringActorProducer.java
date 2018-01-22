package com.globant.demo.akka.config;

import akka.actor.*;
import org.springframework.context.ApplicationContext;

public class SpringActorProducer implements IndirectActorProducer {
  private final ApplicationContext applicationContext;
  private final String actorBeanName;

  public SpringActorProducer(ApplicationContext applicationContext, String actorBeanName) {
    this.applicationContext = applicationContext;
    this.actorBeanName = actorBeanName;
  }

  @Override
  public Actor produce() {
    return (Actor) applicationContext.getBean(actorBeanName);
  }

  @Override
  public Class<? extends Actor> actorClass() {
    return (Class<? extends Actor>) applicationContext.getType(actorBeanName);
  }
}
