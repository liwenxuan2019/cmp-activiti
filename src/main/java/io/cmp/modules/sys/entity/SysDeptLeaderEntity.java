package io.cmp.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 部门负责人
 * 
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-07-16 15:40:26
 */
@Data
@TableName("sys_dept_leader")
public class SysDeptLeaderEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * 部门id
	 */
	private Long deptId;
	/**
	 * 用户id
	 */
	private Long userId;
	/**
	 * 用户名
	 */
	//private String username;
	/**
	 * 用户真实名
	 */
	//private String realname;

}
