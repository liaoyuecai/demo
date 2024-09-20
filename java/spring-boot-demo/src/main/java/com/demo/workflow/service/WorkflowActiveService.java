package com.demo.workflow.service;

import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.PageList;
import com.demo.core.dto.PageListRequest;
import com.demo.core.dto.WebTreeNode;
import com.demo.core.exception.ErrorCode;
import com.demo.core.exception.GlobalException;
import com.demo.core.service.CURDService;
import com.demo.core.utils.JsonUtils;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dao.SysUserRepository;
import com.demo.workflow.datasource.dao.*;
import com.demo.workflow.datasource.dto.*;
import com.demo.workflow.datasource.entity.*;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service("workflowActiveService")
public class WorkflowActiveService extends CURDService<WorkflowActive, WorkflowActiveRepository> {

    @Resource
    SysUserRepository userRepository;
    @Resource
    WorkflowDistributeCCRepository distributeCCRepository;
    @Resource
    WorkflowActiveHistoryRepository activeHistoryRepository;
    @Resource
    WorkflowDistributeRepository distributeRepository;
    @Resource
    WorkflowNodeCCUserRepository nodeCCUserRepository;
    @Resource
    WorkflowNodeInputRepository nodeInputRepository;
    @Resource
    WorkflowNodeJobRepository nodeJobRepository;
    @Resource
    WorkflowNodeRepository nodeRepository;
    @Resource
    WorkflowNodeUserRepository nodeUserRepository;
    @Resource
    WorkflowRecordRepository recordRepository;


    public WorkflowActiveService(@Autowired WorkflowActiveRepository repository) {
        super(repository);
    }


    public List<WebTreeNode> findUserWorkflowList(ApiHttpRequest request) {
        List<Integer> workflowIds = getUserWorkflowIds((AuthUserCache) request.getUser());
        if (workflowIds.isEmpty()) return List.of();
        List<WorkflowRecordTypeDto> list = recordRepository.findRecordsTypeDto(workflowIds);
        if (CollectionUtils.isEmpty(list)) return List.of();
        List<WebTreeNode> nodes = new ArrayList<>();
        Map<Integer, WebTreeNode> nodeMap = new HashMap<>();
        list.forEach(i -> {
            if (!nodeMap.containsKey(i.getTypeId())) {
                WebTreeNode typeNode = new WebTreeNode();
                String key = "type-" + i.getTypeId();
                typeNode.setId(i.getTypeId());
                typeNode.setKey(key);
                typeNode.setValue(key);
                typeNode.setDisabled(true);
                typeNode.setTitle(i.getTypeName());
                nodeMap.put(i.getTypeId(), typeNode);
                nodes.add(typeNode);
            }
            WebTreeNode node = new WebTreeNode();
            node.setKey(i.getId());
            node.setValue(i.getId());
            node.setTitle(i.getWorkflowName());
            nodeMap.get(i.getTypeId()).addChild(node);
        });
        nodes.sort(Comparator.comparingInt(WebTreeNode::getId));
        return nodes;
    }

    /**
     * 开启一个流程
     *
     * @param request
     * @return
     */
    public WorkflowInputAndData start(ApiHttpRequest<Integer> request) {
        List<Integer> workflowIds = getUserWorkflowIds((AuthUserCache) request.getUser());
        if (CollectionUtils.isEmpty(workflowIds) || !workflowIds.contains(request.getData()))
            throw new GlobalException(ErrorCode.ACCESS_DATA_WORKFLOW_ERROR);
        WorkflowInputAndData data = new WorkflowInputAndData();
        data.setNode(nodeRepository.findWorkflowFirstNode(request.getData()));
        if (data.getNode() == null) throw new GlobalException(ErrorCode.PARAMS_ERROR_WORKFLOW_NODE_ERROR);
        List<WorkflowNodeInput> nodeInputs = nodeInputRepository.findByNodeIdAndDeleted(data.getNode().getId(), 0);
        WorkflowActiveHistory history = activeHistoryRepository.findByNodeIdAndStatus(data.getNode().getId(), 0);
        if (history != null) {
            List<String> values = JsonUtils.toList(history.getActiveInput());
            List<NodeInputData> inputs = new ArrayList<>();
            for (int i = 0, l = nodeInputs.size(); i < l; i++)
                inputs.add(new NodeInputData(nodeInputs.get(i), values.get(i)));
            data.setInputs(inputs);
            data.setFilePath(history.getActiveFile());
        } else {
            data.setInputs(nodeInputs.stream().map(i -> new NodeInputData(i, null)).toList());
        }
        return data;
    }

    /**
     * 准备处理某个流程节点
     * 用于锁定流程流转，避免其他人处理
     *
     * @param request
     */
    public void handle(ApiHttpRequest<Integer> request) {
        WorkflowDistribute distribute = distributeRepository.findByWorkflowIdAndUserId(request.getData(), request.getUser().getId());
        if (distribute == null)
            throw new GlobalException(ErrorCode.ACCESS_DATA_WORKFLOW_ERROR);
        int re = repository.updateUpdateBy(request.getUser().getId(), request.getData());
        if (re < 1)
            throw new GlobalException(ErrorCode.ACCESS_DATA_WORKFLOW_HANDLE_ERROR);
    }


