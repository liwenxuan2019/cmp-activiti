

package io.cmp.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.cmp.common.annotation.DataFilter;
import io.cmp.common.exception.RRException;
import io.cmp.common.utils.Constant;
import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.Query;
import io.cmp.modules.sys.dao.SysRoleDao;
import io.cmp.modules.sys.entity.SysDeptEntity;
import io.cmp.modules.sys.entity.SysRoleEntity;
import io.cmp.modules.sys.service.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 角色
 *
 * @author
 */
@Service("sysRoleService")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleDao, SysRoleEntity> implements SysRoleService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private SysRoleMenuService sysRoleMenuService;
	@Autowired
	private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
	@Autowired
	private SysDeptService sysDeptService;
	@Autowired
	private SysRoleDeptService sysRoleDeptService;
    @Autowired
    private SysRoleService sysRoleService;
	@Override
	@DataFilter(subDept = true, user = false)
	public PageUtils queryPage(Map<String, Object> params) {
		String roleName = (String)params.get("roleName");

        String roleCode = (String)params.get("roleCode");

		//Long createUserId = (Long)params.get("createUserId");

		IPage<SysRoleEntity> page = this.page(
			new Query<SysRoleEntity>().getPage(params),
			new QueryWrapper<SysRoleEntity>()
                    .like(StringUtils.isNotBlank(roleCode),"role_code", roleCode)
				    .like(StringUtils.isNotBlank(roleName),"role_name", roleName)

					.apply(params.get(Constant.SQL_FILTER) != null, (String)params.get(Constant.SQL_FILTER))
		);

		for(SysRoleEntity sysRoleEntity : page.getRecords()){
            //通过RoleId查询用户的数量
            if(sysRoleEntity!=null){
                sysRoleEntity.setUserCount(sysUserRoleService.queryUserCountByRoleId(sysRoleEntity.getRoleId()));

            }

			SysDeptEntity sysDeptEntity = sysDeptService.getById(sysRoleEntity.getDeptId());
			if(sysDeptEntity != null){
				sysRoleEntity.setDeptName(sysDeptEntity.getName());
			}
		}
		return new PageUtils(page);
	}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(SysRoleEntity role) {
//     先查询一下
		SysRoleEntity sysRoleEntity	=sysRoleDeptService.findRoleByroleCode(role.getRoleCode());
		if(sysRoleEntity!=null) {
			throw new RRException("角色代码重复");
		}
//		默认创建有效
		role.setIsValid(1);
        role.setCreateTime(new Date());
        this.save(role);

        //检查权限是否越权
        //checkPrems(role);

        //保存角色与菜单关系
		if(role.getMenuIdList()!=null) {
			if ( role.getMenuIdList().size() != 0) {
				sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList());
			}
		}
		//保存角色与部门关系
		if(role.getDeptIdList()!=null) {
			if (role.getDeptIdList().size() != 0) {

				sysRoleDeptService.saveOrUpdate(role.getRoleId(), role.getDeptIdList());
			}
		}

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysRoleEntity role) {

        this.updateById(role);

        //检查权限是否越权
        //checkPrems(role);


		//保存角色与菜单关系
		if(role.getMenuIdList()!=null) {
			if (role.getMenuIdList() != null || role.getMenuIdList().size() != 0) {

				sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList());
			}
		}

		//保存角色与部门关系
		if(role.getDeptIdList()!=null) {
			if (role.getDeptIdList() != null || role.getDeptIdList().size() != 0) {
				sysRoleDeptService.saveOrUpdate(role.getRoleId(), role.getDeptIdList());
			 }
		}


	}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] roleIds) {
        //删除角色
        this.removeByIds(Arrays.asList(roleIds));

        //删除角色与菜单关联
        sysRoleMenuService.deleteBatch(roleIds);

        //删除角色与用户关联
        sysUserRoleService.deleteBatch(roleIds);

		//删除角色与部门关联
		sysRoleDeptService.deleteBatch(roleIds);
    }


    @Override
	public List<Long> queryRoleIdList(Long createUserId) {
		return baseMapper.queryRoleIdList(createUserId);
	}

    @Override
    public SysRoleEntity isValid(Long roleId) {
//        查询角色的信息
        SysRoleEntity role = sysRoleService.getById(roleId);
        if (role!=null&&role.getIsValid()!=null){
            if(role.getIsValid()==1){
                logger.debug(""+role.getIsValid());
                role.setIsValid(0);
                sysRoleService.update(role);
            }else if(role.getIsValid()==0){
                logger.debug(""+role.getIsValid());
                role.setIsValid(1);
                sysRoleService.update(role);
            }
        }
        return role;
    }

	@Override
	public PageUtils queryPageByroleId(Map<String, Object> params,List roleId) {
	logger.debug(""+roleId);
		String roleName = (String)params.get("roleName");

		String roleCode = (String)params.get("roleCode");
		logger.debug(roleName+"\t"+roleCode);
		//Long createUserId = (Long)params.get("createUserId");
		String startTime = (String)params.get("startTime");
		String endTime = (String)params.get("endTime");
		IPage<SysRoleEntity> page = this.page(
				new Query<SysRoleEntity>().getPage(params),
				new QueryWrapper<SysRoleEntity>()
						.like(StringUtils.isNotBlank(roleCode),"role_code", roleCode)
						.like(StringUtils.isNotBlank(roleName),"role_name", roleName)
                        .in("role_id",roleId)
						.between(StringUtils.isNotBlank(endTime),"create_time",startTime,endTime)
						.apply(params.get(Constant.SQL_FILTER) != null, (String)params.get(Constant.SQL_FILTER))
		);

/*		for(SysRoleEntity sysRoleEntity : page.getRecords()){
			//通过RoleId查询用户的数量
			if(sysRoleEntity!=null){
				sysRoleEntity.setUserCount(sysUserRoleService.queryUserCountByRoleId(sysRoleEntity.getRoleId()));

			}

			SysDeptEntity sysDeptEntity = sysDeptService.getById(sysRoleEntity.getDeptId());
			if(sysDeptEntity != null){
				sysRoleEntity.setDeptName(sysDeptEntity.getName());
			}
		}*/
		return new PageUtils(page);
	}

	@Override
	public void notExistCurrRoleAddUser(SysRoleEntity sysRoleEntity) {
		//     先查询一下
		SysRoleEntity role	=sysRoleService.getById(sysRoleEntity.getRoleId());

		//检查权限是否越权
		//checkPrems(role);
//		保存角色与用户的关系
		logger.debug("UserIdList\t"+sysRoleEntity.getUserIdList());
		if(sysRoleEntity.getUserIdList()!=null) {

				sysUserRoleService.saveUserRole(role.getRoleId(), sysRoleEntity.getUserIdList());

		}


	}

	@Override
	public PageUtils findRoleByUserId(Map<String, Object> params) {
		String id = (String)params.get("userId");
		logger.debug("id\t"+id);
		String roleName = (String)params.get("roleName");
		String roleCode = (String)params.get("roleCode");
		IPage<SysRoleEntity> page = this.page(
				new Query<SysRoleEntity>().getPage(params),
				new QueryWrapper<SysRoleEntity>()
					.eq(StringUtils.isNotBlank(id),"creater", id)
						.like(StringUtils.isNotBlank(roleCode),"role_code", roleCode)
						.like(StringUtils.isNotBlank(roleName),"role_name", roleName)
						.apply(params.get(Constant.SQL_FILTER) != null, (String)params.get(Constant.SQL_FILTER))
		);
		for(SysRoleEntity sysRoleEntity : page.getRecords()){
			//通过RoleId查询用户的数量
			if(sysRoleEntity!=null){
				sysRoleEntity.setUserCount(sysUserRoleService.queryUserCountByRoleId(sysRoleEntity.getRoleId()));

			}

			SysDeptEntity sysDeptEntity = sysDeptService.getById(sysRoleEntity.getDeptId());
			if(sysDeptEntity != null){
				sysRoleEntity.setDeptName(sysDeptEntity.getName());
			}
		}

		return new PageUtils(page);
/*		return baseMapper.selectList(new QueryWrapper<SysRoleEntity>()
				.eq(StringUtils.isNotBlank(id),"creater", id)
				.apply(params.get(Constant.SQL_FILTER) != null, (String)params.get(Constant.SQL_FILTER))
		);*/
	}

	@Override
	public List<Long> findParentNodeByMenuId(Long menuId) {
		return sysRoleMenuService.findParentNodeByMenuId(menuId);
	}

	/**
	 * 检查权限是否越权
	 */
	/*private void checkPrems(SysRoleEntity role){
		//如果不是超级管理员，则需要判断角色的权限是否超过自己的权限
		if(role.getCreateUserId() == Constant.SUPER_ADMIN){
			return ;
		}
		
		//查询用户所拥有的菜单列表
		List<Long> menuIdList = sysUserService.queryAllMenuId(role.getCreateUserId());
		
		//判断是否越权
		if(!menuIdList.containsAll(role.getMenuIdList())){
			throw new RRException("新增角色的权限，已超出你的权限范围");
		}
	}*/



}
