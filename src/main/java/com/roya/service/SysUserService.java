package com.roya.service;

import com.google.common.base.Preconditions;
import com.roya.beans.PageQuery;
import com.roya.beans.PageResult;
import com.roya.common.RequestHolder;
import com.roya.dao.SysUserMapper;
import com.roya.exception.ParamException;
import com.roya.model.SysUser;
import com.roya.param.UserParam;
import com.roya.utils.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by idea
 * description :
 *		用户管理的服务
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-14-11:19
 */
@Service
public class SysUserService {

	@Resource
	private SysUserMapper userMapper;
	@Resource
	private SysLogService sysLogService;

	public void insertUser(UserParam param){
		BeanValidator.check(param);
		if (checkTelephoneExist(param.getTelephone(),param.getId())){
			throw new ParamException("电话已被占用");
		}
		if (checkEmailExist(param.getMail(),param.getId())){
			throw new ParamException("邮箱已被占用");
		}
		String password = PasswordUtil.randomPassword();
		String encryptedPassword = MD5Util.encrypt(password);
		String encryptedPhone = PhoneUtil.encryptPhone(param.getTelephone());

		SysUser sysUser = SysUser.builder()
				.username(param.getUsername()).telephone(encryptedPhone)
				.mail(param.getMail()).password(encryptedPassword)
				.status(param.getStatus()).remark(param.getRemark())
				.deptId(param.getDeptId()).build();
		sysUser.setOperator(RequestHolder.getCurrentUser().getUsername());
		sysUser.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
		sysUser.setOperateTime(new Date());

		boolean flag = SendMailUtil.SimpleMessageMail(param.getMail(),"激活密码","您好！欢迎注册权限管理系统，激活账户后即可使用，" +
									"\n\n您的密码："+password+"\n\n请勿转发他人，以免给您造成不必要的损失");
		if (flag){
			userMapper.insertSelective(sysUser);
		}
		sysLogService.saveUserLog(null,sysUser);
	}

	/**
	 * 因为用户登录的时候，
	 * 是使用邮箱或者手机号进行登录
	 */
	// 校验不存在邮箱
	public  boolean checkEmailExist(String mail,Integer userId){
		return  userMapper.countByMail(mail,userId) > 0;
	}
	//校验不存在手机号
	public  boolean checkTelephoneExist(String phone,Integer userId){
		return  userMapper.countByMail(phone,userId) > 0;
	}


	/**
	 * 更新用户
	 * @param param 用户
	 */
	public  void  updateUser(UserParam param){
		BeanValidator.check(param);
		if (checkTelephoneExist(param.getTelephone(),param.getId())){
			throw new ParamException("电话已被占用");
		}
		if (checkEmailExist(param.getMail(),param.getId())){
			throw new ParamException("邮箱已被占用");
		}
		SysUser before = userMapper.selectByPrimaryKey(param.getId());
		Preconditions.checkNotNull(before,"待更新的用户不存在");
		String telephone = PhoneUtil.encryptPhone(param.getTelephone());

		SysUser after = SysUser.builder().
				id(param.getId()).username(param.getUsername()).
				telephone(telephone).mail(param.getMail()).
				status(param.getStatus()).remark(param.getRemark()).deptId(param.getDeptId()).build();
		after.setOperator(RequestHolder.getCurrentUser().getUsername());
		after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
		after.setOperateTime(new Date());

		userMapper.updateByPrimaryKeySelective(after);
		sysLogService.saveUserLog(before,after);
	}




	public SysUser findUserByKey(String key){
		return userMapper.findUserByKey(key);
	}


	public PageResult<SysUser> getPageByDeptId(int deptId, PageQuery pageQuery){
		BeanValidator.check(pageQuery);
		int count = userMapper.countByDeptId(deptId);
		if (count > 0 ){
			List<SysUser> list = userMapper.getPageByDeptId(deptId,pageQuery);
			for (SysUser user: list) {
				user.setTelephone(PhoneUtil.decryptPhone(user.getTelephone()));
			}
			return PageResult.<SysUser>builder().total(count).data(list).build();
		}
		return PageResult.<SysUser>builder().total(count).build();
	}


	public List<SysUser> getAll(){
		return 	userMapper.getAll();
	}


}
