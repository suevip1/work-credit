package com.anjbo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.anjbo.utils.ConfigUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.anjbo.bean.common.DictDto;
import com.anjbo.bean.order.OrderBaseBorrowDto;
import com.anjbo.bean.order.OrderBaseBorrowRelationDto;
import com.anjbo.bean.order.OrderBusinfoDto;
import com.anjbo.bean.order.OrderListDto;
import com.anjbo.bean.product.NotarizationDto;
import com.anjbo.bean.product.ProductDto;
import com.anjbo.bean.user.UserDto;
import com.anjbo.common.Constants;
import com.anjbo.common.RespData;
import com.anjbo.common.RespDataObject;
import com.anjbo.common.RespHelper;
import com.anjbo.common.RespStatus;
import com.anjbo.common.RespStatusEnum;
import com.anjbo.service.FsService;
import com.anjbo.service.OrderBaseBorrowRelationService;
import com.anjbo.service.OrderBaseBorrowService;
import com.anjbo.service.OrderBaseService;
import com.anjbo.service.OrderBusinfoService;
import com.anjbo.service.OrderBusinfoTypeService;
import com.anjbo.utils.InfoTypeQrCodeUtil;
import com.anjbo.utils.StringUtil;
import com.anjbo.utils.ValidHelper;

@Controller
@RequestMapping("/credit/order/businfo/v")
public class OrderBusinfoController extends BaseController {

	Logger log = Logger.getLogger(OrderBusinfoController.class);

	@Resource
	private OrderBusinfoService orderBusinfoService;
	@Resource
	private OrderBusinfoTypeService orderBusinfoTypeService;
	@Resource
	private OrderBaseService orderBaseService;
	@Resource
	private FsService fsService;
	@Resource
	private OrderBaseBorrowService orderBaseBorrowService;
	@Resource
	private OrderBaseBorrowRelationService orderBaseBorrowRelationService;

