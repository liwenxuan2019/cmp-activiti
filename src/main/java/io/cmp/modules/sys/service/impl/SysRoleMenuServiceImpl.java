

package io.cmp.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.cmp.common.exception.RRException;
import io.cmp.modules.sys.dao.SysRoleMenuDao;

import io.cmp.modules.sys.entity.SysMenuEntity;
import io.cmp.modules.sys.entity.SysRoleEntity;
import io.cmp.modules.sys.entity.SysRoleMenuEntity;
import io.cmp.modules.sys.service.SysRoleMenuService;
import io.cmp.modules.sys.service.SysRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;



/**
 * 角色与菜单对应关系
 *
 * @author
 */
@Service("sysRoleMenuService")
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuDao, SysRoleMenuEntity> implements SysRoleMenuService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysRoleMenuService sysRoleMenuService;
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveOrUpdate(Long roleId, List<Long> menuIdList) {
		//先删除角色与菜单关系
		deleteBatch(new Long[]{roleId});

		if(menuIdList.size() == 0){
			return ;
		}

		//保存角色与菜单关系
		for(Long menuId : menuIdList){
			SysRoleMenuEntity sysRoleMenuEntity = new SysRoleMenuEntity();
			sysRoleMenuEntity.setMenuId(menuId);
			//			判断角色是否有效
			SysRoleEntity sysRole=sysRoleService.getById(roleId);


//				通过menuId查询它的所有的上级

				logger.debug("setMenuId11="+menuId);
				sysRoleMenuEntity.setRoleId(roleId);
				this.save(sysRoleMenuEntity);

//			    根据角色ID，获取表中存在的menuId
				List<Long>	existMenuIds=sysRoleMenuService.queryMenuIdList(roleId);
//              传入的menuId
				List<Long> menuIds=sysRoleService.findParentNodeByMenuId(menuId);

				for (Long id: menuIds) {
					if (existMenuIds.contains(id)) {
						continue;
					}
					if(sysRoleMenuEntity.getMenuId()==id){
						continue;
					}

					sysRoleMenuEntity.setMenuId(sysRoleMenuEntity.getMenuId());
					sysRoleMenuEntity.setMenuId(id);
					logger.debug("setMenuId="+id);

					this.save(sysRoleMenuEntity);

				}
				//this.save(sysRoleMenuEntity);

			}


	}

	@Override
	public List<Long> queryMenuIdList(Long roleId) {
		return baseMapper.queryMenuIdList(roleId);
	}

	@Override
	public int deleteBatch(Long[] roleIds){
		return baseMapper.deleteBatch(roleIds);
	}

	@Override
	public List<Long> findParentNodeByMenuId(Long menuId) {
//		用于储存所有的父级Id
		List<Long> menuIds=new ArrayList<>();
		return findParentNode(menuId,menuIds);
	}

	public List<Long> findParentNode(Long menuId,List<Long> menuIds){

		SysMenuEntity sysMenuEntity=baseMapper.findParentNodeByMenuId(menuId);

		if(sysMenuEntity!=null){
			menuIds.add(sysMenuEntity.getParentId());
			findParentNode(sysMenuEntity.getParentId(),menuIds);
		}


		return menuIds;
	}

}
