package com.baozun.scm.primservice.whoperation.manager.auth;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;


public class OpUnitTreeCommand extends BaseCommand {

	private static final long serialVersionUID = 2463898548990062547L;

	private Long id;
    
	/** 组织编码 */
    private String code;

    /** 组织名称 */
    private String name;
    
    private String selectable;
    
    /** 组织类型 */
    private Long ouTypeId;

    /** 父组织 */
    private Long parentUnitId;
    
    private List<OpUnitTreeCommand> nodes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSelectable() {
		return selectable;
	}

	public void setSelectable(String selectable) {
		this.selectable = selectable;
	}

	public List<OpUnitTreeCommand> getNodes() {
		return nodes;
	}

	public void setNodes(List<OpUnitTreeCommand> nodes) {
		this.nodes = nodes;
	}

	public Long getOuTypeId() {
		return ouTypeId;
	}

	public void setOuTypeId(Long ouTypeId) {
		this.ouTypeId = ouTypeId;
	}

	public Long getParentUnitId() {
		return parentUnitId;
	}

	public void setParentUnitId(Long parentUnitId) {
		this.parentUnitId = parentUnitId;
	}

	@Override
	public String toString() {
		return "OpUnitTreeCommand [id=" + id + ", code=" + code + ", name="
				+ name + ", selectable=" + selectable + ", ouTypeId="
				+ ouTypeId + ", parentUnitId=" + parentUnitId + ", nodes="
				+ nodes + "]";
	}
}
