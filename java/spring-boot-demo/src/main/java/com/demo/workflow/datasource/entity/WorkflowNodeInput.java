package com.demo.workflow.datasource.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "workflow_node_input")
public class WorkflowNodeInput {
    /**
     * id
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private Integer nodeId;
    private String inputTitle;
    private Integer inputNecessary;
    private Integer inputType;
    private Integer deleted;
}