    /**
     * 流程提交/回退
     *
     * @param request
     */
    @Transactional
    public void submit(ApiHttpRequest<SaveHistory> request) {
        WorkflowActive active = repository.findByWorkflowIdAndNodeId(request.getData().getWorkflowId(), request.getData().getNodeId());
        WorkflowActiveHistory history = null;
        WorkflowNode node = nodeRepository.findById(request.getData().getNodeId()).get();
        if (active == null) {
            //此时表示为流程开始后的第一次保存
            //先判断用户是否可以操作此流程
            List<Integer> workflowIds = getUserWorkflowIds((AuthUserCache) request.getUser());
            if (CollectionUtils.isEmpty(workflowIds))
                throw new GlobalException(ErrorCode.ACCESS_DATA_WORKFLOW_ERROR);
            if (!workflowIds.contains(node.getWorkflowId()))
                throw new GlobalException(ErrorCode.ACCESS_DATA_WORKFLOW_ERROR);
            active = new WorkflowActive();
            active.setStatus(1);
            active.setNodeId(request.getData().getNodeId());
            active.setWorkflowId(node.getWorkflowId());
            active.setWorkflowName(request.getData().getWorkflowName());
            active.setCreateTime(LocalDateTime.now());
            active.setUpdateBy(request.getUser().getId());
            active.setCreateBy(request.getUser().getId());
            history = new WorkflowActiveHistory();
            history.setCreateBy(request.getUser().getId());
            history.setStatus(1);
            history.setNodeId(request.getData().getNodeId());
            history.setCreateTime(LocalDateTime.now());

        } else {
            //不是第一次保存则判断该节点是否为当前用户处理节点
            if (!request.getUser().getId().equals(active.getUpdateBy()))
                throw new GlobalException(ErrorCode.ACCESS_DATA_WORKFLOW_ERROR);
            history = activeHistoryRepository.findByNodeIdAndStatus(request.getData().getNodeId(), 0);
        }
        history.setActiveStatus(request.getData().getActiveStatus());
        history.setActiveInput(JsonUtils.toJsonStr(request.getData().getInputs()));
        activeHistoryRepository.save(history);
        //流程分发
        history.setActiveStatus(request.getData().getActiveStatus());
        WorkflowNode nextNode = null;
        List<Integer> sendUserIds = null;
        List<Integer> sendCCUserIds = null;
        //回退
        if (request.getData().getActiveStatus() == 2) {
            nextNode = nodeRepository.findById(node.getParentId()).get();
            sendUserIds = nodeRepository.findNodeUserIds(nextNode.getId());
            sendCCUserIds = nodeCCUserRepository.findUserIdsByNodeId(nextNode.getId());
        } else {
            switch (node.getNodeType()) {
                //流程结束
                case 2 -> active.setStatus(0);
                //任务节点
                case 3 -> {
                    nextNode = nodeRepository.findByParentIdAndDeleted(request.getData().getNodeId(), 0);
                    sendUserIds = nodeRepository.findNodeUserIds(nextNode.getId());
                    sendCCUserIds = nodeCCUserRepository.findUserIdsByNodeId(nextNode.getId());
                }
                //开启子流程
                case 4 -> {
                    WorkflowActive childActive = new WorkflowActive();
                    childActive.setWorkflowName(active.getWorkflowName());
                    childActive.setWorkflowId(node.getChildWorkflowId());
                    childActive.setStatus(1);
                    childActive.setParentWorkflowId(node.getWorkflowId());
                    childActive.setCreateBy(request.getUser().getId());
                    childActive.setCreateTime(LocalDateTime.now());
                    repository.save(childActive);
                    WorkflowNode childStartNode = nodeRepository.findStartNode(childActive.getWorkflowId());
                    sendUserIds = nodeRepository.findNodeUserIds(childStartNode.getId());
                    nextNode = nodeRepository.findByParentIdAndDeleted(childStartNode.getId(), 0);
                }
                //决策节点
                case 5 -> {
                    if (request.getData().getActiveStatus() == 3) {
                        nextNode = nodeRepository.findByParentIdAndDeletedAndIsCondition(request.getData().getNodeId(), 0, 1);

                    } else if (request.getData().getActiveStatus() == 4) {
                        nextNode = nodeRepository.findByParentIdAndDeletedAndIsCondition(request.getData().getNodeId(), 0, 0);
                    } else throw new GlobalException(ErrorCode.PARAMS_ERROR_WORKFLOW_NODE_ERROR);
                    sendUserIds = nodeRepository.findNodeUserIds(nextNode.getId());
                    sendCCUserIds = nodeCCUserRepository.findUserIdsByNodeId(nextNode.getId());
                }
            }
        }
        distributeRepository.deleteByWorkflowId(active.getWorkflowId());
        Integer workflowId = active.getWorkflowId();
        //分发
        if (!CollectionUtils.isEmpty(sendUserIds)) {
            distributeRepository.saveAll(sendUserIds.stream().map(i -> new WorkflowDistribute(workflowId, i)).toList());
        } else {
            //不绑定默认流转到创建人
            distributeRepository.save(new WorkflowDistribute(workflowId, active.getCreateBy()));
        }
        Integer nodeHistoryId = history.getId();
        //抄送
        if (!CollectionUtils.isEmpty(sendCCUserIds)) {
            distributeCCRepository.saveAll(sendCCUserIds.stream().map(i -> new WorkflowDistributeCC(workflowId, nodeHistoryId, i)).toList());
        }
        active.setNodeId(nextNode.getId());
        active.setUpdateBy(null);
        repository.save(active);
    }

