package com.demo.sys.service;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.core.service.CURDService;
import com.demo.sys.datasource.dao.SysDepartmentRepository;
import com.demo.sys.datasource.entity.SysDepartment;
import org.springframework.stereotype.Service;

@Service("departmentService")
public class DepartmentService extends CURDService<SysDepartment, SysDepartmentRepository> {

    public DepartmentService(SysDepartmentRepository repository) {
        super(repository);
    }
}
