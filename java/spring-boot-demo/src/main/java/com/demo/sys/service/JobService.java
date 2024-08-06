package com.demo.sys.service;

import com.demo.core.service.impl.DefaultCURDService;
import com.demo.sys.datasource.entity.SysJob;
import com.demo.sys.datasource.entity.SysUser;
import org.springframework.stereotype.Service;

@Service("jobService")
public class JobService extends DefaultCURDService<SysJob> {

}
