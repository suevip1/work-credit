/*
 *Project: ANJBO Generator
 ****************************************************************
 * 版权所有@2018 ANJBO.COM  保留所有权利.
 ***************************************************************/
package com.anjbo.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.anjbo.bean.ProductDto;
import com.anjbo.common.RespData;
import com.anjbo.common.RespDataObject;
import com.anjbo.common.RespPageData;
import com.anjbo.common.RespStatus;

/**
 * 
 * @Author ANJBO Generator 
 * @Date 2018-05-03 09:49:33
 * @version 1.0
 */
@Api(value = "产品相关")
@RequestMapping("/product/v")
public interface IProductController {

	@ApiOperation(value = "分页查询", httpMethod = "POST" ,response = ProductDto.class)
	@RequestMapping(value = "/page")
	public abstract RespPageData<ProductDto> page(@RequestBody ProductDto dto);

	@ApiOperation(value = "查询", httpMethod = "POST" ,response = ProductDto.class)
	@RequestMapping(value = "search", method= {RequestMethod.POST})
	public abstract RespData<ProductDto> search(@RequestBody ProductDto dto);

	@ApiOperation(value = "查找", httpMethod = "POST" ,response = ProductDto.class)
	@RequestMapping(value = "find", method= {RequestMethod.POST})
	public abstract RespDataObject<ProductDto> find(@RequestBody ProductDto dto);

	@ApiOperation(value = "新增", httpMethod = "POST" ,response = ProductDto.class)
	@RequestMapping(value = "add", method= {RequestMethod.POST})
	public abstract RespDataObject<ProductDto> add(@RequestBody ProductDto dto);

	@ApiOperation(value = "编辑", httpMethod = "POST" ,response = ProductDto.class)
	@RequestMapping(value = "edit", method= {RequestMethod.POST})
	public abstract RespStatus edit(@RequestBody ProductDto dto);
	
	
	
}