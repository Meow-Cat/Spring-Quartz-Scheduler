package com.phoenix.service;

import com.phoenix.entity.JobEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface JobService {

    public JobEntity getById(String id);

    public List<JobEntity> findList(JobEntity jobEntity);

    public void save(JobEntity jobEntity);

    public void update(JobEntity jobEntity);

    public void delete(String id);
}
