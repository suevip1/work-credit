/*
 *Project: ANJBO Generator
 ****************************************************************
 * 版权所有@2018 ANJBO.COM  保留所有权利.
 ***************************************************************/
package com.anjbo.controller.contract;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.anjbo.bean.contract.FieldGroupDto;
import com.anjbo.common.RespData;
import com.anjbo.common.RespDataObject;
import com.anjbo.common.RespPageData;
import com.anjbo.common.RespStatus;

/**
 * 
 * @Author ANJBO Generator 
 * @Date 2018-09-20 12:22:21
 * @version 1.0
 */
@Api(value = "字段分组相关")
@RequestMapping("/fieldGroup/v")
public interface IFieldGroupController {

	@ApiOperation(value = "分页查询", httpMethod = "POST" ,response = FieldGroupDto.class)
	@RequestMapping(value = "/page")
	public abstract RespPageData<FieldGroupDto> page(@RequestBody FieldGroupDto dto);

	@ApiOperation(value = "查询", httpMethod = "POST" ,response = FieldGroupDto.class)
	@RequestMapping(value = "search", method= {RequestMethod.POST})
	public abstract RespData<FieldGroupDto> search(@RequestBody FieldGroupDto dto);

	@ApiOperation(value = "查找", httpMethod = "POST" ,response = FieldGroupDto.class)
	@RequestMapping(value = "find", method= {RequestMethod.POST})
	public abstract RespDataObject<FieldGroupDto> find(@RequestBody FieldGroupDto dto);

	@ApiOperation(value = "新增", httpMethod = "POST" ,response = FieldGroupDto.class)
	@RequestMapping(value = "add", method= {RequestMethod.POST})
	public abstract RespDataObject<FieldGroupDto> add(@RequestBody FieldGroupDto dto);

	@ApiOperation(value = "编辑", httpMethod = "POST" ,response = FieldGroupDto.class)
	@RequestMapping(value = "edit", method= {RequestMethod.POST})
	public abstract RespStatus edit(@RequestBody FieldGroupDto dto);

	@ApiOperation(value = "删除", httpMethod = "POST" ,response = FieldGroupDto.class)
	@RequestMapping(value = "delete", method= {RequestMethod.POST})
	public abstract RespStatus delete(@RequestBody FieldGroupDto dto);
		
}