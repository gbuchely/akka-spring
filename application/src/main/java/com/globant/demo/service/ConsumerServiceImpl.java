package com.globant.demo.service;

import com.globant.demo.model.Work;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Stateless service which calculates dummy CPU-intensive task
 */
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private Integer seq = 0;

    @Override
    public Integer getWorkId() {
        return seq++;
    }
}
