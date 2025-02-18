/*
 *Project: ANJBO Generator
 ****************************************************************
 * 版权所有@2018 ANJBO.COM  保留所有权利.
 ***************************************************************/
package com.anjbo.bean.order;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import com.anjbo.bean.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @Author ANJBO Generator 
 * @Date 2018-07-03 20:14:46
 * @version 1.0
 */
@ApiModel(value = "id")
public class BaseHouseLendingDto extends BaseDto{
	
	private static final long serialVersionUID = 1L;
		

	/** id */
	@ApiModelProperty(value = "id")
	private java.lang.Long id;
	
	/** 订单编号 */
	@ApiModelProperty(value = "订单编号")
	private java.lang.String orderNo;
	
	/** 放款银行 */
	@ApiModelProperty(value = "放款银行")
	private java.lang.Integer lendingBankId;
	
	/** 放款银支行 */
	@ApiModelProperty(value = "放款银支行")
	private java.lang.Integer lendingBankBranchId;
	
	/** 放款卡照片 */
	@ApiModelProperty(value = "放款卡照片")
	private java.lang.String lendingImgs;
	
	/** 银行卡账号 */
	@ApiModelProperty(value = "银行卡账号")
	private java.lang.String bankAccount;
	
	/** 银行卡户名 */
	@ApiModelProperty(value = "银行卡户名")
	private java.lang.String bankUserName;
	
	/** 备注 */
	@ApiModelProperty(value = "备注")
	private java.lang.String remark;
	
	/** 是否完成1:是,2:否 */
	@ApiModelProperty(value = "是否完成1:是,2:否")
	private java.lang.Integer isFinish;
	
	/** 放款银行名称 */
	@ApiModelProperty(value = "放款银行名称")
	private java.lang.String lendingBankName;
	
	/** 放款支行名称 */
	@ApiModelProperty(value = "放款支行名称")
	private java.lang.String lendingBankBranchName;
	
	/**放款卡银行预留手机号*/
	private String lendingPhoneNumber;
	/**回款银行*/
	private Integer paymentBankId;
	/**回款银行支行*/
	private Integer paymentBankBranchId;
	/**回款银行名称*/
	private String paymentBankName;
	/**回款支行名称*/
	private String paymentBankBranchName;
	/**回款卡照片*/
	private String paymentImgs;
	/**回款银行卡账号*/
	private String paymentBankAccount;
	/**持卡人姓名*/
	private String paymentBankUserName;
	/**回款卡银行预留手机号*/
	private String paymentPhoneNumber;
	/**回款卡备注*/
	private String paymentRemark;
	

	public void setId(java.lang.Long value) {
		this.id = value;
	}	
	public java.lang.Long getId() {
		return this.id;
	}
	public void setOrderNo(java.lang.String value) {
		this.orderNo = value;
	}	
	public java.lang.String getOrderNo() {
		return this.orderNo;
	}
	public Integer getPaymentBankId() {
		return paymentBankId;
	}
	public void setPaymentBankId(Integer paymentBankId) {
		this.paymentBankId = paymentBankId;
	}
	public Integer getPaymentBankBranchId() {
		return paymentBankBranchId;
	}
	public void setPaymentBankBranchId(Integer paymentBankBranchId) {
		this.paymentBankBranchId = paymentBankBranchId;
	}
	public String getPaymentBankName() {
		return paymentBankName;
	}
	public void setPaymentBankName(String paymentBankName) {
		this.paymentBankName = paymentBankName;
	}
	public String getPaymentBankBranchName() {
		return paymentBankBranchName;
	}
	public void setPaymentBankBranchName(String paymentBankBranchName) {
		this.paymentBankBranchName = paymentBankBranchName;
	}
	public String getPaymentImgs() {
		return paymentImgs;
	}
	public void setPaymentImgs(String paymentImgs) {
		this.paymentImgs = paymentImgs;
	}
	public String getPaymentBankAccount() {
		return paymentBankAccount;
	}
	public void setPaymentBankAccount(String paymentBankAccount) {
		this.paymentBankAccount = paymentBankAccount;
	}
	public String getPaymentBankUserName() {
		return paymentBankUserName;
	}
	public void setPaymentBankUserName(String paymentBankUserName) {
		this.paymentBankUserName = paymentBankUserName;
	}
	public String getPaymentPhoneNumber() {
		return paymentPhoneNumber;
	}
	public void setPaymentPhoneNumber(String paymentPhoneNumber) {
		this.paymentPhoneNumber = paymentPhoneNumber;
	}
	public String getPaymentRemark() {
		return paymentRemark;
	}
	public void setPaymentRemark(String paymentRemark) {
		this.paymentRemark = paymentRemark;
	}
	public void setLendingBankId(java.lang.Integer value) {
		this.lendingBankId = value;
	}	
	public java.lang.Integer getLendingBankId() {
		return this.lendingBankId;
	}
	public void setLendingBankBranchId(java.lang.Integer value) {
		this.lendingBankBranchId = value;
	}	
	public java.lang.Integer getLendingBankBranchId() {
		return this.lendingBankBranchId;
	}
	public void setLendingImgs(java.lang.String value) {
		this.lendingImgs = value;
	}	
	public java.lang.String getLendingImgs() {
		return this.lendingImgs;
	}
	public void setBankAccount(java.lang.String value) {
		this.bankAccount = value;
	}	
	public java.lang.String getBankAccount() {
		return this.bankAccount;
	}
	public void setBankUserName(java.lang.String value) {
		this.bankUserName = value;
	}	
	public java.lang.String getBankUserName() {
		return this.bankUserName;
	}
	public void setRemark(java.lang.String value) {
		this.remark = value;
	}	
	public java.lang.String getRemark() {
		return this.remark;
	}
	public void setIsFinish(java.lang.Integer value) {
		this.isFinish = value;
	}	
	public java.lang.Integer getIsFinish() {
		return this.isFinish;
	}
	public void setLendingBankName(java.lang.String value) {
		this.lendingBankName = value;
	}	
	public java.lang.String getLendingBankName() {
		return this.lendingBankName;
	}
	public void setLendingBankBranchName(java.lang.String value) {
		this.lendingBankBranchName = value;
	}	
	public java.lang.String getLendingBankBranchName() {
		return this.lendingBankBranchName;
	}

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("id",getId())
			.append("orderNo",getOrderNo())
			.append("lendingBankId",getLendingBankId())
			.append("lendingBankBranchId",getLendingBankBranchId())
			.append("lendingImgs",getLendingImgs())
			.append("bankAccount",getBankAccount())
			.append("bankUserName",getBankUserName())
			.append("remark",getRemark())
			.append("isFinish",getIsFinish())
			.append("lendingBankName",getLendingBankName())
			.append("lendingBankBranchName",getLendingBankBranchName())
			.toString();
	}
	public String getLendingPhoneNumber() {
		return lendingPhoneNumber;
	}
	public void setLendingPhoneNumber(String lendingPhoneNumber) {
		this.lendingPhoneNumber = lendingPhoneNumber;
	}
}

