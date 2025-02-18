package com.anjbo.controller.system;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.anjbo.bean.system.AmsDto;
import com.anjbo.bean.system.EmailDto;
import com.anjbo.common.AnjboException;
import com.anjbo.controller.BaseController;
import com.anjbo.service.system.AmsService;
import com.anjbo.utils.EmailUtil;
import com.anjbo.utils.IpUtil;
import com.anjbo.utils.StringUtil;

/**
 * 短信系统控制器
 * @author limh limh@zxsf360.com
 * @date 2016-6-2 下午07:14:41
 */
@Controller
@RequestMapping("/uuams")
public class AmsController extends BaseController{
	protected final Log logger = LogFactory.getLog(this.getClass());  
	@Resource
	private AmsService amsService;
	/**
	 * 小牛装修短信通道
	 * @param request
	 * @param amsDto
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/smsSend.act")
	public String smsSend(HttpServletRequest request,AmsDto amsDto){
		if(amsDto != null && StringUtils.isNotEmpty(amsDto.getSmsContent())) {
			amsDto.setSmsContent(amsDto.getSmsContent().replace("%23", "#").replace("【快鸽按揭】", "【四海恒通】"));
		}
		String svrIp = IpUtil.getClientIp(request);
		amsDto.setSvrIp(svrIp);
		int code = amsService.smsSendChannel4(amsDto);
		return "code="+code;
	}
	/**
	 * 快鸽短信通道
	 * @param request
	 * @param amsDto
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/smsSend2.act")
	public String smsSend2(HttpServletRequest request,AmsDto amsDto){
		if(amsDto != null && StringUtils.isNotEmpty(amsDto.getSmsContent())) {
			amsDto.setSmsContent(amsDto.getSmsContent().replace("%23", "#"));
		}
		String svrIp = IpUtil.getClientIp(request);
		amsDto.setSvrIp(svrIp);
		int code = amsService.smsSendChannel4(amsDto);
		return "code="+code;
	}
	/**
	 * 深圳湾短信通道
	 * @param request
	 * @param amsDto
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/smsSend3.act")
	public String smsSend3(HttpServletRequest request,AmsDto amsDto){
		if(amsDto != null && StringUtils.isNotEmpty(amsDto.getSmsContent())) {
			amsDto.setSmsContent(amsDto.getSmsContent().replace("%23", "#"));
		}
		String svrIp = IpUtil.getClientIp(request);
		amsDto.setSvrIp(svrIp);
		int code = amsService.smsSendChannel4(amsDto);
		return "code="+code;
	}
	
	/**
	 * 众视达/行业通- 短信通道
	 * @param request
	 * @param amsDto
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/smsSend4.act")
	public String smsSend4(HttpServletRequest request,AmsDto amsDto){
		if(amsDto != null && StringUtils.isNotEmpty(amsDto.getSmsContent())) {
			amsDto.setSmsContent(amsDto.getSmsContent().replace("%23", "#"));
		}
		String svrIp = IpUtil.getClientIp(request);
		amsDto.setSvrIp(svrIp);
		int code = amsService.smsSendChannel4(amsDto);
		return "code="+code;
	}
	/**
	 * 邮件通道
	 * @param request
	 * @param amsDto
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/emailSend.act",produces = "text/html;charset=UTF-8")
	public String emailSend(HttpServletRequest request,EmailDto emailDto){
		if(StringUtil.isEmpty(emailDto.getTitle())||
				StringUtil.isEmpty(emailDto.getContent())||
					StringUtil.isEmpty(emailDto.getEmail())){
			return "code=-1&msg=发送邮件失败，原因：参数错误";
		}
		try {
			EmailUtil.sendEmial(emailDto.getTitle(), emailDto.getContent(), emailDto.getEmail());
		} catch (AnjboException e) {
			e.printStackTrace();
			return "code="+e.getCode()+"&msg=发送邮件失败，原因："+e.getMsg();
		}
		return "code=0&msg=发送邮件成功";
	}
	@RequestMapping(value = "")
	public ModelAndView index(){
		return new ModelAndView("/index.html");
	}
}
