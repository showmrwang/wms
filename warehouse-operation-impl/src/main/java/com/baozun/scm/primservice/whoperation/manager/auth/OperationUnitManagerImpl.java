package com.baozun.scm.primservice.whoperation.manager.auth;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.auth.OpUnitTreeCommand;
import com.baozun.scm.primservice.whoperation.dao.auth.OperationUnitDao;
import com.baozun.scm.primservice.whoperation.model.auth.OperationUnit;


@Service("operationUnitManager")
@Transactional
public class OperationUnitManagerImpl implements OperationUnitManager {
    public static final Logger log = LoggerFactory.getLogger(OperationUnitManagerImpl.class);

    @Autowired
    private OperationUnitDao operationUnitDao;

    @Override
    public List<OpUnitTreeCommand> findOpUnitTreeByUserId(Long userId) {
        List<OpUnitTreeCommand> orgAll = operationUnitDao.findCommandList(null);

        List<OpUnitTreeCommand> outList = operationUnitDao.findUnitTreeByUserId(userId);// 获取用户所属组织
        List<OpUnitTreeCommand> rootList = null;
        if (outList != null) {
            // 获取根结点
            rootList = findListByParentId(null, orgAll);
            if (rootList != null) {
                for (OpUnitTreeCommand root : rootList) {
                    assemble(root, outList, orgAll);// 添加子节点
                }
                rootList.add(outList.get(0));
            }
        }

        return rootList;
    }

    /**
     * 递归添加子节点
     * 
     * @param parent
     * @param outList
     * @return
     */
    public Boolean assemble(OpUnitTreeCommand parent, List<OpUnitTreeCommand> outList, List<OpUnitTreeCommand> allOrg) {
        boolean b = false;
        List<OpUnitTreeCommand> childList = findListByParentId(parent.getId(), allOrg);// 获取子节点
        List<OpUnitTreeCommand> childList2 = new ArrayList<OpUnitTreeCommand>();
        for (OpUnitTreeCommand oc : outList) {// 验证当前结点是否是用户拥有的
            // FIXME Long对象直接==判断
            if (oc.getId().equals(parent.getId())) {
                b = true;
                parent.setSelectable("true");
                break;
            }
        }
        if (!b) {
            parent.setSelectable("false");// 页面中此节点是否可以选择
        }
        for (OpUnitTreeCommand o : childList) {// 查找子节点的子节点
            if (assemble(o, outList, allOrg)) {
                childList2.add(o);
                b = true;
            }
        }
        if (childList2.isEmpty()) {
            childList2 = null;
        }
        parent.setNodes(childList2);
        return b;
    }

    /**
     * 获取子节点
     */
    private List<OpUnitTreeCommand> findListByParentId(Long parent, List<OpUnitTreeCommand> allList) {
        List<OpUnitTreeCommand> childList = new ArrayList<OpUnitTreeCommand>();
        for (OpUnitTreeCommand org : allList) {
            if (null == org.getParentUnitId() && null == parent) {
                if (parent == org.getParentUnitId()) {
                    childList.add(org);
                }
            } else {
                if (null != org.getParentUnitId() && null != parent) {
                    if (parent.equals(org.getParentUnitId())) {
                        childList.add(org);
                    }
                }
            }
        }
        return childList;
    }

    @Override
    public List<OperationUnit> findListByParam(OperationUnit operationUnit) {
        return operationUnitDao.findListByParam(operationUnit);
    }

    @Override
    public List<OpUnitTreeCommand> findListByParentId(Long parentId) {
        return operationUnitDao.findListByParentId(parentId);
    }

    @Override
    public OperationUnit findOperationUnitById(Long id) {
        return operationUnitDao.findById(id);
    }
}