	/**
	 * 初始化列表页
	 * 
	 * @param request
	 * @param
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/query")
	public RespDataObject<Map<String, Object>> loadBusInfo(HttpServletRequest request,@RequestBody Map<String,Object> map) {
		RespDataObject<Map<String, Object>> resp = new RespDataObject<Map<String, Object>>();
		try {
			UserDto userDto=getUserDto(request);  //获取用户信息
			String orderNo= MapUtils.getString(map,"orderNo");
			String isApp= MapUtils.getString(map,"isChangLoan");
			//String orderNo = getCreditOrderNo(orderNoOld);
			//查询订单列表信息
			OrderListDto orderListDto = orderBaseService.selectDetail(orderNo);
			Map<String, Object> data = new HashMap<String, Object>();
			
			boolean operate=false;
//			boolean cOperate = false;
			if(!orderNo.isEmpty()){
				if(isApp!=null){//app调用
					 data = orderBusinfoService.getAppBusInfoByOrderNo(map);
				}else{//pc调用
					//查询订单节点是否已经提交经理审批
					if(null!=orderListDto){
						//获取产品名称
						int productId = 0;
						List<ProductDto> prductList = getProductDtos();
						for (ProductDto productDto : prductList) {
							if(productDto.getProductCode().equals(orderListDto.getProductCode())){
								productId=productDto.getId();
								break;
							}
						}
						boolean flag = compareProcessAround(productId,orderListDto.getProcessId(),"placeOrder");
						if(flag){//还未提交经理审批
							map.put("placeOrder", true);
						}else{
							map.put("placeOrder", false);
						}
					}
					 data = orderBusinfoService.getBusinfoTypeTree(map);
				}
				if(null!=orderListDto){
					//获取订单节点
					String acceptMemberUid=orderListDto.getAcceptMemberUid();
					String channelManagerUid=orderListDto.getChannelManagerUid();
					String uid=userDto.getUid();
					//获取产品名称
					//查询订单列表信息
					//String creditOrderNo = getCreditOrderNo(orderNo);
//					OrderListDto order=null;
					//OrderBaseBorrowDto borrow = orderBaseBorrowService.selectOrderBorrowByOrderNo(creditOrderNo);
//					if(borrow!=null&&borrow.getOrderBaseBorrowRelationDto()!=null&&borrow.getOrderBaseBorrowRelationDto().size()>0){
//						for (OrderBaseBorrowRelationDto relation : borrow.getOrderBaseBorrowRelationDto()) {
//							String  temp = relation.getOrderNo();
//							order = orderBaseService.selectDetail(temp);
//						}
//					}
					data.put("progressId", "");
					String processConfig = ConfigUtil.getStringValue(Constants.BASE_FINANCE_LOAN_PROCESS,
							ConfigUtil.CONFIG_BASE);
					boolean flag = !("wanjie".equals(orderListDto.getProcessId())||"pay".equals(orderListDto.getProcessId())||"elementReturn".equals(orderListDto.getProcessId())
							||"rebate".equals(orderListDto.getProcessId())||"已关闭".equals(orderListDto.getState())||"closeOrder".equals(orderListDto.getProcessId())
							||processConfig.contains(orderListDto.getProcessId()));
					if((
						(null!=uid&&null!=orderListDto&&(uid.equals(acceptMemberUid)))
						||(uid.equals(orderListDto.getFacesignUid())&&"facesign".equals(orderListDto.getProcessId()))
						||(uid.equals(orderListDto.getCurrentHandlerUid())&&("auditFirst".equals(orderListDto.getProcessId())
						||"auditFinal".equals(orderListDto.getProcessId())))
						||(uid.equals(channelManagerUid)&&("auditFirst".equals(orderListDto.getProcessId())||"managerAudit".equals(orderListDto.getProcessId())||"placeOrder".equals(orderListDto.getProcessId())))
					   )&&flag){
						operate=true;
						data.put("progressId", orderListDto.getProcessId());
					}
					//畅贷是否可编辑影像资料
//					if(order==null){
//						cOperate = true;
//					}else if(!orderNo.equals(orderNoOld)){
//						boolean cflag = !"wanjie".equals(order.getProcessId()); 
//						if((uid.equals(acceptMemberUid)||"allocationFund".equals(order.getProcessId()))&&cflag){
//							cOperate=true;
//							data.put("progressId", order.getProcessId());
//						}
//					}
				}
			}
//			if(check(orderListDto)){
//				data.put("finshed", true);
//			}else{
//				data.put("finshed", false);
//			}
			if("1".equals(isApp)){
				data.put("operate", false);
			}else{
				data.put("operate", operate);
			}
			data.put("cOperate", operate);
			//获取城市，产品业务类型
//			String temporderNo =getLoanOrderNo(orderNo);
//			OrderBaseBorrowDto orderBaseBorrowDto = orderBaseBorrowService.selectOrderBorrowByOrderNo(temporderNo);
//			orderBaseBorrowDto = getOrderBaseBorrowDto(orderBaseBorrowDto);
			if(orderListDto!=null){
				data.put("cityName", orderListDto.getCityName());
				data.put("productName", orderListDto.getProductName());
			}
			RespHelper.setSuccessDataObject(resp, data);
		} catch (Exception e) {
			log.info("loadBusInfo Exception ==>", e);
		}
		return resp;
	}
	
	@ResponseBody
	@RequestMapping(value = "/getBusinfoAndType")
	public RespDataObject<Map<String, Object>> getBusinfoAndType(HttpServletRequest request,@RequestBody Map<String,Object> map) {
		RespDataObject<Map<String, Object>> resp = new RespDataObject<Map<String, Object>>();
		try {
			Map<String,Object> data = orderBusinfoService.getBusinfoAndType(map);
			RespHelper.setSuccessDataObject(resp, data);
		} catch (Exception e) {
			log.info("loadBusInfo Exception ==>", e);
		}
		return resp;
	}

	/**
	 * 根据产品编码获取影像资料树
	 * @Author KangLG<2018年3月1日>
	 * @param productCode
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/searchByProductCode_{productCode}")
	public RespData<Map<String, Object>> getBusinfoType(@PathVariable("productCode")String productCode) {
		RespData<Map<String, Object>> resp = new RespData<Map<String, Object>>();
		try {
			RespHelper.setSuccessData(resp, orderBusinfoService.searchByProductCode(productCode));
		} catch (Exception e) {
			log.info("loadBusInfo Exception ==>", e);
		}
		return resp;
	}
	
	/**
	 * 上传图片
	 * @param request
	 * @param map
	 * @return
	 */
	@ResponseBody 
	@RequestMapping(value="/save")
	public RespStatus addBusinfo(HttpServletRequest request,@RequestBody Map<String,Object> map){
		RespStatus respStatus = RespHelper.failRespStatus();
		UserDto userDto=getUserDto(request);  //获取用户信息
		map.put("uid", userDto.getUid());
		if(!ValidHelper.isNotEmptyByKeys(map, "uid","typeId","urls","orderNo")){
			respStatus.setMsg(RespStatusEnum.PARAMETER_ERROR.getMsg());
			return respStatus;
		}
		try{
			map.put("orderNo", MapUtils.getString(map, "orderNo"));
			orderBusinfoService.addBusinfo(map);
			respStatus = RespHelper.setSuccessRespStatus(respStatus);
		}catch (Exception e) {
			log.error("addBusinfo Exception  ->",e);
		}
		return respStatus;
	}

