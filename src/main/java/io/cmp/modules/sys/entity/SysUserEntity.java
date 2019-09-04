

package io.cmp.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.cmp.common.validator.group.AddGroup;
import io.cmp.common.validator.group.UpdateGroup;
import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * 系统用户
 *
 * @author
 */
@Data
@TableName("sys_user")
@Configuration
public class SysUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	@TableId
	private Long userId;

	/**
	 * 用户名
	 */
	@NotBlank(message="用户名不能为空")
	@Pattern(regexp = "^[A-Za-z0-9]+$",message = "用户名格式不对，必须为字母或数字")
	private String username;


	/**
	 * 密码
	 */
	//@NotBlank(message="密码不能为空", groups = AddGroup.class)
	private String password;

	/**
	 * 用户真实名
	 */

	@Pattern(regexp = "^^\\s*$|[\\u4e00-\\u9fa5_a-zA-Z0-9]+$",message = "用户名格式不对，中文字母或数字")
	private String realname;

	/**
	 * 分机号
	 */
	@Pattern(regexp = "^\\s*$|^[0-9]*$",message = "分机号格式不对，必须为数字")
	private String extension;

	/**
	 * 是否绑定分机号
	 */
	private String  isExtension;

	/**
	 * 盐
	 */
	private String salt;

	/**
	 * 邮箱
	 */
	//@NotBlank(message="邮箱不能为空", groups = {AddGroup.class, UpdateGroup.class})
	//@Email(message="邮箱格式不正确", groups = {AddGroup.class, UpdateGroup.class})
	@Pattern(regexp = "^\\s*$|^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$",message = "邮箱格式不正确")
	private String email;

	/**
	 * 手机号
	 */
	//@Pattern(regexp = "^1([345789])\\\\d{9}$",message = "手机号格式不正确")
	private String mobile;

	/**
	 * 状态  0：禁用   1：正常
	 */
	private Integer status;

	/**
	 * 角色ID列表
	 */
	@TableField(exist=false)
	private List<Long> roleIdList;

	/**
	 * 角色ID
	 */
	@TableField(exist=false)
	@NotBlank(message="角色不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private Long roleId;
	/**
	 * 部门D列表
	 */
	@TableField(exist=false)
	private List<Long> deptIdList;


	/**
	 * 部门ID
	 */
	@NotNull(message="部门不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private Long deptId;

	/**
	 * 部门名称
	 */
	@TableField(exist=false)
	private String deptName;


	/**
	 * 状态  0：保密  1：男 2 : 女
	 */
	private Integer sex;

	/**
	 * 状态  0：解锁  1：上锁
	 */
	private Integer autoUnlock;
	/**
	 * 状态  0：未登陆 1：男 2 : 登陆
	 */
	//private Integer whetherLogin;此字段暂时不需要
	/**
	 * 状态  0：首次登陆 1：已经登陆过几次了
	 */
	private Integer firstLogin;
	/**
	 *   证件号码
	 */
	@Pattern(regexp = "^\\s*$|^\\d{15}|\\d{18}$",message = "身份张号码格式不对")
	private String idNum;
	/**
	 * 部门负责人
	 */
	private Integer isDeptLeader;

	/**
	 * 工龄
	 */
	//@Pattern(regexp = "^[0-9]*$",message = "工龄格式不对，必须为数字")
	private Integer workingYears;

	/**
	 * 本人下面的部门
	 */
	@TableField(exist=false)
	private List<SysDeptEntity> sysDeptEntity;

	@TableField(exist=false)
	private MultipartFile headPortrait;

	private String headPortraits;
//	默认头像路径
	@TableField(exist=false)
	private String defaultHeadPortrait;
	/**
	 * 上传图片的地址
	 */
	@TableField(exist=false)
	private String  location;
	/**
	 *有效时间
	 */
	//@DateTimeFormat(pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date expirationTime;

	/**
	 * 出生日期
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date birthBeBorn;

	/**
	 * 创建时间
	 */

	private Date createTime;


}
