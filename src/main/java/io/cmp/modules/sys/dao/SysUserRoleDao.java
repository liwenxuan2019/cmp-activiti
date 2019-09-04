

package io.cmp.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.cmp.modules.sys.entity.SysUserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户与角色对应关系
 *
 * @author
 */
@Mapper
public interface SysUserRoleDao extends BaseMapper<SysUserRoleEntity> {
	
	/**
	 * 根据用户ID，获取角色ID列表
	 */
	List<Long> queryRoleIdList(Long userId);


	/**
	 * 根据角色ID数组，批量删除
	 */
	int deleteBatch(Long[] roleIds);

	/**
	 * 根据角色ID，获取用户的数量
	 */
	Long queryUserCount(Long roleId);

	/**
	 * 根据角色ID，获取用户
	 */
	List queryUserListByroleId(Long roleId);

	int deleteBatchByUserId(Long[] userId);

	List queryUserIdByroleId(Long roleId);
}
