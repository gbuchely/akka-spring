package com.globant.demo.config.coordination;

import akka.Done;
import akka.Done$;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.dispatch.Futures;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import de.heikoseeberger.constructr.coordination.Coordination;
import de.heikoseeberger.constructr.coordination.etcd.EtcdCoordination;
import org.springframework.beans.factory.annotation.Autowired;
import scala.collection.immutable.HashSet;
import scala.collection.immutable.Set;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

public class EtcdCoordinationAux implements Coordination {

    private String clusterName;
    private ActorSystem system;

    private EtcdCoordination coordinator;

    public EtcdCoordinationAux(String clusterName, ActorSystem system) {
        this.clusterName = clusterName;
        this.system = system;
        init();
    }

    private void init() {
        coordinator = new EtcdCoordination(this.clusterName,this.system);
        System.out.println("############### ETCD INITIALIZATION ##############: " + clusterName + " " + system);
    }

    @Override
    public Future<Set<Address>> getNodes() {
        System.out.println("############### ETCD COORDINATION ##############: " + "getNodes");
        Future<Set<Address>> nodes = coordinator.getNodes();
        return nodes;
    }

    @Override
    public Future<Object> lock(Address self, FiniteDuration ttl) {
        System.out.println("############### ETCD COORDINATION ##############: " + "lock");
        Future<Object> object = coordinator.lock(self, ttl);
        System.out.println("############### self: " + self.toString() + " ttl: " + ttl.toString());
        return object;
    }

    @Override
    public Future<Done> addSelf(Address self, FiniteDuration ttl) {
        System.out.println("############### ETCD COORDINATION ##############: " + "addSelf");
        String address = "akka.tcp://demosystem@127.0.0.1:2552";
        Future<Done$> done = coordinator.addSelf(self, ttl);
        System.out.println("############### self: " + self.toString() + " ttl: " + ttl.toString());
        return Futures.successful(Done.getInstance());
    }

    @Override
    public Future<Done> refresh(Address self, FiniteDuration ttl) {
        System.out.println("############### ETCD COORDINATION ##############: " + "refresh");
        String address = "akka.tcp://demosystem@127.0.0.1:2552";
        Future<Done$> done = coordinator.refresh(self, ttl);
        System.out.println("############### self: " + self.toString() + " ttl: " + ttl.toString());
        DiscoveryManager.getInstance();
        return Futures.successful(Done.getInstance());
    }
}