	/**
	 * 批量新增影像资料
	 * @param request
	 * @param list
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/batchBusinfo")
	public RespStatus batchBusinfo(HttpServletRequest request,@RequestBody List<Map<String,Object>> list){
		RespStatus respStatus = RespHelper.failRespStatus();
		try{
			if(list.size()<=0){
				respStatus.setMsg("请上传影像资料");
				return respStatus;
			}
			UserDto user = getUserDto(request);
			Map<String,Object> map = list.get(0);
			map.put("uid",user.getUid());
			if(!ValidHelper.isNotEmptyByKeys(map, "uid","typeId","url","orderNo")){
				respStatus.setMsg(RespStatusEnum.PARAMETER_ERROR.getMsg());
				return respStatus;
			}
			String orderNo = MapUtils.getString(map,"orderNo");
			orderBusinfoService.batchBusinfo(list,orderNo,user);
			respStatus = RespHelper.setSuccessRespStatus(RespStatusEnum.SUCCESS.getMsg());
		} catch (Exception e){
			log.error("addBusinfo Exception  ->",e);
		}
		return respStatus;
	}

	/**
	 * 修改图片
	 * @param request
	 * @param map
	 * @return
	 */
	@ResponseBody 
	@RequestMapping(value="/move")
	public RespStatus moveBusinfo(HttpServletRequest request,@RequestBody Map<String,Object> map){
		RespStatus respStatus = RespHelper.failRespStatus();
		if(!ValidHelper.isNotEmptyByKeys(map, "businfoIds","toTypeId")){
			respStatus.setMsg(RespStatusEnum.PARAMETER_ERROR.getMsg());
			return respStatus;
		}
		try{
			String[] businfoIdsArray =  map.get("businfoIds").toString().replace("[", "").replace("]", "").split(",");
			Integer[] businfoIds = new Integer[businfoIdsArray.length];
			for(int i=0;i<businfoIdsArray.length;i++){
				businfoIds[i] = Integer.parseInt(businfoIdsArray[i].trim());
			}
			map.put("businfoIds", businfoIds);
			orderBusinfoService.moveBusinfo(map);
			respStatus = RespHelper.setSuccessRespStatus(respStatus);
		}catch (Exception e) {
			log.error("moveBusinfo Exception  ->",e);
		}
		return respStatus;
	}
	
	@ResponseBody 
	@RequestMapping(value="/deleteAll")
	public RespStatus delBusinfo(HttpServletRequest request,@RequestBody Map<String,Object> map){
		RespStatus respStatus = RespHelper.failRespStatus();
		UserDto userDto=getUserDto(request);  //获取用户信息
		map.put("uid", userDto.getUid());
		if(!ValidHelper.isNotEmptyByKeys(map,"orderNo")){
			respStatus.setMsg(RespStatusEnum.PARAMETER_ERROR.getMsg());
			return respStatus;
		}
		try{
			map.put("orderNo", MapUtils.getString(map, "orderNo"));
			orderBusinfoService.delBusinfo(map);
			respStatus = RespHelper.setSuccessRespStatus(respStatus);
		}catch (Exception e) {
			log.error("delBusinfo Exception  ->",e);
		}
		return respStatus;
	}
	
