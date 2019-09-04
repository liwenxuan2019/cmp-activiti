package io.cmp.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * 
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-08-21 09:44:52
 */
@Data
@TableName("uk_user")
public class UkUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	//@TableId
	private String id;

	@TableId
	private Long userId;
	/**
	 * 语言
	 */
	private String language;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 安全级别
	 */
	private String secureconf;
	/**
	 * 邮件
	 */
	private String email;
	/**
	 * 姓
	 */
	private String firstname;
	/**
	 * 名
	 */
	private String midname;
	/**
	 * 名
	 */
	private String lastname;
	/**
	 * 职位
	 */
	private String jobtitle;
	/**
	 * 部门
	 */
	private String department;
	/**
	 * 性别
	 */
	private String gender;
	/**
	 * 生日
	 */
	private String birthday;
	/**
	 * 昵称
	 */
	private String nickname;
	/**
	 * 用户类型
	 */
	private String usertype;
	/**
	 * 角色
	 */
	private String rulename;
	/**
	 * 备用
	 */
	private String searchprojectid;
	/**
	 * 租户ID
	 */
	private String orgi;
	/**
	 * 企业ID
	 */
	private String orgid;
	/**
	 * 创建人
	 */
	private String creater;
	/**
	 * 创建时间
	 */
	private Date createtime;
	/**
	 * 备注
	 */
	private String memo;
	/**
	 * 更新时间
	 */
	private Date updatetime;
	/**
	 * 部门
	 */
	private String organ;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 最后 一次密码修改时间
	 */
	private Date passupdatetime;
	/**
	 * 签名
	 */
	private String sign;
	/**
	 * 是否已删除
	 */
	private Integer del;
	/**
	 * 姓名
	 */
	private String uname;
	/**
	 * 登录修改密码
	 */
	private Integer musteditpassword;
	/**
	 * 工号
	 */
	private Integer agent;
	/**
	 * 技能组
	 */
	private String skill;
	/**
	 * 省份
	 */
	private String province;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 关注人数
	 */
	private Integer fans;
	/**
	 * 被关注次数
	 */
	private Integer follows;
	/**
	 * 积分
	 */
	private Integer integral;
	/**
	 * 最后登录时间
	 */
	private Date lastlogintime;
	/**
	 * 状态
	 */
	private String status;
	/**
	 * 离线时间
	 */
	private Date deactivetime;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 数据状态
	 */
	private Integer datastatus;
	/**
	 * 启用呼叫中心坐席
	 */
	private Integer callcenter;
	/**
	 * 是否超级管理员
	 */
	private Integer superuser;
	/**
	 * 最大接入访客数量
	 */
	private Integer maxuser;
	/**
	 * 默认排序方式
	 */
	private String ordertype;
	/**
	 * 关闭默认进入操作指南页
	 */
	private Integer disabledesk;
	/**
	 * 坐席绑定的平台ID
	 */
	private String hostid;
	/**
	 * 坐席绑定的分机ID
	 */
	private String extid;
	/**
	 * 坐席绑定的分机号码
	 */
	private String extno;
	/**
	 * 坐席启用分机绑定
	 */
	private Integer bindext;
	/**
	 * 最近商机分配时间
	 */
	private Date lastdisdate;
	/**
	 * 累计商机分配数量
	 */
	private Integer disnum;
	/**
	 * 最近工单分配时间
	 */
	private Date lastworkorderdate;
	/**
	 * 累计工单分配数量
	 */
	private Integer workordernum;
	/**
	 * 启用工单分配
	 */
	private Integer workorder;
	/**
	 * 启用商机分配
	 */
	private Integer bussopdis;
	/**
	 * 是否是新版多租户中新增的用户（true 是）
	 */
	private Integer tenant;

}
