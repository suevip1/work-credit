/*
 *Project: ANJBO Generator
 ****************************************************************
 * 版权所有@2018 ANJBO.COM  保留所有权利.
 ***************************************************************/
package com.anjbo.controller.impl.order;

import java.util.ArrayList;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.anjbo.controller.api.UserApi;
import com.anjbo.controller.BaseController;
import com.anjbo.bean.order.BaseHousePropertypeopleDto;
import com.anjbo.common.RespHelper;
import com.anjbo.common.RespData;
import com.anjbo.common.RespDataObject;
import com.anjbo.common.RespPageData;
import com.anjbo.common.RespStatus;
import com.anjbo.common.RespStatusEnum;
import com.anjbo.controller.order.IBaseHousePropertypeopleController;
import com.anjbo.service.order.BaseHousePropertypeopleService;

/**
 * 
 * @Author ANJBO Generator 
 * @Date 2018-07-03 20:14:46
 * @version 1.0
 */
@RestController
public class BaseHousePropertypeopleController extends BaseController implements IBaseHousePropertypeopleController{

	@Resource private BaseHousePropertypeopleService baseHousePropertypeopleService;
	
	@Resource private UserApi userApi;

	/**
	 * 分页查询
	 * @author Generator 
	 */
	@Override
	public RespPageData<BaseHousePropertypeopleDto> page(@RequestBody BaseHousePropertypeopleDto dto){
		RespPageData<BaseHousePropertypeopleDto> resp = new RespPageData<BaseHousePropertypeopleDto>();
		try {
			resp.setCode(RespStatusEnum.SUCCESS.getCode());
			resp.setMsg(RespStatusEnum.SUCCESS.getMsg());
			resp.setRows(baseHousePropertypeopleService.search(dto));
			resp.setTotal(baseHousePropertypeopleService.count(dto));
		}catch (Exception e) {
			logger.error("分页异常,参数："+dto.toString(), e);
			resp.setCode(RespStatusEnum.FAIL.getCode());
			resp.setMsg(RespStatusEnum.FAIL.getMsg());
		}
		return resp;
	}


	/**
	 * 查询
	 * @author Generator 
	 */
	@Override
	public RespData<BaseHousePropertypeopleDto> search(@RequestBody BaseHousePropertypeopleDto dto){ 
		RespData<BaseHousePropertypeopleDto> resp = new RespData<BaseHousePropertypeopleDto>();
		try {
			return RespHelper.setSuccessData(resp, baseHousePropertypeopleService.search(dto));
		}catch (Exception e) {
			logger.error("查询异常,参数："+dto.toString(), e);
			return RespHelper.setFailData(resp, new ArrayList<BaseHousePropertypeopleDto>(), RespStatusEnum.FAIL.getMsg());
		}
	}

	/**
	 * 查找
	 * @author Generator 
	 */
	@Override
	public RespDataObject<BaseHousePropertypeopleDto> find(@RequestBody BaseHousePropertypeopleDto dto){ 
		RespDataObject<BaseHousePropertypeopleDto> resp = new RespDataObject<BaseHousePropertypeopleDto>();
		try {
			return RespHelper.setSuccessDataObject(resp, baseHousePropertypeopleService.find(dto));
		}catch (Exception e) {
			logger.error("查找异常,参数："+dto.toString(), e);
			return RespHelper.setFailDataObject(resp, new BaseHousePropertypeopleDto(), RespStatusEnum.FAIL.getMsg());
		}
	}

	/**
	 * 新增
	 * @author Generator 
	 */
	@Override
	public RespDataObject<BaseHousePropertypeopleDto> add(@RequestBody BaseHousePropertypeopleDto dto){ 
		RespDataObject<BaseHousePropertypeopleDto> resp = new RespDataObject<BaseHousePropertypeopleDto>();
		try {
			dto.setCreateUid(userApi.getUserDto().getUid());
			return RespHelper.setSuccessDataObject(resp, baseHousePropertypeopleService.insert(dto));
		}catch (Exception e) {
			logger.error("新增异常,参数："+dto.toString(), e);
			return RespHelper.setFailDataObject(resp, new BaseHousePropertypeopleDto(), RespStatusEnum.FAIL.getMsg());
		}
	}

	/**
	 * 编辑
	 * @author Generator 
	 */
	@Override
	public RespStatus edit(@RequestBody BaseHousePropertypeopleDto dto){ 
		RespStatus resp = new RespStatus();
		try {
			dto.setUpdateUid(userApi.getUserDto().getUid());
			baseHousePropertypeopleService.update(dto);
			return RespHelper.setSuccessRespStatus(resp);
		}catch (Exception e) {
			logger.error("编辑异常,参数："+dto.toString(), e);
			return RespHelper.failRespStatus();
		}
	}

	/**
	 * 删除
	 * @author Generator 
	 */
	@Override
	public RespStatus delete(@RequestBody BaseHousePropertypeopleDto dto){ 
		RespStatus resp = new RespStatus();
		try {
			baseHousePropertypeopleService.delete(dto);
			return RespHelper.setSuccessRespStatus(resp);
		}catch (Exception e) {
			logger.error("删除异常,参数："+dto.toString(), e);
			return RespHelper.failRespStatus();
		}
	}
		
}