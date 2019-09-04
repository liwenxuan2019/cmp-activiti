package io.cmp.common.utils;

import io.cmp.modules.sys.entity.*;
import io.cmp.modules.sys.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MenuTreeList {
    private Logger logger = LoggerFactory.getLogger(getClass());


    private static MenuTreeList instance = null;

    public static synchronized MenuTreeList getMenuTreeList() {

        if (instance == null) {

            instance = new MenuTreeList();

        }

        return instance;

    }

    private MenuTreeList() {

    }


    /**
     * 递归部门
     */
    public List<SysDeptEntity> getDepts(Long deptId, SysDeptService sysDeptService) {

        List<SysDeptEntity> deptVosList = new ArrayList<>();
        List<SysDeptEntity> deptEntityList = sysDeptService.queryListParentId(deptId);

        if (deptEntityList.size() != 0) {
            System.err.println("64515");
            for (SysDeptEntity deptVo : deptEntityList) {
                SysDeptEntity deptVo2 = new SysDeptEntity();
                deptVo2.setDeptId(deptVo.getDeptId());
                deptVo2.setName(deptVo.getName());
                deptVo2.setParentId(deptVo.getParentId());
                deptVo2.setDelFlag(deptVo.getDelFlag());
                deptVo2.setOrderNum(deptVo.getOrderNum());
                deptVo2.setList(getDepts(deptVo.getDeptId(), sysDeptService));
                deptVo2.setName(deptVo.getName());
                deptVo2.setParentName(sysDeptService.getById(sysDeptService.getById(deptVo.getDeptId()).getParentId()).getName());
                deptVosList.add(deptVo2);
            }
        }


        return deptVosList;

    }


    /**
     * 递归菜单
     */
    public List<SysMenuEntity> getMenus(Long menuId, SysMenuService sysMenuService) {

        List<SysMenuEntity> menuVosList = new ArrayList<>();
        List<SysMenuEntity> menuEntityList = sysMenuService.queryMenuListParentId(menuId);

        if (menuEntityList.size() != 0) {
            System.err.println("64515");
            for (SysMenuEntity menuVo : menuEntityList) {


                SysMenuEntity menuVo2 = new SysMenuEntity();
                menuVo2.setMenuId(menuVo.getMenuId());
                menuVo2.setName(menuVo.getName());
                menuVo2.setParentId(menuVo.getParentId());
                menuVo2.setOperateCode(menuVo.getOperateCode());
                menuVo2.setUrl(menuVo.getUrl());
                menuVo2.setOrderNum(menuVo.getOrderNum());
                menuVo2.setList(getMenus(menuVo.getMenuId(), sysMenuService));
                menuVo2.setName(menuVo.getName());
                menuVo2.setParentName(sysMenuService.getById(sysMenuService.getById(menuVo.getMenuId()).getParentId()).getName());
                menuVosList.add(menuVo2);
            }
        }


        return menuVosList;

    }

    /**
     * 之前的递归菜单--->表未分开
     */
    public List<SysMenuEntity> getMenuList(Long userId, SysUserService sysUserService, SysMenuService sysMenuService, List<Long> menuIds) {

        List<SysMenuEntity> menuVosList = new ArrayList<>();
        List<SysMenuEntity> menuEntityList = sysUserService.findMenuByParentId(userId,menuIds);


        if (menuEntityList.size() != 0) {
            System.err.println("64515");
            for (SysMenuEntity menuVo : menuEntityList) {
                SysMenuEntity menuVo2 = new SysMenuEntity();
                if(menuVo.getName().equals("视图")){
//                    查询菜单视图表


                   // menuVo2.setMenuViewList(sysMenuViewService);
                }
                menuVo2.setMenuId(menuVo.getMenuId());
                menuVo2.setName(menuVo.getName());
                menuVo2.setParentId(menuVo.getParentId());
                menuVo2.setUrl(menuVo.getUrl());
                menuVo2.setPerms(menuVo.getPerms());
                menuVo2.setType(menuVo.getType());
                menuVo2.setIcon(menuVo.getIcon());
                menuVo2.setOrderNum(menuVo.getOrderNum());
                menuVo2.setOpen(menuVo.getOpen());
                SysMenuEntity menuVo3=sysMenuService.getById(menuVo.getParentId());
                if(menuVo3!=null){
                    if(menuVo3.getName()!=null){
                        menuVo2.setParentName(menuVo3.getName());
                    }

                }

                menuVo2.setList(getMenuList(menuVo.getMenuId(), sysUserService,sysMenuService,menuIds));

                menuVosList.add(menuVo2);
            }
        }
        return  menuVosList;


    }
    /**
     * 递归菜单---->表分开
     */
    public List<SysMenuEntity> getMenuList1(Long userId, SysUserService sysUserService, SysMenuService sysMenuService, List<Long> menuIds, SysMenuViewService  sysMenuViewService,SysMenuOperationService sysMenuOperationService) {
        SysUserEntity user= sysUserService.getById(userId);

            List<SysMenuEntity> menuVosList = new ArrayList<>();
            List<SysMenuEntity> menuEntityList = sysUserService.findMenuByParentId(userId,menuIds);


            if (menuEntityList.size() != 0) {
                System.err.println("64515");
                for (SysMenuEntity menuVo : menuEntityList) {
                    SysMenuEntity menuVo2 = new SysMenuEntity();
                    if(menuVo.getName().equals("视图")){
//                    查询菜单视图表
                        // menuVo2.setMenuViewList1(sysMenuViewService.queryMenuViewList(menuVo.getMenuId()));
                        List<SysMenuViewEntity> list=sysMenuViewService.queryMenuViewList(menuVo.getMenuId());
                        // Collections.reverse(list);
                        for (SysMenuViewEntity s:list) {
                            SysMenuEntity sysMenuEntity = new SysMenuEntity();
                            sysMenuEntity.setParentId(s.getMenuId());
                            sysMenuEntity.setUrl(s.getViewUrl());
                            sysMenuEntity.setName(s.getViewName());
                            sysMenuEntity.setMenuId(s.getId());
                            menuVo2.getList().add(sysMenuEntity);
                            // menuVosList.add(sysMenuEntity);
                        }
                           menuVo2.setList(menuVo.getList());
                        menuVo2.setMenuViewList1(list);
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

                            menuVo2.getList().add(sysMenuEntity);

                          //  menuVosList.add(sysMenuEntity);

                        }
                        logger.debug("menuVo2.getList()\t"+menuVo2.getList());
                        //  menuVo2.setList(menuVo.getList());
                        menuVo2.setMenuOperationList1(list);

                    }
                    menuVo2.setMenuId(menuVo.getMenuId());
                    menuVo2.setName(menuVo.getName());
                    menuVo2.setParentId(menuVo.getParentId());
                    menuVo2.setUrl(menuVo.getUrl());
                    menuVo2.setPerms(menuVo.getPerms());
                    menuVo2.setType(menuVo.getType());
                    menuVo2.setIcon(menuVo.getIcon());
                    menuVo2.setOrderNum(menuVo.getOrderNum());
                    menuVo2.setOpen(menuVo.getOpen());
                    SysMenuEntity menuVo3=sysMenuService.getById(menuVo.getParentId());
                    if(menuVo3!=null){
                        if(menuVo3.getName()!=null){
                            menuVo2.setParentName(menuVo3.getName());
                        }

                    }
                   // List<SysMenuEntity>  list=   getMenuList1(menuVo.getMenuId(), sysUserService,sysMenuService,menuIds,sysMenuViewService,sysMenuOperationService);
                    menuVo2.setList(getMenuList1(menuVo.getMenuId(), sysUserService,sysMenuService,menuIds,sysMenuViewService,sysMenuOperationService));

                    menuVosList.add(menuVo2);
                }
            }
            return  menuVosList;

        }

    /**
     * 单纯递归菜单
     */

    public List<SysMenuEntity> getMenuList2(Long userId,Long menuId, SysUserService sysUserService, SysMenuService sysMenuService, List<Long> menuIds, SysMenuViewService  sysMenuViewService,SysMenuOperationService sysMenuOperationService) {
        SysUserEntity user= sysUserService.getById(userId);

        if(user.getUsername().trim().equals("admin")){
            logger.debug("admin");
            //            普通员工
            List<SysMenuEntity> menuVosList = new ArrayList<>();
            List<SysMenuEntity> menuEntityList = sysUserService.findMenuByParentId1(menuId,0);


                System.err.println("64515");
                for (SysMenuEntity menuVo : menuEntityList) {
                    SysMenuEntity menuVo2 = new SysMenuEntity();
                    menuVo2.setMenuId(menuVo.getMenuId());
                    menuVo2.setName(menuVo.getName());
                    menuVo2.setParentId(menuVo.getParentId());
                    menuVo2.setUrl(menuVo.getUrl());
                    menuVo2.setOperateCode(menuVo.getOperateCode());
                    menuVo2.setPerms(menuVo.getPerms());
                    menuVo2.setType(menuVo.getType());
                    menuVo2.setIcon(menuVo.getIcon());
                    menuVo2.setOrderNum(menuVo.getOrderNum());
                    menuVo2.setOpen(menuVo.getOpen());
                    SysMenuEntity menuVo3=sysMenuService.getById(menuVo.getParentId());
                    if(menuVo3!=null){
                        if(menuVo3.getName()!=null){
                            menuVo2.setParentName(menuVo3.getName());
                        }

                    }
                    menuVo2.setList(getMenuList2(userId,menuVo.getMenuId(), sysUserService,sysMenuService,menuIds,sysMenuViewService,sysMenuOperationService));

                    menuVosList.add(menuVo2);
                }

            return  menuVosList;

        }

        else if(user.getUsername().trim().equals("manger")){
            logger.debug("manger");
            //            普通员工
            List<SysMenuEntity> menuVosList = new ArrayList<>();
            List<SysMenuEntity> menuEntityList = sysUserService.findMenuByParentId(menuId,null);

            if (menuEntityList.size() != 0) {
                System.err.println("64515");
                for (SysMenuEntity menuVo : menuEntityList) {
                    SysMenuEntity menuVo2 = new SysMenuEntity();
                    menuVo2.setMenuId(menuVo.getMenuId());
                    menuVo2.setName(menuVo.getName());
                    menuVo2.setParentId(menuVo.getParentId());
                    menuVo2.setUrl(menuVo.getUrl());
                    menuVo2.setPerms(menuVo.getPerms());
                    menuVo2.setType(menuVo.getType());
                    menuVo2.setOperateCode(menuVo.getOperateCode());
                    menuVo2.setIcon(menuVo.getIcon());
                    menuVo2.setOrderNum(menuVo.getOrderNum());
                    menuVo2.setOpen(menuVo.getOpen());
                    SysMenuEntity menuVo3=sysMenuService.getById(menuVo.getParentId());
                    if(menuVo3!=null){
                        if(menuVo3.getName()!=null){
                            menuVo2.setParentName(menuVo3.getName());
                        }

                    }
                    menuVo2.setList(getMenuList2(userId,menuVo.getMenuId(), sysUserService,sysMenuService,menuIds,sysMenuViewService,sysMenuOperationService));

                    menuVosList.add(menuVo2);
                }
            }
            return  menuVosList;

        }

        else{
            logger.debug("common");
//            普通员工
            List<SysMenuEntity> menuVosList = new ArrayList<>();
            logger.debug(""+menuId);
            List<SysMenuEntity> menuEntityList = sysUserService.findMenuByParentId(menuId,menuIds);
               if(menuEntityList!=null){
                System.err.println("64515");
                for (SysMenuEntity menuVo : menuEntityList) {
                    SysMenuEntity menuVo2 = new SysMenuEntity();
                    menuVo2.setMenuId(menuVo.getMenuId());
                    menuVo2.setName(menuVo.getName());
                    menuVo2.setParentId(menuVo.getParentId());
                    menuVo2.setUrl(menuVo.getUrl());
                    menuVo2.setPerms(menuVo.getPerms());
                    menuVo2.setType(menuVo.getType());
                    menuVo2.setIcon(menuVo.getIcon());
                    menuVo2.setOrderNum(menuVo.getOrderNum());
                    menuVo2.setOperateCode(menuVo.getOperateCode());
                    menuVo2.setOpen(menuVo.getOpen());
                    SysMenuEntity menuVo3=sysMenuService.getById(menuVo.getParentId());
                    if(menuVo3!=null){
                        if(menuVo3.getName()!=null){
                            menuVo2.setParentName(menuVo3.getName());
                        }

                    }


                    menuVo2.setList(getMenuList2(userId,menuVo.getMenuId(), sysUserService,sysMenuService,menuIds,sysMenuViewService,sysMenuOperationService));
                    menuVosList.add(menuVo2);
                }
}
            return  menuVosList;

        }


    }

    /**
     *公共的功能
     */



    /**
     * 递归删除
     */


    public void deleteContentCategoryById(long menuId,SysMenuViewService sysMenuViewService,SysMenuService sysMenuService,SysMenuOperationService sysMenuOperationService) {

        //判断是否有子菜单或按钮

        List<SysMenuEntity> menuList = sysMenuService.queryListParentId(menuId);

        if (menuList.size() > 0) {
            sysMenuService.delete(menuId);
            for (SysMenuEntity sysMenuEntity : menuList) {
//				删除
                sysMenuService.delete(sysMenuEntity.getMenuId());
//				再去删除menu_view
                List<SysMenuViewEntity> list=sysMenuViewService.queryMenuViewList(sysMenuEntity.getMenuId());
                if(list!=null){
                    for (SysMenuViewEntity sysMenuViewEntity:list) {
                        sysMenuViewService.removeById(sysMenuViewEntity.getId());
                    }
                }
//				再去删除menu_view
                logger.debug("sysMenuEntity.getMenuId()\t"+sysMenuEntity.getMenuId());
                List<SysMenuOperationEntity> list1=sysMenuOperationService.queryOperationViewList(sysMenuEntity.getMenuId());
                logger.debug("list1\t"+list1);

                if(list1!=null){
                    for (SysMenuOperationEntity sysMenuOperationEntity:list1) {
                        logger.debug("sysMenuOperationEntity.getId()\t"+sysMenuOperationEntity.getId());
                        sysMenuOperationService.removeById(sysMenuOperationEntity.getId());
                    }
                }


                deleteContentCategoryById(sysMenuEntity.getMenuId(),sysMenuViewService,sysMenuService,sysMenuOperationService);
            }
        }
    }

}
