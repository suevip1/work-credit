/*
 *Project: ANJBO Generator
 ****************************************************************
 * 版权所有@2018 ANJBO.COM  保留所有权利.
 ***************************************************************/
package com.anjbo.controller.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.anjbo.controller.BaseController;
import com.anjbo.bean.AgencyDto;
import com.anjbo.bean.UserDto;
import com.anjbo.common.RespHelper;
import com.anjbo.common.Constants;
import com.anjbo.common.Enums;
import com.anjbo.common.RespData;
import com.anjbo.common.RespDataObject;
import com.anjbo.common.RespPageData;
import com.anjbo.common.RespStatus;
import com.anjbo.common.RespStatusEnum;
import com.anjbo.controller.IAgencyController;
import com.anjbo.service.AgencyService;
import com.anjbo.service.UserService;
import com.anjbo.utils.SingleUtils;
import com.anjbo.utils.StringUtil;

/**
 * 
 * @Author ANJBO Generator
 * @Date 2018-05-10 16:54:57
 * @version 1.0
 */
@RestController
public class AgencyController extends BaseController implements IAgencyController {

	@Resource
	private AgencyService agencyService;

	@Resource
	private UserService userService;

	/**
	 * 分页查询
	 * 
	 * @author Generator
	 */
	@Override
	public RespPageData<AgencyDto> page(@RequestBody AgencyDto dto) {
		RespPageData<AgencyDto> resp = new RespPageData<AgencyDto>();
		try {
			resp.setCode(RespStatusEnum.SUCCESS.getCode());
			resp.setMsg(RespStatusEnum.SUCCESS.getMsg());
			resp.setRows(agencyService.search(dto));
			resp.setTotal(agencyService.count(dto));
		} catch (Exception e) {
			logger.error("分页异常,参数：" + dto.toString(), e);
			resp.setCode(RespStatusEnum.FAIL.getCode());
			resp.setMsg(RespStatusEnum.FAIL.getMsg());
		}
		return resp;
	}

	/**
	 * 查询
	 * 
	 * @author Generator
	 */
	@Override
	public RespData<AgencyDto> search(@RequestBody AgencyDto dto) {
		RespData<AgencyDto> resp = new RespData<AgencyDto>();
		try {
			return RespHelper.setSuccessData(resp, agencyService.search(dto));
		} catch (Exception e) {
			logger.error("查询异常,参数：" + dto.toString(), e);
			return RespHelper.setFailData(resp, new ArrayList<AgencyDto>(), RespStatusEnum.FAIL.getMsg());
		}
	}
	
	/**
	 * 查询
	 * 
	 * @author Generator
	 */
	@Override
	public RespData<AgencyDto> search2(@RequestBody AgencyDto dto) {
		RespData<AgencyDto> resp = new RespData<AgencyDto>();
		try {
			List<AgencyDto> agencyDtos = agencyService.search(dto);
			boolean fl = false;
			for (AgencyDto agencyDto : agencyDtos) {
				if("四海恒通".equals(agencyDto.getName())) {
					fl = true;
				}
			}
			if(!fl) {
				AgencyDto agencyDto = new AgencyDto();
				agencyDto.setId(1);
				agencyDto.setAgencyCode(1000);
				agencyDto.setName("四海恒通");
				agencyDto.setSimName("四海恒通");
				agencyDto.setType(1);
				agencyDtos.add(0, agencyDto);
			}
			return RespHelper.setSuccessData(resp, agencyDtos);
		} catch (Exception e) {
			logger.error("查询异常,参数：" + dto.toString(), e);
			return RespHelper.setFailData(resp, new ArrayList<AgencyDto>(), RespStatusEnum.FAIL.getMsg());
		}
	}

	/**
	 * 查找
	 * 
	 * @author Generator
	 */
	@Override
	public RespDataObject<AgencyDto> find(@RequestBody AgencyDto dto) {
		RespDataObject<AgencyDto> resp = new RespDataObject<AgencyDto>();
		try {
			return RespHelper.setSuccessDataObject(resp, agencyService.find(dto));
		} catch (Exception e) {
			logger.error("查找异常,参数：" + dto.toString(), e);
			return RespHelper.setFailDataObject(resp, new AgencyDto(), RespStatusEnum.FAIL.getMsg());
		}
	}

	/**
	 * 新增
	 * 
	 * @author Generator
	 */
	@Override
	public RespDataObject<AgencyDto> add(@RequestBody AgencyDto dto) {
		RespDataObject<AgencyDto> resp = new RespDataObject<AgencyDto>();
		try {
			return RespHelper.setSuccessDataObject(resp, agencyService.insert(dto));
		} catch (Exception e) {
			logger.error("新增异常,参数：" + dto.toString(), e);
			return RespHelper.setFailDataObject(resp, new AgencyDto(), RespStatusEnum.FAIL.getMsg());
		}
	}

