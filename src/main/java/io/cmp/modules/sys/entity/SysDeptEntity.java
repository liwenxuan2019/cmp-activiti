

package io.cmp.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;


/**
 * 部门管理
 *
 * @author
 */
@Data
@TableName("sys_dept")
public class SysDeptEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 部门ID
	 */
	@TableId
	private Long deptId;
	/**
	 * 上级部门ID，一级部门为0
	 */
	private Long parentId;
	/**
	 * 部门名称
	 */
	private String name;
	/**
	 * 上级部门名称
	 */
	@TableField(exist=false)
	private String parentName;
	private Integer orderNum;
	@TableLogic
	private Integer delFlag;
	/**
	 * 部门代码
	 */
	@NotBlank(message="部门代码不能为空")
	@Pattern(regexp = "^[A-Za-z0-9]+$",message = "部门代码格式不对，必须为字母或数字")
	private String deptCode;
	/**
	 * ztree属性
	 */
	@TableField(exist=false)
	private Boolean open;
	@TableField(exist=false)
	private List<?> list;
	/**
	 * 部门负责人
	 */

	//private  Integer departHead;

	/**
	 * 多个部门负责人
	 */
	@TableField(exist=false)
	private List<Long> manyDepartHeads;
	@TableField(exist=false)
	private String manyDepartHead;

	/**
	 * 多个部门负责人
	 */
	@TableField(exist=false)
	private List<SysUserEntity> sysUserList;
	/**
	 * 部门下员工数量
	 */
	@TableField(exist=false)
	private Integer userCount;
	/**
	 * 负责人数量
	 */
	@TableField(exist=false)
	private Integer deptLeaderCount;


}
