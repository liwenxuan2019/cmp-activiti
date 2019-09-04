

package io.cmp.modules.sys.controller;

import io.cmp.common.annotation.SysLog;
import io.cmp.common.exception.RRException;
import io.cmp.common.utils.Constant;
import io.cmp.common.utils.MenuTreeList;
import io.cmp.common.utils.R;
import io.cmp.modules.sys.entity.SysMenuEntity;
import io.cmp.modules.sys.entity.SysMenuOperationEntity;
import io.cmp.modules.sys.entity.SysMenuViewEntity;
import io.cmp.modules.sys.entity.SysUserEntity;
import io.cmp.modules.sys.service.ShiroService;
import io.cmp.modules.sys.service.SysMenuOperationService;
import io.cmp.modules.sys.service.SysMenuService;
import io.cmp.modules.sys.service.SysMenuViewService;
import io.cmp.modules.sys.vo.SysMenuVo;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 系统菜单
 *
 * @author
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends AbstractController {
	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private ShiroService shiroService;
	@Autowired
	private SysMenuViewService sysMenuViewService;
	@Autowired
	private SysMenuOperationService sysMenuOperationService;
	/**
	 * 导航菜单
	 */
	@GetMapping("/nav")
	public R nav(){
		List<SysMenuEntity> menuList = sysMenuService.getUserMenuList(getUserId());
		Set<String> permissions = shiroService.getUserPermissions(getUserId());
		return R.ok().put("menuList", menuList).put("permissions", permissions);
	}

	/**
	 * 通过用户id获取导航菜单显示操作视图
	 */
	@GetMapping("/navByUserId")
	public R navByUserId(){
		logger.debug("getUserId()="+getUserId());
		List<SysMenuEntity> menuList = sysMenuService.getUserMenuListDisplayView(getUserId());
		return R.ok().put("menuList",menuList);

	}

	/*	*//**
	 * 所有菜单列表
	 *//*
	@GetMapping("/list")
	@RequiresPermissions("sys:menu:list")
	public List<SysMenuEntity> list(){
		SysUserEntity sysUserEntity=getUser();
		if(sysUserEntity.getUsername().trim().equals("admin")){
			logger.debug("进了");
			List<SysMenuEntity> menuList = sysMenuService.list();
			List<SysMenuEntity> resMenuList = new ArrayList<>();
			for(SysMenuEntity sysMenuEntity : menuList){
				if(sysMenuEntity.getToolBar()==1){
//					查询出来批量更新
				List<SysMenuEntity>	list=sysMenuService.queryListParentId(sysMenuEntity.getMenuId());
					for (SysMenuEntity sys:
					list) {
						sys.setToolBar(1);
						sysMenuService.updateById(sys);
					}
				}else{
					if(sysMenuEntity.getToolBar()==0){
						resMenuList.add(sysMenuEntity);
						//				设置上级名字
						SysMenuEntity parentMenuEntity = sysMenuService.getById(sysMenuEntity.getParentId());
						if(parentMenuEntity != null){
							sysMenuEntity.setParentName(parentMenuEntity.getName());

						}
					}

				}
*//*
//				设置对象
			List<SysMenuViewEntity> list=sysMenuViewService.queryMenuViewList(sysMenuEntity.getMenuId());
			if(list!=null){
				sysMenuEntity.setMenuViewList1(list);
			}
			List<SysMenuOperationEntity> list1=	sysMenuOperationService.queryOperationViewList(sysMenuEntity.getMenuId());
			if(list1!=null){
				sysMenuEntity.setMenuOperationList1(list1);
			}*//*

			}

			return resMenuList;

		}
		List<SysMenuEntity> menuList = sysMenuService.list();
		for(SysMenuEntity sysMenuEntity : menuList){
			SysMenuEntity parentMenuEntity = sysMenuService.getById(sysMenuEntity.getParentId());
			if(parentMenuEntity != null){
				sysMenuEntity.setParentName(parentMenuEntity.getName());
			}
*//*
//				设置对象
			List<SysMenuViewEntity> list=sysMenuViewService.queryMenuViewList(sysMenuEntity.getMenuId());
			if(list!=null){
				sysMenuEntity.setMenuViewList1(list);
			}
			List<SysMenuOperationEntity> list1=	sysMenuOperationService.queryOperationViewList(sysMenuEntity.getMenuId());
			if(list1!=null){
				sysMenuEntity.setMenuOperationList1(list1);
			}*//*

		}



		return menuList;
	}*/
	/**
	 * 所有菜单列表1
	 */
	@GetMapping("/list")
	//@RequiresPermissions("sys:menu:list")
	public List<SysMenuEntity> list1(){
		SysUserEntity sysUserEntity=getUser();
logger.debug("sysUserEntity.getUserId()\t"+sysUserEntity.getUserId());
		List<SysMenuEntity> menuList= sysMenuService.getUserMenuList(sysUserEntity.getUserId());
		return menuList;
	}
	/**
	 * 选择菜单(添加、修改菜单)
	 */
	@GetMapping("/select")
	//@RequiresPermissions("sys:menu:select")
	public R select(){
		//查询列表数据
		List<SysMenuEntity> menuList = sysMenuService.queryNotButtonList();
		
		//添加顶级菜单
		SysMenuEntity root = new SysMenuEntity();
		root.setMenuId(0L);
		root.setName("一级菜单");
		root.setParentId(-1L);
		root.setOpen(true);
		menuList.add(root);
		
		return R.ok().put("menuList", menuList);
	}
	
	/**
	 * 菜单信息
	 */
	@GetMapping("/info/{menuId}")
	//@RequiresPermissions("sys:menu:info")
	public R info(@PathVariable("menuId") Long menuId){
		SysMenuEntity menu = sysMenuService.getById(menuId);
		return R.ok().put("menu", menu);
	}
	
	/**
	 * 保存
	 */
	@SysLog("保存菜单")
	@PostMapping("/save")
	@RequiresPermissions("sys:menu:save")
	public R save(@RequestBody SysMenuEntity menu){
		//数据校验
		verifyForm(menu);
		
		sysMenuService.save(menu);
		
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@SysLog("修改菜单")
	@PostMapping("/update")
	@RequiresPermissions("sys:menu:update")
	public R update(@RequestBody SysMenuEntity menu){
		//数据校验
		verifyForm(menu);
				
		sysMenuService.updateById(menu);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@SysLog("删除菜单")
	@PostMapping("/delete/{menuId}")
	@RequiresPermissions("sys:menu:delete")
	public R delete(@PathVariable("menuId") long menuId){
/*		if(menuId <= 31){
			return R.error("系统菜单，不能删除");
		}*/

		//判断是否有子菜单或按钮
	List<SysMenuEntity> menuList = sysMenuService.queryListParentId(menuId);
		if(menuList.size() == 0){
			sysMenuService.delete(menuId);
		}

		MenuTreeList.getMenuTreeList().deleteContentCategoryById(menuId,sysMenuViewService,sysMenuService,sysMenuOperationService);
		//sysMenuService.delete(menuId);
//		本地测试
		//deleteContentCategoryById(menuId);

		return R.ok();
	}


	
	/**
	 * 验证参数是否正确
	 */
	private void verifyForm(SysMenuEntity menu){
		if(StringUtils.isBlank(menu.getName())){
			throw new RRException("菜单名称不能为空");
		}
		
		if(menu.getParentId() == null){
			throw new RRException("上级菜单不能为空");
		}
		if(menu.getToolBar()==null){
			menu.setToolBar(0);
		}else{
			menu.setToolBar(menu.getToolBar());
		}
		//菜单
/*		if(menu.getType() == Constant.MenuType.MENU.getValue()){
			if(StringUtils.isBlank(menu.getUrl())){
				throw new RRException("菜单URL不能为空");
			}
		}*/
//本人注掉的验证
/*		//上级菜单类型
		int parentType = Constant.MenuType.CATALOG.getValue();
		if(menu.getParentId() != 0){
			SysMenuEntity parentMenu = sysMenuService.getById(menu.getParentId());
			parentType = parentMenu.getType();
		}
		
		//目录、菜单
		if(menu.getType() == Constant.MenuType.CATALOG.getValue() ||
				menu.getType() == Constant.MenuType.MENU.getValue()){
			if(parentType != Constant.MenuType.CATALOG.getValue()){
				throw new RRException("上级菜单只能为目录类型");
			}
			return ;
		}
		
		//按钮
		if(menu.getType() == Constant.MenuType.BUTTON.getValue()){
			if(parentType != Constant.MenuType.MENU.getValue()){
				throw new RRException("上级菜单只能为菜单类型");
			}
			return ;
		}*/
	}


	/**
	 * 视图树和操作树和为一颗树
	 */
	@SysLog("保存菜单")
	@PostMapping("/MergeOneTree")
	@RequiresPermissions("sys:menu:MergeOneTree")
	public R MergeOneTree(@RequestBody SysMenuVo menu){
		//数据校验
		verifyForm(menu);

		sysMenuService.MergeOneTree(menu);

		return R.ok();
	}


	/**
	 * 更新菜单-->视图树和操作树和为一颗树
	 */
	@SysLog("更新菜单")
	@PostMapping("/upadteMergeOneTree")
	@RequiresPermissions("sys:menu:upadteMergeOneTree")
	public R upadteMergeOneTree(@RequestBody SysMenuVo menu){
		//数据校验
		verifyForm(menu);

		sysMenuService.upadteMergeOneTree(menu);

		return R.ok();
	}

	/**
	 * 视图树和操作树和为一颗树
	 */
	@SysLog("保存菜单")
	@PostMapping("/MergeOneTree1")
	@RequiresPermissions("sys:menu:MergeOneTree")
	public R MergeOneTree1(@RequestBody SysMenuVo menu){
		//数据校验
		verifyForm(menu);

		sysMenuService.MergeOneTree1(menu);

		return R.ok();
	}

	/**
	 * 更新菜单-->视图树和操作树和为一颗树
	 */
	@SysLog("更新菜单")
	@PostMapping("/upadteMergeOneTree1")
	@RequiresPermissions("sys:menu:upadteMergeOneTree")
	public R upadteMergeOneTree1(@RequestBody SysMenuVo menu){

		//数据校验
		verifyForm(menu);

		sysMenuService.upadteMergeOneTree1(menu);

		return R.ok();
	}



/*
//	test递归删除

	public void deleteContentCategoryById(long menuId) {

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
				deleteContentCategoryById(sysMenuEntity.getMenuId());
			}
		}
	}*/

/**
 * 是否是默认的url
 */


	/**
	 * 通过menuId递归查询子数据
	 */
	@GetMapping("/getMenuList/{menuId}")
	//@RequiresPermissions("sys:menu:info")
	public R getMenuList(@PathVariable("menuId") Long menuId){
		List<SysMenuEntity> menu=MenuTreeList.getMenuTreeList().getMenus(menuId,sysMenuService);
		return R.ok().put("menu", menu);
	}

	/**
	 * 通过menuId递归查询子数据1
	 */
	@GetMapping("/getMenuList1/{menuId}")
	//@RequiresPermissions("sys:menu:info")
	public R getMenuList1(@PathVariable("menuId") Long menuId){
		SysMenuEntity sysMenuEntity=new SysMenuEntity();
		List<SysMenuEntity>  list=sysMenuService.getMenuList1(menuId);
		List<SysMenuEntity>  viewRes=new ArrayList<>();
		List<SysMenuEntity>  operateRes=new ArrayList<>();

		for (SysMenuEntity menu:list) {
			logger.debug(menu.getName());
			List<SysMenuEntity> viewRes1=sysMenuService.getMenuList1(menu.getMenuId());
			logger.debug(""+viewRes1);

			for (SysMenuEntity sysMenuVo:
			viewRes1 ) {
				if(sysMenuService.getById(sysMenuVo.getParentId()).getName().equals("视图")){
					viewRes.add(sysMenuVo);
				}
				if(sysMenuService.getById(sysMenuVo.getParentId()).getName().equals("操作")){
					operateRes.add(sysMenuVo);
				}
			}
		}
		sysMenuEntity.setMenuViewList(viewRes);
		sysMenuEntity.setMenuOperationList(operateRes);

		return R.ok().put("menu", sysMenuEntity);
	}

	/**
	 * 判断操作代码是否重复
	 */
	@GetMapping("/repeatOperateCode/{operateCode}")
	//@RequiresPermissions("sys:role:info")
	public R repeatOperateCode(@PathVariable("operateCode") String operateCode) {
		if(operateCode!=null){
			SysMenuEntity sysMenuEntity	=sysMenuService.findByOperateCode(operateCode);
			if(sysMenuEntity!=null){
				return R.error("操作代码重复");
			}else{
				return R.ok();
			}
		}else{
			return R.error("操作代码不能为空");
		}


	}

}
