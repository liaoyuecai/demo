package com.demo.workflow.service;

import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.exception.ErrorCode;
import com.demo.core.exception.GlobalException;
import com.demo.core.service.CURDService;
import com.demo.core.utils.JsonUtils;
import com.demo.sys.datasource.dao.SysUserRepository;
import com.demo.sys.datasource.dto.SimpleUserDto;
import com.demo.workflow.datasource.dao.*;
import com.demo.workflow.datasource.dto.WorkflowRecordDto;
import com.demo.workflow.datasource.dto.WorkflowSaveNode;
import com.demo.workflow.datasource.entity.*;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service("workflowRecordService")
public class WorkflowRecordService extends CURDService<WorkflowRecord, WorkflowRecordRepository> {

    @Resource
    SysUserRepository userRepository;

    @Resource
    WorkflowNodeRepository nodeRepository;
    @Resource
    WorkflowNodeUserRepository nodeUserRepository;
    @Resource
    WorkflowNodeCCUserRepository nodeCCUserRepository;
    @Resource
    WorkflowNodeJobRepository nodeJobRepository;
    @Resource
    WorkflowNodeInputRepository nodeInputRepository;

    public WorkflowRecordService(@Autowired WorkflowRecordRepository repository) {
        super(repository);
    }


    public List<SimpleUserDto> findUsers() {
        return userRepository.findSimpleUsersDto();
    }


    public List<WorkflowRecordDto> findRecordsDto() {
        return repository.findRecordsDto();
    }


