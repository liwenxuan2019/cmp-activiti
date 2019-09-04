package io.cmp.modules.sys.controller;

import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.R;
import io.cmp.modules.sys.entity.SysMenuOperationEntity;
import io.cmp.modules.sys.service.SysMenuOperationService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 菜单操作配置表
 *
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-07-07 15:52:28
 */
@RestController
@RequestMapping("sys/sysmenuoperation")
public class SysMenuOperationController {
    @Autowired
    private SysMenuOperationService sysMenuOperationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:sysmenuoperation:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysMenuOperationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:sysmenuoperation:info")
    public R info(@PathVariable("id") Long id) {
        SysMenuOperationEntity sysMenuOperation = sysMenuOperationService.getById(id);

        return R.ok().put("sysMenuOperation", sysMenuOperation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:sysmenuoperation:save")
    public R save(@RequestBody SysMenuOperationEntity sysMenuOperation) {
        sysMenuOperationService.save(sysMenuOperation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:sysmenuoperation:update")
    public R update(@RequestBody SysMenuOperationEntity sysMenuOperation) {
        sysMenuOperationService.updateById(sysMenuOperation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:sysmenuoperation:delete")
    public R delete(@RequestBody Long[] ids) {
        sysMenuOperationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
