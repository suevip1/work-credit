/*
 *Project: ANJBO Generator
 ****************************************************************
 * 版权所有@2018 ANJBO.COM  保留所有权利.
 ***************************************************************/
package com.anjbo.controller.impl.risk;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.anjbo.bean.FundCostDto;
import com.anjbo.bean.FundDto;
import com.anjbo.bean.ModelConfigDto;
import com.anjbo.bean.UserDto;
import com.anjbo.bean.order.BaseBorrowDto;
import com.anjbo.bean.order.BaseCustomerDto;
import com.anjbo.bean.order.BaseHouseDto;
import com.anjbo.bean.order.BaseListDto;
import com.anjbo.bean.order.FlowDto;
import com.anjbo.bean.risk.AllocationFundDto;
import com.anjbo.bean.risk.AuditFinalDto;
import com.anjbo.bean.risk.AuditOfficerDto;
import com.anjbo.bean.risk.CreditDto;
import com.anjbo.common.Constants;
import com.anjbo.common.Enums;
import com.anjbo.common.RespData;
import com.anjbo.common.RespDataObject;
import com.anjbo.common.RespHelper;
import com.anjbo.common.RespStatus;
import com.anjbo.common.RespStatusEnum;
import com.anjbo.common.SMSConstants;
import com.anjbo.controller.OrderBaseController;
import com.anjbo.controller.api.ToolsApi;
import com.anjbo.controller.api.UserApi;
import com.anjbo.controller.risk.IAllocationFundController;
import com.anjbo.service.order.BaseBorrowService;
import com.anjbo.service.order.BaseCustomerService;
import com.anjbo.service.order.BaseHouseService;
import com.anjbo.service.order.BaseListService;
import com.anjbo.service.risk.AllocationFundService;
import com.anjbo.service.risk.AuditFinalService;
import com.anjbo.service.risk.AuditOfficerService;
import com.anjbo.service.risk.CreditService;
import com.anjbo.utils.AmsUtil;
import com.anjbo.utils.BeanToMapUtil;
import com.anjbo.utils.IdcardUtils;

/**
 * 
 * @Author ANJBO Generator 
 * @Date 2018-07-03 19:44:31
 * @version 1.0
 */
@RestController
public class AllocationFundController extends OrderBaseController implements IAllocationFundController{
	
	@Resource private AllocationFundService allocationFundService;
	
	@Resource private UserApi userApi;
	@Resource private ToolsApi toolsApi;
	
	@Resource private BaseBorrowService baseBorrowService;
	
	@Resource private BaseCustomerService baseCustomerService;
	
	@Resource private BaseHouseService baseHouseService;
	
	@Resource private CreditService creditService;
	
	@Resource private BaseListService baseListService;
	
	@Resource private AuditOfficerService auditOfficerService;
	
	@Resource private AuditFinalService auditFinalService;
	
