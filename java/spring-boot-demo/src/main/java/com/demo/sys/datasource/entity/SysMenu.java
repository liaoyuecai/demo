package com.demo.sys.datasource.entity;

import com.demo.core.entity.TableBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "sys_menu")
public class SysMenu extends TableBaseEntity {

    private Integer parentId;
    private Integer menuSort;
    private String menuName;
    private String menuPath;
    private String menuIcon;

}