

package io.cmp.modules.sys.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.cmp.common.utils.Constant;
import io.cmp.common.utils.MapUtils;
import io.cmp.common.utils.R;
import io.cmp.modules.sys.entity.SysMenuOperationEntity;
import io.cmp.modules.sys.entity.SysMenuViewEntity;
import io.cmp.modules.sys.entity.SysUserEntity;
import io.cmp.modules.sys.service.*;
import io.cmp.modules.sys.dao.SysMenuDao;
import io.cmp.modules.sys.entity.SysMenuEntity;
import io.cmp.modules.sys.vo.SysMenuVo;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service("sysMenuService")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuDao, SysMenuEntity> implements SysMenuService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysMenuViewService sysMenuViewService;

    @Autowired
    private SysMenuOperationService sysMenuOperationService;

    @Autowired
    private SysRoleService sysRoleService;

    @Override
    public List<SysMenuEntity> queryCurrentListParentId(Long parentId, List<Long> menuIdList) {
        List<SysMenuEntity> menuList = queryListParentId(parentId);
/*		if (menuIdList == null) {
			return menuList;
		}*/

        List<SysMenuEntity> userMenuList = new ArrayList<>();
        logger.debug("进了");
        for (SysMenuEntity menu : menuList) {

            //List<SysMenuEntity> list = sysMenuService.queryListParentId(menu.getMenuId());
/*          if(menu.getParentId()!=0){
			menu.setParentName(sysMenuService.getById(menu.getParentId()).getName());
            }
           if(menu.getParentName()!=null){
           if(!(menu.getParentName().equals("操作"))&&!(menu.getParentName().equals("视图"))){
			   userMenuList.add(menu);
		   }

		   }else {
			   userMenuList.add(menu);
		   }*/


            userMenuList.add(menu);


        }
        return userMenuList;
    }

    @Override
    public List<SysMenuEntity> queryListParentId(Long parentId, List<Long> menuIdList) {
        List<SysMenuEntity> menuList = queryListParentId(parentId);
/*		if (menuIdList == null) {
			return menuList;
		}*/

        List<SysMenuEntity> userMenuList = new ArrayList<>();
        logger.debug("进了");
        for (SysMenuEntity menu : menuList) {

            //List<SysMenuEntity> list = sysMenuService.queryListParentId(menu.getMenuId());
/*          if(menu.getParentId()!=0){
			menu.setParentName(sysMenuService.getById(menu.getParentId()).getName());
            }
           if(menu.getParentName()!=null){
           if(!(menu.getParentName().equals("操作"))&&!(menu.getParentName().equals("视图"))){
			   userMenuList.add(menu);
		   }

		   }else {
			   userMenuList.add(menu);
		   }*/


            if (!menu.getName().equals("操作") && !menu.getName().equals("视图")) {
                if (menu.getParentId() != 0) {
                    menu.setParentName(sysMenuService.getById(menu.getParentId()).getName());
                }
                if (menu.getParentName() != null) {
                    if (!(menu.getParentName().equals("操作")) && !(menu.getParentName().equals("视图"))) {
                        userMenuList.add(menu);
                    }

                } else {
                    userMenuList.add(menu);
                }
            }

            //userMenuList.add(menu);

        }
        return userMenuList;
    }

    @Override
    public List<SysMenuEntity> queryCommonListParentId(Long parentId, List<Long> menuIdList) {
        List<SysMenuEntity> menuList = queryListParentId(parentId);
/*		if (menuIdList == null) {
			return menuList;
		}*/

        List<SysMenuEntity> userMenuList = new ArrayList<>();
        logger.debug("进了");
        for (SysMenuEntity menu : menuList) {
            if (!menu.getName().equals("操作") && !menu.getName().equals("视图")) {
                if (menu.getParentId() != 0) {
                    menu.setParentName(sysMenuService.getById(menu.getParentId()).getName());
                }
                if (menu.getParentName() != null) {
                    if (!(menu.getParentName().equals("操作")) && !(menu.getParentName().equals("视图"))) {
                        userMenuList.add(menu);
                    }

                } else {
                    userMenuList.add(menu);
                }
            }
            //userMenuList.add(menu);

        }
        return userMenuList;
    }

    @Override
    public List<SysMenuEntity> queryCommonOperationListParentId(Long parentId, List<Long> menuIdList, Integer per) {
        List<SysMenuEntity> menuList = queryListParentId(parentId);
/*		if (menuIdList == null) {
			return menuList;
		}*/
        logger.debug("per=" + per);
        List<SysMenuEntity> userMenuList = new ArrayList<>();
        logger.debug("进了");
        for (SysMenuEntity menu : menuList) {


            userMenuList.add(menu);


        }

        logger.debug("userMenuList=" + userMenuList);


        return userMenuList;
    }


    @Override
    public List<SysMenuEntity> queryListParentIdDisplayView(Long parentId, List<Long> menuIdList) {

        List<SysMenuEntity> menuList = queryListParentId(parentId);
        if (menuIdList == null) {
            return menuList;
        }

        List<SysMenuEntity> userMenuList = new ArrayList<>();
        for (SysMenuEntity menu : menuList) {
            if (menuIdList.contains(menu.getMenuId())) {


                userMenuList.add(menu);
            }
        }
        return userMenuList;
    }

    @Override
    public List<SysMenuEntity> queryadminListParentId(Long parentId, List<Long> menuIdList) {
        List<SysMenuEntity> menuList = queryAdminListParentId(parentId);
        List<SysMenuEntity> userMenuList = new ArrayList<>();
        for (SysMenuEntity menu : menuList) {
            if (!menu.getName().equals("操作") && !menu.getName().equals("视图")) {
                if (menu.getParentId() != 0) {
                    menu.setParentName(sysMenuService.getById(menu.getParentId()).getName());
                }
                if (menu.getParentName() != null) {
                    if (!(menu.getParentName().equals("操作")) && !(menu.getParentName().equals("视图"))) {
                        userMenuList.add(menu);
                    }

                } else {
                    userMenuList.add(menu);
                }
            }
            //userMenuList.add(menu);
        }
        return userMenuList;
    }

    @Override
    public List<SysMenuEntity> queryMenuListParentId(Long parentId) {

        return baseMapper.queryListParentId(parentId);
    }

    @Override
    public List<SysMenuEntity> queryListParentId(Long parentId) {
        return baseMapper.queryListParentId(parentId);
    }

    @Override
    public List<SysMenuEntity> queryAdminListParentId(Long parentId) {
        return baseMapper.queryAdminListParentId(parentId);
    }

    @Override
    public List<SysMenuEntity> queryNotButtonList() {
        return baseMapper.queryNotButtonList();
    }

    @Override
    public List<SysMenuEntity> getUserMenuList(Long userId) {
        //系统管理员，拥有最高权限
        if (userId == Constant.SUPER_MANGER) {
            logger.debug("SUPER_MANGER");
            return getAllMenuList(null);
        }
        /**
         * 后台人员使用的权限
         * 可以查看到工具菜单
         */
        if (userId == Constant.SUPER_ADMIN) {
            logger.debug("SUPER_ADMIN");
            return getAdminMenuList(null);
        }
        //用户菜单列表
        List<Long> menuIdList = sysUserService.queryAllMenuId(userId);
        logger.debug("menuIdList=" + menuIdList);
        return getCommonAllMenuList(menuIdList);
    }

    @Override
    public List<SysMenuEntity> getUserMenuListDisplayView(Long userId) {
        //系统管理员，拥有最高权限
        if (userId == Constant.SUPER_MANGER) {
            logger.debug("SUPER_MANGER");
            return getAllMenuListDisplayView(null);
        }
        /**
         * 后台人员使用的权限
         * 可以查看到工具菜单
         */
        if (userId == Constant.SUPER_ADMIN) {
            logger.debug("SUPER_ADMIN");
            return getAdminMenuList(null);
        }
        //用户菜单列表
        List<Long> menuIdList = sysUserService.queryAllMenuId(userId);
        logger.debug("menuIdList=" + menuIdList);
        return getAllMenuListDisplayView(menuIdList);
    }

    @Override
    public List<SysMenuEntity> getCurrentUserMenuList(Long userId) {
        //用户菜单列表
        List<Long> menuIdList = sysUserService.queryAllMenuId(userId);
        return getCurrentAllMenuList(menuIdList);
    }


    @Override
    public void delete(Long menuId) {
        //删除菜单
        this.removeById(menuId);
        //删除菜单与角色关联
        sysRoleMenuService.removeByMap(new MapUtils().put("menu_id", menuId));
    }

    /**
     * 视图树和操作树和为一颗树
     */
    @Override
    @Transactional
    public SysMenuEntity MergeOneTree(SysMenuEntity sysMenuEntity) {

//		先有对象
        SysMenuEntity sysMenu = new SysMenuEntity();

//		一级菜单-->通过传来的的parentId查询【客户管理】
        baseMapper.insert(sysMenuEntity);
        logger.debug("菜单ID\t" + sysMenuEntity.getMenuId());

/*//		二级菜单--->通过固定的id查询【3、客户】
		SysMenuEntity sysMenu2=new SysMenuEntity();
		sysMenu2.setName("客户");
		sysMenu2.setParentId(sysMenuEntity.getMenuId());
		baseMapper.insert(sysMenu2);*/

/*//      三级菜单--->通过固定的id查询【4、操作】
		SysMenuEntity sysMenu3=new SysMenuEntity();
		sysMenu3.setParentId(sysMenuEntity.getMenuId());
		sysMenu3.setName("操作");
		baseMapper.insert(sysMenu3);*/

//     三级菜单--->通过固定的id查询【4、操作】
//		然后从menu中获得menuMangerList一个一个设置到三级菜单中
        List<SysMenuEntity> list = sysMenuEntity.getList();
//      获取操作集合中的数据

        if (list != null && list.size() != 0) {
//      三级菜单--->通过固定的id查询【4、操作】
            SysMenuEntity sysMenu3 = new SysMenuEntity();
            sysMenu3.setParentId(sysMenuEntity.getMenuId());
            sysMenu3.setName("操作");
            baseMapper.insert(sysMenu3);
            for (SysMenuEntity sysMenuEntity2 : list) {
                SysMenuOperationEntity sysMenuOperationEntity = new SysMenuOperationEntity();
                sysMenuOperationEntity.setMenuId(sysMenu3.getMenuId());

                //sysMenuOperationEntity.setOperationCode(sysMenuEntity2.get);
                sysMenuOperationEntity.setOperationName(sysMenuEntity2.getName());
                sysMenuOperationEntity.setCreateCode(sysMenuEntity.getCreateCode());
                sysMenuOperationEntity.setCreateName(sysMenuEntity.getCreateName());
                sysMenuOperationEntity.setCreateTime(new Date());
                sysMenuOperationService.save(sysMenuOperationEntity);

            }
        }


//      获取操作集合中的数据

/*    if(list!=null||list.size()!=0){

	   for (SysMenuEntity sysMenu4 : list) {
		  sysMenu4.setParentId(sysMenu3.getMenuId());
		   baseMapper.insert(sysMenu4);
	     }
    }*/
/*//     三级菜单--->通过固定的id查询【5、视图】
		SysMenuEntity sysMenu5=new SysMenuEntity();
		sysMenu5.setParentId(sysMenuEntity.getMenuId());
		sysMenu5.setName("视图");
		baseMapper.insert(sysMenu5);*/
/*//		然后从menu中获得menuViewList一个一个设置到三级菜单中
		List<SysMenuEntity> viewList=sysMenuEntity.getMenuViewList();
	    if(viewList!=null||viewList.size()!=0){
			for (SysMenuEntity sysMenu6 : viewList) {
				sysMenu6.setParentId(sysMenu5.getMenuId());
				baseMapper.insert(sysMenu6);
			}
	    }*/
//      然后对菜单视图树进行操作

        List<SysMenuEntity> viewList = sysMenuEntity.getMenuViewList();
        if (viewList != null && viewList.size() != 0) {

            //     三级菜单--->通过固定的id查询【5、视图】
            SysMenuEntity sysMenu5 = new SysMenuEntity();
            sysMenu5.setParentId(sysMenuEntity.getMenuId());
            sysMenu5.setName("视图");
            baseMapper.insert(sysMenu5);
            for (SysMenuEntity sysMenu6 : viewList) {
//      插入数据
                SysMenuViewEntity sysMenuViewEntity = new SysMenuViewEntity();
                sysMenuViewEntity.setCreateCode(sysMenuEntity.getCreateCode());
                sysMenuViewEntity.setCreateName(sysMenuEntity.getCreateName());
                sysMenuViewEntity.setCreateTime(new Date());

                sysMenuViewEntity.setIsView("1");
                sysMenuViewEntity.setMenuId(sysMenu5.getMenuId());
                sysMenuViewEntity.setOrderNum(sysMenu6.getOrderNum());
                sysMenuViewEntity.setViewName(sysMenu6.getName());
                sysMenuViewEntity.setViewUrl(sysMenu6.getUrl());
                sysMenuViewService.save(sysMenuViewEntity);

//				增加后直接往操作者表中添加数据
                //addOperrator(sysMenuViewEntity,sysMenuEntity,sysMenuViewEntity.getMenuId());

            }
        }


        return sysMenu;
    }

    @Override
    @Transactional
    public SysMenuEntity upadteMergeOneTree(SysMenuEntity sysMenuEntity) {

//		先有对象
        SysMenuEntity sysMenu = new SysMenuEntity();

//		一级菜单-->通过传来的的parentId查询【客户管理】
        baseMapper.updateById(sysMenuEntity);
        logger.debug("菜单ID\t" + sysMenuEntity.getMenuId());

/*//		二级菜单--->通过固定的id查询【3、客户】
		SysMenuEntity sysMenu2=new SysMenuEntity();
		sysMenu2.setParentId(sysMenuEntity.getMenuId());
		sysMenu2.setName("客户");
		baseMapper.updateById(sysMenu2);*/

//      三级菜单--->通过固定的id查询【4、操作】

//		获取Id

        List<SysMenuEntity> idList = sysMenuService.queryListParentId(sysMenuEntity.getMenuId());
        SysMenuEntity sysMenu3 = null;
        if (idList != null && idList.size() != 0) {
            for (SysMenuEntity menu : idList) {
                if (menu.getName() != null) {
                    if (menu.getName().trim().equals("操作")) {
                        baseMapper.updateById(menu);
                        sysMenu3 = new SysMenuEntity();
                        sysMenu3.setMenuId(menu.getMenuId());
                    }
                }
            }

        }

//     三级菜单--->通过固定的id查询【4、操作】
//		然后从menu中获得menuMangerList一个一个设置到三级菜单中
        List<SysMenuEntity> list = sysMenuEntity.getList();
//      获取操作集合中的数据

        if (list != null && list.size() != 0) {

            if (sysMenu3.getMenuId() != null) {
                for (SysMenuEntity sysMenuEntity2 : list) {
                    SysMenuOperationEntity sysMenuOperationEntity = new SysMenuOperationEntity();

                    sysMenuOperationEntity.setMenuId(sysMenu3.getMenuId());


                    //sysMenuOperationEntity=sysMenuOperationService.getById(sysMenuEntity2.getMenuId());
                    List<SysMenuOperationEntity> menuOperation = sysMenuOperationService.queryOperationViewList(sysMenu3.getMenuId());

                    for (SysMenuOperationEntity sysMenuOperationEntity1 :
                            menuOperation) {
                        //sysMenuOperationEntity.setOperationCode(sysMenuEntity2.get);
                        sysMenuOperationEntity1.setOperationName(sysMenuEntity2.getName());
                        sysMenuOperationEntity1.setUpdateCode(sysMenuEntity.getCreateCode());
                        sysMenuOperationEntity1.setUpdateName(sysMenuEntity.getCreateName());
                        sysMenuOperationEntity1.setUpdateTime(new Date());
                        sysMenuOperationService.updateById(sysMenuOperationEntity1);
                    }


                }
			/*	sysMenu4.setParentId(sysMenu3.getMenuId());
				baseMapper.updateById(sysMenu4);*/
            }
        }
/*//     三级菜单--->通过固定的id查询【5、视图】
		SysMenuEntity sysMenu5=new SysMenuEntity();
		sysMenu5.setParentId(sysMenuEntity.getMenuId());
		sysMenu5.setName("视图");
		baseMapper.updateById(sysMenu5);*/

        List<SysMenuEntity> idList2 = sysMenuService.queryListParentId(sysMenuEntity.getMenuId());
        SysMenuEntity sysMenu5 = null;
        if (idList2 != null && idList2.size() != 0) {
            if (idList2 != null) {
                for (SysMenuEntity menu : idList2) {
                    if (menu.getName() != null) {
                        if (menu.getName().trim().equals("视图")) {
                            baseMapper.updateById(menu);
                            sysMenu5 = new SysMenuEntity();
                            sysMenu5.setMenuId(menu.getMenuId());
                        }
                    }
                }
            }
        }
//		然后从menu中获得menuViewList一个一个设置到三级菜单中
        List<SysMenuEntity> viewList = sysMenuEntity.getMenuViewList();
        if (viewList != null && viewList.size() != 0) {
            for (SysMenuEntity sysMenu6 : viewList) {

//      修改数据
		/*		SysMenuViewEntity sysMenuViewEntity=new SysMenuViewEntity();
				sysMenuViewEntity.setId(sysMenu6.getMenuId());*/
//				获取需要更新的数据
                if (sysMenu5.getMenuId() != null) {
                    List<SysMenuViewEntity> menuViewEntity = sysMenuViewService.queryMenuViewList(sysMenu5.getMenuId());

                    for (SysMenuViewEntity sysMenuViewEntity :
                            menuViewEntity) {
                        sysMenuViewEntity.setUpdateName(sysMenuEntity.getCreateName());
                        sysMenuViewEntity.setUpdateCode(sysMenuEntity.getCreateCode());
                        sysMenuViewEntity.setUpdateTime(new Date());
                        sysMenuViewEntity.setIsView("1");
                        if (sysMenu5.getMenuId() != null) {
                            sysMenuViewEntity.setMenuId(sysMenu5.getMenuId());
                        }

                        sysMenuViewEntity.setOrderNum(sysMenu6.getOrderNum());
                        sysMenuViewEntity.setViewName(sysMenu6.getName());
                        sysMenuViewEntity.setViewUrl(sysMenu6.getUrl());
                        sysMenuViewService.updateById(sysMenuViewEntity);
                        //sysMenuViewEntity=	sysMenuViewService.getById(sysMenu6.getMenuId());

                        //logger.debug("id1\t"+sysMenuViewEntity.getMenuId());
                    }

                }
/*				for (sysMenuViewEntity1:menuViewEntity){
					sysMenuViewEntity.setUpdateName(sysMenuEntity.getCreateName());
					sysMenuViewEntity.setUpdateCode(sysMenuEntity.getCreateCode());
					sysMenuViewEntity.setUpdateTime(new Date());
					sysMenuViewEntity.setIsView("1");
					sysMenuViewEntity.setMenuId(sysMenu5.getMenuId());
					sysMenuViewEntity.setOrderNum(sysMenu6.getOrderNum());
					sysMenuViewEntity.setViewName(sysMenu6.getName());
					sysMenuViewEntity.setViewUrl(sysMenu6.getUrl());
					sysMenuViewService.updateById(sysMenuViewEntity);
					sysMenuViewEntity=	sysMenuViewService.getById(sysMenu6.getMenuId());

					logger.debug("id1\t"+sysMenuViewEntity.getMenuId());
				}*/

                //addOperrator(sysMenuViewEntity,sysMenuEntity,sysMenuViewEntity.getMenuId());

            }

        }


        return sysMenu;
    }

    /**
     * 往操作表中添加数据
     */

    public void addOperrator(SysMenuViewEntity sysMenuViewEntity, SysMenuEntity sysMenuEntity, Long id) {
        //				增加后直接往操作者表中添加数据
        //sysMenuViewEntity.setId(sysMenu6.getMenuId());

        logger.debug("id\t" + id);
        SysMenuOperationEntity sysMenuOperationEntity = new SysMenuOperationEntity();
        sysMenuOperationEntity.setMenuId(id);
        sysMenuOperationEntity.setOperationCode(sysMenuEntity.getUpdateUserCode());
        sysMenuOperationEntity.setOperationName(sysMenuViewEntity.getViewName());
        sysMenuOperationEntity.setCreateCode(sysMenuViewEntity.getCreateCode());
        sysMenuOperationEntity.setCreateName(sysMenuViewEntity.getCreateName());
        sysMenuOperationEntity.setCreateTime(sysMenuViewEntity.getCreateTime());
        sysMenuOperationEntity.setUpdateCode(sysMenuViewEntity.getUpdateCode());
        sysMenuOperationEntity.setUpdateName(sysMenuViewEntity.getUpdateName());
        sysMenuOperationEntity.setUpdateTime(sysMenuViewEntity.getUpdateTime());
        sysMenuOperationService.save(sysMenuOperationEntity);
    }

    @Override
    public SysMenuEntity MergeOneTree1(SysMenuVo sysMenuEntity) {

//		先有对象
        SysMenuEntity sysMenu = new SysMenuEntity();

//		一级菜单-->通过传来的的parentId查询【客户管理】
        baseMapper.insert(sysMenuEntity);
        logger.debug("菜单ID\t" + sysMenuEntity.getMenuId());

        List<SysMenuEntity> list = sysMenuEntity.getList();
//      获取操作集合中的数据
//      创建菜单的时候一定有操作--不进行控制

        //if (list != null && list.size() != 0) {
//      三级菜单--->通过固定的id查询【4、操作】
        SysMenuEntity sysMenu3 = new SysMenuEntity();
        sysMenu3.setParentId(sysMenuEntity.getMenuId());
        sysMenu3.setName("操作");
        baseMapper.insert(sysMenu3);
        for (SysMenuEntity sysMenu4 : list) {
            sysMenu4.setParentId(sysMenu3.getMenuId());
            baseMapper.insert(sysMenu4);
        }
        //}
/*//     三级菜单--->通过固定的id查询【5、视图】
		SysMenuEntity sysMenu5=new SysMenuEntity();
		sysMenu5.setParentId(sysMenuEntity.getMenuId());
		sysMenu5.setName("视图");
		baseMapper.insert(sysMenu5);*/
/*//		然后从menu中获得menuViewList一个一个设置到三级菜单中
		List<SysMenuEntity> viewList=sysMenuEntity.getMenuViewList();
	    if(viewList!=null||viewList.size()!=0){
			for (SysMenuEntity sysMenu6 : viewList) {
				sysMenu6.setParentId(sysMenu5.getMenuId());
				baseMapper.insert(sysMenu6);
			}
	    }*/
//      然后对菜单视图树进行操作

        List<SysMenuEntity> viewList = sysMenuEntity.getMenuViewList();
        if (viewList != null && viewList.size() != 0) {
            //     三级菜单--->通过固定的id查询【5、视图】
            SysMenuEntity sysMenu5 = new SysMenuEntity();
            sysMenu5.setParentId(sysMenuEntity.getMenuId());
            sysMenu5.setName("视图");
            baseMapper.insert(sysMenu5);
            for (SysMenuEntity sysMenu6 : viewList) {
                sysMenu6.setParentId(sysMenu5.getMenuId());
                if (sysMenu6.getDefaultUrl() == null || sysMenu6.getDefaultUrl().equals("")) {
                    sysMenu6.setDefaultUrl(0);
                }
                baseMapper.insert(sysMenu6);

            }
        }


        return sysMenu;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysMenuEntity upadteMergeOneTree1(SysMenuVo sysMenuEntity) {
       // baseMapper.updateById(sysMenuEntity);
        //        定义一个值判断是否有视图


        boolean flag = false;
//      操作
        List<SysMenuEntity> list = sysMenuEntity.getList();
//      获取操作集合中的数据
        if (list != null && list.size() != 0) {
//            用一个List把视图先储存起来
            List<SysMenuEntity> res = new ArrayList<>();
            List<SysMenuEntity> result = new ArrayList<>();
            for (SysMenuEntity sysMenu4 : list) {
//                如果出现新增的就跳过

                logger.debug("sysMenu4=" + sysMenu4);
                if (sysMenu4.getMenuId() == null) {
                    result.add(sysMenu4);
                } else {
//                    用于最后的更新
                    SysMenuEntity sys = baseMapper.getById(sysMenu4.getMenuId());

                    if (sys != null) {

                        sys.setName(sysMenu4.getName());
                        sys.setOperateCode(sysMenu4.getOperateCode());
                        res.add(sys);
                    }
                }
            }

            for (SysMenuEntity sysMenu4 : list) {

                SysMenuEntity sys = baseMapper.getById(sysMenu4.getMenuId());
                if (sys != null) {
                    if (sys.getParentId() != null) {
                        logger.debug("进了------");

                        baseMapper.deleteByParentId(sys.getParentId());
                    }
                }
            }
//            修改的时候添加操作

            for (SysMenuEntity sysMenu4 : result) {
                if (sysMenu4 != null) {
                    if (sysMenu4.getMenuId() == null) {
                        List<SysMenuEntity> operate = baseMapper.queryListParentId(sysMenuEntity.getMenuId());
//                        logger.debug("operate==="+operate);
                        logger.debug("操作进了111");
                        for (SysMenuEntity s :
                                operate) {
                            if (s.getName().equals("操作")) {
                                logger.debug("操作进了");
                                sysMenu4.setParentId(s.getMenuId());
                                baseMapper.insert(sysMenu4);
                            }
                        }
                    }
                }
            }

            for (SysMenuEntity sysRes : res) {
//                保存
                if (sysRes != null) {
                    if (sysRes.getDefaultUrl() == null) {
                        sysRes.setDefaultUrl(0);
                    }

                    List<SysMenuEntity> operate = baseMapper.queryListParentId(sysMenuEntity.getMenuId());

/*                    logger.debug("操作进了111" + "\tsize\t" + operate.size());
                    if (operate != null && operate.size() != 0) {

                        for (SysMenuEntity s:operate) {
                            if (s.getName().equals("视图")) {

                                flag = true;
                            } else {
                                flag = false;
                            }
                        }

                    }*/
                    logger.debug("operate===" + operate);
                    for (SysMenuEntity s :
                            operate) {
                        if (s.getName().equals("操作")) {
                            sysRes.setParentId(s.getMenuId());
                            baseMapper.insert(sysRes);
                        }
                    }
                }
            }

        } else {

//            删除全部操作

            List<SysMenuEntity> operate = baseMapper.queryListParentId(sysMenuEntity.getMenuId());

            for (SysMenuEntity s :
                    operate) {
                if (s.getName().equals("操作")) {
                    if (s.getMenuId() != 0) {
                        logger.debug("进了---操作---");
                        baseMapper.deleteByParentId(s.getMenuId());
                    }
                }
            }

        }
//      视图
        List<SysMenuEntity> viewList = sysMenuEntity.getMenuViewList();
//      开起第二个集合用来做修改视图

        List<SysMenuEntity> updateViewList = new ArrayList<>();


        for (SysMenuEntity sysMenu6 : viewList) {

            logger.debug("sysMenu6=" + sysMenu6);

            updateViewList.add(sysMenu6);

        }


        if (viewList != null && viewList.size() != 0) {
            List<SysMenuEntity> operates = baseMapper.queryListParentId(sysMenuEntity.getMenuId());

            logger.debug("操作进了111" + "\tsize\t" + operates.size());
            if (operates != null && operates.size() != 0) {

                for (SysMenuEntity s:operates) {
                    if (s.getName().equals("视图")) {

                        flag = true;
                        break;
                    }
                }

            }

//            修改的时候添加视图--先获取操作的父节点-判断有没有视图
            if (list != null && list.size() != 0) {
                for (SysMenuEntity sysMenuView : list) {


                    List<SysMenuEntity> views = baseMapper.queryListParentId(sysMenuView.getMenuId());
                    logger.debug("views===" + views);
                    logger.debug("views.size()=" + views.size());

                }
            }
            logger.debug("flag=" + flag);
//            判断是否有视图
            if (flag) {

//            用一个List把视图先储存起来
                List<SysMenuEntity> res = new ArrayList<>();
//
                List<SysMenuEntity> result = new ArrayList<>();

//            用于储存res中的默认视图
                SysMenuEntity resDefault = new SysMenuEntity();

//            用于储存result中的默认视图
                SysMenuEntity resultDefault = new SysMenuEntity();
                for (SysMenuEntity sysMenu6 : viewList) {

                    if (sysMenu6.getMenuId() == null) {

                        result.add(sysMenu6);
                    } else {
                        SysMenuEntity sys = baseMapper.getById(sysMenu6.getMenuId());
//                    不选择或者就不是默认视图
                        if (sysMenu6.getDefaultUrl() == null) {

                            sys.setDefaultUrl(0);
                        } else {
                            sys.setDefaultUrl(sysMenu6.getDefaultUrl());
                        }
                        logger.debug("sys=" + sys);
                        if (sys != null) {
                            sys.setName(sysMenu6.getName());
                            sys.setOperateCode(sysMenu6.getOperateCode());
                            sys.setUrl(sysMenu6.getUrl());
                            sys.setDefaultUrl(sysMenu6.getDefaultUrl());
                            if (sysMenu6.getDefaultUrl() == 1) {
                                resDefault.setMenuId(sysMenu6.getMenuId());
                                resDefault.setDefaultUrl(1);

                            }
                            res.add(sys);
                        }
                    }
                }
                logger.debug("res=" + res);

                for (SysMenuEntity sysMenu6 : viewList) {

                    SysMenuEntity sys = baseMapper.getById(sysMenu6.getMenuId());
                    if (sys != null) {
                        if (sys.getParentId() != null) {
                            logger.debug("进了---111---");
                            if (sys.getParentId() != 0) {


                                baseMapper.deleteByParentId(sys.getParentId());
                            }
                        }
                    }

                }
//            修改的时候添加视图
                for (SysMenuEntity sysMenu6 : result) {
                    logger.debug("挤进了");
                    logger.debug(""+sysMenu6);
                    if (sysMenu6.getMenuId() == null) {
                        logger.debug("挤进了2222");
//            删除全部视图
                        List<SysMenuEntity> views = baseMapper.queryListParentId(sysMenuEntity.getMenuId());
                        logger.debug("视图进了111");
                        for (SysMenuEntity s :
                                views) {
                            logger.debug(s.getName());
                            if (s.getName().equals("视图")) {
                                logger.debug("test=" + s.getName());
                                logger.debug("视图进了");
                                sysMenu6.setParentId(s.getMenuId());
//                            设置默认视图
               /*             if(){

                            }
                            sysMenu6.setDefaultUrl();*/
                                baseMapper.insert(sysMenu6);
                            }
                        }

                    }
                }


                for (SysMenuEntity sysRes : res) {
//                保存
                    if (sysRes != null) {
                        if (sysRes.getDefaultUrl() == null) {
                            sysRes.setDefaultUrl(0);
                        }
                        List<SysMenuEntity> views = baseMapper.queryListParentId(sysMenuEntity.getMenuId());
                        logger.debug("视图进了111");
                        for (SysMenuEntity s :
                                views) {
                            logger.debug(s.getName());
                            if (s.getName().equals("视图")) {
                                sysRes.setParentId(s.getMenuId());
                                baseMapper.insert(sysRes);
                            }
                        }

                    }
                }


            } else {
                Long parentId = Long.valueOf(0);
                logger.debug("视图进了，，，，，，，，，");
//                添加视图
                List<SysMenuEntity> operate = baseMapper.queryListParentId(sysMenuEntity.getMenuId());
                if (operate != null && operate.size() != 0) {
                    parentId = operate.get(0).getParentId();
                }
                SysMenuEntity sysView = new SysMenuEntity();
                sysView.setName("视图");
                sysView.setParentId(parentId);

                baseMapper.insert(sysView);

//                添加新视图
                List<SysMenuEntity> viewLists = sysMenuEntity.getMenuViewList();

                if (viewLists != null && viewLists.size() != 0) {
                    logger.debug("添加222");
                    for (SysMenuEntity sysMenuView :
                            viewLists) {
                        logger.debug("添加111");
//                        设置父节点ID
                        sysMenuView.setParentId(sysView.getMenuId());

                        baseMapper.insert(sysMenuView);
                    }

                }


            }
        } else {

//            删除全部视图
            List<SysMenuEntity> views = baseMapper.queryListParentId(sysMenuEntity.getMenuId());

            for (SysMenuEntity s :
                    views) {
                if (s.getName().equals("视图")) {
                    logger.debug("进了---视图---");
                    baseMapper.deleteByParentId(s.getMenuId());
                }
            }
        }
        sysMenuEntity.setList(null);
        sysMenuEntity.setMenuOperationList(null);

        sysMenuEntity.setMenuViewList1(null);
        sysMenuEntity.setMenuViewList(null);
        sysMenuEntity.setMenuOperationList1(null);
        baseMapper.updateById(sysMenuEntity);
        return null;
    }

    @Override
    public List<SysMenuEntity> getMenuList1(Long menuId) {
        return baseMapper.queryListParentId(menuId);
    }

    @Override
    public R queryTwoLevelMenu(Integer userId, Integer parentId) {
        //        用于判断是否为空
        SysMenuEntity sys = sysMenuService.getById(parentId);
//        最终返回的数据
        List<SysMenuEntity> result = new ArrayList<>();
        SysUserEntity sysUserEntity = sysUserService.getById(userId);

        if (sysUserEntity.getUsername().trim().equals("admin")) {
            List<SysMenuEntity> list = sysUserService.queryLevelTwoMenu(null, 0, Integer.parseInt(sys.getMenuId().toString()));
            logger.debug("list=" + list);
            SysMenuEntity sys1 = sysMenuService.getById(parentId);
            if (sys1.getToolBar() == 1) {
                return R.ok().put("menuList", new ArrayList<>());
            }
            if (sys != null) {
                result.add(sys);
                for (SysMenuEntity sysMenu : list) {
//                  操作下面的数据
                    List<SysMenuEntity> reslist = sysMenuService.queryListParentId(sysMenu.getMenuId());
                    logger.debug("reslist=" + reslist);
                    for (SysMenuEntity sysMenuEntity : reslist) {
//                        为toolbar是0
                        if(sysMenuEntity.getToolBar()==null||sysMenuEntity.getToolBar()==0) {

                            result.add(sysMenuEntity);
                        }
                    }

                }
                return R.ok().put("menuList", result);
            } else {
                return R.ok().put("menuList", new ArrayList<>());
            }
        } else if (sysUserEntity.getUsername().trim().equals("manger")) {
            List<SysMenuEntity> list = sysUserService.queryLevelTwoMenu(null, null, Integer.parseInt(sys.getMenuId().toString()));
            if (sys != null) {
                result.add(sys);
                for (SysMenuEntity sysMenu : list) {
//                  操作下面的数据
                    List<SysMenuEntity> reslist = sysMenuService.queryListParentId(sysMenu.getMenuId());
                    logger.debug("reslist=" + reslist);
                    for (SysMenuEntity sysMenuEntity : reslist) {

                        result.add(sysMenuEntity);
                    }

                }

                return R.ok().put("menuList", result);
            } else {
                return R.ok().put("menuList", new ArrayList<>());
            }


        } else {

            List<SysMenuEntity> menuList = sysMenuService.getUserMenuOperation(sysUserEntity.getUserId(), parentId);
            //用户菜单列表


/*//      找到当前父级ID的上级

            // SysMenuEntity sys= sysMenuService.getById(parentId);
            if (sys != null) {

                List<SysMenuEntity> list = sysUserService.queryLevelTwoMenu(userId, null, Integer.parseInt(sys.getMenuId().toString()));
                logger.debug("list=" + list);
                for (SysMenuEntity sysMenu : list) {
//                  操作下面的数据
                    List<SysMenuEntity> reslist = sysMenuService.queryListParentId(sysMenu.getMenuId());
                    logger.debug("reslist=" + reslist);
                    for (SysMenuEntity sysMenuEntity : reslist) {

                        result.add(sysMenuEntity);
                    }
                }


//            获取操作那一级别的数据
                return R.ok().put("menuList", result);
            } else {
                return R.ok().put("menuList", new ArrayList<>());
            }*/

            return R.ok().put("menuList", menuList);

        }

    }

    @Override
    public List<SysMenuEntity> getUserMenuOperation(Long userId, Integer per) {
//        功能菜单
        SysMenuEntity sys1 = sysMenuService.getById(per);
        List<Long> menuIdList = sysUserService.queryAllMenuId(userId);

        List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();
        logger.debug("menuIdList1\t" + menuIdList);
        List<SysMenuEntity> sysList = new ArrayList<>();
        for (Long id : menuIdList) {
            SysMenuEntity sysMenuEntity = sysMenuService.getById(id);
            if (sysMenuEntity != null) {
                if (sysMenuEntity.getName() != null) {
                    logger.debug(sysMenuEntity.getName());
                    if (sysMenuEntity.getName().equals("操作") || sysMenuEntity.getName().equals("视图")) {
                        sysList = sysMenuService.queryListParentId(per.longValue());

                        // logger.debug("sss1122="+sysList);
                    }

                }
            }
        }


//      最终的数据
        if(sysList!=null&&sysList.size()!=0) {
            //        添加功能菜单

            subMenuList.add(sys1);
            for (SysMenuEntity sys : sysList) {
                SysMenuEntity sysMenu = sysMenuService.getById(sys.getMenuId());
                logger.debug("sss=" + sysMenu.getMenuId());
                List<SysMenuEntity> res = sysMenuService.queryListParentId(sysMenu.getMenuId());
                for (SysMenuEntity sysMenuEntity : res) {
//                再去判断menuIdList是否
                    if (menuIdList.contains(sysMenuEntity.getMenuId())) {
                        subMenuList.add(sysMenuEntity);
                    }

                }
            }
        }
        return subMenuList;
    }

    @Override
    public SysMenuEntity findByOperateCode(String operateCode) {
        return baseMapper.findByOperateCode(operateCode);
    }

    /**
     * 获取所有菜单列表
     */
    private List<SysMenuEntity> getAllMenuList(List<Long> menuIdList) {
        //查询根菜单列表
        List<SysMenuEntity> menuList = queryListParentId(0L, menuIdList);
        //递归获取子菜单
        getMenuTreeList(menuList, menuIdList);

        return menuList;
    }


    /**
     * 获取普通用户的菜单列表
     */
    private List<SysMenuEntity> getCommonAllMenuList(List<Long> menuIdList) {
        //查询根菜单列表
        List<SysMenuEntity> menuList = queryCommonListParentId(0L, menuIdList);
        //递归获取子菜单
        getCommonMenuTreeList(menuList, menuIdList);

        return menuList;
    }

    /**
     * 获取普通用户的菜单列表
     */
    private List<SysMenuEntity> getCommonOperationMenuList(List<Long> menuIdList, Integer per) {
        //查询根菜单列表
        List<SysMenuEntity> menuList = queryCommonOperationListParentId(0L, menuIdList, per);
        //递归获取子菜单
        getCommonMenuOperationTreeList(menuList, menuIdList, per);

        return menuList;
    }

    /**
     * 获取所有菜单列表
     */
    private List<SysMenuEntity> getCurrentAllMenuList(List<Long> menuIdList) {
        //查询根菜单列表
        List<SysMenuEntity> menuList = queryCurrentListParentId(0L, menuIdList);
        //递归获取子菜单
        getCurrentMenuTreeList(menuList, menuIdList);

        return menuList;
    }

    /**
     * 获取所有admin菜单列表
     */
    private List<SysMenuEntity> getAdminMenuList(List<Long> menuIdList) {
        //查询根菜单列表
        List<SysMenuEntity> menuList = queryadminListParentId(0L, menuIdList);
        //递归获取子菜单
        getAdminMenuTreeList(menuList, menuIdList);

        return menuList;
    }


    private List<SysMenuEntity> getCurrentMenuTreeList(List<SysMenuEntity> menuList, List<Long> menuIdList) {
        List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();

        for (SysMenuEntity entity : menuList) {

            entity.setList(getCurrentMenuTreeList(queryCurrentListParentId(entity.getMenuId(), menuIdList), menuIdList));

            subMenuList.add(entity);
        }

        return subMenuList;
    }

    /**
     * 获取用户下面的操作
     *
     * @param menuList
     * @param menuIdList
     * @return
     */


    private List<SysMenuEntity> getCommonMenuOperationTreeList(List<SysMenuEntity> menuList, List<Long> menuIdList, Integer per) {
        List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();
        List<SysMenuEntity> list = new ArrayList();
        logger.debug("menuIdList1\t" + menuIdList);

        for (Long id : menuIdList) {
            logger.debug(""+id);

            SysMenuEntity sysMenuEntity = sysMenuService.getById(id);
//                if(entitys!=null){
//                    if(entitys.getName()!=null){
//                        logger.debug(entitys.getName());
//                        if(entitys.getName().equals("操作")){
            subMenuList.add(sysMenuEntity);
//                        }
//
//                    }
        }
//            }


        //if (!entity.getName().equals("操作") && !entity.getName().equals("视图")) {
/*            if (menuIdList.contains(entity.getMenuId())) {

                if(entity.getName().equals("操作")){
                    entity.setList(getCommonMenuOperationTreeList(queryCommonOperationListParentId(entity.getMenuId(), menuIdList, per), menuIdList, per));

                }else{ logger.debug(entity.getName());

                    continue;
                }

            }*/
        //}


        //}


        return subMenuList;
    }


    private List<SysMenuEntity> getCommonMenuTreeList(List<SysMenuEntity> menuList, List<Long> menuIdList) {
        List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();
        logger.debug("menuIdList1\t" + menuIdList);
        for (SysMenuEntity entity : menuList) {


            //if (!entity.getName().equals("操作") && !entity.getName().equals("视图")) {
            if (menuIdList.contains(entity.getMenuId())) {
                entity.setList(getCommonMenuTreeList(queryCommonListParentId(entity.getMenuId(), menuIdList), menuIdList));

            } else {
                continue;
            }
            //}


            //}
            subMenuList.add(entity);
        }

        return subMenuList;
    }

    /**
     * 递归
     */
    private List<SysMenuEntity> getMenuTreeList(List<SysMenuEntity> menuList, List<Long> menuIdList) {
        List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();
        logger.debug("menuIdList1\t" + menuIdList);
        for (SysMenuEntity entity : menuList) {


            if (!entity.getName().equals("操作") && !entity.getName().equals("视图")) {
                //if (menuIdList.contains(entity.getMenuId())) {
                entity.setList(getMenuTreeList(queryListParentId(entity.getMenuId(), menuIdList), menuIdList));

                //	} else {
                //	continue;
                //}
            }


            //}
            subMenuList.add(entity);
        }

        return subMenuList;
    }

    /**
     * 递归
     */
    private List<SysMenuEntity> getAdminMenuTreeList(List<SysMenuEntity> menuList, List<Long> menuIdList) {
        List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();

        for (SysMenuEntity entity : menuList) {

            //目录
            //	if(entity.getType() == Constant.MenuType.CATALOG.getValue()){
            if (!entity.getName().equals("操作") && !entity.getName().equals("视图")) {
                logger.debug(entity.getName());
                entity.setList(getAdminMenuTreeList(queryadminListParentId(entity.getMenuId(), menuIdList), menuIdList));
            }
            //}
            subMenuList.add(entity);
        }

        return subMenuList;
    }

    /**
     * 获取所有菜单列表
     */
    private List<SysMenuEntity> getAllMenuListDisplayView(List<Long> menuIdList) {
        //查询根菜单列表
        List<SysMenuEntity> menuList = queryListParentIdDisplayView(0L, menuIdList);
        //递归获取子菜单
        getMenuTreeListDisplayView(menuList, menuIdList);

        return menuList;
    }

    /**
     * 递归
     */
    private List<SysMenuEntity> getMenuTreeListDisplayView(List<SysMenuEntity> menuList, List<Long> menuIdList) {
        List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();

        for (SysMenuEntity entity : menuList) {
            entity.setList(getMenuTreeListDisplayView(queryListParentIdDisplayView(entity.getMenuId(), menuIdList), menuIdList));

            subMenuList.add(entity);
        }

        return subMenuList;
    }

}