	/**
	 * 提交
	 * @author lic 
	 */
	@Override
	public RespStatus processSubmit(@RequestBody List<AllocationFundDto> fund) {
		RespStatus resp = new RespStatus();
		try {
			UserDto user = userApi.getUserDto();
//			dto.setCreateUid(userDto.getUid());
//			dto.setUpdateUid(userDto.getUid());
//			
//			if(null==fund||fund.size()<=0){
//				result.setMsg("请选择资金分配");
//				return result;
//			}
			AllocationFundDto f = fund.get(0);
			AllocationFundDto tempFund = new AllocationFundDto();
			tempFund.setOrderNo(f.getOrderNo());
//			boolean isSubmit = isSubmit(f.getOrderNo(),"allocationFund");
//			if(isSubmit){
//				result.setMsg("该订单已经被提交");
//				return result;
//			}
//			boolean isWithdraw = isWithdraw(f.getOrderNo(),"allocationFund");
//			if(isWithdraw){
//				result.setMsg("该订单已经被撤回");
//				return result;
//			}
			
			List<String> tmpList = new ArrayList<String>(); 
			List<AllocationFundDto> list = allocationFundService.search(tempFund);
			for(AllocationFundDto obj:fund){
				obj.setAuditTime(new Date());
				obj.setHandleUid(user.getUid());
				obj.setCreateUid(user.getUid());
				obj.setLoanDirectiveUid(f.getLoanDirectiveUid());
				if(f.getFinanceUid()==null){
					if(list!=null && list.size()>0){
						AllocationFundDto dto=list.get(0);
						obj.setFinanceUid(dto.getFinanceUid());
					}else{
						resp.setMsg("该订单未选择过财务处理人！");
						return resp;
					}
				}else{
					obj.setFinanceUid(f.getFinanceUid());
				}
				tmpList.add(obj.getFundCode());
			}

			String disableFund = isFundDisable(tmpList);
			if(StringUtils.isNotBlank(disableFund)){
				resp.setMsg("该"+disableFund+"资金方已被禁用!");
				return resp;
			}
			AllocationFundDto temp = new AllocationFundDto();
			temp.setOrderNo(f.getOrderNo());
			logger.info("删除历史资方记录："+allocationFundService.delete(temp));
			logger.info("批量插入资方记录："+allocationFundService.batchInsert(fund));

			FlowDto next = new FlowDto();
			next.setOrderNo(f.getOrderNo());
			next.setCurrentProcessId("allocationFund");
			next.setCurrentProcessName("推送金融机构");

			next.setHandleUid(user.getUid());
			next.setHandleName(user.getName());
			next.setUpdateUid(user.getUid());
			

				UserDto nextUser = null;
				BaseListDto listDto=new BaseListDto();
				listDto.setOrderNo(f.getOrderNo());
				listDto = baseListService.find(listDto);
				BaseBorrowDto orderBaseBorrowDto=new BaseBorrowDto();
				orderBaseBorrowDto.setOrderNo(f.getOrderNo());
				BaseBorrowDto baseBorrowDto = baseBorrowService.find(orderBaseBorrowDto);
				if(listDto.getAuditSort()==1||"06".equals(baseBorrowDto.getProductCode())||"07".equals(baseBorrowDto.getProductCode())){  //先审批再面签
					if("04".equals(baseBorrowDto.getProductCode())){ //房抵贷
						nextUser = userApi.findUserDtoByUid(baseBorrowDto.getFacesignUid());
						next.setNextProcessId("facesign");
						next.setNextProcessName("面签");
					}else if("06".equals(baseBorrowDto.getProductCode())) {
						nextUser = userApi.findUserDtoByUid(listDto.getAcceptMemberUid());
						next.setNextProcessId("applyLoan");
						next.setNextProcessName("申请放款");
					}else if("07".equals(baseBorrowDto.getProductCode())) {
						nextUser = userApi.findUserDtoByUid(user.getUid());
						next.setNextProcessId("fundDocking");
						next.setNextProcessName("资料推送");
					}else{
						nextUser = userApi.findUserDtoByUid(baseBorrowDto.getNotarialUid());
						next.setNextProcessId("notarization");
						next.setNextProcessName("公证");
						String ProductName="债务置换";
						if(baseBorrowDto!=null && !"01".equals(baseBorrowDto.getProductCode()) && !"02".equals(baseBorrowDto.getProductCode())){
							ProductName=baseBorrowDto.getProductName();
						}
					}
				}else{//先面签后审批
					if(3000 <= baseBorrowDto.getLoanAmount()){  //借款金额大于1000万 走法务
						next.setNextProcessId("auditJustice");
						next.setNextProcessName("法务审批");
						AuditOfficerDto officerAuditDto=new AuditOfficerDto();
						officerAuditDto.setOrderNo(f.getOrderNo());
						officerAuditDto = auditOfficerService.find(officerAuditDto);
						if(officerAuditDto!=null && null!=officerAuditDto.getJusticeUid()){
							nextUser = userApi.findUserDtoByUid(officerAuditDto.getJusticeUid());
						}else{
							AuditFinalDto finalAuditDto=new AuditFinalDto();
							finalAuditDto.setOrderNo(f.getOrderNo());
							finalAuditDto = auditFinalService.find(finalAuditDto);
							if(finalAuditDto!=null && (null !=finalAuditDto.getOfficerUid() && finalAuditDto.getOfficerUidType()==2)){
								nextUser = userApi.findUserDtoByUid(finalAuditDto.getOfficerUid());
							}else{
								resp.setCode(RespStatusEnum.FAIL.getCode());
								resp.setMsg("暂无法务处理人！");
								return resp;
							}
							
						}
					}else{
						//提放到要件校验
						if("05".equals(listDto.getProductCode())) {
							next.setNextProcessId("element");
							next.setNextProcessName("要件校验");
							nextUser = userApi.findUserDtoByUid(baseBorrowDto.getElementUid());
						}else {
							next.setNextProcessId("fundDocking");
							next.setNextProcessName("资料推送");
							nextUser = userApi.findUserDtoByUid(user.getUid());
						}
					}
				}
			
			BaseListDto orderListDto = new BaseListDto();
			orderListDto.setOrderNo(f.getOrderNo());
			orderListDto.setCurrentHandlerUid(nextUser.getUid());
			orderListDto.setCurrentHandler(nextUser.getName());
			goNextNode(next,orderListDto);
			return RespHelper.setSuccessRespStatus(resp);
		}catch (Exception e) {
			logger.error("提交异常,参数："+JSON.toJSONString(fund), e);
			return RespHelper.failRespStatus();
		}
	}
	/**
	 * 判断资金方是否被禁用
	 * @param fund
	 * @param http
	 * @return list 被禁用资金方
	 */
	public String isFundDisable(List<String> fund){
		FundDto fundDto = new FundDto();
		fundDto.setStatus(100);
		List<FundDto> list = userApi.selectFundListByStatus(fundDto);
		StringBuilder buf = new StringBuilder();
		for(FundDto tmp:list){
			if(tmp.getStatus()==0&&fund.contains(tmp.getFundCode())){
				buf.append(tmp.getFundCode()).append(",");
			}
		}
		return buf.toString();
	}
	/**
	 * 详情
	 * @author lic 
	 */
	@Override
	public RespData<AllocationFundDto> processDetails(@RequestBody AllocationFundDto dto) {
		RespData<AllocationFundDto> resp = new RespData<AllocationFundDto>();
		try {
//			if(StringUtils.isBlank(obj.getOrderNo())){
//				result.setMsg("加载资金方详情失败,订单编号不能为空!");
//				return result;
//			}
			List<AllocationFundDto> list = allocationFundService.search(dto);
			FundDto fundDto = new FundDto();
			for (AllocationFundDto allocationFundDto : list) {
				fundDto.setId(allocationFundDto.getFundId());
				FundDto findFundDto = userApi.findByFundId(fundDto);
				allocationFundDto.setFundCode(findFundDto.getFundCode());
				allocationFundDto.setFundDesc(findFundDto.getFundDesc());
			}
			resp.setCode(RespStatusEnum.SUCCESS.getCode());
			resp.setMsg(RespStatusEnum.SUCCESS.getMsg());
			return RespHelper.setSuccessData(resp, list);
		}catch (Exception e) {
			logger.error("详情异常,参数："+dto.toString(), e);
			return RespHelper.setFailData(resp, null, RespStatusEnum.FAIL.getMsg());
		}
	}

