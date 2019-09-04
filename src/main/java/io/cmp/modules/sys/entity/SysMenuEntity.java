

package io.cmp.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单管理
 *
 * @author
 */
@Data
@TableName("sys_menu")
public class SysMenuEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 菜单ID
	 */
	@TableId
	private Long menuId;

	/**
	 * 父菜单ID，一级菜单为0
	 */
	private Long parentId;

	/**
	 * 当前登录人代码
	 */
	@TableField(exist=false)
	private String createCode;

	/**
	 * 当前登录人名字
	 */
	@TableField(exist=false)
	private String createName;

	/**
	 * 当前修改人代码
	 */
	@TableField(exist=false)
	private String updateUserCode;

	/**
	 * 当前修改人名字
	 */
	@TableField(exist=false)
	private String updateUserName;
	/**
	 * 父菜单名称
	 */
	@TableField(exist=false)
	private String parentName;

	/**
	 * 菜单名称
	 */
	@NotBlank(message="菜单名称不能为空")
	private String name;

	/**
	 * 菜单URL
	 */
	private String url;

	/**
	 * 授权(多个用逗号分隔，如：user:list,user:create)
	 */
	private String perms;

	/**
	 * 类型     0：目录   1：菜单   2：按钮
	 */
	private Integer type;

	/**
	 * 菜单图标
	 */
	private String icon;
	/**
	 * 是否为工具菜单
	 */
	private Integer toolBar;
	/**
	 * 排序
	 */
	private Integer orderNum;
	/**
	 * 操作代码
	 */
	@NotBlank(message="代码不能为空")
	@Pattern(regexp = "^[A-Za-z0-9]+$",message = "操作代码格式不对，必须为字母或数字")
	private String operateCode;

	/**
	 * 默认视图
	 */
	private Integer defaultUrl;
	/**
	 * ztree属性
	 */
	@TableField(exist=false)
	private Boolean open;

	/**
	 * 图标类型
	 */
	private Integer iconType;
	/**
	 * 操作
	 */
	@TableField(exist=false)
	private List<SysMenuEntity> list;
	/**
	 * 视图
	 */
	@TableField(exist=false)
	private List<SysMenuEntity> menuViewList;

	@TableField(exist=false)
	private List<SysMenuEntity> menuOperationList;
	/**
	 * 改动之后的数据-->原来的数据先不动
	 */
	@TableField(exist=false)
	private List<SysMenuViewEntity> menuViewList1;
	@TableField(exist=false)
	private List<SysMenuOperationEntity> menuOperationList1;
	@TableField(exist=false)
	private List<Long> menuIds;

}
