

package io.cmp.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.cmp.common.utils.PageUtils;
import io.cmp.modules.sys.entity.SysRoleEntity;

import java.util.List;
import java.util.Map;


/**
 * 角色
 *
 * @author
 */
public interface SysRoleService extends IService<SysRoleEntity> {

	PageUtils queryPage(Map<String, Object> params);

	void saveRole(SysRoleEntity role);

	void update(SysRoleEntity role);

	void deleteBatch(Long[] roleIds);

	
	/**
	 * 查询用户创建的角色ID列表
	 */
	List<Long> queryRoleIdList(Long createUserId);

	/**
	 * 让角色失效
	 */
	SysRoleEntity  isValid(Long roleId);

	/**
	 * 根据roleId进行条件查询
	 * @param params
	 * @return
	 */
	PageUtils queryPageByroleId(Map<String, Object> params,List roleId);


    void notExistCurrRoleAddUser(SysRoleEntity role);


	PageUtils findRoleByUserId(Map<String, Object> params);

	List<Long> findParentNodeByMenuId(Long menuId);
}