	/**
	 * 预匹配资方
	 * @param request
	 * @param obj
	 * @return
	 */
	@Override
	public RespDataObject<Map<String,Object>> preMatchedFund(@RequestBody AllocationFundDto dto){
		RespDataObject<Map<String,Object>> result = new RespDataObject<Map<String,Object>>();
		result.setCode(RespStatusEnum.FAIL.getCode());
		result.setMsg(RespStatusEnum.FAIL.getMsg());
		try{
			String orderNo = dto.getOrderNo();
			if(StringUtils.isBlank(orderNo)){
				result.setMsg("加载预匹配资方失败,订单编号不能为空!");
				return result;
			}
			result.setData(preMatchedFundInfo(orderNo));
			result.setCode(RespStatusEnum.SUCCESS.getCode());
			result.setMsg(RespStatusEnum.SUCCESS.getMsg());
		} catch (Exception e){
			logger.error("加载预匹配资方异常,异常信息为:",e);
		}
		return result;
	}
	
	private Map<String,Object> preMatchedFundInfo(String orderNo){
		//借款信息
		BaseBorrowDto borrow = new BaseBorrowDto();
		borrow.setOrderNo(orderNo);
		borrow = baseBorrowService.find(borrow);
		//客户信息
		BaseCustomerDto customer = new BaseCustomerDto();
		customer.setOrderNo(orderNo);
		customer = baseCustomerService.find(customer);
		//获取客户年龄
		String customerCardNumber = customer.getCustomerCardNumber();
		int age = IdcardUtils.getAgeByIdCard(customerCardNumber);
			
		//房产信息
		BaseHouseDto house = new BaseHouseDto();
		house.setOrderNo(orderNo);
		house = baseHouseService.find(house);
		//征信信息
		CreditDto credit = new CreditDto();
		credit.setOrderNo(orderNo);
		credit = creditService.find(credit);
		//获取资方业务模型集合
		ModelConfigDto modelConfigDto = new ModelConfigDto();
		List<ModelConfigDto> list = toolsApi.searchFundModel(modelConfigDto);
		//资方模型集合
		
		Map<Integer,Boolean> flagMap = new HashMap<Integer,Boolean>();
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i).getFundId()>=0&&list.get(i).getState()==1) {
				logger.info("资方id:"+list.get(i).getFundId());
				if(list.get(i).getExpression()==1) {
					String x="";
					if(list.get(i).getProject().equals(Enums.FundModelEnum.LOAN_AMOUNT.getName())) {
						x = String.valueOf(borrow.getLoanAmount());
					}else if(list.get(i).getProject().equals(Enums.FundModelEnum.CUSTOMER_AGE.getName())) {
						x = String.valueOf(age);
					}else if(list.get(i).getProject().equals(Enums.FundModelEnum.LATELY_HALF_YEAR_SELECT_NUMBER.getName())) {
						x = String.valueOf(credit.getLatelyHalfYearSelectNumber());
					}
					boolean flag = calculationExpression(list.get(i).getAuditFirstExpression(), x, list.get(i).getAuditFirstResult());
					if(flag&&(MapUtils.getBoolean(flagMap,list.get(i).getFundId())==null||MapUtils.getBoolean(flagMap,list.get(i).getFundId()))) {
						flagMap.put(list.get(i).getFundId(), true);
					}else if(!flag) {
						flagMap.put(list.get(i).getFundId(), false);
					}
				}else if(list.get(i).getExpression()==2) {
					String x="";
					if(list.get(i).getProject().equals(Enums.FundModelEnum.ORIGINAL_LOAN_BANK.getName())) {
						if(borrow.getIsOldLoanBank()==null) {
							x = "--";
						}else if(borrow.getIsOldLoanBank()==1) {
							x = "是";
						}else if(borrow.getIsOldLoanBank()==2) {
							x = "否";
						}
					}else if(list.get(i).getProject().equals(Enums.FundModelEnum.NEW_LOAN_BANK.getName())) {
						if(borrow.getIsLoanBank()==null) {
							x = "--";
						}else if(borrow.getIsLoanBank()==1) {
							x = "是";
						}else if(borrow.getIsLoanBank()==2) {
							x = "否";
						}
					}else if(list.get(i).getProject().equals(Enums.FundModelEnum.CUSTOMER_TYPE.getName())) {
						if(borrow.getCustomerType()==null) {
							x = "--";
						}else if(borrow.getCustomerType()==1) {
							x = "个人";
						}else if(borrow.getCustomerType()==2) {
							x = "小微企业";
						}
					}else if(list.get(i).getProject().equals(Enums.FundModelEnum.MORTGAGE_TYPE.getName())) {
						if(borrow.getMortgageType()==1) {
							x = "一次抵押";
						}else if(borrow.getMortgageType()==2) {
							x = "二次抵押";
						}else {
							x = "--";
						}
					}else if(list.get(i).getProject().equals(Enums.FundModelEnum.MORTGAGE_ISBANK.getName())) {
						if("04".equals(borrow.getProductCode())) {
							if(house.getMortgageIsBank()==null) {
								x = "--";
							}else if(house.getMortgageIsBank()==1) {
								x = "是";
							}else if(house.getMortgageIsBank()==2) {
								x = "否";
							}
						}else {
							x = "--";
						}
					}
					if((list.get(i).getAuditFirstResult().equals("不限")||list.get(i).getAuditFirstResult().equals(x))
							&&(MapUtils.getBoolean(flagMap,list.get(i).getFundId())==null||MapUtils.getBoolean(flagMap,list.get(i).getFundId()))) {
						flagMap.put(list.get(i).getFundId(), true);
					}else if(!list.get(i).getAuditFirstResult().equals("不限")&&!list.get(i).getAuditFirstResult().equals(x)) {
						flagMap.put(list.get(i).getFundId(), false);
					}
					if("--".equals(x)&&MapUtils.getBoolean(flagMap,list.get(i).getFundId())==null) {
						flagMap.put(list.get(i).getFundId(), true);
					}
				}else if(list.get(i).getExpression()==3) {
					String x="";
					if(list.get(i).getProject().equals(Enums.FundModelEnum.CITY.getName())) {
						x = String.valueOf(borrow.getCityName().substring(0,2));
					}else if(list.get(i).getProject().equals(Enums.FundModelEnum.PRODUCT_NAME.getName())) {
						x = String.valueOf(borrow.getProductName());
					}
					if(list.get(i).getAuditFirstResult().contains(x)&&
							(MapUtils.getBoolean(flagMap,list.get(i).getFundId())==null||MapUtils.getBoolean(flagMap,list.get(i).getFundId()))) {
						flagMap.put(list.get(i).getFundId(), true);
					}else if(!list.get(i).getAuditFirstResult().contains(x)){
						flagMap.put(list.get(i).getFundId(), false);
					}
				}
			}
		}
		FundDto fund = new FundDto();
		List<ModelConfigDto> preFundModelList = new ArrayList<ModelConfigDto>();
		List<Map<String,Object>> preFundModelMapList = new ArrayList<Map<String,Object>>();
		//匹配资金成本最小的资方
		double minDayRate = 100;
		int minDayRateFundId = 0;
		for(Map.Entry<Integer, Boolean> entry: flagMap.entrySet()) {
			if(entry.getValue()) {
				FundCostDto fundCostDto = new FundCostDto();
				fundCostDto.setFundId(entry.getKey());
				fundCostDto.setProductId(Integer.parseInt(borrow.getCityCode()+borrow.getProductCode()));
				fundCostDto = userApi.findFundCostByFindId(fundCostDto);
				if(minDayRateFundId==0&&(fundCostDto==null||fundCostDto.getDayRate()==null||fundCostDto.getDayRate()<=0)) {
					minDayRateFundId = entry.getKey();
				}else if(fundCostDto!=null&&fundCostDto.getDayRate()<minDayRate){
					minDayRate = fundCostDto.getDayRate();
					minDayRateFundId = fundCostDto.getFundId();
				}
			}
		}
		if(minDayRateFundId!=0) {
			//匹配到的资方
			fund.setId(minDayRateFundId);
			fund = 	userApi.findByFundId(fund);
			ModelConfigDto modelConfig = new ModelConfigDto();
			modelConfig.setState(1);
			modelConfig.setFundId(minDayRateFundId);
			preFundModelList = toolsApi.searchFundModel(modelConfig);
			for(ModelConfigDto model : preFundModelList) {
				//已启用的项进行对比
				if(model.getState()==2) {
					continue;
				}
				String x="";
				Map<String,Object> m = BeanToMapUtil.beanToMap(model);
				if(model.getProject().equals(Enums.FundModelEnum.LOAN_AMOUNT.getName())) {
					x= String.valueOf(borrow.getLoanAmount())+"万元";
				}else if(model.getProject().equals(Enums.FundModelEnum.CUSTOMER_AGE.getName())) {
					x= String.valueOf(age==0?"--":age);
				}else if(model.getProject().equals(Enums.FundModelEnum.LATELY_HALF_YEAR_SELECT_NUMBER.getName())) {
					x= String.valueOf(credit.getLatelyHalfYearSelectNumber());
				}else if(model.getProject().equals(Enums.FundModelEnum.ORIGINAL_LOAN_BANK.getName())) {
					if(borrow.getIsOldLoanBank()==null) {
						x= "--";
					}else if(borrow.getIsOldLoanBank()==1) {
						x= "是";
					}else if(borrow.getIsOldLoanBank()==2) {
						x= "否";
					}
				}else if(model.getProject().equals(Enums.FundModelEnum.NEW_LOAN_BANK.getName())) {
					if(borrow.getIsLoanBank()==null) {
						x= "--";
					}else if(borrow.getIsLoanBank()==1) {
						x= "是";
					}else if(borrow.getIsLoanBank()==2) {
						x= "否";
					}
				}else if(model.getProject().equals(Enums.FundModelEnum.CUSTOMER_TYPE.getName())) {
					if(borrow.getCustomerType()==null) {
						x= "--";
					}else if(borrow.getCustomerType()==1) {
						x= "个人";
					}else if(borrow.getCustomerType()==2) {
						x= "小微企业";
					}
				}else if(model.getProject().equals(Enums.FundModelEnum.MORTGAGE_TYPE.getName())) {
					if(borrow.getMortgageType()==1) {
						x = "一次抵押";
					}else if(borrow.getMortgageType()==2) {
						x = "二次抵押";
					}else {
						x = "--";
					}
				}else if(model.getProject().equals(Enums.FundModelEnum.MORTGAGE_ISBANK.getName())) {
					if("04".equals(borrow.getProductCode())) {
						if(house.getMortgageIsBank()==null) {
							x = "--";
						}else if(house.getMortgageIsBank()==1) {
							x = "是";
						}else if(house.getMortgageIsBank()==2) {
							x = "否";
						}
					}else {
						x = "--";
					}
				}else if(model.getProject().equals(Enums.FundModelEnum.CITY.getName())) {
					x = String.valueOf(borrow.getCityName().substring(0,2));
				}else if(model.getProject().equals(Enums.FundModelEnum.PRODUCT_NAME.getName())) {
					x = String.valueOf(borrow.getProductName());
				}
				//expression为1是，设置auditFirstResult值
				if(model.getExpression()==1) {
					String[] a = model.getAuditFirstResult().split(",");
					String auditFirstResult = model.getAuditFirstExpression().replace("{0}", a[0]);
					if(a.length>1) {
						auditFirstResult = auditFirstResult.replace("{1}", a[1]);
					}
					m.put("auditFirstResult", auditFirstResult);
				}
				m.put("orderInfo", x);
				preFundModelMapList.add(m);
			}
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("fundName", fund.getFundName());
		map.put("preFundModel", preFundModelMapList);
		return map;
	}
	
	/**
	 * 计算表达式
	 * @param Expression
	 * @param x
	 * @param result
	 * @return
	 */
	private boolean calculationExpression(String expression ,String x ,String result){
		String[] results = result.split(",");
		String[] expressions = expression.split("X");
		
		boolean fl = true;
		
		
		//x处于中间
		if(expressions.length > 1){
			
			if(expressions[0].contains("<")){
				
				fl = Integer.valueOf(results[0]) < Double.valueOf(x) ;
				
			}
			
			if(expressions[0].contains("≤")){

				fl = Integer.valueOf(results[0]) <= Double.valueOf(x) ;
				
			}
			
			if(expressions[1].contains("<")){
				
				fl = Double.valueOf(x) < Integer.valueOf(results[1])  ;
				
			}
			
			if(expressions[1].contains("≤")){

				fl = Double.valueOf(x) <= Integer.valueOf(results[1])   ;
				
			}
			
		}else{
			
			if(expression.contains("<")){
				
				fl = Integer.valueOf(result) < Double.valueOf(x) ;
				
			}
			
			if(expression.contains("≤")){

				fl = Integer.valueOf(result) <= Double.valueOf(x) ;
				
			}
			
			if(expression.contains("=")){

				fl = Double.valueOf(result) == Double.valueOf(x) ;
				
			}
			
		}
		
		return fl;
	}	
}