	/**
	 * 编辑
	 * 
	 * @author Generator
	 */
	@Override
	public RespStatus edit(@RequestBody AgencyDto dto) {
		RespStatus resp = new RespStatus();
		try {
			if (null == dto.getId()) {
				RespHelper.setFailRespStatus(resp, "更新机构信息缺少主键");
				return resp;
			}
			AgencyDto tmp = agencyService.find(dto);
			if (3 == dto.getSignStatus()) {
				if (1 == tmp.getStatus()) {
					RespHelper.setFailRespStatus(resp, "该机构的后台账号为解冻的状态，解除合作之前请先冻结该后台账号!");
					return resp;
				}
				if (Enums.AgencyEnum.KUAIGE.getAgencyId() == dto.getId()) {
					RespHelper.setFailRespStatus(resp, "抱歉该机构不能解除合作");
					return resp;
				}
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("agencyId", tmp.getId());
				param.put("agencyName", tmp.getName());
				param.put("agencyCode", tmp.getAgencyCode());
				param.put("agencyStatus", 3);
				resp = SingleUtils.getRestTemplate()
						.postForEntity(Constants.LINK_ANJBO_APP_URL + "/mortgage/agency/updateAgencyNotify", param,
								RespStatus.class)
						.getBody();
				logger.info("解除合作" + tmp.getName() + "更新快鸽APP用户机构状态===" + resp + "====");
				UserDto user = new UserDto();
				user.setAgencyId(dto.getId());
				userService.updateUnbind(user);

			} else if (null != dto.getStatus() && (0 == dto.getStatus() || 1 == dto.getStatus())) {
				if(Enums.AgencyEnum.KUAIGE.getAgencyId()==dto.getId()
						&&0==dto.getStatus()){
					RespHelper.setFailRespStatus(resp,"抱歉该机构不能被冻结");
					return resp;
				}
				Map<String,Object> param = new HashMap<String,Object>();
				param.put("agencyId",tmp.getId());
				param.put("agencyName",tmp.getName());
				param.put("agencyCode",tmp.getAgencyCode());
				param.put("agencyStatus",0==dto.getStatus()?1:0);
				int isEnable = 0;
				if(0==dto.getStatus()){
					isEnable = 1;
				}
				resp = SingleUtils.getRestTemplate()
						.postForEntity(Constants.LINK_ANJBO_APP_URL + "/mortgage/agency/updateAgencyNotify", param,
								RespStatus.class)
						.getBody();
				logger.info("冻结"+tmp.getName()+"更新快鸽APP用户机构状态==="+resp+"====");
				Map<String,Object> tmpAccount = new HashMap<String,Object>();
				tmpAccount.put("agencyId",tmp.getId());
				tmpAccount.put("isEnable",isEnable);
				tmpAccount.put("name",tmp.getContactMan());
				tmpAccount.put("mobile",tmp.getContactTel());
				tmpAccount.put("indateStart",null);
				tmpAccount.put("indateEnd",null);
				tmpAccount.put("uid",tmp.getAccountUid());
//				result = httpUtil.getRespStatus(Constants.LINK_CREDIT,"/credit/user/base/v/editAgentAdmin", tmpAccount);
//				log.info("冻结"+tmp.getName()+"同步用户更新机构状态:==="+request+"===");
			}
			agencyService.update(dto);
			return RespHelper.setSuccessRespStatus(resp);
		}  catch (Exception e){
			logger.error("更新机构信息异常:",e);
			if(StringUtil.isNotEmpty(e.getMessage()) &&
					e.getMessage().contains("MySQLIntegrityConstraintViolationException")){
				String msg = "该机构名称或简称已存在，请重新填写";
				if(e.getMessage().contains("agencyNameUnique")){
					msg = "该机构名称已存在，请重新填写";
				}else if(e.getMessage().contains("agencySimNameUnique")){
					msg = "该机构简称已存在，请重新填写";
				}
				return RespHelper.setFailRespStatus(resp,msg);
			}
			return RespHelper.failRespStatus();
		}
	}

	/**
	 * 删除
	 * 
	 * @author Generator
	 */
	@Override
	public RespStatus delete(@RequestBody AgencyDto dto) {
		RespStatus resp = new RespStatus();
		try {
			agencyService.delete(dto);
			return RespHelper.setSuccessRespStatus(resp);
		} catch (Exception e) {
			logger.error("删除异常,参数：" + dto.toString(), e);
			return RespHelper.failRespStatus();
		}
	}

}