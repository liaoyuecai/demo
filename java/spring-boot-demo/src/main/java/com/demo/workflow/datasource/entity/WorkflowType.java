package com.demo.workflow.datasource.entity;

import com.demo.core.entity.TableBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "workflow_type")
public class WorkflowType extends TableBaseEntity {
    private String typeName;

}