

package io.cmp.modules.sys.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.cmp.common.annotation.SysLog;
import io.cmp.common.exception.RRException;
import io.cmp.common.utils.Constant;
import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.R;
import io.cmp.common.validator.ValidatorUtils;
import io.cmp.modules.app.entity.UserEntity;
import io.cmp.modules.sys.entity.SysDeptEntity;
import io.cmp.modules.sys.entity.SysMenuEntity;
import io.cmp.modules.sys.entity.SysRoleEntity;
import io.cmp.modules.sys.entity.SysUserEntity;
import io.cmp.modules.sys.service.*;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理
 *
 * @author
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends AbstractController {
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysRoleDeptService sysRoleDeptService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 角色列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("sys:role:list")
    public R list(@RequestParam Map<String, Object> params) {
        //如果不是超级管理员，则只查询自己创建的角色列表
		/*if(getUserId() != Constant.SUPER_ADMIN){
			params.put("createUserId", getUserId());
		}*/

        PageUtils page = sysRoleService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 角色列表
     */
    @GetMapping("/select")
    //@RequiresPermissions("sys:role:select")
    public R select() {
        Map<String, Object> map = new HashMap<>();

        //如果不是超级管理员，则只查询自己所拥有的角色列表
		/*if(getUserId() != Constant.SUPER_ADMIN){
			map.put("create_user_id", getUserId());
		}*/

        List<SysRoleEntity> list = (List<SysRoleEntity>) sysRoleService.listByMap(map);
        //	通过RoleId查询用户的数量
        for (SysRoleEntity sysRoleEntity : list) {
            if (sysRoleEntity != null) {
                sysRoleEntity.setUserCount(sysUserRoleService.queryUserCountByRoleId(sysRoleEntity.getRoleId()));
            }

        }

        return R.ok().put("list", list);
    }

    /**
     * 角色信息
     */
    @GetMapping("/info/{roleId}")
    //@RequiresPermissions("sys:role:info")
    public R info(@PathVariable("roleId") Long roleId) {
        SysRoleEntity role = sysRoleService.getById(roleId);

        //查询角色对应的菜单
        List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
        List<Long> ids = new ArrayList<>();
        List<SysMenuEntity> resList = new ArrayList<>();
        for (Long id: menuIdList) {
            SysMenuEntity sysMenuEntity = sysMenuService.getById(id);
            if (sysMenuEntity!=null) {

                List<SysMenuEntity> list = sysMenuService.queryListParentId(sysMenuEntity.getMenuId());
                if (list != null && list.size() != 0) {
                    for (SysMenuEntity sys : list) {
                        resList.add(sys);
                    }
                } else {
                    ids.add(id);
                }
            }
        }
//      条件过滤
        for (Long id: menuIdList) {
            if(resList==null){
                if(resList.size()==0){
                    ids.add(id);
                }

            }
        }
        logger.debug(""+ids);
        role.setMenuIdList(ids);

        //查询角色对应的部门
        List<Long> deptIdList = sysRoleDeptService.queryDeptIdList(new Long[]{roleId});
        role.setDeptIdList(deptIdList);
        role.setUserCount(sysUserRoleService.queryUserCountByRoleId(roleId));
        return R.ok().put("role", role);
    }

    /**
     * 保存角色
     */
    @SysLog("保存角色")
    @PostMapping("/save")
    @RequiresPermissions("sys:role:save")
    public R save(@RequestBody SysRoleEntity role) {
        ValidatorUtils.validateEntity(role);

        //role.setCreateUserId(getUserId());
        sysRoleService.saveRole(role);

        return R.ok();
    }

    /**
     * 修改角色
     */
    @SysLog("修改角色")
    @PostMapping("/update")
    @RequiresPermissions("sys:role:update")
    public R update(@RequestBody SysRoleEntity role) {
        ValidatorUtils.validateEntity(role);
        role.setIsValid(1);
        //role.setCreateUserId(getUserId());
        sysRoleService.update(role);

        return R.ok();
    }

    /**
     * 删除角色
     */
    @SysLog("删除角色")
    @PostMapping("/delete")
    @RequiresPermissions("sys:role:delete")
    public R delete(@RequestBody Long[] roleIds) {

//        先判断该角色下面是否有用户

        for (Long roleId:roleIds) {
           List<Long> userIds= sysUserRoleService.queryUserIdByroleId(roleId);

            if(userIds!=null&&userIds.size()!=0){

                throw new RRException("该角色下面存在用户,不能删除");

            }else{
                sysRoleService.deleteBatch(roleIds);
            }
        }





        return R.ok();
    }

    /**
     * 角色是否有效
     */
    @SysLog("角色是否有效")
    @PostMapping("/isValid/{roleId}")
    @RequiresPermissions("sys:role:update")
    public R isValid(@PathVariable("roleId") Long roleId) {

//        查询角色的信息
        SysRoleEntity role = sysRoleService.isValid(roleId);

        return R.ok().put("role", role);
    }


    /**
     * 通过角色ID查询用户
     */
    @SysLog("通过角色ID查询用户")
    @GetMapping("/queryUserListByroleId")
    //@RequiresPermissions("sys:role:select")
    public R queryUserListByroleId(@RequestParam Map<String, Object> params) {
        logger.debug("controller limit==================" + params.get("roleId"));
//   查询用户ID
        Long l = Long.valueOf((String) params.get("roleId"));
        List userIds = sysUserRoleService.queryUserIdByroleId(l);

/*		PageUtils userList =  null;
//   进行分页
		if (userIds != null && userIds.size() != 0) {
				userList = sysUserService.queryPageByrUserId(params, userIds);
		}
		if(userList!=null){
			return R.ok().put("role", userList);
		}else{
			return R.ok().put("role", userList);
		}*/

//   进行分页

        PageUtils userList = sysUserService.queryPageByrUserId(params, userIds);


        return R.ok().put("role", userList);


    }

    /**
     * 过滤掉当前角色下面的用户
     */
    @SysLog("过滤掉当前角色下面的用户")
    @GetMapping("/notExistCurrRole")
    //@RequiresPermissions("sys:role:select")
    public R notExistCurrRole(@RequestParam Map<String, Object> params) {
        logger.debug("controller limit==================" + params.get("roleId"));
//   查询用户ID
        Long roleId = Long.valueOf((String) params.get("roleId"));
        List userIds = sysUserRoleService.queryUserIdByroleId(roleId);

        PageUtils userList = null;
//   进行分页
        if (userIds != null && userIds.size() != 0) {
            userList = sysUserService.notExistCurrRole(params, userIds);
        } else {
            userList = sysUserService.queryPage(params);
        }

        return R.ok().put("role", userList);
    }

    /**
     * 为不再当前角色下的用户添加该角色
     * userIdList
     * <p>
     * roleId
     */

    @SysLog("为不再当前角色下的用户添加该角色")
    @PostMapping("/notExistCurrRoleAddUser")
    @RequiresPermissions("sys:role:save")
    public R notExistCurrRoleAddUser(@RequestBody SysRoleEntity role) {
        //role.setCreateUserId(getUserId());

        sysRoleService.notExistCurrRoleAddUser(role);

        return R.ok();
    }

    /**
     * 查看本人添加的角色
     */
    @SysLog("查看本人添加的角色")
    @GetMapping("/findRoleByUserId")
    //@RequiresPermissions("sys:role:select")
    public R findRoleByUserId(@RequestParam Map<String, Object> params) {
//        查询角色的信息
        PageUtils rolList = sysRoleService.findRoleByUserId(params);

        return R.ok().put("role", rolList);
    }

    /**
     * 角色信息
     */
    @GetMapping("/findParentNodeByMenuId/{menuId}")
    //@RequiresPermissions("sys:role:info")
    public R findParentNodeByMenuId(@PathVariable("menuId") Long menuId) {

        List<Long> menus = sysRoleService.findParentNodeByMenuId(menuId);
        return R.ok().put("role", menus);
    }

    /**
     * 判断角色代码是否重复
     */
    @GetMapping("/repeatRoleCode/{roleCode}")
    //@RequiresPermissions("sys:role:info")
    public R repeatRoleCode(@PathVariable("roleCode") String roleCode) {
        if(roleCode!=null){
            SysRoleEntity sysRoleEntity	=sysRoleDeptService.findRoleByroleCode(roleCode);
            if(sysRoleEntity!=null){
                return R.error("角色代码重复");
            }else{
                return R.ok();
            }
        }else{
            return R.error("角色代码不能为空");
        }


    }


}