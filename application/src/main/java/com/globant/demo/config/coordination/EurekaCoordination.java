package com.globant.demo.config.coordination;

import akka.Done;
import akka.Done$;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.dispatch.Futures;
import com.github.everpeace.constructr.coordination.redis.RedisCoordination;
import com.globant.demo.service.ProcessorServiceImpl;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import de.heikoseeberger.constructr.coordination.Coordination;
import de.heikoseeberger.constructr.coordination.etcd.EtcdCoordination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import scala.collection.immutable.HashSet;
import scala.collection.immutable.Set;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.List;

public class EurekaCoordination implements Coordination {

    private ApplicationInfoManager applicationInfoManager;
    private EurekaClient eurekaClient;

    private String clusterName;
    private ActorSystem system;
    private ActorRef remoteActor = null;
    String path = null;

    private EtcdCoordination coordinator;
    private RedisCoordination redisCoordination;

    public EurekaCoordination(String clusterName, ActorSystem system) {
        this.clusterName = clusterName;
        this.system = system;
        System.out.println("------------------------COORDINATOR---------------------");
        init();
    }

    private void init() {
        coordinator = new EtcdCoordination(this.clusterName,this.system);
        System.out.println("############### ETCD INITIALIZATION ##############: " + clusterName + " " + system);
    }

    @Override
    public Future<Set<Address>> getNodes() {
        System.out.println("############### EUREKA COORDINATION ##############: " + "getNodes");
        Future<Set<Address>> nodes = redisCoordination.getNodes();
        ProcessorServiceImpl.discoveryClientRef.getInstances("akkaEureka1");
        //Address add = new Address()
        return nodes;
    }

    @Override
    public Future<Object> lock(Address self, FiniteDuration ttl) {
        System.out.println("############### EUREKA COORDINATION ##############: " + "lock");
        Future<Object> object = redisCoordination.lock(self, ttl);
        System.out.println("############### self: " + self.toString() + " ttl: " + ttl.toString());
        return object;
    }

    @Override
    public Future<Done> addSelf(Address self, FiniteDuration ttl) {
        System.out.println("############### EUREKA COORDINATION ##############: " + "addSelf");
        Future<Done> done = redisCoordination.addSelf(self, ttl);
        System.out.println("############### self: " + self.toString() + " ttl: " + ttl.toString());
        return Futures.successful(Done.getInstance());
    }

    @Override
    public Future<Done> refresh(Address self, FiniteDuration ttl) {
        System.out.println("############### EUREKA COORDINATION ##############: " + "refresh");
        String address = "akka.tcp://demosystem@127.0.0.1:2552";
        //System.out.println(ProcessorServiceImpl.discoveryClientRef.getInstances("akkaEureka1"));
        Future<Done> done = redisCoordination.refresh(self, ttl);
        System.out.println("############### self: " + self.toString() + " ttl: " + ttl.toString());
        DiscoveryManager.getInstance();
        return Futures.successful(Done.getInstance());
    }

}