	@ResponseBody 
	@RequestMapping(value="/lookOver")
	public RespDataObject<Map<String, Object>> lookOver(HttpServletRequest request,@RequestBody Map<String,Object> map){
		log.info("查看影像资料orderNo:"+MapUtils.getString(map, "orderNo"));
		RespDataObject<Map<String, Object>> respDataObject = RespHelper.failDataObject(null);
		if(!ValidHelper.isNotEmptyByKeys(map, "typeId","orderNo")){
			respDataObject.setMsg(RespStatusEnum.PARAMETER_ERROR.getMsg());
			log.info("参数异常");
			return respDataObject;
		}
		try{
			map.put("orderNo", MapUtils.getString(map, "orderNo"));
			Map<String, Object> data = orderBusinfoService.lookOver(map);
			respDataObject = RespHelper.setSuccessDataObject(respDataObject, data);
		}catch (Exception e) {
			log.error("lookOver Exception  ->",e);
			log.info("查询失败");
		}
		return respDataObject;
	}
	
	@ResponseBody 
	@RequestMapping(value="/getAllType")
	public RespDataObject<Map<String, Object>> getAllType(HttpServletRequest request,@RequestBody Map<String,Object> map){
		RespDataObject<Map<String, Object>> respDataObject = RespHelper.failDataObject(null);
		if(!ValidHelper.isNotEmptyByKeys(map, "typeId","orderNo")){
			respDataObject.setMsg(RespStatusEnum.PARAMETER_ERROR.getMsg());
			return respDataObject;
		}
		try{
			map.put("orderNo", MapUtils.getString(map, "orderNo"));
			Map<String, Object> data = orderBusinfoService.getAllType(map);
			respDataObject = RespHelper.setSuccessDataObject(respDataObject, data);
		}catch (Exception e) {
			log.error("getAllType Exception  ->",e);
		}
		return respDataObject;
	}
	
	@ResponseBody 
	@RequestMapping(value="/deleteIds")
	public RespDataObject<Map<String, Object>> deleteImg(HttpServletRequest request,@RequestBody Map<String,Object> map){
		RespDataObject<Map<String, Object>> respStatus = new RespDataObject<Map<String, Object>>();
		respStatus.setCode(RespStatusEnum.FAIL.getCode());
		respStatus.setMsg(RespStatusEnum.FAIL.getMsg());
		UserDto userDto=getUserDto(request);  //获取用户信息
		map.put("uid", userDto.getUid());
		if(!map.containsKey("ids")&&!map.containsKey("typeId")){
			return respStatus;
		}
		try{
			String msg=RespStatusEnum.SUCCESS.getMsg();
			//过滤不能删除的id
			String orderNo = MapUtils.getString(map, "orderNo");
			OrderListDto orderListDto = orderBaseService.selectDetail(orderNo);
			if(orderListDto!=null&&!"placeOrder".equals(orderListDto.getProcessId())){
				map.put("isOrder", 2);
				msg="删除非提单资料成功";
			}
			int success = orderBusinfoService.deleteImgByIds(map);
			if(success<=0){
				respStatus.setMsg("提单资料不能删除!");
				return respStatus;
			}
			Map<String, Object> data = orderBusinfoService.lookOver(map);
			respStatus.setData(data);
			respStatus.setCode(RespStatusEnum.SUCCESS.getCode());
			respStatus.setMsg(msg);
		}catch (Exception e) {
			log.error("deleteImg Exception  ->",e);
			respStatus.setMsg("删除失败!");
			return respStatus;
		}
		return respStatus;
	}
	
