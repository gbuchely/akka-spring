package com.globant.demo.service;

import com.globant.demo.model.Work;

import java.util.List;

public interface PublisherService {

    List<Work> getWorkList();
    void addNewWork(Work work);
    void cleanWorkList();

}
