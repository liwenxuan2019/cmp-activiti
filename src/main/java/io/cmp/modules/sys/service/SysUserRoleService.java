

package io.cmp.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.cmp.modules.sys.entity.SysUserRoleEntity;

import java.util.List;



/**
 * 用户与角色对应关系
 *
 * @author
 */
public interface SysUserRoleService extends IService<SysUserRoleEntity> {
	
	void saveOrUpdate(Long userId, List<Long> roleIdList);
	
	/**
	 * 根据用户ID，获取角色ID列表
	 */
	List<Long> queryRoleIdList(Long userId);

	/**
	 * 根据角色ID数组，批量删除
	 */
	int deleteBatch(Long[] roleIds);

	/**
	 * 根据角色ID，获取该角色下面的数量
	 */
	Long queryUserCountByRoleId(Long roleId);


	/**
	 * 根据角色ID，获取用户
	 */
	List queryUserListByroleId(Long roleId);


	int deleteBatchByUserId(Long[] userId);

	/**
	 * 根据角色ID，获取用户ID
	 */
	List queryUserIdByroleId(Long roleId);

	void saveUserRole(Long userId, List<Long> roleIdList);
}
