package io.cmp.modules.sys.controller;

import io.cmp.common.utils.R;
import io.cmp.modules.sys.entity.SysUserEntity;
import io.cmp.modules.sys.entity.SysUserTokenEntity;
import io.cmp.modules.sys.form.SysLoginForm;
import io.cmp.modules.sys.service.HttpAPIService;
import io.cmp.modules.sys.service.SysCaptchaService;
import io.cmp.modules.sys.service.SysUserService;
import io.cmp.modules.sys.service.SysUserTokenService;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * 登录相关
 *
 * @author
 */
@RestController
public class SysLoginController{
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserTokenService sysUserTokenService;
	@Autowired
	private SysCaptchaService sysCaptchaService;

	@Autowired
	private HttpAPIService httpAPIService;

	@Value("${httpclient.ukfUrl}")
	private String ukfUrl;

	/**
	 * 验证码
	 */
	@GetMapping("captcha.jpg")
	public void captcha(HttpServletResponse response, String uuid)throws IOException {
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setContentType("image/jpeg");

		//获取图片验证码
		BufferedImage image = sysCaptchaService.getCaptcha(uuid);

		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(image, "jpg", out);
		IOUtils.closeQuietly(out);
	}

	/**
	 * 登录
	 */
/*	@PostMapping("/sys/login")
	public Map<String, Object> login(@RequestBody SysLoginForm form)throws IOException {
		*//*boolean captcha = sysCaptchaService.validate(form.getUuid(), form.getCaptcha());
		if(!captcha){
			return R.error("验证码不正确");
		}*//*

		//用户信息
		SysUserEntity user = sysUserService.queryByUserName(form.getUsername());

		//账号不存在、密码错误
		if(user == null || !user.getPassword().equals(new Sha256Hash(form.getPassword(), user.getSalt()).toHex())) {
			return R.error("账号或密码不正确");
		}

		//账号锁定
		if(user.getStatus() == 0){
			return R.error("账号已被锁定,请联系管理员");
		}

		//生成token，并保存到数据库
		R r = sysUserTokenService.createToken(user.getUserId());
		return r;
	}*/


	/**
	 * 登录判断时间以及本用户是否已经登陆增加了该用户是否失效
	 */
	@PostMapping("/sys/login")
	public Map<String, Object> loginOptimization(@RequestBody SysLoginForm form)throws IOException {
		/*boolean captcha = sysCaptchaService.validate(form.getUuid(), form.getCaptcha());
		if(!captcha){
			return R.error("验证码不正确");
		}*/

		//用户信息78=

		SysUserEntity user = sysUserService.queryByUserName(form.getUsername());

/*		if(user.getPassword().equals(new Sha256Hash(form.getUsername(), user.getSalt()).toHex())){
			throw new RRException("您是首次登陆请修改密码");

		}*/

		//账号不存在、密码错误
		if(user == null || !user.getPassword().equals(new Sha256Hash(form.getPassword(), user.getSalt()).toHex())) {
			return R.error("账号或密码不正确");
		}
		if(user.getExpirationTime()!=null){
			if(user.getExpirationTime().getTime()-new Date().getTime()<0){
				return R.error("账号过期了");

			}
		}


		//账号失效
		if(user.getStatus() == 0){
			return R.error("账号失效了");
		}
		SysUserTokenEntity tokenEntity=	sysUserTokenService.getById(user.getUserId());
		//		先给一个默认值
		R r =null;

//		账号上锁
		if(user.getAutoUnlock()==1){
			logger.debug("token0\t"+user.getAutoUnlock());
//			token不为空在去判断时间
		//	SysUserTokenEntity tokenEntity1=	sysUserTokenService.getById(user.getUserId());
		//	if(tokenEntity1!=null) {
			//	if (tokenEntity1.getExpireTime().getTime() > new Date().getTime() &&tokenEntity1.getToken()!=null) {

				//	throw new RRException("选择比当前时间大的时间");
			//	}
			//}else{
				//生成token，并保存到数据库
				r = sysUserTokenService.createToken(user.getUserId());

			//}

		}else if(user.getAutoUnlock()==0){
			logger.debug("token1\t"+user.getAutoUnlock());
//      直接返回不用执行
			if(tokenEntity!=null){
				//当前时间
				Date now = new Date();
				//过期时间
				Date expireTime = new Date(now.getTime() + 3600 * 12 * 1000);
				tokenEntity.setToken(tokenEntity.getToken());
				tokenEntity.setUpdateTime(now);
				tokenEntity.setExpireTime(expireTime);

				//更新token
				sysUserTokenService.updateById(tokenEntity);
				r = R.ok().put("token", tokenEntity.getToken()).put("expire",tokenEntity.getExpireTime() );

			}else{

				r = sysUserTokenService.createToken(user.getUserId());
			}
		}
		if(user.getFirstLogin()!=null&&user.getFirstLogin().equals(0)){
			//throw new RRException("您是首次登陆请修改密码");
			user.setFirstLogin(1);
			user.setPassword(new Sha256Hash(form.getPassword(), user.getSalt()).toHex());
			//sysUserService.update(user);
			sysUserService.updateById(user);
			//throw new RRException();
			return r.put("fist_Login","您是首次登陆请修改密码");
		}

	/*	try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userName",user.getUsername());
			String responseMessage =httpAPIService.doGet(ukfUrl,map);
			logger.info(responseMessage);
		}
		catch (Exception e)
		{
			logger.info(e.toString());
		}*/


		return r;
	}

	
}
