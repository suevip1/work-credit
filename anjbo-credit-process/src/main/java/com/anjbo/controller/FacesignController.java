package com.anjbo.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.anjbo.bean.order.OrderBaseBorrowDto;
import com.anjbo.bean.order.OrderFlowDto;
import com.anjbo.bean.order.OrderListDto;
import com.anjbo.bean.product.FaceSignDto;
import com.anjbo.bean.product.FacesignRecognitionDto;
import com.anjbo.bean.risk.AllocationFundDto;
import com.anjbo.bean.risk.FinalAuditDto;
import com.anjbo.bean.risk.OfficerAuditDto;
import com.anjbo.bean.user.UserDto;
import com.anjbo.common.Constants;
import com.anjbo.common.RespDataObject;
import com.anjbo.common.RespHelper;
import com.anjbo.common.RespStatus;
import com.anjbo.common.RespStatusEnum;
import com.anjbo.service.FacesignService;
import com.anjbo.service.ManagerAuditService;
import com.anjbo.service.NotarizationService;
import com.anjbo.utils.AmsUtil;
import com.anjbo.utils.CommonDataUtil;
import com.anjbo.utils.ConfigUtil;
import com.anjbo.utils.StringUtil;

/**
 * 面签
 * @author yis
 *
 */
@Controller
@RequestMapping("/credit/process/facesign/v")
public class FacesignController extends BaseController{

	@Resource FacesignService facesignService;
	
	@Resource NotarizationService notarizationService;

	@Resource ManagerAuditService managerAuditService;
	/**
	 * 详情
	 * @param request
	 * @param faceSignDto
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/detail") 
	public RespDataObject<FaceSignDto> detail(HttpServletRequest request,@RequestBody  FaceSignDto faceSignDto){
		 RespDataObject<FaceSignDto> resp=new RespDataObject<FaceSignDto>();
		 OrderBaseBorrowDto borrowDto=new OrderBaseBorrowDto();
		 borrowDto.setOrderNo(faceSignDto.getOrderNo());
		 RespDataObject<OrderBaseBorrowDto> obj=httpUtil.getRespDataObject(Constants.LINK_CREDIT, "/credit/order/borrowother/v/queryBorrow", borrowDto,OrderBaseBorrowDto.class);
		 OrderBaseBorrowDto baseBorrowDto=  obj.getData();
//		 if(baseBorrowDto!=null && baseBorrowDto.getIsChangLoan()==1){ //畅贷
//			String orderNo=getCreditOrderNo(faceSignDto.getOrderNo());
//			faceSignDto.setOrderNo(orderNo);
//		 }
		 FaceSignDto dto=facesignService.selectFacesign(faceSignDto);
		 if(dto==null ){
			 dto=new FaceSignDto();
			 dto.setOrderNo(faceSignDto.getOrderNo());
		 }
		 SimpleDateFormat format=new SimpleDateFormat("YYYY-MM-dd");
		 if(dto.getFaceSignTime()!=null)
			 dto.setFaceSignTimeStr(format.format(dto.getFaceSignTime()));
		 isFace(new RespStatus(),dto);
		 resp.setData(dto);
		 resp.setCode(RespStatusEnum.SUCCESS.getCode());
		 resp.setMsg(RespStatusEnum.SUCCESS.getMsg());
		 return resp;
	}
	
	/**
	 * 发送验证码
	 * @param request
	 * @param response
	 *//*
	@RequestMapping("/getValidPhoneCode")
	@ResponseBody
	public RespStatus getValidPhoneCode(HttpServletRequest request,
            HttpServletResponse response,@RequestBody FaceSignDto faceSignDto) {
		RespStatus resp = new RespStatus();
		if(StringUtil.isBlank(faceSignDto.getMobile())) {
			RespHelper.setFailRespStatus(resp, "手机号不能为空");
		}
		String ipWhite = ConfigUtil.getStringValue(Constants.BASE_AMS_IPWHITE,ConfigUtil.CONFIG_BASE);
		String key = Constants.SMS_FACESIGN_VERIFICATIONCODE;
		String verficationCode = getRanomNumCode(4);
		AmsUtil.smsSend(faceSignDto.getMobile(), ipWhite,key,verficationCode);
		request.getSession().setAttribute("facesign"+faceSignDto.getMobile(), verficationCode);
		RespHelper.setSuccessRespStatus(resp);
		return resp;
    }*/

