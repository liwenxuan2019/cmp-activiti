

package io.cmp.modules.sys.controller;

import io.cmp.common.exception.RRException;
import io.cmp.common.utils.Constant;
import io.cmp.common.utils.MenuTreeList;
import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.R;
import io.cmp.common.validator.ValidatorUtils;
import io.cmp.modules.sys.entity.SysDeptEntity;
import io.cmp.modules.sys.entity.SysDeptLeaderEntity;
import io.cmp.modules.sys.entity.SysUserEntity;
import io.cmp.modules.sys.service.SysDeptLeaderService;
import io.cmp.modules.sys.service.SysDeptService;
import io.cmp.modules.sys.service.SysRoleService;
import io.cmp.modules.sys.service.SysUserService;
import io.cmp.modules.sys.vo.NewEmployee;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * 部门管理
 *
 * @author
 */
@RestController
@RequestMapping("/sys/dept")
public class SysDeptController extends AbstractController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private SysDeptService sysDeptService;
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysDeptLeaderService sysDeptLeaderService;
	/**
	 * 列表
	 */
	@GetMapping("/list")
	//@RequiresPermissions("sys:dept:list")
	public List<SysDeptEntity> list() {
		List<SysDeptEntity> deptList = sysDeptService.queryList(new HashMap<String, Object>());

        for (SysDeptEntity sysDeptEntity:
                deptList) {
            List<SysUserEntity> lists=sysUserService.findByDeptId(sysDeptEntity.getDeptId());
            logger.debug(""+lists.size());
            sysDeptEntity.setUserCount(lists.size());
        }

		return deptList;

	}

	/**
	 * 选择部门(添加、修改菜单)
	 */
	@GetMapping("/select")
	@RequiresPermissions("sys:dept:select")
	public R select() {
		List<SysDeptEntity> deptList = sysDeptService.queryList(new HashMap<String, Object>());

		//添加一级部门
		if (getUserId() == Constant.SUPER_ADMIN) {
			SysDeptEntity root = new SysDeptEntity();
			root.setDeptId(0L);
			root.setName("一级部门");
			root.setParentId(-1L);
			root.setOpen(true);
			deptList.add(root);
		}

		return R.ok().put("deptList", deptList);
	}

	/**
	 * 上级部门Id(管理员则为0)
	 */
	@GetMapping("/info")
	//@RequiresPermissions("sys:dept:list")
	public R info() {
		long deptId = 0;
		if (getUserId() != Constant.SUPER_ADMIN) {
			List<SysDeptEntity> deptList = sysDeptService.queryList(new HashMap<String, Object>());
			Long parentId = null;
			for (SysDeptEntity sysDeptEntity : deptList) {
				if (parentId == null) {
					parentId = sysDeptEntity.getParentId();
					continue;
				}

				if (parentId > sysDeptEntity.getParentId().longValue()) {
					parentId = sysDeptEntity.getParentId();
				}
			}
			deptId = parentId;
		}

		return R.ok().put("deptId", deptId);
	}

	/**
	 * 信息
	 */
	@GetMapping("/querySubDeptByDeptId/{deptId}")
	//@RequiresPermissions("sys:dept:querySubDeptByDeptId")
	public R querySubDeptByDeptId(@PathVariable("deptId") Long deptId) throws Exception {
logger.debug("94611111======");
//		查询当前部门的信息
		SysDeptEntity dept = sysDeptService.getById(deptId);

		if(dept==null){
            throw new RRException("此部门已被删除");
        }

//		第一种返回数据的方式
        /*
//      根据父部门Id查询子部门列表
		List<SysDeptEntity> parentList=sysDeptService.queryListParentId(dept.getDeptId());

		for (SysDeptEntity sys: parentList) {


				List<SysDeptEntity> parentList1=sysDeptService.queryListParentId(dept.getDeptId());

		}

		List<SysDeptEntity> res=new ArrayList<>();
		res.add(dept);
//		设置父部门的名字
		for (SysDeptEntity sys: parentList) {
			sys.setParentName(dept.getName());
			res.add(sys);
		}

        return R.ok().put("dept", res);*/


		//List<SysDeptEntity> parentList=	getDepartmentList(dept, new ArrayList<>());
//		设置子部门
        //		第二种返回数据的方式

       // List<SysDeptEntity> parentList = getDepts(dept.getDeptId());
//		设置部门员工数量


        List<SysDeptEntity> parentList =MenuTreeList.getMenuTreeList().getDepts(dept.getDeptId(),sysDeptService);
       // MenuTreeList.getMenuTreeList().list(dept.getDeptId());
		dept.setList(parentList);
		List<SysUserEntity> list=sysUserService.findByDeptId(deptId);
		logger.debug(""+list.size());
			dept.setUserCount(list.size());
		return R.ok().put("dept", dept);
	}


	/**
	 * 信息
	 */
	@GetMapping("/info/{deptId}")
	//@RequiresPermissions("sys:dept:info")
	public R info(@PathVariable("deptId") Long deptId){
		SysDeptEntity dept = sysDeptService.getById(deptId);
		if(dept!=null){
			List<SysDeptLeaderEntity> list=sysDeptLeaderService.findUserByDeptId(deptId);
			List<SysUserEntity> result=new ArrayList<>();
			List<Long> ids=new ArrayList<>();
			for (SysDeptLeaderEntity sysDeptLeaderEntity:list) {
				if(sysDeptLeaderEntity!=null){
					SysUserEntity sysUserEntity=sysUserService.getById(sysDeptLeaderEntity.getUserId())	;
					result.add(sysUserEntity);
					ids.add(sysDeptLeaderEntity.getUserId());
				}

			}
            List<SysDeptEntity> parentList =MenuTreeList.getMenuTreeList().getDepts(dept.getDeptId(),sysDeptService);
            // MenuTreeList.getMenuTreeList().list(dept.getDeptId());
            dept.setList(parentList);
            List<SysUserEntity> lists=sysUserService.findByDeptId(deptId);
//            该部门下的用户数量
           if(lists!=null){
			   dept.setUserCount(lists.size());
		   }else{
			   dept.setUserCount(0);
		   }
			dept.setManyDepartHeads(ids);
			dept.setSysUserList(result);
//			负责人数量
			if(ids!=null){
				dept.setDeptLeaderCount(ids.size());
			}else{
				dept.setDeptLeaderCount(0);
			}

			return R.ok().put("dept", dept);
		}
		return R.ok().put("dept", dept);
	}
	
