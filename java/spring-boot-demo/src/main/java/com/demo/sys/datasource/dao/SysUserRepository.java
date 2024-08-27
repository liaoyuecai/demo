package com.demo.sys.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.sys.datasource.dto.SimpleUserDto;
import com.demo.sys.datasource.entity.SysUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserRepository extends CustomerBaseRepository<SysUser> {


    SysUser findByUsernameAndStatusAndDeleted(String username, int i, int i1);

    SysUser findByUsernameAndDeleted(String username, int i);

    @Query(value = """
            SELECT new SimpleUserDto(u.id,u.realName,u.phone)  FROM SysUser u 
            WHERE u.deleted = 0 and u.status = 1
            """)
    List<SimpleUserDto> findSimpleUsersDto();
}
