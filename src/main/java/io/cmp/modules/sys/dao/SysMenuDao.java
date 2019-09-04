

package io.cmp.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.cmp.modules.sys.entity.SysMenuEntity;
import io.cmp.modules.sys.vo.SysMenuVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单管理
 *
 * @author
 */
@Mapper
public interface SysMenuDao extends BaseMapper<SysMenuEntity> {
	
	/**
	 * 根据父菜单，查询子菜单
	 * @param parentId 父菜单ID
	 */
	List<SysMenuEntity> queryListParentId(Long parentId);
	List<SysMenuEntity> queryAdminListParentId(Long parentId);
	/**
	 * 获取不包含按钮的菜单列表
	 */
	List<SysMenuEntity> queryNotButtonList();

	/**
	 *
	 * 插入一条数据
	 */
    int	insertSelective(SysMenuEntity menu);

    SysMenuEntity findByOperateCode(String operateCode);

    SysMenuEntity getById(Long menuId);

	void deleteByParentId(Long parentId);
}
