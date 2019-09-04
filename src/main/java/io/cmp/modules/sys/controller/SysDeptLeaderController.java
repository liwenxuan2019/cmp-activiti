package io.cmp.modules.sys.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.cmp.modules.sys.entity.SysDeptLeaderEntity;
import io.cmp.modules.sys.service.SysDeptLeaderService;
import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.R;



/**
 * 部门负责人
 *
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-07-16 15:40:26
 */
@RestController
@RequestMapping("sys/sysdeptleader")
public class SysDeptLeaderController {
    @Autowired
    private SysDeptLeaderService sysDeptLeaderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("sys:sysdeptleader:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysDeptLeaderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("sys:sysdeptleader:info")
    public R info(@PathVariable("id") Long id){
		SysDeptLeaderEntity sysDeptLeader = sysDeptLeaderService.getById(id);

        return R.ok().put("sysDeptLeader", sysDeptLeader);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("sys:sysdeptleader:save")
    public R save(@RequestBody SysDeptLeaderEntity sysDeptLeader){
		sysDeptLeaderService.save(sysDeptLeader);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("sys:sysdeptleader:update")
    public R update(@RequestBody SysDeptLeaderEntity sysDeptLeader){
		sysDeptLeaderService.updateById(sysDeptLeader);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("sys:sysdeptleader:delete")
    public R delete(@RequestBody Long[] ids){
		sysDeptLeaderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
