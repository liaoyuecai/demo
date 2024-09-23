package com.demo.workflow.datasource.dto;

import com.demo.workflow.datasource.entity.WorkflowNode;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class WorkflowEdit {
   private List<WorkflowHistoryDto> history;
   private WorkflowHistoryDto active;
   private WorkflowNode node;
}