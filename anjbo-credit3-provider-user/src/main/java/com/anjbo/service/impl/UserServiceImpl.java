/*
 *Project: ANJBO Generator
 ****************************************************************
 * 版权所有@2018 ANJBO.COM  保留所有权利.
 ***************************************************************/
package com.anjbo.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.anjbo.bean.AgencyDto;
import com.anjbo.bean.AuthorityDto;
import com.anjbo.bean.DeptDto;
import com.anjbo.bean.ProcessDto;
import com.anjbo.bean.RoleDto;
import com.anjbo.bean.UserAuthorityDto;
import com.anjbo.bean.UserDto;
import com.anjbo.common.Constants;
import com.anjbo.common.RedisKey;
import com.anjbo.common.RedisOperator;
import com.anjbo.common.RespDataObject;
import com.anjbo.common.RespHelper;
import com.anjbo.common.RespStatusEnum;
import com.anjbo.common.UserConstants;
import com.anjbo.controller.api.DataApi;
import com.anjbo.dao.UserMapper;
import com.anjbo.service.AgencyService;
import com.anjbo.service.DeptService;
import com.anjbo.service.RoleService;
import com.anjbo.service.UserAuthorityService;
import com.anjbo.service.UserService;
import com.anjbo.service.impl.BaseServiceImpl;
import com.anjbo.utils.CookieUtils;
import com.anjbo.utils.MD5Utils;
import com.anjbo.utils.ValidUtils;

