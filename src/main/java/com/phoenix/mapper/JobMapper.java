package com.phoenix.mapper;

import com.phoenix.entity.JobEntity;

import java.util.List;

public interface JobMapper {

    public JobEntity getById(String id);

    public List<JobEntity> findList(JobEntity jobEntity);

    public void save(JobEntity jobEntity);

    public void update(JobEntity jobEntity);

    public void delete(String id);
}
