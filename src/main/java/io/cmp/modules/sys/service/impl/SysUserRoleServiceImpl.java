

package io.cmp.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.cmp.common.exception.RRException;
import io.cmp.common.utils.MapUtils;
import io.cmp.modules.sys.dao.SysUserRoleDao;
import io.cmp.modules.sys.entity.SysRoleEntity;
import io.cmp.modules.sys.entity.SysUserRoleEntity;
import io.cmp.modules.sys.service.SysRoleService;
import io.cmp.modules.sys.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



/**
 * 用户与角色对应关系
 *
 * @author
 */
@Service("sysUserRoleService")
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleDao, SysUserRoleEntity> implements SysUserRoleService {
	@Autowired
	private SysRoleService sysRoleService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveOrUpdate(Long userId, List<Long> roleIdList) {
		//先删除用户与角色关系
		this.removeByMap(new MapUtils().put("user_id", userId));

		if (roleIdList == null || roleIdList.size() == 0) {
			return;
		}

		//保存用户与角色关系
		for (Long roleId : roleIdList) {
			SysUserRoleEntity sysUserRoleEntity = new SysUserRoleEntity();
			sysUserRoleEntity.setUserId(userId);
			//			判断角色是否有效
			SysRoleEntity sysRole = sysRoleService.getById(roleId);

			if (sysRole != null) {
				if (sysRole.getIsValid() == 1) {
					sysUserRoleEntity.setRoleId(roleId);

					this.save(sysUserRoleEntity);
				} else {
					throw new RRException("角色被禁用了");

				}
			}
		}
	}

	@Override
	public List<Long> queryRoleIdList(Long userId) {
		return baseMapper.queryRoleIdList(userId);
	}

	@Override
	public int deleteBatch(Long[] roleIds) {
		return baseMapper.deleteBatch(roleIds);
	}

	@Override
	public Long queryUserCountByRoleId(Long roleId) {
		return baseMapper.queryUserCount(roleId);
	}

	@Override
	public List queryUserListByroleId(Long roleId) {
		return baseMapper.queryUserListByroleId(roleId);
	}

	@Override
	public int deleteBatchByUserId(Long[] userId) {
		return baseMapper.deleteBatchByUserId(userId);
	}

	@Override
	public List queryUserIdByroleId(Long roleId) {
		return baseMapper.queryUserIdByroleId(roleId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveUserRole(Long roleId, List<Long> userIdList) {
		//先删除用户与角色关系
		for (Long userId : userIdList) {
			this.removeByMap(new MapUtils().put("user_id", userId));
		}


		if (userIdList == null || userIdList.size() == 0) {
			return;
		}

		//保存角色与用户关系
		for (Long userId : userIdList) {
			SysUserRoleEntity sysUserRoleEntity = new SysUserRoleEntity();
					sysUserRoleEntity.setRoleId(roleId);
					sysUserRoleEntity.setUserId(userId);
					this.save(sysUserRoleEntity);

			}
		}
	}



