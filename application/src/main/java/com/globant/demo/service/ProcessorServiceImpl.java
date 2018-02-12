package com.globant.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

/**
 * Stateless service which calculates dummy CPU-intensive task
 */
@Service
public class ProcessorServiceImpl implements ProcessorService {

    @Autowired
    private DiscoveryClient discoveryClient;

    public static DiscoveryClient discoveryClientRef;

    @PostConstruct
    private void init() {
        System.out.println("------------------------SERVICE-------" + discoveryClient.toString() + " --------------");
        discoveryClientRef = discoveryClient;
        System.out.println("------------------------SERVICE-------" + discoveryClientRef.toString() + " --------------");
    }

    @Override
    public int compute(int n) {
        return nthPrime(n);
    }

    private int nthPrime(int n) {
        int candidate, count;
        for (candidate = 2, count = 0; count < n; ++candidate) {
            if (isPrime(candidate)) {
                ++count;
            }
        }
        return candidate - 1;
    }

    private boolean isPrime(int n) {
        for (int i = 2; i < n; ++i) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    public DiscoveryClient getDiscoveryClient() {
        return discoveryClient;
    }
}
