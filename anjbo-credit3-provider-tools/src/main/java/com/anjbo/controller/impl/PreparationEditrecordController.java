/*
 *Project: ANJBO Generator
 ****************************************************************
 * 版权所有@2018 ANJBO.COM  保留所有权利.
 ***************************************************************/
package com.anjbo.controller.impl;

import java.util.ArrayList;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.anjbo.controller.api.UserApi;
import com.anjbo.controller.BaseController;
import com.anjbo.bean.PreparationEditrecordDto;
import com.anjbo.common.RespHelper;
import com.anjbo.common.RespData;
import com.anjbo.common.RespDataObject;
import com.anjbo.common.RespPageData;
import com.anjbo.common.RespStatus;
import com.anjbo.common.RespStatusEnum;
import com.anjbo.controller.IPreparationEditrecordController;
import com.anjbo.service.PreparationEditrecordService;

/**
 * 
 * @Author ANJBO Generator 
 * @Date 2018-06-25 16:16:14
 * @version 1.0
 */
@RestController
public class PreparationEditrecordController extends BaseController implements IPreparationEditrecordController{

	@Resource private PreparationEditrecordService preparationEditrecordService;
	
	@Resource private UserApi userApi;

	/**
	 * 分页查询
	 * @author Generator 
	 */
	@Override
	public RespPageData<PreparationEditrecordDto> page(@RequestBody PreparationEditrecordDto dto){
		RespPageData<PreparationEditrecordDto> resp = new RespPageData<PreparationEditrecordDto>();
		try {
			resp.setCode(RespStatusEnum.SUCCESS.getCode());
			resp.setMsg(RespStatusEnum.SUCCESS.getMsg());
			resp.setRows(preparationEditrecordService.search(dto));
			resp.setTotal(preparationEditrecordService.count(dto));
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
	public RespData<PreparationEditrecordDto> search(@RequestBody PreparationEditrecordDto dto){ 
		RespData<PreparationEditrecordDto> resp = new RespData<PreparationEditrecordDto>();
		try {
			return RespHelper.setSuccessData(resp, preparationEditrecordService.search(dto));
		}catch (Exception e) {
			logger.error("查询异常,参数："+dto.toString(), e);
			return RespHelper.setFailData(resp, new ArrayList<PreparationEditrecordDto>(), RespStatusEnum.FAIL.getMsg());
		}
	}

	/**
	 * 查找
	 * @author Generator 
	 */
	@Override
	public RespDataObject<PreparationEditrecordDto> find(@RequestBody PreparationEditrecordDto dto){ 
		RespDataObject<PreparationEditrecordDto> resp = new RespDataObject<PreparationEditrecordDto>();
		try {
			return RespHelper.setSuccessDataObject(resp, preparationEditrecordService.find(dto));
		}catch (Exception e) {
			logger.error("查找异常,参数："+dto.toString(), e);
			return RespHelper.setFailDataObject(resp, new PreparationEditrecordDto(), RespStatusEnum.FAIL.getMsg());
		}
	}

	/**
	 * 新增
	 * @author Generator 
	 */
	@Override
	public RespDataObject<PreparationEditrecordDto> add(@RequestBody PreparationEditrecordDto dto){ 
		RespDataObject<PreparationEditrecordDto> resp = new RespDataObject<PreparationEditrecordDto>();
		try {
			dto.setCreateUid(userApi.getUserDto().getUid());
			return RespHelper.setSuccessDataObject(resp, preparationEditrecordService.insert(dto));
		}catch (Exception e) {
			logger.error("新增异常,参数："+dto.toString(), e);
			return RespHelper.setFailDataObject(resp, new PreparationEditrecordDto(), RespStatusEnum.FAIL.getMsg());
		}
	}

	/**
	 * 编辑
	 * @author Generator 
	 */
	@Override
	public RespStatus edit(@RequestBody PreparationEditrecordDto dto){ 
		RespStatus resp = new RespStatus();
		try {
			dto.setUpdateUid(userApi.getUserDto().getUid());
			preparationEditrecordService.update(dto);
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
	public RespStatus delete(@RequestBody PreparationEditrecordDto dto){ 
		RespStatus resp = new RespStatus();
		try {
			preparationEditrecordService.delete(dto);
			return RespHelper.setSuccessRespStatus(resp);
		}catch (Exception e) {
			logger.error("删除异常,参数："+dto.toString(), e);
			return RespHelper.failRespStatus();
		}
	}
		
}