package com.anjbo.bean.yntrust;

import com.anjbo.bean.BaseDto;

/**
 * 云南信托与快鸽关联信息
 */
public class YntrustMappingDto extends BaseDto {
    /**
     *
     *
     * This field corresponds to the database column tbl_third_yntrust_mapping.id
     *
     * @mbggenerated 2018-03-08
     */
    private Integer id;

    /**
     *快鸽订单号
     *
     * This field corresponds to the database column tbl_third_yntrust_mapping.orderNo
     *
     * @mbggenerated 2018-03-08
     */
    private String orderNo;

    /**
     *此次推送给云南信托对应全局编号
     *
     * This field corresponds to the database column tbl_third_yntrust_mapping.uniqueId
     *
     * @mbggenerated 2018-03-08
     */
    private String uniqueId;

    /**
     *推送状态
     *
     * This field corresponds to the database column tbl_third_yntrust_mapping.status
     *
     * @mbggenerated 2018-03-08
     */
    private Integer status;
    /**
     * 状态名称
     */
    private String statusName;
    /**
     * 云南信托产品编码
     */
    private String ynProductCode;
    /**
     * 云南信托产品编码名称
     */
    private String ynProductName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId == null ? null : uniqueId.trim();
    }

    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getYnProductCode() {
        return ynProductCode;
    }

    public void setYnProductCode(String ynProductCode) {
        this.ynProductCode = ynProductCode;
    }

    public String getYnProductName() {
        return ynProductName;
    }

    public void setYnProductName(String ynProductName) {
        this.ynProductName = ynProductName;
    }
}