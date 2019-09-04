

package io.cmp.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.cmp.common.exception.RRException;
import io.cmp.common.utils.Constant;
import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.Query;
import io.cmp.common.utils.UKTools;
import io.cmp.modules.sys.dao.SysUserDao;
import io.cmp.modules.sys.entity.*;
import io.cmp.modules.sys.service.*;
import io.cmp.modules.sys.vo.BatchAddUser;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 系统用户
 *
 * @author
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private SysRoleDeptService sysRoleDeptService;
    @Autowired
    private SysMenuViewService sysMenuViewService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysMenuOperationService sysMenuOperationService;
    @Autowired
    private SysDeptLeaderService sysDeptLeaderService;
    @Autowired
    private UkUserService ukUserService;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String username = (String) params.get("username");
        //Long createUserId = (Long)params.get("createUserId");
//		按照真实名字模糊查询
        String realname = (String) params.get("realname");

        String startTime = (String) params.get("startTime");
        String endTime = (String) params.get("endTime");
        IPage<SysUserEntity> page = this.page(
                new Query<SysUserEntity>().getPage(params),
                new QueryWrapper<SysUserEntity>()
                        .like(StringUtils.isNotBlank(username), "username", username)
                        .like(StringUtils.isNotBlank(realname), "realname", realname)
                        .between(StringUtils.isNotBlank(endTime), "create_time", startTime, endTime)
                        .apply(params.get(Constant.SQL_FILTER) != null, (String) params.get(Constant.SQL_FILTER))
        );

        for (SysUserEntity sysUserEntity : page.getRecords()) {
            List<Long> roleList = sysUserService.userRoleListByUserid(sysUserEntity.getUserId());
            SysDeptEntity sysDeptEntity = sysDeptService.getById(sysUserEntity.getDeptId());
            if (sysDeptEntity != null) {
                sysUserEntity.setRoleIdList(roleList);
                sysUserEntity.setDeptName(sysDeptEntity.getName());
            }
        }

        return new PageUtils(page);
    }

    @Override
    public List<String> queryAllPerms(Long userId) {
        return baseMapper.queryAllPerms(userId);
    }

    @Override
    public List<Long> queryAllMenuId(Long userId) {
        return baseMapper.queryAllMenuId(userId);
    }

    @Override
    public SysUserEntity queryByUserName(String username) {
        return baseMapper.queryByUserName(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(SysUserEntity user) {
        user.setCreateTime(new Date());
        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
        user.setSalt(salt);
//      用户户选择的出生日期比当前时间大
        if (user.getBirthBeBorn() != null) {
            if (user.getBirthBeBorn().getTime() > new Date().getTime()) {

                throw new RRException("出生日期不能比当前时间早");


            }


        }
        this.save(user);

        //检查角色是否越权
        //checkRole(user);

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysUserEntity user) {
        logger.debug("修改用户======");
        user.setStatus(1);
        this.updateById(user);
        //检查角色是否越权
        //checkRole(user);

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());


        //同步用户数据到UKF用户表
        Long userId= user.getUserId();
        String userName= user.getUsername();
        String realName= user.getRealname();
        String mobile=  user.getMobile();
        String email= user.getEmail();
        logger.info("userId="+userId);
        logger.info("userName="+userName);
        logger.info("realName="+realName);
        logger.info("mobile="+mobile);
        logger.info("email="+email);

        UkUserEntity ukUserEntity = new UkUserEntity();

        if(!StringUtils.isBlank(userName)) {
            ukUserEntity.setUsername(userName);
        }
        if(!StringUtils.isBlank(userName)) {
            ukUserEntity.setPassword(UKTools.md5(userName));
        }
        if(!StringUtils.isBlank(realName)) {
            ukUserEntity.setNickname(realName);
        }
        if(!StringUtils.isBlank(mobile)) {
            ukUserEntity.setMobile(mobile);
        }
        if(!StringUtils.isBlank(email)) {
            ukUserEntity.setEmail(email);
        }
        if(!StringUtils.isBlank(userName)) {
            ukUserEntity.setUname(userName);
        }
        ukUserEntity.setUserId(userId);
        ukUserEntity.setSecureconf("5");
        ukUserEntity.setOrgi("ukewo");
        ukUserEntity.setOrgid("ukewo");
        ukUserEntity.setCreatetime(new Date());
        ukUserEntity.setUpdatetime(new Date());
        ukUserEntity.setPassupdatetime(new Date());

        ukUserService.updateById(ukUserEntity);


    }

    @Override
    public void deleteBatch(Long[] userId) {
        this.removeByIds(Arrays.asList(userId));
        ukUserService.removeByIds(Arrays.asList(userId));
    }

    @Override
    public boolean updatePassword(Long userId, String password, String newPassword) {
        SysUserEntity userEntity = new SysUserEntity();
        userEntity.setPassword(newPassword);
        return this.update(userEntity,
                new QueryWrapper<SysUserEntity>().eq("user_id", userId).eq("password", password));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDefaultPassword(SysUserEntity user) {
        user.setCreateTime(new Date());
        //user.setAccountVerdue(user.getAccountVerdue(), DateUtils.DATE_PATTERN);
        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setPassword(new Sha256Hash(user.getUsername(), salt).toHex());
        user.setSalt(salt);
        if (user.getAutoUnlock() == null) {
            user.setAutoUnlock(0);

        }
        if (user.getFirstLogin() == null) {
            user.setFirstLogin(0);

        }

        user.setStatus(1);
        if (user.getExpirationTime() == null) {
            user.setExpirationTime(null);
        } else {
            if (user.getExpirationTime().getTime() - new Date().getTime() > 0) {
                user.setExpirationTime(user.getExpirationTime());
            } else {
                throw new RRException("选择比当前时间大的时间");
            }

        }
        //      用户户选择的出生日期比当前时间大
        if (user.getBirthBeBorn() != null) {
            if (user.getBirthBeBorn().getTime() > new Date().getTime()) {
                throw new RRException("出生日期不能比当前时间早 ");

            }
        }
//        默认给一个头像
        //user.setHeadPortraits(user.getDefaultHeadPortrait());
        user.setHeadPortraits(null);
        this.save(user);

        //检查角色是否越权
        //checkRole(user);

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());

        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");

        //同步用户数据到UKF用户表
        Long userId= user.getUserId();
        String userName= user.getUsername();
        String realName= user.getRealname();
        String mobile=  user.getMobile();
        String email= user.getEmail();

        logger.info("uuid="+uuid);
        logger.info("userId="+userId);
        logger.info("userName="+userName);
        logger.info("realName="+realName);
        logger.info("mobile="+mobile);
        logger.info("email="+email);

        UkUserEntity ukUserEntity = new UkUserEntity();
        ukUserEntity.setId(uuid);
        ukUserEntity.setUserId(userId);

        if(!StringUtils.isBlank(userName)) {
            ukUserEntity.setUsername(userName);
        }
        if(!StringUtils.isBlank(userName)) {
            ukUserEntity.setPassword(UKTools.md5(userName));
        }
        if(!StringUtils.isBlank(realName)) {
            ukUserEntity.setNickname(realName);
        }
        if(!StringUtils.isBlank(mobile)) {
            ukUserEntity.setMobile(mobile);
        }
        if(!StringUtils.isBlank(email)) {
            ukUserEntity.setEmail(email);
        }
        if(!StringUtils.isBlank(userName)) {
            ukUserEntity.setUname(userName);
        }

        ukUserEntity.setSecureconf("5");
        ukUserEntity.setOrgi("ukewo");
        ukUserEntity.setOrgid("ukewo");
        ukUserEntity.setCreatetime(new Date());
        ukUserEntity.setUpdatetime(new Date());
        ukUserEntity.setPassupdatetime(new Date());

        ukUserService.insertUkUser(ukUserEntity);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetresetPass(Long userId) {
        SysUserEntity userEntity = sysUserService.getById(userId);

        userEntity.setPassword(new Sha256Hash(userEntity.getUsername(), userEntity.getSalt()).toHex());

        String userName= userEntity.getUsername();
        logger.info("userName="+userName);

        UkUserEntity ukUserEntity = new UkUserEntity();
        ukUserEntity.setUserId(userEntity.getUserId());

        if(!StringUtils.isBlank(userName)) {
            ukUserEntity.setPassword(UKTools.md5(userName));
        }
        ukUserService.updateById(ukUserEntity);

        return this.update(userEntity,
                new QueryWrapper<SysUserEntity>().eq("user_id", userId).eq("username", userEntity.getUsername()));
    }

    @Override
    public boolean userIsValid(Long userId) {

        SysUserEntity userEntity = sysUserService.getById(userId);
        userEntity.setPassword(userEntity.getPassword());
        userEntity.setSalt(userEntity.getSalt());
        if (userEntity != null) {
            if (userEntity.getStatus() == 1) {
                userEntity.setStatus(0);
            } else {
                userEntity.setStatus(1);
            }

        }


        //userEntity.setPassword(new Sha256Hash(userEntity.getPassword(), userEntity.getSalt()).toHex());
        return this.update(userEntity,
                new QueryWrapper<SysUserEntity>().eq("user_id", userId).eq("username", userEntity.getUsername()));
    }

    @Override
    public List<Long> userRoleListByUserid(Long userId) {
        return sysUserDao.queryRoleListByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddUsers(BatchAddUser batchAddUser) {
        logger.debug(""+batchAddUser.getScopeNum());
//   查出来一个默认的用户
        SysUserEntity sysUserEntity = new SysUserEntity();
        sysUserEntity.setExpirationTime(null);
        sysUserEntity.setCreateTime(new Date());
        sysUserEntity.setEmail(null);
        sysUserEntity.setMobile(null);
        sysUserEntity.setExtension(null);
        sysUserEntity.setStatus(1);
        sysUserEntity.setAutoUnlock(0);
//		设置部门
        if (batchAddUser.getDeptId() != null) {
            sysUserEntity.setDeptId(batchAddUser.getDeptId());

        }


        sysUserEntity.setFirstLogin(0);
        if (batchAddUser != null) {
            if (batchAddUser.getPrefix() != null) {

                if (batchAddUser.getScopeNum() != null) {

                    if (batchAddUser.getScopeNum1() != null) {
                        if (batchAddUser.getScopeNum() < 0) {
                            throw new RRException("最小值不能小于0");

                        }
                        if (batchAddUser.getScopeNum1() == 0) {
                            throw new RRException("最大值不能为0");
                        }
                        if (batchAddUser.getScopeNum() > batchAddUser.getScopeNum1()) {
                            throw new RRException("区间范围有误");

                        }

                        for (int i = batchAddUser.getScopeNum(); i <= batchAddUser.getScopeNum1(); i++) {
//						设置真实名字
                            String name = batchAddUser.getPrefix() + (i);

                            sysUserEntity.setRealname(name);
                            sysUserEntity.setUsername(name);
                            String salt = RandomStringUtils.randomAlphanumeric(20);
                            sysUserEntity.setPassword(new Sha256Hash(name, salt).toHex());
                            sysUserEntity.setSalt(salt);


                            //先去查询一下数据库中有改用户名-->判断是否重复
                            SysUserEntity sysUser = sysUserDao.queryByUserName(name);
                            if (sysUser == null) {
                                this.save(sysUserEntity);

                                String uuid = UUID.randomUUID().toString();
                                uuid = uuid.replace("-", "");

                                //同步用户数据到UKF用户表
                                Long userId= sysUserEntity.getUserId();
                                String userName= sysUserEntity.getUsername();
                                String realName= sysUserEntity.getRealname();
                                String mobile=  sysUserEntity.getMobile();
                                String email= sysUserEntity.getEmail();

                                logger.info("uuid="+uuid);
                                logger.info("userId="+userId);
                                logger.info("userName="+userName);
                                logger.info("realName="+realName);
                                logger.info("mobile="+mobile);
                                logger.info("email="+email);

                                UkUserEntity ukUserEntity = new UkUserEntity();
                                ukUserEntity.setId(uuid);
                                ukUserEntity.setUserId(userId);

                                if(!StringUtils.isBlank(userName)) {
                                    ukUserEntity.setUsername(userName);
                                }
                                if(!StringUtils.isBlank(userName)) {
                                    ukUserEntity.setPassword(UKTools.md5(userName));
                                }
                                if(!StringUtils.isBlank(realName)) {
                                    ukUserEntity.setNickname(realName);
                                }
                                if(!StringUtils.isBlank(mobile)) {
                                    ukUserEntity.setMobile(mobile);
                                }
                                if(!StringUtils.isBlank(email)) {
                                    ukUserEntity.setEmail(email);
                                }
                                if(!StringUtils.isBlank(userName)) {
                                    ukUserEntity.setUname(userName);
                                }

                                ukUserEntity.setSecureconf("5");
                                ukUserEntity.setOrgi("ukewo");
                                ukUserEntity.setOrgid("ukewo");
                                ukUserEntity.setCreatetime(new Date());
                                ukUserEntity.setUpdatetime(new Date());
                                ukUserEntity.setPassupdatetime(new Date());

                                ukUserService.insertUkUser(ukUserEntity);

                            }

                            List<Long> roleIds = batchAddUser.getRoleIdList();
                            if (roleIds != null || roleIds.size() > 0) {

                                //保存用户与角色关系
                                sysUserRoleService.saveOrUpdate(sysUserEntity.getUserId(), roleIds);


                            }

                        }
                    }

                }

            }

        }
    }

    @Override
    public PageUtils queryPageByrUserId(Map<String, Object> params, List list) {

        logger.debug("service limit==================" + params.get("limit"));
        String username = (String) params.get("username");
        //Long createUserId = (Long)params.get("createUserId");
//		按照真实名字模糊查询
        String realname = (String) params.get("realname");

        String startTime = (String) params.get("startTime");
        String endTime = (String) params.get("endTime");
        if (list == null || list.size() == 0) {
            logger.debug("进了");
            IPage<SysUserEntity> page = this.page(
                    new Query<SysUserEntity>().getPage(params),
                    new QueryWrapper<SysUserEntity>()
                            .eq("password", "1123")
                            .between(StringUtils.isNotBlank(endTime), "create_time", startTime, endTime)
                            .apply(params.get(Constant.SQL_FILTER) != null, (String) params.get(Constant.SQL_FILTER))
            );
            return new PageUtils(page);
        } else {
            IPage<SysUserEntity> page = this.page(
                    new Query<SysUserEntity>().getPage(params),
                    new QueryWrapper<SysUserEntity>()
                            .like(StringUtils.isNotBlank(username), "username", username)
                            .like(StringUtils.isNotBlank(realname), "realname", realname)
                            .in("user_id", list)
                            .between(StringUtils.isNotBlank(endTime), "create_time", startTime, endTime)
                            .apply(params.get(Constant.SQL_FILTER) != null, (String) params.get(Constant.SQL_FILTER))
            );


            for (SysUserEntity sysUserEntity : page.getRecords()) {
                List<Long> roleList = sysUserService.userRoleListByUserid(sysUserEntity.getUserId());
                SysDeptEntity sysDeptEntity = sysDeptService.getById(sysUserEntity.getDeptId());
                if (sysDeptEntity != null) {
                    // sysUserEntity.setRoleIdList(roleList);
                    sysUserEntity.setDeptName(sysDeptEntity.getName());
                }
            }

            return new PageUtils(page);
        }
    }

    @Override
    public List<Long> findMenuIdByUserId(long userId) {
        return sysUserDao.findMenuIdByUserId(userId);

    }

    @Override
    public List<SysMenuEntity> findMenuByParentId(long menuId, List<Long> menuIds) {
        return sysUserDao.findMenuByParentId(menuId, menuIds);
    }

    @Override
    public List<SysMenuEntity> findMenuByParentId1(Long menuId, Integer toolBar) {
/*		List<SysMenuEntity> lists=baseMapper.findMenuByParentId1(menuId,toolBar);
		SysMenuEntity ss =new SysMenuEntity();
		for (SysMenuEntity menuVo:lists) {


		if(menuVo.getName().equals("视图")){
//                    查询菜单视图表
			// menuVo2.setMenuViewList1(sysMenuViewService.queryMenuViewList(menuVo.getMenuId()));
			List<SysMenuViewEntity> list=sysMenuViewService.queryMenuViewList(menuVo.getMenuId());
			logger.debug("list\t"+list);
			// Collections.reverse(list);
			List<SysMenuEntity> resList=new ArrayList<>();
			for (SysMenuViewEntity s:list) {
				SysMenuEntity sysMenuEntity = new SysMenuEntity();
				sysMenuEntity.setParentId(s.getMenuId());
				sysMenuEntity.setUrl(s.getViewUrl());
				sysMenuEntity.setName(s.getViewName());
				sysMenuEntity.setMenuId(s.getId());
				//menuVo2.getList().add(sysMenuEntity);
				// menuVosList.add(sysMenuEntity);
				resList.add(sysMenuEntity);
			}
			menuVo.setList(resList);
			logger.debug(menuVo.getList());
			//menuVo2.setList(menuVo.getList());
			//menuVo2.setMenuViewList1(list);
			// menuVo2.setList(sysMenuViewService.queryMenuViewList(menuVo.getMenuId()));
		}
		if(menuVo.getName().equals("操作")){
			//menuVo2.setMenuOperationList1(sysMenuOperationService.queryOperationViewList(menuVo.getMenuId()));
			// menuVo2.setList(sysMenuOperationService.queryOperationViewList(menuVo.getMenuId()));
			List<SysMenuOperationEntity> list=   sysMenuOperationService.queryOperationViewList(menuVo.getMenuId());
			// Collections.reverse(list);
			for (SysMenuOperationEntity s:list) {
				SysMenuEntity sysMenuEntity = new SysMenuEntity();
				sysMenuEntity.setParentId(s.getMenuId());
				sysMenuEntity.setMenuId(s.getId());
				sysMenuEntity.setName(s.getOperationName());

				//menuVo2.getList().add(sysMenuEntity);

				//  menuVosList.add(sysMenuEntity);

			}


		}

		ss.setList(menuVo.getList());
		}*/


        //return ss.getList();

        return baseMapper.findMenuByParentId1(menuId, toolBar);
    }

    @Override
    public PageUtils notExistCurrRole(Map<String, Object> params, List list) {

        logger.debug("service limit==================" + params.get("limit"));
        String username = (String) params.get("username");
        //Long createUserId = (Long)params.get("createUserId");
//		按照真实名字模糊查询
        String realname = (String) params.get("realname");

        String startTime = (String) params.get("startTime");
        String endTime = (String) params.get("endTime");
        IPage<SysUserEntity> page = this.page(
                new Query<SysUserEntity>().getPage(params),
                new QueryWrapper<SysUserEntity>()
                        .like(StringUtils.isNotBlank(username), "username", username)
                        .like(StringUtils.isNotBlank(realname), "realname", realname)
                        .notIn("user_id", list)
                        .between(StringUtils.isNotBlank(endTime), "create_time", startTime, endTime)
                        .apply(params.get(Constant.SQL_FILTER) != null, (String) params.get(Constant.SQL_FILTER))
        );


        for (SysUserEntity sysUserEntity : page.getRecords()) {
            List<Long> roleList = sysUserService.userRoleListByUserid(sysUserEntity.getUserId());
            SysDeptEntity sysDeptEntity = sysDeptService.getById(sysUserEntity.getDeptId());
            if (sysDeptEntity != null) {
                // sysUserEntity.setRoleIdList(roleList);
                sysUserEntity.setDeptName(sysDeptEntity.getName());
            }
        }

        return new PageUtils(page);
    }

    @Override
    public List<SysMenuEntity> queryLevelOneMenu(Integer userId, Integer toolBar, Integer parentId) {
        return baseMapper.queryLevelOneMenu(userId, toolBar, parentId);
    }

    @Override
    public List<SysMenuEntity> queryLevelTwoMenu(Integer userId, Integer toolBar, Integer parentId) {
        return baseMapper.queryLevelTwoMenu(userId, toolBar, parentId);
    }

    @Override
    public List<SysUserEntity> userList() {

//		查询出所有的用户
        List<SysUserEntity> list = baseMapper.userList();
//		最终返回的数据

        List<SysDeptEntity> result = new ArrayList<>();
        for (SysUserEntity sysUserEntity : list) {
//			取出部门ID
            if (sysUserEntity.getDeptId() != null) {
                List<SysDeptEntity> res = new ArrayList<>();
//				本用户的部门
                SysDeptEntity sysDeptEntity = sysDeptService.getById(sysUserEntity.getDeptId());
                res.add(sysDeptEntity);
                sysUserEntity.setSysDeptEntity(res);

            }


/*//		通过用户ID查询部门负责人的数据
            List<SysDeptLeaderEntity> deptList = sysDeptLeaderService.findByUserId(sysUserEntity.getUserId());

            for (SysDeptLeaderEntity sysDeptLeaderEntity : deptList) {
                //List<SysDeptEntity> res = new ArrayList<>();
                if(sysDeptLeaderEntity!=null){
                    SysDeptEntity sysDeptEntity=  sysDeptService.getById(sysDeptLeaderEntity.getDeptId());
                   // res.add(sysDeptEntity);
                    sysUserEntity.getSysDeptEntity().add(sysDeptEntity);

                }

            }*/

        }

        //sysUser.setSysDeptEntity(res);
        return list;
    }

    @Override
    public List<SysUserEntity> findByDeptId(long deptId) {
        return baseMapper.findByDeptId(deptId);
    }


    //   文件上传
    @Override
    public void upload(SysUserEntity user) {

//      文件路径
        String paths[] = new String[2];
//      文件类型
        String contentTypes[] = new String[2];
//      文件名
        String name = null;
        // 接收参数username
        logger.debug("uploadPath = " + user.getLocation());

        // 如果文件不为空，写入上传路径
        if (user.getHeadPortrait() != null) {
            // 接收参数username
            logger.debug("uploadPath = " + user.getLocation());
            // 如果文件不为空，写入上传路径
            if (!user.getHeadPortrait().isEmpty()) {
                //上传文件路径
                String path = user.getLocation();
                // logger.debug("URL = " + request.getRequestURL());

                logger.debug("path = " + path);
                paths = path.split(":");
                String contentType = user.getHeadPortrait().getContentType();

                contentTypes = contentType.split("/");
                logger.debug("contentType = " + contentTypes[1]);
                // 上传文件名
                if (contentTypes[1].trim().equals("jpg") || contentTypes[1].trim().equals("jpeg") || contentTypes[1].trim().equals("png")) {
                    String filename = user.getHeadPortrait().getOriginalFilename();
                    File filepath = new File(path, filename);
                    // 判断路径是否存在，如果不存在就创建一个
                    if (!filepath.getParentFile().exists()) {
                        filepath.getParentFile().mkdirs();
                    }
                    // 将上传文件保存到一个目标文件当中
                    name = File.separator + UUID.randomUUID().toString().replace("-", "");
                    name = name.substring(3);
                    try {
                        //user.getHeadPortrait().transferTo(new File(path + name+"."+filename));
                        user.getHeadPortrait().transferTo(new File(path + name+"."+filename));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    user.setHeadPortraits(paths[1] + name+"."+filename );


                } else {
                    throw new RRException("图片格式支持jpg、png、jepg");
                }


            }
        } else {
//           设置一个默认的头像
            //user.setHeadPortraits(user.getDefaultHeadPortrait());
            user.setHeadPortraits(null);
        }

        this.updateById(user);
    }
}

