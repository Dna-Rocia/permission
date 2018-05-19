package com.roya.service;

import com.google.common.base.Preconditions;
import com.roya.beans.PageQuery;
import com.roya.beans.PageResult;
import com.roya.common.RequestHolder;
import com.roya.dao.SysUserMapper;
import com.roya.exception.ParamException;
import com.roya.model.SysUser;
import com.roya.param.UserParam;
import com.roya.utils.BeanValidator;
import com.roya.utils.MD5Util;
import com.roya.utils.PasswordUtil;
import com.roya.utils.PhoneUtil;
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


	public void insertUser(UserParam param){
		BeanValidator.check(param);
		if (checkTelephoneExist(param.getTelephone(),param.getId())){
			throw new ParamException("电话已被占用");
		}
		if (checkEmailExist(param.getMail(),param.getId())){
			throw new ParamException("邮箱已被占用");
		}
		String password = PasswordUtil.randomPassword();
		password = "123456";
		String encryptedPassword = MD5Util.encrypt(password);
		String encryptedPhone = PhoneUtil.encryptPhone(param.getTelephone());


		SysUser sysUser = SysUser.builder()
				.username(param.getUsername()).telephone(encryptedPhone)
				.mail(param.getMail()).password(encryptedPassword)
				.status(param.getStatus()).remark(param.getRemark())
				.deptId(param.getDeptId()).build();
		sysUser.setOperator(RequestHolder.getCurrentUser().getUsername());
		sysUser.setOperateIp("127.0.0.1"); //todo
		sysUser.setOperateTime(new Date());


		//todo  sendEmail 发送成功之后才能进行数据的插入




		userMapper.insertSelective(sysUser);

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
				status(param.getStatus()).remark(param.getRemark()).build();
		after.setOperator(RequestHolder.getCurrentUser().getUsername());
		after.setOperateIp("127.0.0.1"); //todo
		after.setOperateTime(new Date());

		userMapper.updateByPrimaryKeySelective(after);
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




}
