/*
 *Project: ANJBO Generator
 ****************************************************************
 * 版权所有@2018 ANJBO.COM  保留所有权利.
 ***************************************************************/
package com.anjbo.dao.order;

import java.util.Map;

import com.anjbo.bean.order.BaseCustomerDto;
import com.anjbo.dao.BaseMapper;

/**
 * 
 * @Author ANJBO Generator 
 * @Date 2018-07-03 20:14:45
 * @version 1.0
 */
public interface BaseCustomerMapper extends BaseMapper<BaseCustomerDto>{
	Map<Object, String> allCustomerNos(String orderNo);
}