    @Override
    @Transactional
    public WorkflowRecord save(ApiHttpRequest<WorkflowRecord> request) {
        Integer id = request.getData().getId();
        WorkflowRecord record = super.save(request);
        if (record.getWorkflowStatus() == 0)
            return record;
        if (id != null) {
            nodeRepository.updateDeletedByWorkflowId(id);
        }
        Map<Integer, WorkflowSaveNode> map = JsonUtils.toMap(record.getWorkflowNodes(), new TypeToken<Map<Integer, WorkflowSaveNode>>() {
        }.getType());
        Collection<WorkflowSaveNode> nodes = map.values();
        Map<Integer, Integer> parentsMap = new HashMap<>();
        Map<Integer, WorkflowNode> nodeMap = new HashMap<>();
        List<WorkflowNode> list = new ArrayList<>();
        AtomicReference<Integer> startNode = new AtomicReference<>();
        nodes.forEach(n -> {
            switch (n.getType()) {
                case 1, 2, 3, 4, 5:
                    if (n.getType() == 1) {
                        if (startNode.get() != null)
                            throw new GlobalException(ErrorCode.PARAMS_ERROR_WORKFLOW_NODE_ERROR);
                        startNode.set(n.getKey());
                    }
                    WorkflowNode node = getInitWorkflowNode(n, record);
                    list.add(node);
                    nodeMap.put(n.getKey(), node);
                    break;
                case 6:
                    if (n.getStartNode() == null || n.getEndNode() == null) {
                        throw new GlobalException(ErrorCode.PARAMS_ERROR_WORKFLOW_NODE_ERROR);
                    }
                    parentsMap.put(n.getEndNode(), n.getStartNode());
                    break;
            }
        });
        if (startNode.get() == null) throw new GlobalException(ErrorCode.PARAMS_ERROR_WORKFLOW_NODE_ERROR);
        //先保存生成节点id
        nodeRepository.saveAll(list);
        //校验决策节点下属节点是否符合一真一假规则，决策节点下连接节点应为2个，2个节点应为一真一假
        Map<Integer, Integer> ifNodeValue = new HashMap<>();
        Map<Integer, Integer> ifNodeNumber = new HashMap<>();

        nodeMap.keySet().forEach(key -> {
            WorkflowNode node = nodeMap.get(key);
            //开始节点不允许有上级节点
            if (node.getNodeType() == 1 && parentsMap.containsKey(key))
                throw new GlobalException(ErrorCode.PARAMS_ERROR_WORKFLOW_NODE_ERROR);
            if (node.getNodeType() != 1 && node.getNodeType() != 6) {
                if (parentsMap.containsKey(key)) {
                    WorkflowNode parent = nodeMap.get(parentsMap.get(key));
                    if (parent == null) throw new GlobalException(ErrorCode.PARAMS_ERROR_WORKFLOW_NODE_ERROR);
                    node.setParentId(parent.getId());
                    //决策节点
                    if (parent.getNodeType().equals(5)) {
                        if (!ifNodeValue.containsKey(parent.getId())) {
                            ifNodeValue.put(parent.getId(), 0);
                            ifNodeNumber.put(parent.getId(), 0);
                        }
                        ifNodeValue.put(parent.getId(), ifNodeValue.get(parent.getId()) + node.getIsCondition());
                        ifNodeNumber.put(parent.getId(), ifNodeNumber.get(parent.getId()) + 1);
                    }
                } else {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR_WORKFLOW_NODE_ERROR);
                }
            }
        });
        ifNodeValue.keySet().forEach(key -> {
            if (!ifNodeValue.get(key).equals(1) || !ifNodeNumber.get(key).equals(2))
                throw new GlobalException(ErrorCode.PARAMS_ERROR_WORKFLOW_NODE_ERROR);
        });
        //保存父节点id
        nodeRepository.saveAll(list);
        //保存节点绑定的用户、岗位、抄送、输入
        List<WorkflowNodeUser> nodeUsers = new ArrayList<>();
        List<WorkflowNodeCCUser> nodeCCUsers = new ArrayList<>();
        List<WorkflowNodeInput> nodeInputs = new ArrayList<>();
        List<WorkflowNodeJob> nodeJobs = new ArrayList<>();
        nodes.forEach(n -> {
            switch (n.getType()) {
                case 1, 2, 3, 4, 5:
                    WorkflowNode node = nodeMap.get(n.getKey());
                    if (node!=null){
                        if (n.getUserIds()!=null && !n.getUserIds().isEmpty())
                            n.getUserIds().forEach(i->nodeUsers.add(new WorkflowNodeUser(node.getId(),i)));
                        if (n.getCcUserIds()!=null && !n.getCcUserIds().isEmpty())
                            n.getUserIds().forEach(i->nodeCCUsers.add(new WorkflowNodeCCUser(node.getId(),i)));
                        if (n.getJobIds()!=null && !n.getJobIds().isEmpty())
                            n.getUserIds().forEach(i->nodeJobs.add(new WorkflowNodeJob(node.getId(),i)));
                        if (n.getInputs()!=null && !n.getInputs().isEmpty()){
                            n.getInputs().forEach(i->i.setNodeId(node.getId()));
                            nodeInputs.addAll(n.getInputs());
                        }
                    }
                    break;
            }
        });
        if (!nodeUsers.isEmpty()) nodeUserRepository.saveAll(nodeUsers);
        if (!nodeCCUsers.isEmpty()) nodeCCUserRepository.saveAll(nodeCCUsers);
        if (!nodeJobs.isEmpty()) nodeJobRepository.saveAll(nodeJobs);
        if (!nodeInputs.isEmpty()) nodeInputRepository.saveAll(nodeInputs);
        return record;
    }

    private WorkflowNode getInitWorkflowNode(WorkflowSaveNode n, WorkflowRecord record) {
        WorkflowNode node = new WorkflowNode();
        node.setWorkflowId(record.getId());
        node.setNodeName(n.getName());
        node.setNodeType(n.getType());
        node.setChildWorkflowId(n.getChildWorkflowId());
        node.setDeleted(0);
        node.setIsCondition(n.isCondition() ? 1 : 0);
        node.setIsReturn(n.isReturn() ? 1 : 0);
        node.setIsUploadFile(n.isUploadFile() ? 1 : 0);
        return node;
    }
}