    /**
     * 保存流程处理信息
     *
     * @param request
     */
    @Transactional
    public void saveHistory(ApiHttpRequest<SaveHistory> request) {
        WorkflowActive active = repository.findByWorkflowIdAndNodeId(request.getData().getWorkflowId(), request.getData().getNodeId());
        WorkflowActiveHistory history = null;
        if (active == null) {
            //此时表示为流程开始后的第一次保存
            //先判断用户是否可以操作此流程
            List<Integer> workflowIds = getUserWorkflowIds((AuthUserCache) request.getUser());
            if (CollectionUtils.isEmpty(workflowIds))
                throw new GlobalException(ErrorCode.ACCESS_DATA_WORKFLOW_ERROR);
            WorkflowNode node = nodeRepository.findById(request.getData().getNodeId()).get();
            if (!workflowIds.contains(node.getWorkflowId()))
                throw new GlobalException(ErrorCode.ACCESS_DATA_WORKFLOW_ERROR);
            active = new WorkflowActive();
            active.setNodeId(request.getData().getNodeId());
            active.setWorkflowId(node.getWorkflowId());
            active.setCreateTime(LocalDateTime.now());
            active.setUpdateBy(request.getUser().getId());
            active.setCreateBy(request.getUser().getId());
            history = new WorkflowActiveHistory();
            history.setCreateBy(request.getUser().getId());
            history.setStatus(0);
            history.setNodeId(request.getData().getNodeId());
            history.setCreateTime(LocalDateTime.now());
            history.setActiveStatus(0);
            repository.save(active);
        } else {
            //不是第一次保存则判断该节点是否为当前用户处理节点
            if (!request.getUser().getId().equals(active.getUpdateBy()))
                throw new GlobalException(ErrorCode.ACCESS_DATA_WORKFLOW_ERROR);
            history = activeHistoryRepository.findByNodeIdAndStatus(request.getData().getNodeId(), 0);
        }
        history.setActiveInput(JsonUtils.toJsonStr(request.getData().getInputs()));
        activeHistoryRepository.save(history);
    }

    /**
     * 获取用户能够启动的流程
     *
     * @param user
     * @return
     */
    List<Integer> getUserWorkflowIds(AuthUserCache user) {
        List<WorkflowNode> allStartNodes = nodeRepository.findAddStartNodes();
        if (CollectionUtils.isEmpty(allStartNodes)) return List.of();
        Map<Integer, Integer> nodeToWorkflow = allStartNodes.stream()
                .collect(Collectors.toMap(WorkflowNode::getId, WorkflowNode::getWorkflowId));
        List<Integer> allStartNodeIds = allStartNodes.stream().map(WorkflowNode::getId).toList();
        Integer userId = user.getId();
        List<Integer> userJobs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(user.getJobList())) {
            user.getJobList().forEach(i -> userJobs.add(i.getId()));
        }
        List<WorkflowNodeUser> nodeUsers = nodeUserRepository.findByNodeIdIn(allStartNodeIds);
        Set<Integer> userWorkflows = nodeUsers.stream().map(i->nodeToWorkflow.get(i.getNodeId())).collect(Collectors.toSet());
        List<WorkflowNodeJob> nodeJobs = nodeJobRepository.findByNodeIdIn(allStartNodeIds);
        Set<Integer> jobWorkflows = nodeJobs.stream().map(i->nodeToWorkflow.get(i.getNodeId())).collect(Collectors.toSet());
        List<Integer> workflowIds = new ArrayList<>();
        nodeUsers.forEach(i -> {
            if (userId.equals(i.getId()) && nodeToWorkflow.containsKey(i.getNodeId())) {
                workflowIds.add(nodeToWorkflow.get(i.getId()));
                nodeToWorkflow.remove(i.getNodeId());
            }
        });
        nodeJobs.forEach(i -> {
            if (userJobs.contains(i.getId()) && nodeToWorkflow.containsKey(i.getNodeId())) {
                workflowIds.add(nodeToWorkflow.get(i.getId()));
                nodeToWorkflow.remove(i.getNodeId());
            }
        });
        if (!nodeToWorkflow.isEmpty()) {
            workflowIds.addAll(nodeToWorkflow.values().stream().filter(i-> !userWorkflows.contains(i) && !jobWorkflows.contains(i)).toList());
        }
        return workflowIds;
    }

    public PageList<WorkflowActiveDto> findDtoPage(PageListRequest<WorkflowActiveDto> request) {
        return request.toPageList(repository.findDtoPage(request.getData().getWorkflowName(), request.getUser().getId(), request.toPageable()));
    }
}
