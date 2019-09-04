

package io.cmp.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.cmp.modules.sys.entity.SysRoleMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import io.cmp.modules.sys.entity.SysMenuEntity;
import java.util.List;

/**
 * 角色与菜单对应关系
 *
 * @author
 */
@Mapper
public interface SysRoleMenuDao extends BaseMapper<SysRoleMenuEntity> {
	
	/**
	 * 根据角色ID，获取菜单ID列表
	 */
	List<Long> queryMenuIdList(Long roleId);

	/**
	 * 根据角色ID数组，批量删除
	 */
	int deleteBatch(Long[] roleIds);

	SysMenuEntity findParentNodeByMenuId(Long menuId);
}
