package com.phoenix.service.impl;

import com.phoenix.entity.JobEntity;
import com.phoenix.mapper.JobMapper;
import com.phoenix.service.JobService;
import com.phoenix.util.UUIDUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
public class JobServiceImpl implements JobService {

    @Resource
    private JobMapper jobMapper;

    @Override
    public JobEntity getById(String id) {
        return jobMapper.getById(id);
    }

    @Override
    public List<JobEntity> findList(JobEntity jobEntity) {
        return jobMapper.findList(jobEntity);
    }

    @Override
    public void save(JobEntity jobEntity) {
        jobEntity.setId(UUIDUtil.uuid());
        jobMapper.save(jobEntity);
    }

    @Override
    public void update(JobEntity jobEntity) {
        jobMapper.update(jobEntity);
    }

    @Override
    public void delete(String id) {
        jobMapper.delete(id);
    }

}
