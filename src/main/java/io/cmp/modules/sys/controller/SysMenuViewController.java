package io.cmp.modules.sys.controller;

import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.R;
import io.cmp.modules.sys.entity.SysMenuViewEntity;
import io.cmp.modules.sys.service.SysMenuViewService;
import io.cmp.modules.sys.entity.SysMenuViewEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 菜单视图表
 *
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-07-07 15:52:28
 */
@RestController
@RequestMapping("sys/sysmenuview")
public class SysMenuViewController {
    @Autowired
    private SysMenuViewService sysMenuViewService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:sysmenuview:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysMenuViewService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:sysmenuview:info")
    public R info(@PathVariable("id") Long id) {
        SysMenuViewEntity sysMenuView = sysMenuViewService.getById(id);

        return R.ok().put("sysMenuView", sysMenuView);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:sysmenuview:save")
    public R save(@RequestBody SysMenuViewEntity sysMenuView) {
        sysMenuViewService.save(sysMenuView);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:sysmenuview:update")
    public R update(@RequestBody SysMenuViewEntity sysMenuView) {
        sysMenuViewService.updateById(sysMenuView);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:sysmenuview:delete")
    public R delete(@RequestBody Long[] ids) {
        sysMenuViewService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