	/**
     * 生成随机数字
     * @param 编码长度
     * @return String
     */
    public static String getRanomNumCode(int length) {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < length; i++) {

            result += random.nextInt(10);
        }
        return result;
    }
	
	/**
	 * 添加
	 * @param request
	 * @param response
	 * @return
	 */
	   @RequestMapping(value = "/add" ) 
		public @ResponseBody
		RespStatus add(HttpServletRequest request,
				HttpServletResponse response,@RequestBody FaceSignDto faceSignDto) {
		    RespStatus rd = new RespStatus();
			rd.setCode(RespStatusEnum.SUCCESS.getCode());
			rd.setMsg(RespStatusEnum.SUCCESS.getMsg());
			try {
				/*String sesVerficationCode = request.getSession().getAttribute("facesign"+faceSignDto.getMobile()).toString();
				if(!faceSignDto.getVerficationCode().equals(sesVerficationCode)) {
					rd.setMsg("您输入的验证码不正确!");
					return rd;
				}*/
				if(StringUtils.isBlank(faceSignDto.getOrderNo())){
					rd.setMsg("保存缺少订单编号!");
					return rd;
				}
				OrderListDto listDto=new OrderListDto();
				listDto.setOrderNo(faceSignDto.getOrderNo());
				RespDataObject<OrderListDto> listobj=httpUtil.getRespDataObject(Constants.LINK_CREDIT, "/credit/order/base/v/selectDetailByOrderNo", listDto,OrderListDto.class);
				listDto=listobj.getData();
				boolean isSubmit = isSubmit(faceSignDto.getOrderNo(),"facesign");
				if(isSubmit){
					rd.setMsg("该订单已经被提交");
					return rd;
				}
				boolean isWithdraw = isWithdraw(faceSignDto.getOrderNo(),"facesign");
				if(listDto.getAuditSort()==1 && isWithdraw){
					rd.setMsg("该订单已经被撤回");
					return rd;
				}
//				isFace(rd,faceSignDto);
//				if(RespStatusEnum.FAIL.getCode().equals(rd.getCode())){
//					return rd;
//				}
				UserDto dto=getUserDto(request);  //获取用户信息
				OrderBaseBorrowDto borrowDto=new OrderBaseBorrowDto();
				borrowDto.setOrderNo(faceSignDto.getOrderNo());
				RespDataObject<OrderBaseBorrowDto> obj=httpUtil.getRespDataObject(Constants.LINK_CREDIT, "/credit/order/borrowother/v/queryBorrow", borrowDto,OrderBaseBorrowDto.class);
				OrderBaseBorrowDto baseBorrowDto=  obj.getData();
				//面签加影像资料上传校验
				//先面签则不校验面签资料
				if(listDto.getAuditSort()==1){
					OrderBaseBorrowDto order = new OrderBaseBorrowDto();
					order.setOrderNo(faceSignDto.getOrderNo());
					RespStatus resp = httpUtil.getRespStatus(Constants.LINK_CREDIT, "/credit/order/businfo/v/faceBusinfoCheck",order);
					if(resp.getCode().equals(RespStatusEnum.FAIL.getCode())){
						return resp;
					}
				}
				faceSignDto.setCreateUid(dto.getUid());
				faceSignDto.setUpdateUid(dto.getUid());
				FaceSignDto signDto=facesignService.selectFacesign(faceSignDto);
				if(signDto==null){
					facesignService.addFacesign(faceSignDto);
				}else{
					facesignService.updateFacesign(faceSignDto);
				}
				OrderFlowDto orderFlowDto=new OrderFlowDto();  
				orderFlowDto.setOrderNo(faceSignDto.getOrderNo());
				orderFlowDto.setCurrentProcessId("facesign");
				orderFlowDto.setCurrentProcessName("面签");
				orderFlowDto.setHandleUid(dto.getUid());  //当前处理人
				orderFlowDto.setHandleName(dto.getName());

				OrderListDto orderListDto = new OrderListDto();
				orderListDto.setOrderNo(faceSignDto.getOrderNo());

				
				//V3.0.1版本   --V3.2版本
				if("04".equals(baseBorrowDto.getProductCode())){
					orderFlowDto.setNextProcessId("notarization");
					orderFlowDto.setNextProcessName("公证");
					UserDto nextUser = CommonDataUtil.getUserDtoByUidAndMobile(baseBorrowDto.getNotarialUid());
					orderListDto.setCurrentHandlerUid(nextUser.getUid()); 
					orderListDto.setCurrentHandler(nextUser.getName());
					OrderFlowDto flowDto = httpUtil.getObject(Constants.LINK_CREDIT, "/credit/order/flow/v/selectEndOrderFlow", orderFlowDto,OrderFlowDto.class);
					if(flowDto!=null&&flowDto.getIsNewWalkProcess()!=2){ //不重新走流程
						String ipWhite = ConfigUtil.getStringValue(Constants.BASE_AMS_IPWHITE,ConfigUtil.CONFIG_BASE); //ip
						String ProductName="债务置换";
						if(baseBorrowDto!=null && !"01".equals(baseBorrowDto.getProductCode()) && !"02".equals(baseBorrowDto.getProductCode())){
							ProductName=baseBorrowDto.getProductName();
						}
						//发送给公证操作人
						AmsUtil.smsSend(nextUser.getMobile(), ipWhite, Constants.SMS_TEMPLATE_PROCESS, ProductName,baseBorrowDto.getBorrowerName(),baseBorrowDto.getLoanAmount(),"公证");
					}
				}else{
					if(baseBorrowDto.getLoanAmount()!=null&&3000 <= baseBorrowDto.getLoanAmount() && listDto.getAuditSort()==1){  //借款金额大于1000万 走法务
						orderFlowDto.setNextProcessId("auditJustice");
						orderFlowDto.setNextProcessName("法务审批");
						OfficerAuditDto officerAuditDto=new OfficerAuditDto();
						officerAuditDto.setOrderNo(faceSignDto.getOrderNo());
						officerAuditDto = httpUtil.getObject(Constants.LINK_CREDIT, "/credit/risk/officer/v/detail" ,officerAuditDto, OfficerAuditDto.class);
						if(officerAuditDto!=null && null!=officerAuditDto.getJusticeUid()){
							UserDto nextUser=CommonDataUtil.getUserDtoByUidAndMobile(officerAuditDto.getJusticeUid());
							orderListDto.setCurrentHandlerUid(nextUser.getUid()); 
							orderListDto.setCurrentHandler(nextUser.getName());
						}else{ //查询终审法务
							FinalAuditDto finalAuditDto=new FinalAuditDto();
							finalAuditDto.setOrderNo(faceSignDto.getOrderNo());
							finalAuditDto = httpUtil.getObject(Constants.LINK_CREDIT, "/credit/risk/final/v/detail" ,finalAuditDto, FinalAuditDto.class);
							if(finalAuditDto!=null && (null !=finalAuditDto.getOfficerUid() && finalAuditDto.getOfficerUidType()==2)){
								UserDto nextUser=CommonDataUtil.getUserDtoByUidAndMobile(finalAuditDto.getOfficerUid());
								orderListDto.setCurrentHandlerUid(nextUser.getUid()); 
								orderListDto.setCurrentHandler(nextUser.getName());
							}else{
								rd.setCode(RespStatusEnum.FAIL.getCode());
								rd.setMsg("暂无法务处理人！");
								return rd;
							}
						}
					}else if(listDto.getAuditSort()==1){
	//					if(null != baseBorrowDto && ("01".equals(baseBorrowDto.getProductCode()) ||"02".equals(baseBorrowDto.getProductCode()))){  //置换贷
								orderFlowDto.setNextProcessId("fundDocking");
								orderFlowDto.setNextProcessName("资料推送");
								AllocationFundDto allocationFundDto =new AllocationFundDto();
								allocationFundDto.setOrderNo(faceSignDto.getOrderNo());
								List<AllocationFundDto> list = httpUtil.getList(Constants.LINK_CREDIT, "/credit/risk/allocationfund/v/detail",allocationFundDto, AllocationFundDto.class);
								String nextUid = list==null?null:list.get(0).getCreateUid();
								UserDto nextUser=CommonDataUtil.getUserDtoByUidAndMobile(nextUid);
								orderListDto.setCurrentHandlerUid(nextUser.getUid()); //下一处理人分配资金
								orderListDto.setCurrentHandler(nextUser.getName());
//								OrderFlowDto flowDto = httpUtil.getObject(Constants.LINK_CREDIT, "/credit/order/flow/v/selectEndOrderFlow", orderFlowDto,OrderFlowDto.class);
//								if(flowDto!=null&&flowDto.getIsNewWalkProcess()!=2){ //不重新走流程
//									//发送短信
//									String ipWhite = ConfigUtil.getStringValue(Constants.BASE_AMS_IPWHITE,ConfigUtil.CONFIG_BASE); //ip
//									String ProductName="债务置换";
//									if(baseBorrowDto!=null && !"01".equals(baseBorrowDto.getProductCode()) && !"02".equals(baseBorrowDto.getProductCode())){
//										ProductName=baseBorrowDto.getProductName();
//									}
//									//发送给操作人
//									AmsUtil.smsSend(nextUser.getMobile(), ipWhite, Constants.SMS_TEMPLATE_MANAGER, ProductName,baseBorrowDto.getBorrowerName(),baseBorrowDto.getLoanAmount(),"资料推送");
//								}
	//					}else if(null != baseBorrowDto && "03".equals(baseBorrowDto.getProductCode())){ //畅贷
	//					}
					}else{  //分配订单
//						orderFlowDto.setNextProcessId("managerAudit");
//						orderFlowDto.setNextProcessName("分配订单");
//						UserDto nextUser=CommonDataUtil.getUserDtoByUidAndMobile(listDto.getDistributionOrderUid());
//						orderListDto.setCurrentHandlerUid(nextUser.getUid()); //下一处理人分配资金
//						orderListDto.setCurrentHandler(nextUser.getName());
						orderFlowDto.setNextProcessId("placeOrder");
						orderFlowDto.setNextProcessName("提单");
						String currentHandlerUid = StringUtil.isEmpty(baseBorrowDto.getAcceptMemberUid())?baseBorrowDto.getCreateUid():baseBorrowDto.getAcceptMemberUid();
						UserDto nextUser=CommonDataUtil.getUserDtoByUidAndMobile(currentHandlerUid);
						orderListDto.setCurrentHandlerUid(nextUser.getUid()); //下一处理人完善订单
						orderListDto.setCurrentHandler(nextUser.getName());
					}
				}
				logger.info("面签后下一节点"+orderListDto.getCustomerType());
				goNextNode(orderFlowDto, orderListDto);
			} catch (Exception e) {
				e.printStackTrace();
				rd.setCode(RespStatusEnum.FAIL.getCode());
				rd.setMsg(RespStatusEnum.FAIL.getMsg());
			}
			return rd;
		}
	public void isFace(RespStatus result,FaceSignDto faceSignDto){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("orderNo",faceSignDto.getOrderNo());
		Integer isFace = httpUtil.getObject(Constants.LINK_CREDIT, "/credit/order/base/v/isFace", map, Integer.class);
		if(2==isFace){
			result.setCode(RespStatusEnum.SUCCESS.getCode());
			return;
		}
		OrderListDto tmp = new OrderListDto();
		tmp.setOrderNo(faceSignDto.getOrderNo());
		tmp = httpUtil.getObject(Constants.LINK_CREDIT, "/credit/order/base/v/selectDetailByOrderNo", tmp, OrderListDto.class);
		String faceCityCode = ConfigUtil.getStringValue(Constants.BASE_FACE_CITY_CODE,ConfigUtil.CONFIG_BASE);
		if(null!=tmp&&StringUtils.isNotBlank(faceCityCode)
				&&null!=faceCityCode
				&&StringUtils.isNotBlank(tmp.getCityCode())
				&&faceCityCode.contains(tmp.getCityCode())){
			FacesignRecognitionDto face = new FacesignRecognitionDto();
			face.setOrderNo(faceSignDto.getOrderNo());
			List<FacesignRecognitionDto> list = facesignService.listFacesignRecognition(face);
			if(null!=list&&list.size()>0){
				faceSignDto.setIsFace(1);
				for (FacesignRecognitionDto f:list){
					if(2==f.getIsSuccess()){
						faceSignDto.setIsFace(2);
						result.setCode(RespStatusEnum.FAIL.getCode());
						result.setMsg("APP所有人脸识别需都匹配成功");
						break;
					}
				}
			} else {
				faceSignDto.setIsFace(0);
				result.setCode(RespStatusEnum.FAIL.getCode());
				result.setMsg("请先在APP进行人脸识别");
			}
		} else {
			result.setCode(RespStatusEnum.SUCCESS.getCode());
			faceSignDto.setIsFace(3);
		}
	}
	@ResponseBody
	@RequestMapping(value = "/faceRecognitionDetail")
	public RespDataObject<List<FacesignRecognitionDto>> faceRecognitionDetail(HttpServletRequest request, @RequestBody  FacesignRecognitionDto face){
		RespDataObject<List<FacesignRecognitionDto>> resp=new RespDataObject<List<FacesignRecognitionDto>>();
		resp.setCode(RespStatusEnum.FAIL.getCode());
		resp.setMsg(RespStatusEnum.FAIL.getMsg());
		try{
			if(StringUtils.isBlank(face.getOrderNo())){
				resp.setMsg("获取人脸识别信息缺少订单编号");
				return resp;
			}
			List<FacesignRecognitionDto> list = facesignService.listFacesignRecognition(face);
			resp.setData(list);
			resp.setCode(RespStatusEnum.SUCCESS.getCode());
			resp.setMsg(RespStatusEnum.SUCCESS.getMsg());
		} catch (Exception e){
			e.printStackTrace();
		}
		return resp;
	}

	@ResponseBody
	@RequestMapping(value = "/faceRecognition")
	public RespDataObject<FacesignRecognitionDto> faceRecognition(HttpServletRequest request, @RequestBody  FacesignRecognitionDto face){
		RespDataObject<FacesignRecognitionDto> resp=new RespDataObject<FacesignRecognitionDto>();
		resp.setCode(RespStatusEnum.FAIL.getCode());
		resp.setMsg(RespStatusEnum.FAIL.getMsg());
		try{
			if(StringUtils.isBlank(face.getOrderNo())){
				resp.setMsg("人脸识别信息缺少订单编号");
				return resp;
			}
			UserDto user = getUserDto(request);
			face.setCreateUid(user.getUid());
			facesignService.insertFacesignRecognition(face);
			resp.setData(face);
			resp.setCode(RespStatusEnum.SUCCESS.getCode());
			resp.setMsg(RespStatusEnum.SUCCESS.getMsg());
		} catch (Exception e){
			e.printStackTrace();
		}
		return resp;
	}
	
	@ResponseBody
	@RequestMapping(value = "/insertFacesignRecognitions")
	public RespStatus insertFacesignRecognitions(HttpServletRequest request, @RequestBody  List<FacesignRecognitionDto> faces){
		RespStatus resp=new RespStatus();
		resp.setCode(RespStatusEnum.FAIL.getCode());
		resp.setMsg(RespStatusEnum.FAIL.getMsg());
		try{
			facesignService.insertFacesignRecognitions(faces);
			resp.setCode(RespStatusEnum.SUCCESS.getCode());
			resp.setMsg(RespStatusEnum.SUCCESS.getMsg());
		} catch (Exception e){
			e.printStackTrace();
		}
		return resp;
	}

	@ResponseBody
	@RequestMapping(value = "/updateFaceRecognition")
	public RespStatus updateFaceRecognition(HttpServletRequest request, @RequestBody  FacesignRecognitionDto face){
		RespStatus resp=new RespStatus();
		resp.setCode(RespStatusEnum.FAIL.getCode());
		resp.setMsg(RespStatusEnum.FAIL.getMsg());
		try{
			if(0==face.getId()){
				resp.setMsg("更新人脸识别信息缺少主键id");
				return resp;
			}
			facesignService.updateFacesignRecognition(face);
			resp.setCode(RespStatusEnum.SUCCESS.getCode());
			resp.setMsg(RespStatusEnum.SUCCESS.getMsg());
		} catch (Exception e){
			e.printStackTrace();
		}
		return resp;
	}
	@ResponseBody
	@RequestMapping(value = "/selectFaceRecognition")
	public RespDataObject<FacesignRecognitionDto> selectFaceRecognition(HttpServletRequest request, @RequestBody  FacesignRecognitionDto face){
		RespDataObject<FacesignRecognitionDto> resp=new RespDataObject<FacesignRecognitionDto>();
		resp.setCode(RespStatusEnum.FAIL.getCode());
		resp.setMsg(RespStatusEnum.FAIL.getMsg());
		try{
			if(0==face.getId()){
				resp.setMsg("查询人脸识别实现缺少主键!");
				return resp;
			}
			face = facesignService.selectFacesignRecognitionById(face);
			resp.setData(face);
			resp.setCode(RespStatusEnum.SUCCESS.getCode());
			resp.setMsg(RespStatusEnum.SUCCESS.getMsg());
		} catch (Exception e){
			e.printStackTrace();
		}
		return resp;
	}

	@ResponseBody
	@RequestMapping(value = "/deleteFacesignRecognition")
	public RespStatus deleteFacesignRecognition(HttpServletRequest request, @RequestBody  FacesignRecognitionDto face){
		RespStatus resp=new RespStatus();
		resp.setCode(RespStatusEnum.FAIL.getCode());
		resp.setMsg(RespStatusEnum.FAIL.getMsg());
		try{
			if(0==face.getId()){
				resp.setMsg("更新人脸识别信息缺少主键id");
				return resp;
			}
			int count = facesignService.deleteFacesignRecognition(face);
			resp.setCode(RespStatusEnum.SUCCESS.getCode());
			resp.setMsg(RespStatusEnum.SUCCESS.getMsg());
		} catch (Exception e){
			e.printStackTrace();
		}
		return resp;
	}
	
	@ResponseBody
	@RequestMapping(value = "/deleteByOrderNoAndCustomerType")
	public RespStatus deleteByOrderNoAndCustomerType(HttpServletRequest request, @RequestBody  FacesignRecognitionDto face){
		RespStatus resp=new RespStatus();
		resp.setCode(RespStatusEnum.FAIL.getCode());
		resp.setMsg(RespStatusEnum.FAIL.getMsg());
		try{
			if(null==face.getOrderNo()||null==face.getCustomerType()){
				resp.setMsg("删除人脸识别信息缺少订单号或客户类型");
				return resp;
			}
			facesignService.deleteByOrderNoAndCustomerType(face);
			resp.setCode(RespStatusEnum.SUCCESS.getCode());
			resp.setMsg(RespStatusEnum.SUCCESS.getMsg());
		} catch (Exception e){
			e.printStackTrace();
		}
		return resp;
	}
}