/*	*//**
	 * 保存
	 *//*
	@PostMapping("/save")
	@RequiresPermissions("sys:dept:save")
	public R save(@RequestBody SysDeptEntity dept){

//        通过机构代码查询是否存在该机构代码
        if(dept.getDeptCode()!=null&&!(dept.getDeptCode().trim().equals(""))){
            if(sysDeptService.queryBydeptCode(dept.getDeptCode())!=null){
                throw new RRException("机构代码重复");
            }else{

                sysDeptService.save(dept);
            }
        }else{

            throw new RRException("机构代码不能为空");
        }
		return R.ok();
	}
	*/
	/**
	 * 修改
	 */
/*	@PostMapping("/update")
	@RequiresPermissions("sys:dept:update")
	public R update(@RequestBody SysDeptEntity dept){

        if(dept.getDeptCode()!=null&&!(dept.getDeptCode().trim().equals(""))) {
            sysDeptService.updateById(dept);
        }else{
            throw new RRException("机构代码不能为空");
        }
		return R.ok();
	}*/
	
	/**
	 * 删除
	 */
	@PostMapping("/delete/{deptId}")
	@RequiresPermissions("sys:dept:delete")
	public R delete(@PathVariable("deptId") long deptId){
		if(deptId==1){
			return R.error("根节点部门不能删除");
		}


		//判断是否有子部门
		List<Long> deptList = sysDeptService.queryDetpIdList(deptId);
		if(deptList.size() > 0){
			return R.error("请先删除子部门");
		}


//		查询一下
		List<SysUserEntity> deptUserList=sysUserService.findByDeptId(deptId);

		if(deptUserList!=null&&deptUserList.size()!=0) {
			return R.error("该部门下有员工不能删除");

		}else{

//		删除部门的时候把其他的关系表也删除了
		//sysRoleService.deleteBatch();
		sysDeptService.removeById(deptId);
		//                先删除中间表中的关系
		sysDeptLeaderService.deleteBatchs(new Long[]{deptId});
		}
		return R.ok();
	}

	/**
	 * 用户列表
	 */

	@GetMapping("/lists")
	//@RequiresPermissions("sys:user:list")
	public R list(@RequestParam Map<String, Object> params){
		//只有超级管理员，才能查看所有管理员列表
		/*if(getUserId() != Constant.SUPER_ADMIN){
			params.put("createUserId", getUserId());
		}*/
		PageUtils page = sysUserService.queryPage(params);

		return R.ok().put("page", page);
	}

	/**
	 * 引入员工
	 */
	@PostMapping("/newEmployee")
	@RequiresPermissions("sys:dept:save")
		public R newEmployee(@RequestBody NewEmployee newEmployee){

		sysDeptService.newEmployee(newEmployee);

		return R.ok();
	}

