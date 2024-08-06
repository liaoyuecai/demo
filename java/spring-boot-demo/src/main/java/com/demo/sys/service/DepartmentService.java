package com.demo.sys.service;

import com.demo.core.service.impl.DefaultCURDService;
import com.demo.sys.datasource.entity.SysDepartment;
import org.springframework.stereotype.Service;

@Service("departmentService")
public class DepartmentService extends DefaultCURDService<SysDepartment> {

}