	/**
	 * 上传照片到临时地址
	 * 
	 * @param params
	 *            要上传的图片文件列表
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/uploadPhoto")
	@ResponseBody
	public RespDataObject<Map<String,Object>> uploadPhoto(HttpServletRequest request,@RequestBody Map<String,Object> params) {
		String infoType = MapUtils.getString(params, "infoType");
		String orderNo = MapUtils.getString(params, "orderNo");
		int index = MapUtils.getIntValue(params, "index");
		String imagePath = MapUtils.getString(params, "imagePath");
		RespDataObject<Map<String,Object>> resp = new RespDataObject<Map<String,Object>>();
		Map retMap = new HashMap();
		// 校验参数
		if (/*file == null || */StringUtil.isEmpty(infoType)) {
			resp.setCode("ERROR");
			resp.setMsg("参数异常");
			return resp;
		}
		// 根据资料类型名称获取类型ID
		//Map infoTypeMap = orderBusinfoService.getInfoTypeByName(infoType);
		int infoTypeId = Integer.parseInt(infoType);
		List<String> listImagePath = new ArrayList<String>();
		Map<String,Object> map = new HashMap<String, Object>();
		// 上传到临时位置
		// 上传文件并得到url列表
		boolean flag = false;
			try {
				/*imagePath = fsService.uploadImageByFile(file[0]);
				listImagePath.add(imagePath);*/
				map.put("img", imagePath);
				if (!StringUtil.isEmpty(imagePath)) {
					// 存储到数据库
					OrderBusinfoDto orderBusinfoDto = new OrderBusinfoDto();
					orderBusinfoDto.setCreateTime(new Date());
					orderBusinfoDto.setUrl(imagePath);
					orderBusinfoDto.setCreateUid(getUserDto(request).getUid());
					if (!StringUtil.isEmpty(orderNo)) {
						orderBusinfoDto.setOrderNo(orderNo);
					}
					orderBusinfoDto.setTypeId(infoTypeId);
					orderBusinfoDto.setIndex(index);
					orderBusinfoDto.setIsPs(MapUtils.getInteger(params,"isPs"));
					orderBusinfoService.saveOrderBusinfo(orderBusinfoDto);
					map.put("id", orderBusinfoDto.getId());
				}
			}catch (Exception e) {
				log.error("uploadPhoto   上传图片出错-" + e.getMessage());
				listImagePath.add("");
				flag = true;
				resp.setMsg("上传图片失败");
			}
		if (flag) {
			resp.setCode("ERROR");
		} else {
			resp.setCode("SUCCESS");
		}
		retMap.put("imgData", map);
		resp.setData(retMap);
		return resp;
	}
	
	/**
	 * 带二维码资料的上传
	 * 
	 * @param file
	 *            资料照片，贴有二维码
	 * @throws IOException 
	 * 
	 */
	@RequestMapping(value = "/infoWithQRCode")
	@ResponseBody
	public RespDataObject<Map<String, Object>> infoWithQRCode(HttpServletRequest request, @RequestParam MultipartFile file,
			@RequestParam("orderNo") String orderNo) throws IOException {
		RespDataObject<Map<String, Object>> resp = new RespDataObject<Map<String, Object>>();
		Map<String, Object> retMap = new HashMap<String, Object>();
		if (file.isEmpty() || StringUtil.isEmpty(orderNo)) {
			resp.setCode(RespStatusEnum.PARAMETER_ERROR.getCode());
			resp.setMsg("参数异常");
			return resp;
		}
		OrderBusinfoDto busInfo = new OrderBusinfoDto();
		busInfo.setCreateUid(getUserDto(request).getUid());
		busInfo.setOrderNo(orderNo);
		String imagePath = "";
		// 上传到临时位置
		boolean flag = false;
		if (!file.isEmpty()) {
			// 解析二维码
			try {
				String infoTypeName = InfoTypeQrCodeUtil.getInfoTypeFromPic(file);
				if (infoTypeName == null) {
					flag = true;
					resp.setCode("ANALYZE_ERROR");
					resp.setMsg("解析二维码失败");
				} else {
					// 查询数据库获取类型获取类型
					List<Map> listType = orderBusinfoTypeService.getAllBusType();
					for (Map map : listType) {
						if (infoTypeName.equals((String) map.get("name"))) {
							busInfo.setTypeId((Integer) map.get("id"));
						}
					}
				}
			} catch (Exception e) {
				flag = true;
				resp.setCode("ANALYZE_ERROR");
				resp.setMsg("解析二维码失败");
			}
			// 上传到fs
			try {
				imagePath = fsService.uploadImageByFile(file);
				busInfo.setUrl(imagePath);
			} catch (IllegalStateException e) {
				log.error(" infoWithQRCode   移动图片出错-" + e.getMessage());
				imagePath = "";
				flag = true;
				resp.setCode("ERROR");
				resp.setMsg("移动图片失败");
			}
			retMap.put("img", imagePath);
		}
		// 保存资料信息
		try {
			busInfo.setCreateTime(new Date());
			busInfo.setIndex(0);
			orderBusinfoService.saveOrderBusinfo(busInfo);
			retMap.put("infoId", busInfo.getId());
		} catch (Exception e) {
			flag = true;
			resp.setCode("ERROR");
			resp.setMsg("保存图片失败");
		}
		if (!flag) {
			resp.setCode("SUCCESS");
		}
		resp.setData(retMap);
		return resp;
	}
	
	/**
	 * 返回债务置换贷款订单和畅贷集合
	 * @param request
	 * @param map
	 * @return
	 */
	@ResponseBody 
	@RequestMapping(value="/orderList")
	public RespDataObject<List<Map<String, Object>>> orderList(HttpServletRequest request,@RequestBody Map<String,Object> map){
		RespDataObject<List<Map<String, Object>>> respDataObject = RespHelper.failDataObject(null);
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(!ValidHelper.isNotEmptyByKeys(map,"orderNo")){
			respDataObject.setMsg(RespStatusEnum.PARAMETER_ERROR.getMsg());
			return respDataObject;
		}
		try{
			Map<String,Object> maps = new HashMap<String, Object>();
			String orderNo = MapUtils.getString(map, "orderNo");
			OrderListDto orderListDto = orderBaseService.selectDetail(orderNo);
			if(orderListDto.getProductCode().equals("03")&&StringUtils.isNotBlank(orderListDto.getRelationOrderNo())){
				int productId = Integer.parseInt(orderListDto.getCityCode()+orderListDto.getProductCode());
				if(compareProcessAround(productId,"facesign",orderListDto.getProcessId())){
					maps = new HashMap<String, Object>();
					maps.put("orderNo", orderNo);
					maps.put("productName", "面签影像资料");
					maps.put("isChangLoan", "1");
					list.add(maps);
				}
				if(!"facesign".equals(orderListDto.getProcessId())){
					maps = new HashMap<String, Object>();
					maps.put("orderNo", orderListDto.getRelationOrderNo());
					maps.put("productName", "关联订单的影像资料");
					maps.put("isChangLoan", "1");
					list.add(maps);
				}
			}else{
				maps.put("orderNo", orderNo);
				maps.put("productName", orderListDto.getProductName()+"影像资料");
				maps.put("isChangLoan", "2");
				list.add(maps);
			}
			respDataObject = RespHelper.setSuccessDataObject(respDataObject, list);
		}catch (Exception e) {
			log.error("orderList Exception  ->",e);
		}
		return respDataObject;
	}
	
	public boolean check(OrderListDto orderListDto){
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("productCode", orderListDto.getProductCode());
		map.put("hasChangLoan", hasChangLoan(orderListDto.getOrderNo()));
		map.put("orderNo", orderListDto.getOrderNo());
		map.put("auditSort", orderListDto.getAuditSort());
		//不公证时，不需要传公证委托书 
		if(orderListDto.getAuditSort()==2){
			NotarizationDto obj = new NotarizationDto();
			obj.setOrderNo(orderListDto.getOrderNo());
			obj.setRelationOrderNo(orderListDto.getRelationOrderNo());
			obj = httpUtil.getObject(Constants.LINK_CREDIT, "/credit/process/notarization/v/init", obj, NotarizationDto.class);
			map.put("notarizationType", obj.getNotarizationType());
		}else{
			map.put("notarizationType", null);
		}
		//查看是否有畅贷，来查询已经上传的影像资料，可能有畅贷资料，但是畅贷已经删除了
		int count1=orderBusinfoTypeService.mustBusInfoCount(map);
		int count2=orderBusinfoService.hasBusInfoCount(map);
		if(count1!=count2){
			return false;
		}
		return true;
	}
	
	public int hasChangLoan(String orderNo){
		OrderBaseBorrowDto orderBaseBorrowDto = orderBaseBorrowService.selectOrderBorrowByOrderNo(orderNo);
		if(orderBaseBorrowDto!=null && orderBaseBorrowDto.getOrderBaseBorrowRelationDto()!=null&&orderBaseBorrowDto.getOrderBaseBorrowRelationDto().size()>0){
			return 1;
		}
		return 2;
	}
	
	
	/**
	 * 获取债务置换贷款订单号
	 * @param orderNo
	 * @return
	 */
	public String getLoanOrderNo(String orderNo){
		OrderBaseBorrowRelationDto orderBaseBorrowRelationDto = orderBaseBorrowRelationService.selectRelationByOrderNo(orderNo);
		if(orderBaseBorrowRelationDto!=null && orderBaseBorrowRelationDto.getRelationOrderNo()!=null){
			return orderBaseBorrowRelationDto.getRelationOrderNo();
		}
		return orderNo;
	}
	
	/**
	 * 给订单列表字段赋值
	 * 
	 * @param orderBaseBorrowDto
	 * @return
	 */
	public OrderBaseBorrowDto getOrderBaseBorrowDto(OrderBaseBorrowDto orderBaseBorrowDto){
		//获取所有城市
		List<DictDto> dictList = getDictDtoByType("bookingSzAreaOid");
		for (DictDto dictDto : dictList) {
			if(dictDto.getCode().equals(orderBaseBorrowDto.getCityCode())){
				orderBaseBorrowDto.setCityName(dictDto.getName());
				break;
			}
		}
		//获取产品名称
		List<ProductDto> prductList = getProductDtos();
		for (ProductDto productDto : prductList) {
			if(productDto.getProductCode().equals(orderBaseBorrowDto.getProductCode())){
				orderBaseBorrowDto.setProductName(productDto.getProductName());
				break;
			}
		}
		return orderBaseBorrowDto;
	}
	
	/**
	 * 面签影像资料校验
	 * @param request
	 * @param orderBaseBorrowDto
	 * @return
	 */
	@RequestMapping("/faceBusinfoCheck")
	@ResponseBody
	public RespStatus faceBusinfoCheck(HttpServletRequest request,@RequestBody OrderBaseBorrowDto orderBaseBorrowDto){
		RespStatus resp = new RespStatus();
		OrderListDto orderList = orderBaseService.selectDetail(orderBaseBorrowDto.getOrderNo());
		if(orderList.getAuditSort()==2||orderBusinfoService.faceBusinfoCheck(orderList.getOrderNo(), orderList.getProductCode(),orderList.getAuditSort())){
			resp.setCode(RespStatusEnum.SUCCESS.getCode());
			resp.setMsg(RespStatusEnum.SUCCESS.getMsg());
		}else{
			resp.setCode(RespStatusEnum.FAIL.getCode());
			resp.setMsg("面签资料不完整");
		}
		return resp;
	}
	
	/**
	 * 公证影像资料校验
	 * @param request
	 * @param orderBaseBorrowDto
	 * @return
	 */
	@RequestMapping("/notarizationBusinfoCheck")
	@ResponseBody
	public RespStatus notarizationBusinfoCheck(HttpServletRequest request,@RequestBody OrderBaseBorrowDto orderBaseBorrowDto){
		RespStatus resp = new RespStatus();
		OrderListDto orderList = orderBaseService.selectDetail(orderBaseBorrowDto.getOrderNo());
		if(!"04".equals(orderList.getProductCode())||(orderBusinfoService.notarizationBusinfoCheck(orderList.getOrderNo(), orderList.getProductCode())
				&&"04".equals(orderList.getProductCode()))){
			resp.setCode(RespStatusEnum.SUCCESS.getCode());
			resp.setMsg(RespStatusEnum.SUCCESS.getMsg());
		}else{
			resp.setCode(RespStatusEnum.FAIL.getCode());
			resp.setMsg("公证委托书未传");
		}
		return resp;
	}
	
}
