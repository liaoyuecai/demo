package com.demo.sys.service;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.core.service.CURDService;
import com.demo.sys.datasource.dao.SysJobRepository;
import com.demo.sys.datasource.entity.SysJob;
import org.springframework.stereotype.Service;

@Service("jobService")
public class JobService extends CURDService<SysJob, SysJobRepository> {

    public JobService(SysJobRepository  repository) {
        super(repository);
    }
}
