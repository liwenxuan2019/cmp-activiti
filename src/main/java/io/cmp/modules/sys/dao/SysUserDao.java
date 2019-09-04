

package io.cmp.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.cmp.modules.sys.entity.SysMenuEntity;
import io.cmp.modules.sys.entity.SysUserEntity;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统用户
 *
 * @author
 */
@Mapper
public interface SysUserDao extends BaseMapper<SysUserEntity> {
	
	/**
	 * 查询用户的所有权限
	 * @param userId  用户ID
	 */
	List<String> queryAllPerms(Long userId);
	
	/**
	 * 查询用户的所有菜单ID
	 */
	List<Long> queryAllMenuId(Long userId);
	
	/**
	 * 根据用户名，查询系统用户
	 */
	SysUserEntity queryByUserName(String username);

	/**
	 * 查询用户的所有角色
	 */
	List<Long> queryRoleListByUserId(Long userId);
	/**
	 * 批量增加用户
	 */
    void  batchAddUsers(String userName);

	/**
	 * 通过用户ID查询菜单ID
	 * @param userId
	 * @return
	 */

	List<Long> findMenuIdByUserId(long userId);

	/**
	 * 通过父级Id查询菜单
	 */
	List<SysMenuEntity> findMenuByParentId(long menuId,List<Long> menuIds);
	List<SysMenuEntity> findMenuByParentId1(@Param("menuId") Long menuId,@Param("toolBar")Integer toolBar);

	List<SysMenuEntity> queryLevelOneMenu(@Param("userId")Integer userId,@Param("toolBar")Integer toolBar,@Param("menuId")Integer menuId);

	List<SysMenuEntity> queryLevelTwoMenu(@Param("userId")Integer userId,@Param("toolBar")Integer toolBar,@Param("menuId")Integer menuId);

    List<SysUserEntity> userList();

    List<SysUserEntity> findByDeptId(long deptId);
}
