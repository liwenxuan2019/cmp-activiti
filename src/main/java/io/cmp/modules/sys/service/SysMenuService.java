

package io.cmp.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.cmp.common.utils.R;
import io.cmp.modules.sys.entity.SysMenuEntity;
import io.cmp.modules.sys.vo.SysMenuVo;

import java.util.List;


/**
 * 菜单管理
 *
 * @author
 */
public interface SysMenuService extends IService<SysMenuEntity> {

	/**
	 * 根据父菜单，查询子菜单
	 * @param parentId 父菜单ID
	 * @param menuIdList  用户菜单ID
	 */
	List<SysMenuEntity> queryCurrentListParentId(Long parentId, List<Long> menuIdList);
	List<SysMenuEntity> queryListParentId(Long parentId, List<Long> menuIdList);
	List<SysMenuEntity> queryCommonListParentId(Long parentId, List<Long> menuIdList);
	List<SysMenuEntity> queryCommonOperationListParentId(Long parentId, List<Long> menuIdList,Integer per);
	List<SysMenuEntity> queryListParentIdDisplayView(Long parentId, List<Long> menuIdList);
	List<SysMenuEntity> queryadminListParentId(Long parentId, List<Long> menuIdList);
	/**queryadminListParentId
	 * 根据父菜单，查询子菜单
	 * @param parentId 父菜单ID
	 */
	List<SysMenuEntity> queryMenuListParentId(Long parentId);
	List<SysMenuEntity> queryListParentId(Long parentId);
	List<SysMenuEntity> queryAdminListParentId(Long parentId);
	/**queryAdminListParentId
	 * 获取不包含按钮的菜单列表
	 */
	List<SysMenuEntity> queryNotButtonList();
	
	/**
	 * 获取用户所有菜单列表
	 */
	List<SysMenuEntity> getUserMenuList(Long userId);
	List<SysMenuEntity> getUserMenuListDisplayView(Long userId);

	/**
	 * 获取当前用户菜单列表
	 */
	List<SysMenuEntity> getCurrentUserMenuList(Long userId);

	/**
	 * 删除
	 */
	void delete(Long menuId);

	/**
	 * 保存---->视图树和操作树和为一颗树
	 */
	SysMenuEntity MergeOneTree(SysMenuEntity sysMenuEntity);

	/**
	 * 修改--->视图树和操作树和为一颗树
	 */
	SysMenuEntity upadteMergeOneTree(SysMenuEntity sysMenuEntity);

	SysMenuEntity MergeOneTree1(SysMenuVo menu);

	SysMenuEntity upadteMergeOneTree1(SysMenuVo menu);


	List<SysMenuEntity>  getMenuList1(Long menuId);

    R queryTwoLevelMenu(Integer userId, Integer parentId);

	List<SysMenuEntity> getUserMenuOperation(Long userId,Integer parentId);

	SysMenuEntity findByOperateCode(String operateCode);
}
