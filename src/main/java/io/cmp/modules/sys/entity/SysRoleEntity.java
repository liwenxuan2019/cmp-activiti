

package io.cmp.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 角色
 *
 * @author
 */
@Data
@TableName("sys_role")
public class SysRoleEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 角色ID
	 */
	@TableId
	private Long roleId;

	/**
	 * 角色代码
	 */
	@NotBlank(message="角色代码不能为空")
	@Pattern(regexp = "^[A-Za-z0-9]+$",message = "角色代码格式不对，必须为字母或数字")
	private String roleCode;

	/**
	 * 角色名称
	 */
	@NotBlank(message="角色名称不能为空")
	@Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9]+$",message = "用户名格式不对，中文字母或数字")
	private String roleName;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 部门ID
	 */
	//@NotNull(message="部门不能为空")
	private Long deptId;

	/**
	 * 部门名称
	 */
	@TableField(exist=false)
	private String deptName;

	/**
	 * 菜单列表
	 */
	@TableField(exist=false)
	private List<Long> menuIdList;
	/**
	 * 部门列表
	 */
	@TableField(exist=false)
	private List<Long> deptIdList;
	
	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 用户数量
	 */
    @TableField(exist=false)
	private Long userCount;

	/**
	 * 是否失效
	 */
	private Integer isValid;
	/**
	 * 用户列表
	 */
	@TableField(exist=false)
	private List<Long> userIdList;

	private Integer creater;


	
}
