package com.demo.sys.service;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.core.service.CURDService;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dao.SysDepartmentRepository;
import com.demo.sys.datasource.entity.SysDepartment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("departmentService")
public class DepartmentService extends CURDService<SysDepartment, SysDepartmentRepository> {

    public DepartmentService(@Autowired SysDepartmentRepository repository) {
        super(repository);
    }


}
