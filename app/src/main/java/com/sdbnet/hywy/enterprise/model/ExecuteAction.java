package com.sdbnet.hywy.enterprise.model;

import java.io.Serializable;

public class ExecuteAction implements Serializable {

	private static final long serialVersionUID = 6499019604661603285L;
	
	private String actidx; // 动作序号
	private String action; // 动作码
	private String actname; // 动作名称
	private String actmemo; // 动作说明
	private String lineno; // 轨迹编号
	private String linename; // 轨迹名称
	private String sign; // 是否更新到订单状态
	private String btnname; // 按钮名称
	private String workflow; // 工作流程
	private String startnode; // 是否为起始节点
	private String iscall; // 呼叫权限
	private String islocate; // 定位权限
	private String isscan; // 扫描权限
	
	public String getIscall() {
		return iscall;
	}

	public void setIscall(String iscall) {
		this.iscall = iscall;
	}

	public String getIslocate() {
		return islocate;
	}

	public void setIslocate(String islocate) {
		this.islocate = islocate;
	}

	public String getIsscan() {
		return isscan;
	}

	public void setIsscan(String isscan) {
		this.isscan = isscan;
	}

	public String getBtnname() {
		return btnname;
	}

	public void setBtnname(String btnname) {
		this.btnname = btnname;
	}

	public String getWorkflow() {
		return workflow;
	}

	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}

	public String getStartnode() {
		return startnode;
	}

	public void setStartnode(String startnode) {
		this.startnode = startnode;
	}
	
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getActype() {
		return actype;
	}

	public void setActype(String actype) {
		this.actype = actype;
	}

	private String actype;

	public String getActidx() {
		return actidx;
	}

	public void setActidx(String actidx) {
		this.actidx = actidx;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActname() {
		return actname;
	}

	public void setActname(String actname) {
		this.actname = actname;
	}

	public String getActmemo() {
		return actmemo;
	}

	public void setActmemo(String actmemo) {
		this.actmemo = actmemo;
	}

	public String getLineno() {
		return lineno;
	}

	public void setLineno(String lineno) {
		this.lineno = lineno;
	}

	public String getLinename() {
		return linename;
	}

	public void setLinename(String linename) {
		this.linename = linename;
	}

}
