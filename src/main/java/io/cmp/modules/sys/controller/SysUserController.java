

package io.cmp.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.cmp.common.annotation.SysLog;

import io.cmp.common.exception.RRException;
import io.cmp.common.utils.MenuTreeList;
import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.R;
import io.cmp.common.utils.UKTools;
import io.cmp.common.validator.Assert;
import io.cmp.common.validator.ValidatorUtils;
import io.cmp.common.validator.group.AddGroup;
import io.cmp.modules.sys.entity.*;
import io.cmp.modules.sys.form.PasswordForm;

import io.cmp.modules.sys.service.*;
import io.cmp.modules.sys.vo.BatchAddUser;
import io.cmp.modules.sys.vo.UpdateMail;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * 系统用户
 *
 * @author
 */
@RestController
@RequestMapping("/sys/user")
@PropertySource(value = "classpath:application.yml")
@ConfigurationProperties("com.upload")
public class SysUserController extends AbstractController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleService SysRoleService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysMenuViewService sysMenuViewService;
    @Autowired
    private SysMenuOperationService sysMenuOperationService;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysDeptLeaderService sysDeptLeaderService;



    @Value("${com.upload.location}")
    private String location;
    @Value("${com.upload.defaultHeadPortrait}")
    private String defaultHeadPortrait;
    /**
     * 所有用户列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("sys:user:list")
    public R list(@RequestParam Map<String, Object> params) {
        //只有超级管理员，才能查看所有管理员列表
		/*if(getUserId() != Constant.SUPER_ADMIN){
			params.put("createUserId", getUserId());
		}*/
        PageUtils page = sysUserService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取登录的用户信息
     */
    @GetMapping("/info")
    public R info() {

        List<Long> roleList = sysUserService.userRoleListByUserid(getUser().getUserId());
        SysUserEntity sysUserEntity = getUser();
        sysUserEntity.setRoleIdList(roleList);
        if(sysUserEntity.getDeptId()!=null){
          SysDeptEntity dept= sysDeptService.getById(sysUserEntity.getDeptId());
          if(dept!=null){
              sysUserEntity.setDeptName(dept.getName());
          }
        }


        return R.ok().put("user", sysUserEntity);
    }

    /**
     * 修改登录用户密码
     */
    @SysLog("修改密码")
    @PostMapping("/password")
    //@RequiresPermissions("sys:user:password")
    public R password(@RequestBody PasswordForm form) {
        if (getUserId() == 1L) {
            return R.error("系统管理员不能修改密码");
        }
        if (getUserId() == 2L) {
            return R.error("系统管理员不能修改密码");
        }
        Assert.isBlank(form.getNewPassword(), "新密码不为能空");

        //sha256加密
        String password = new Sha256Hash(form.getPassword(), getUser().getSalt()).toHex();
        //sha256加密
        String newPassword = new Sha256Hash(form.getNewPassword(), getUser().getSalt()).toHex();

        //更新密码
        boolean flag = sysUserService.updatePassword(getUserId(), password, newPassword);

        if (!flag) {
            return R.error("原密码不正确");
        }

        return R.ok();
    }

    /**
     * 用户信息
     */
    @GetMapping("/info/{userId}")
    //@RequiresPermissions("sys:user:info")
    public R info(@PathVariable("userId") Long userId) {
        SysUserEntity user = sysUserService.getById(userId);

        //获取用户所属的角色列表
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
        if (user != null) {
            user.setRoleIdList(roleIdList);

        }


        return R.ok().put("user", user);
    }

    /**
     * 保存用户
     */
    @SysLog("保存用户")
    @PostMapping("/save")
    @RequiresPermissions("sys:user:save")
    public R save(@RequestBody SysUserEntity user) {
        // ValidatorUtils.validateEntity(user, AddGroup.class);

        //user.setCreateUserId(getUserId());
        user.setIsDeptLeader(0);
        sysUserService.saveUser(user);

        return R.ok();
    }

    /**
     * 创建用户默认密码
     */
    @SysLog("创建用户默认密码")
    @PostMapping("/createDefaultPassword")
    @RequiresPermissions("sys:user:createDefaultPassword")
    public R createDefaultPassword(@RequestBody SysUserEntity user) {
//		此处不需要表单验证
        //ValidatorUtils.validateEntity(user, AddGroup.class);
        //logger.debug(user.getAccountVerdue());
        // logger.debug(user.getAccountVerdue().getTime());
        //user.setCreateUserId(getUserId());

        //表单校验
        //        设置图片位置
        user.setLocation(location);
//        设置默认地址
        user.setDefaultHeadPortrait(defaultHeadPortrait);
        ValidatorUtils.validateEntity(user);
        user.setIsDeptLeader(0);
        user.setAutoUnlock(0);
        sysUserService.createDefaultPassword(user);

        return R.ok();
    }



    /**
     * 修改用户
     */
    @SysLog("修改用户")
    @PostMapping("/update")
    @RequiresPermissions("sys:user:update")

    public R update(@RequestBody SysUserEntity user) {

        if (user.getUserId() == 1L) {
            return R.error("系统管理员不能修改");
        }
        if (user.getUserId() == 2L) {
            return R.error("系统管理员不能修改");
        }
        ValidatorUtils.validateEntity(user);

        //user.setCreateUserId(getUserId());

        sysUserService.update(user);

        return R.ok();
    }

    /**
     * 删除用户
     */
    @SysLog("删除用户")
    @PostMapping("/delete")
    @RequiresPermissions("sys:user:delete")
    public R delete(@RequestBody Long[] userIds) {
        if (ArrayUtils.contains(userIds, 1L)) {
            return R.error("系统管理员不能删除");
        }
        if (ArrayUtils.contains(userIds, 2L)) {
            return R.error("系统管理员不能删除");
        }

        if (ArrayUtils.contains(userIds, getUserId())) {
            return R.error("当前用户不能删除");
        }
        logger.debug(""+userIds);
	/*	for (Long userId:userIds) {
			List<Long> roleList=sysUserService.userRoleListByUserid(userId);

			Long[] a = new Long[20];
			Long[] l = (Long[]) roleList.toArray(a);
		}*/
        sysUserRoleService.deleteBatchByUserId(userIds);


        sysUserService.deleteBatch(userIds);

        return R.ok();
    }


    /**
     * 重置密码
     */
    @SysLog("重置密码")
    @PostMapping("/resetPass/{userId}")
    @RequiresPermissions("sys:user:resetPass")
    public R resetPass(@PathVariable("userId") Long userId) {


        sysUserService.resetresetPass(userId);

        return R.ok();
    }

    /**
     * 不用就直接可以修改密码的方法
     */
    @SysLog("修改密码")
    @PostMapping("/freeToken")
    @RequiresPermissions("sys:user:freeToken")
    public R freeToken(@RequestBody SysUserEntity sysUserEntity) {

        SysUserEntity userEntity = sysUserService.queryByUserName(sysUserEntity.getUsername());
        userEntity.setPassword(new Sha256Hash(sysUserEntity.getPassword(), userEntity.getSalt()).toHex());

        if (userEntity != null) {

            sysUserService.update(userEntity,
                    new QueryWrapper<SysUserEntity>().eq("user_id", userEntity.getUserId()).eq("username", userEntity.getUsername()));
        }
        return R.ok();
    }

    /**
     * 用户是否有效
     */
    @SysLog("是否有效")
    @PostMapping("/isvalid/{userId}")

    public R isvalid(@PathVariable("userId") Long userId) {


        sysUserService.userIsValid(userId);

        return R.ok();
    }

    /**
     * 用户下面的角色
     */
    @SysLog("用户下面的角色")
    @GetMapping("/userRoleList")
    //@RequiresPermissions("sys:menu:currentUserMenu")
    public R userRoleList(@RequestParam Map<String, Object> params) {
        String res = (String) params.get("userId");
        logger.debug("res\t" + res);
        Long l = Long.valueOf(res);
//      admin和manger都是拥有哦所有的角色
        if (l == 1 || l == 2) {
            return R.ok().put("roleList", SysRoleService.queryPage(params));

        }
        List<Long> roleList = sysUserService.userRoleListByUserid(l);
        PageUtils pageUtils = null;
        if (roleList == null || roleList.size() == 0) {
            return R.ok().put("roleList", pageUtils);
        } else {
            return R.ok().put("roleList", SysRoleService.queryPageByroleId(params, roleList));
        }


    }

    /**
     * batchAddUsers
     * 批量添加用户
     */
    @SysLog("批量添加用户")
    @PostMapping("/batchAddUsers")
    @RequiresPermissions("sys:user:batchAddUsers")
    public R batchAddUsers(@RequestBody BatchAddUser batchAddUser) {
//		此处不需要表单验证
        //ValidatorUtils.validateEntity(user, AddGroup.class);
        //logger.debug(user.getAccountVerdue());
        // logger.debug(user.getAccountVerdue().getTime());
        //user.setCreateUserId(getUserId());
        batchAddUser.setIsDeptLeader(0);
        sysUserService.batchAddUsers(batchAddUser);

        return R.ok();
    }
    /*	*//**
     * 当前用户的下面的菜单
     *//*

	@SysLog("用户的菜单")
	@GetMapping("/currentUserMenu/{userId}")
	@RequiresPermissions("sys:menu:currentUserMenu")
	public R currentUserMenu(@PathVariable("userId") long userId){

		if(sysUserService.getById(userId)==null){
			return R.ok().put("menuList",new ArrayList<>());

		}

//		查询当前的数据



//      先查询出来当前用户的下面的菜单ID
		List<Long> menuIds=sysUserService.findMenuIdByUserId(userId);

//		递归出所有的菜单
		Long l=new Long(0);
//		通过查询出来的menuIds去递归出菜单信息

		List<SysMenuEntity> list=MenuTreeList.getMenuTreeList().getMenuList(l,sysUserService,sysMenuService,menuIds);

//		再去判段该菜单是否在此

		return R.ok().put("menuList",list);
	}*/


    /**
     * 当前用户的下面的菜单
     */

    @SysLog("用户的菜单")
    @GetMapping("/currentUserMenu/{userId}")
    //@RequiresPermissions("sys:menu:currentUserMenu")
    public R currentUserMenuNow(@PathVariable("userId") long userId) {

        if (sysUserService.getById(userId) == null) {
            return R.ok().put("menuList", new ArrayList<>());

        }

		/*List<SysMenuEntity> list=sysMenuService.getUserMenuList(userId);

		return R.ok().put("menuList",list);*/

//		查询当前的数据


//      先查询出来当前用户的下面的菜单ID
        List<Long> menuIds = sysUserService.findMenuIdByUserId(userId);
        Integer b = 1;
        Long menuIdss = b.longValue();
        menuIds.add(menuIdss);
//		递归出所有的菜单
        //Long l=new Long(0);
        Integer a = 0;
        Long menuId = a.longValue();
//		通过查询出来的menuIds去递归出菜单信息

        //List<SysMenuEntity> list=MenuTreeList.getMenuTreeList().getMenuList(userId,sysUserService,sysMenuService,menuIds);
  /*    if(userId!=1&&userId!=2){
       	logger.debug("进了++++");
	   List<SysMenuEntity> menuList= sysMenuService.getCurrentUserMenuList(userId);
	   return R.ok().put("menuList",menuList);
       }*/
        List<SysMenuEntity> list = MenuTreeList.getMenuTreeList().getMenuList2(userId, menuId, sysUserService, sysMenuService, menuIds, sysMenuViewService, sysMenuOperationService);

//		再去判段该菜单是否在此
        return R.ok().put("menuList", list);
    }

    /**
     * 用户下面的一级菜单
     */

    @SysLog("用户的一级菜单")
    @GetMapping("/queryLevelOneMenu/{userId}/{parentId}")
    //@RequiresPermissions("sys:menu:currentUserMenu")
    public R levelOneMenu(@PathVariable("userId") Integer userId, @PathVariable("parentId") Integer parentId) {
        SysUserEntity sysUserEntity = sysUserService.getById(userId);
        if (sysUserEntity.getUsername().trim().equals("admin")) {
            List<SysMenuEntity> list = sysUserService.queryLevelOneMenu(null, null, parentId);
            return R.ok().put("menuList", list);
        } else if (sysUserEntity.getUsername().trim().equals("manger")) {
            List<SysMenuEntity> list = sysUserService.queryLevelOneMenu(null, null, parentId);
            return R.ok().put("menuList", list);
        } else {

            List<SysMenuEntity> list = sysUserService.queryLevelOneMenu(userId, null, parentId);
            return R.ok().put("menuList", list);

        }

    }


    /**
     * 用户下面的二级菜单
     */

    @SysLog("用户的二级菜单")
    @GetMapping("/queryLevelTwoMenu/{userId}/{parentId}")
    //@RequiresPermissions("sys:menu:currentUserMenu")
    public R queryLevelTwoMenu(@PathVariable("userId") Integer userId, @PathVariable("parentId") Integer parentId) {
        R r = sysMenuService.queryTwoLevelMenu(userId, parentId);
        return r;
    }

    /**
     * 查询所有用户的以及所属部门
     */
    @GetMapping("/deptBelowUser")

    public R userList() {


        List<SysUserEntity> userList = sysUserService.userList();
        return R.ok().put("userList", userList);
    }

    /**
     * 判断用户代码是否重复
     */
    @GetMapping("/repeatUserName/{userName}")
    //@RequiresPermissions("sys:role:info")
    public R repeatUserName(@PathVariable("userName") String userName) {
        if (userName != null) {
            SysUserEntity sysUserEntity = sysUserService.queryByUserName(userName);
            if (sysUserEntity != null) {
                return R.error("用户代码重复");
            } else {
                return R.ok();
            }
        } else {
            return R.error("用户代码不能为空");
        }


    }

    /**
     * 通过deptId查询该部门下表的员工
     */

    @SysLog("通过deptId查询该部门下表的员工")
    @GetMapping("/findUserListByDeptId/{deptId}")

    public R findUserListByDeptId(@PathVariable("deptId") Long deptId) {


        List<SysUserEntity> userList = sysUserService.findByDeptId(deptId);

        return R.ok().put("userList", userList);
    }

    /**
     * 是否为部门负责人
     */

    @SysLog("是否为部门负责人")
    @PostMapping("/isDeptLeader/{userId}")

    public R isDeptLeader(@PathVariable("userId") Long userId) {


        SysUserEntity sysUserEntity = sysUserService.getById(userId);

        if (sysUserEntity != null) {
            if (sysUserEntity.getIsDeptLeader() != null) {
                if (sysUserEntity.getIsDeptLeader() == 0) {
                    sysUserEntity.setIsDeptLeader(1);
//                  获取该用户的部门
                    if (sysUserEntity.getDeptId() != null) {
                        sysDeptService.getById(sysUserEntity.getDeptId());
//                      往中间表中添加负责人
                        SysDeptLeaderEntity sysDeptLeaderEntity = new SysDeptLeaderEntity();
                        sysDeptLeaderEntity.setDeptId(sysUserEntity.getDeptId());
                        sysDeptLeaderEntity.setUserId(sysUserEntity.getUserId());
                        sysDeptLeaderService.save(sysDeptLeaderEntity);
                    }

                } else {
                    sysUserEntity.setIsDeptLeader(0);
                    //  sysDeptLeaderService.deleteBatchs(new Long[]{sysUserEntity.getDeptId()});
                    sysDeptLeaderService.deleteByUserId(userId);

                }

            }
            sysUserService.update(sysUserEntity);
        }

        return R.ok();
    }
    /**
     * 上传头像
     */

    @SysLog("修改用户头像")
    @PostMapping(value = "/upload")


    //@PostMapping(value = "/upload",consumes = "multipart/*",headers = "content-type=multipart/form-date")
    //@RequiresPermissions("sys:user:upload")
    public R upload(@ApiParam(value = "文件") MultipartFile headPortrait,HttpServletRequest request) {

        SysUserEntity user = getUser();
        logger.debug("headPortrait = " + headPortrait);
        user.setHeadPortrait(headPortrait);
        //        设置图片位置
        user.setLocation(location);
        logger.debug("location\t"+location);
//        设置默认地址
        user.setDefaultHeadPortrait(defaultHeadPortrait);
        if (user.getUserId() == 1L) {
            return R.error("系统管理员不能修改");
        }
        if (user.getUserId() == 2L) {
            return R.error("系统管理员不能修改");
        }
        ValidatorUtils.validateEntity(user);

        //user.setCreateUserId(getUserId());

        sysUserService.upload(user);

        return R.ok();
    }


    /**
     * 修改邮箱或者手机号
     */
    /**
     * 判断用户代码是否重复
     */
    @PostMapping("/updateMailOrPhoneNum")
    //@RequiresPermissions("sys:role:info")
    public R updateMailOrPhoneNum(@RequestBody UpdateMail condition) {

        SysUserEntity sys=getUser();
        if(sys!=null) {

            if (sys.getUsername().trim().equals("admin")) {
                return R.error("系统管理员不能修改");
            }
            if(sys.getUsername().trim().equals("manger")){
                return R.error("系统管理员不能修改");

            }
        }
      //  ValidatorUtils.validateEntity(sys, AddGroup.class);
        if(condition.getEmail()!=null&&condition.getEmail().trim()!=""){
           boolean flag= condition.getEmail().trim().matches("^\\s*$|^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
           if(flag){
               sys.setEmail(condition.getEmail());
           }else{
               return R.error("邮箱格式不正确");

           }


        }
        if(condition.getMobile()!=null&&condition.getMobile().trim()!=""){
            boolean flag=condition.getMobile().trim().matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
            if(flag){
                sys.setMobile(condition.getMobile());
            }else{
                return R.error("手机号格式不正确");
            }



        }
        boolean flag=sysUserService.updateById(sys);
        if(flag){
            return R.ok();
        }else{
            return R.error("修改失败");
        }



    }


}
