package io.cmp.modules.sys.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.cmp.modules.sys.entity.SysMenuEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.cmp.modules.sys.entity.SysAreaEntity;
import io.cmp.modules.sys.service.SysAreaService;
import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.R;



/**
 * 地区表
 *
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-06-25 14:26:47
 */
@RestController
@RequestMapping("sys/area")
public class SysAreaController {
    @Autowired
    private SysAreaService sysAreaService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:area:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysAreaService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 根据父节点ID查询列表
     */
    @GetMapping("/queryListParentId/{parentId}")
    @RequiresPermissions("sys:area:queryListParentId")
    public List<SysAreaEntity> queryListParentId(@PathVariable("parentId") Long parentId){
        List<SysAreaEntity> areaList = sysAreaService.queryListParentId(parentId);
        return areaList;
}

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:area:info")
    public R info(@PathVariable("id") Long id){
		SysAreaEntity area = sysAreaService.getById(id);

        return R.ok().put("area", area);
    }



    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:area:save")
    public R save(@RequestBody SysAreaEntity area){
		sysAreaService.save(area);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:area:update")
    public R update(@RequestBody SysAreaEntity area){
		sysAreaService.updateById(area);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:area:delete")
    public R delete(@RequestBody Long[] ids){
		sysAreaService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