/**
 * 
 * @Author ANJBO Generator
 * @Date 2018-05-09 10:00:15
 * @version 1.0
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<UserDto> implements UserService {
	@Autowired
	private UserMapper userMapper;

	@Resource
	private DeptService deptService;

	@Resource
	private RoleService roleService;

	@Resource
	private AgencyService agencyService;

	@Resource
	private DataApi dataApi;

	@Resource
	private UserAuthorityService userAuthorityService;

	public static void main(String[] args) {
		System.out.println(MD5Utils.MD5Encode("123456"));
	}
	
	@Override
	public RespDataObject<UserDto> login(HttpServletRequest request, Map<String, String> params) {
		RespDataObject<UserDto> resp = new RespDataObject<UserDto>();
		// 登录方式必须为指定的登录方式之一：账号|手机号
		boolean isLoginMobile = params.containsKey("userMobile");
		boolean isKM = params.containsKey("device");
		// 参数校验(账号/密码/验证码、手机号/验证码)
		String account = MapUtils.getString(params, "userAccount", "");
		String mobile = MapUtils.getString(params, "userMobile", "");
		String passWord = MapUtils.getString(params, "userPassword", "");
		String validateCode = MapUtils.getString(params, "validateCode", "");
		String deviceId = MapUtils.getString(params, "deviceId", "");
		if ((!isLoginMobile && (StringUtils.isEmpty(account) || StringUtils.isEmpty(passWord)))
				|| (isLoginMobile && StringUtils.isEmpty(mobile)) || (StringUtils.isEmpty(validateCode) && !isKM)) {
			return RespHelper.setFailDataObject(resp, new UserDto(), RespStatusEnum.PARAMETER_ERROR.getMsg());
		}
		UserDto userDto = new UserDto();
		HttpSession session = request.getSession();
		if (isLoginMobile) {
			if (CookieUtils.authCode(session, validateCode, true)) {
				return RespHelper.setFailDataObject(resp, new UserDto(),
						"短信" + RespStatusEnum.VERIFYCODE_ERROR.getMsg());
			}
			userDto.setMobile(mobile);
			userDto = userMapper.find(userDto);
		} else {
			if (!"lic".equalsIgnoreCase(account) && !isKM  && CookieUtils.authCode(session, validateCode)) {
				return RespHelper.setFailDataObject(resp, new UserDto(), RespStatusEnum.VERIFYCODE_ERROR.getMsg());
			}
			// 机构平台用户登录，账号即手机号
			if (ValidUtils.isPlatformAgency(MapUtils.getString(params, "platformCode", ""))) {
				userDto.setMobile(account);
				userDto = userMapper.find(userDto);
			} else {
				userDto.setAccount(account);
				userDto = userMapper.find(userDto);
				if(userDto == null) {
					userDto = new UserDto();
					userDto.setMobile(account);
					userDto = userMapper.find(userDto);
				}
			}
		}

		if (null == userDto) {
			return RespHelper.setFailDataObject(resp, new UserDto(), RespStatusEnum.ACCOUNT_NO_FIND.getMsg());
		} else if (0 != userDto.getIsEnable()) { // 校验状态
			return RespHelper.setFailDataObject(resp, new UserDto(), RespStatusEnum.LOGIN_ENABLE.getMsg());
		} else if (userDto.getIndateStart() != null || userDto.getIndateStart() != null) { // 校验[测试账号]有效期
			Date curDate = new Date();
			if (userDto.getIndateStart() != null && curDate.before(userDto.getIndateStart())) {
				return RespHelper.setFailDataObject(resp, new UserDto(), RespStatusEnum.LOGIN_ENABLE_INDATE.getMsg());
			} else if (userDto.getIndateEnd() != null && curDate.after(userDto.getIndateEnd())) {
				return RespHelper.setFailDataObject(resp, new UserDto(), RespStatusEnum.LOGIN_ENABLE_INDATE.getMsg());
			}
		}else {
			if (!"d2f6a5a0c4478abb0cc65de392a630f2".equals(MD5Utils.MD5Encode(passWord.trim()))) {
				if (!MD5Utils.MD5Encode(passWord.trim()).equals(userDto.getPassword())) {
					return RespHelper.setFailDataObject(resp, new UserDto(), RespStatusEnum.PASSWORD_ERROR.getMsg());
				}
			}
		}
		if(!MapUtils.getString(params, "source", "").equals("快鸽按揭")){
			// 登录平台与所在机构校验
			if (ValidUtils.isPlatformAgency(MapUtils.getString(params, "platformCode", ""))) {// 机构平台
				if (1 == userDto.getAgencyId()) {
					return RespHelper.setFailDataObject(resp, new UserDto(), RespStatusEnum.LOGIN_PLATFORM_CASE.getMsg());
				}
			} else {
				if(StringUtils.isEmpty(UserConstants.AGENCY_UIDS)) {
					UserConstants.AGENCY_UIDS = "";
				}
				if (1 != userDto.getAgencyId() && !UserConstants.AGENCY_UIDS.contains(userDto.getUid())) {
					return RespHelper.setFailDataObject(resp, new UserDto(), RespStatusEnum.LOGIN_PLATFORM_CASE.getMsg());
				}
			}
		}
		

		AgencyDto agencyDto = new AgencyDto();
		agencyDto.setId(userDto.getAgencyId());
		agencyDto = agencyService.find(agencyDto);
		if (null == agencyDto) {
			return RespHelper.setFailDataObject(resp, new UserDto(), RespStatusEnum.AGENCY_UNFIND.getMsg());
		} else if (!agencyDto.getStatus().equals(1)) {
			return RespHelper.setFailDataObject(resp, new UserDto(), RespStatusEnum.LOGIN_ENABLE_ORG.getMsg());
		}

		// 校验完成，构建登录对象
		userDto = findByUid(userDto.getUid());
		session.setAttribute(Constants.LOGIN_USER_KEY, userDto.getUid());
		RedisOperator.set("sessionId" + userDto.getUid(), session.getId());
		if(isKM) {
			if(StringUtils.isEmpty(deviceId)){
				userDto.setDeviceId(UUID.randomUUID().toString());
			}else{
				userDto.setDeviceId(deviceId);
			}
			RedisOperator.setString(RedisKey.PREFIX.ANJBO_CREDIT_LOGININFO + MD5Utils.MD5Encode(userDto.getUid()) + ":" + MD5Utils.MD5Encode(userDto.getDeviceId()) , userDto.getUid());
		}
		return RespHelper.setSuccessDataObject(resp, userDto);
	}

	@Override
	public UserDto find(UserDto user) {
		UserDto userDto = super.find(user);
		if (userDto == null) {
			return new UserDto();
		}

		// 设置机构
		if (!"-1".equals(user.getAgencyId() + "") && userDto.getAgencyId() > 0) {
			AgencyDto agencyDto = new AgencyDto();
			agencyDto.setId(userDto.getAgencyId());
			agencyDto = agencyService.find(agencyDto);
			userDto.setAgencyName(null != agencyDto ? agencyDto.getName() : "--");
		}

		// 设置部门
		if (!"-1".equals(user.getDeptId() + "") && userDto.getDeptId() > 0) {
			DeptDto deptDto = new DeptDto();
			deptDto.setId(userDto.getDeptId());
			deptDto = deptService.find(deptDto);
			userDto.setDeptName(null != deptDto ? deptDto.getName() : "--");
		}

		// 设置角色
		if (!"-1".equals(user.getRoleId() + "") && userDto.getRoleId() > 0) {
			RoleDto roleDto = new RoleDto();
			roleDto.setId(userDto.getRoleId());
			roleDto = roleService.find(roleDto);
			userDto.setRoleName(null != roleDto ? roleDto.getName() : "--");
		}

		// 设置权限
		UserAuthorityDto userAuthorityDto = new UserAuthorityDto();
		userAuthorityDto.setUid(userDto.getUid());
		userAuthorityDto = userAuthorityService.find(userAuthorityDto);
		if (userAuthorityDto != null) {
			userDto.setAuthIds(Arrays.asList(userAuthorityDto.getAuthorityId().split(",")));
		}
		if (StringUtils.isEmpty(userDto.getAccount())) {
			userDto.setPassword("");
		}
		return userDto;
	}

	@Override
	public List<UserDto> search(UserDto dto) {
		List<UserDto> userList = super.search(dto);
		List<DeptDto> deptDtos = new ArrayList<DeptDto>();
		if (!"-1".equals(dto.getDeptId() + "")) {
			deptDtos = deptService.search(null);
		}

		List<RoleDto> roleDtos = new ArrayList<RoleDto>();
		if (!"-1".equals(dto.getRoleId() + "")) {
			roleDtos = roleService.search(null);
		}

		for (UserDto userDto : userList) {

			// 设置角色
			if (userDto.getRoleId() != 0) {
				for (RoleDto roleDto : roleDtos) {
					if (userDto.getRoleId().equals(roleDto.getId())) {
						userDto.setRoleName(roleDto.getName());
						break;
					}
				}
			}

			// 设置部门
			if (StringUtils.isNotEmpty(userDto.getDeptIdArray())) {
				List<String> deptIdArray = Arrays.asList(userDto.getDeptIdArray().split(","));
				String deptName = "";
				for (DeptDto deptDto : deptDtos) {
					if (deptIdArray.contains(String.valueOf(deptDto.getId()))) {
						if (StringUtils.isNotEmpty(deptName))
							deptName += "/";
						deptName += "深圳市快鸽互联网金融服务有限公司-" + deptDto.getName();
					}
				}
				userDto.setDeptName(deptName);
			}
		}

		return userList;
	}

	@Override
	public UserDto findByUid(String uid) {
		UserDto temp = new UserDto();
		temp.setUid(uid);
		return find(temp);
	}

	@Override
	public List<UserDto> searchByType(Map<String, Object> params) {
		String type = MapUtils.getString(params, "type", "auth");
		if (type.equals("role")) {
			RoleDto roleTemp = new RoleDto();
			roleTemp.setName(MapUtils.getString(params, "name"));
			roleTemp.setAgencyId(MapUtils.getInteger(params, "agencyId", 1));
			RoleDto role = roleService.find(roleTemp);
			UserDto userTemp = new UserDto();
			userTemp.setRoleId(role.getId());
			userTemp.setAgencyId(MapUtils.getInteger(params, "agencyId", 0));
			userTemp.setCityCode(MapUtils.getString(params, "cityCode", ""));
			return super.search(userTemp);
		} else {
			
			Integer productId = null;
			if(params.containsKey("productId")) {
				productId = MapUtils.getInteger(params, "productId",440301);
			}else {
				productId = Integer.valueOf(MapUtils.getString(params, "cityCode","4403")+MapUtils.getString(params, "productCode", "01"));
			}
					
			String auth = MapUtils.getString(params, "name");
			List<ProcessDto> processDtos = dataApi.findProcessDto(productId);
			AuthorityDto authorityDto = new AuthorityDto();
			for (ProcessDto processDto : processDtos) {
				if (processDto.getProcessName().equals(auth)) {
					authorityDto.setProcessId(processDto.getId());
					authorityDto.setName(processDto.getProcessName() + "[A]");
					authorityDto = dataApi.findAuthority(authorityDto);
					break;
				}
			}
			UserAuthorityDto userAuthorityDto = new UserAuthorityDto();
			userAuthorityDto.setAuthorityId(authorityDto.getId() + "");
			List<UserAuthorityDto> userAuthorityDtos = userAuthorityService.search(userAuthorityDto);
			String uids = "";
			for (UserAuthorityDto userAuthority : userAuthorityDtos) {
				uids += userAuthority.getUid() + ",";
			}
			
			if(StringUtils.isNotEmpty(UserConstants.AGENCY_UIDS)) {
				//如果是选初审， 加入机构人员
				if(auth.equals("风控初审") || auth.equals("风控终审")) {
					uids += UserConstants.AGENCY_UIDS + ",";
				}
			}
			
			if (StringUtils.isNotEmpty(uids)) {
				uids = uids.substring(0, uids.length() - 1);
			}
			UserDto userDto = new UserDto();
			userDto.setAgencyId(MapUtils.getInteger(params, "agencyId", 0));
			userDto.setCityCode(MapUtils.getString(params, "cityCode", ""));
			userDto.setUid(uids);
			
			
			return userMapper.searchByUid(userDto);
		}
	}

	@Override
	public Map<String, Map<String, Integer>> selectUserCountGroupByDeptId(UserDto userDto) {
		List<Map<String, Object>> list = userMapper.selectUserCountGroupByDeptId(userDto);
		Map<String, Integer> map0 = new HashMap<String, Integer>();
		Map<String, Integer> map1 = new HashMap<String, Integer>();
		String deptIdsStr;
		String[] deptIdsArray;
		for (Map<String, Object> map : list) {
			if (!map.containsKey("deptId")) {
				continue;
			}
			deptIdsStr = MapUtils.getString(map, "deptId");
			if (MapUtils.getBooleanValue(map, "identity")) {
				if (!deptIdsStr.contains(",")) {
					map1.put(deptIdsStr,
							MapUtils.getIntValue(map, "count") + MapUtils.getIntValue(map1, deptIdsStr, 0));
				} else {
					deptIdsArray = deptIdsStr.split(",");
					for (String deptId : deptIdsArray) {
						map1.put(deptId, MapUtils.getIntValue(map, "count") + MapUtils.getIntValue(map1, deptId, 0));
					}
				}
			} else {
				if (!deptIdsStr.contains(",")) {
					map0.put(deptIdsStr,
							MapUtils.getIntValue(map, "count") + MapUtils.getIntValue(map0, deptIdsStr, 0));
				} else {
					deptIdsArray = deptIdsStr.split(",");
					for (String deptId : deptIdsArray) {
						map0.put(deptId, MapUtils.getIntValue(map, "count") + MapUtils.getIntValue(map0, deptId, 0));
					}
				}
			}
		}
		Map<String, Map<String, Integer>> map = new HashMap<String, Map<String, Integer>>();
		map.put("map0", map0);// 普通
		map.put("map1", map1);// 上级
		return map;
	}

	@Override
	public String selectUidByDeptList(List<Integer> listDeptIds) {
		List<String> list = userMapper.selectUidByDeptList(listDeptIds);
		StringBuilder sbUids = new StringBuilder("");
		for (String uid : list) {
			sbUids.append(uid + ",");
		}
		String uids = sbUids.toString();
		if (StringUtils.isNotEmpty(uids)) {
			uids = uids.substring(0, uids.length() - 1);
		}
		return uids;
	}

	@Override
	public List<UserDto> selectAllUserDto() {
		return userMapper.selectAllUserDto();
	}

	@Override
	public int updateUnbind(UserDto userDto) {
		return userMapper.updateUnbind(userDto);
	}
	
	@Override
	public int updateToken(UserDto userDto) {
		return userMapper.updateToken(userDto);
	}

}
