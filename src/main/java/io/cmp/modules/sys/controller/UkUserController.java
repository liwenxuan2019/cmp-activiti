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

import io.cmp.modules.sys.entity.UkUserEntity;
import io.cmp.modules.sys.service.UkUserService;
import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.R;



/**
 * 用户表
 *
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-08-21 09:44:52
 */
@RestController
@RequestMapping("sys/ukuser")
public class UkUserController {
    @Autowired
    private UkUserService ukUserService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("sys:ukuser:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = ukUserService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("sys:ukuser:info")
    public R info(@PathVariable("id") String id){
		UkUserEntity ukUser = ukUserService.getById(id);

        return R.ok().put("ukUser", ukUser);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("sys:ukuser:save")
    public R save(@RequestBody UkUserEntity ukUser){
		ukUserService.save(ukUser);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("sys:ukuser:update")
    public R update(@RequestBody UkUserEntity ukUser){
		ukUserService.updateById(ukUser);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("sys:ukuser:delete")
    public R delete(@RequestBody String[] ids){
		ukUserService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