/*//	递归子部门
	public List<SysDeptEntity> getDepts(Long deptId) {

		List<SysDeptEntity> res = new ArrayList<>();
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
				deptVo2.setList(getDepts(deptVo.getDeptId()));
				deptVo2.setName(deptVo.getName());
				deptVo2.setParentName(sysDeptService.getById(sysDeptService.getById(deptVo.getDeptId()).getParentId()).getName());
				deptVosList.add(deptVo2);
			}
	}


		return deptVosList;

	}*/

   /**
     * 查询不在该部门下的员工-->把某一个或多个员工设置成负责人
     * 传入部门ID-->查询sys_user表
     */
    @PostMapping("/tosave")
    @RequiresPermissions("sys:dept:save")
    public R toSaveDeptHeaders(@RequestBody SysDeptEntity dept){

        return sysDeptService.toSaveDeptHeaders(dept);
    }


    /**
     * 修改部门负责人
     */
    @PostMapping("/toupdate")
    @RequiresPermissions("sys:dept:update")
    public R toupdateHader(@RequestBody SysDeptEntity dept){
        return  sysDeptService.toupdateHader(dept);
    }

    /**
     *
     * 确定部门添加负责人
     */
    @PostMapping("/save")
    @RequiresPermissions("sys:dept:save")
    public R saveDeptHeaders(@RequestBody SysDeptEntity dept){
        return sysDeptService.saveDeptHeaders(dept);
    }
   /**
     * 确定修改部门负责人
     */
    @PostMapping("/update")
    @RequiresPermissions("sys:dept:update")
    public R updateHader(@RequestBody SysDeptEntity dept){
        return sysDeptService.updateHader(dept);
    }
	/**
	 * 判断角色代码是否重复
	 */
	@GetMapping("/repeatDeptCode/{deptCode}")
	//@RequiresPermissions("sys:role:info")
	public R repeatDeptCode(@PathVariable("deptCode") String deptCode) {
		if(deptCode!=null){
			SysDeptEntity sysDeptEntity	=sysDeptService.queryBydeptCode(deptCode);
			if(sysDeptEntity!=null){
				return R.error("机构代码重复");
			}
		}else{
			return R.error("机构代码不能为空");
		}
		return R.ok();

	}
    /**
     *
     * 添加部门负责人
     */
    @PostMapping("/saveDeptLeader")
    @RequiresPermissions("sys:dept:save")
    public R saveDeptLeader(@RequestBody SysDeptEntity dept){
		ValidatorUtils.validateEntity(dept);
        return sysDeptService.saveDeptLeader(dept);
    }
    /**
     * 修改部门负责人
     */
    @PostMapping("/updateLeader")
    @RequiresPermissions("sys:dept:update")
    public R updateLeader(@RequestBody SysDeptEntity dept){
		ValidatorUtils.validateEntity(dept);
        return sysDeptService.updateLeader(dept);
    }

}
