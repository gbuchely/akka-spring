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
public class PublisherServiceImpl implements PublisherService {

    private static List<Work> workRepository;

    @PostConstruct
    private void init() {
        workRepository = new ArrayList<>();    }

    @Override
    public List<Work> getWorkList() {
        return workRepository;
    }

    @Override
    public void addNewWork(Work work) {
        workRepository.add(work);
    }

    @Override
    public void cleanWorkList() {
        workRepository.clear();
    }
}
