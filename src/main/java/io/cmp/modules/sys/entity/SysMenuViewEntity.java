package io.cmp.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 菜单视图表
 * 
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-07-07 15:52:28
 */
@Data
@TableName("sys_menu_view")
public class SysMenuViewEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * 菜单id
	 */
	private Long menuId;
	/**
	 * 视图名称
	 */
	private String viewName;
	/**
	 * 视图url
	 */
	private String viewUrl;
	/**
	 * 是否默认
	 */
	private String isView;
	/**
	 * 排序号
	 */
	private Integer orderNum;
	/**
	 * 创建人代码
	 */
	private String createCode;
	/**
	 * 创建人
	 */
	private String createName;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 修改人代码
	 */
	private String updateCode;
	/**
	 * 修改人
	 */
	private String updateName;
	/**
	 * 修改时间
	 */
	private Date updateTime;

}
