package io.cmp.modules.weixin.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.cmp.modules.weixin.entity.CrmWeixinAppidEntity;
import io.cmp.modules.weixin.service.CrmWeixinAppidService;
import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.R;



/**
 * 微信appidtoken表
 *
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-11-04 11:20:33
 */
@RestController
@RequestMapping("weixin/crmweixinappid")
public class CrmWeixinAppidController {
    @Autowired
    private CrmWeixinAppidService crmWeixinAppidService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("weixin:crmweixinappid:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = crmWeixinAppidService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("weixin:crmweixinappid:info")
    public R info(@PathVariable("id") String id){
		CrmWeixinAppidEntity crmWeixinAppid = crmWeixinAppidService.getById(id);

        return R.ok().put("crmWeixinAppid", crmWeixinAppid);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("weixin:crmweixinappid:save")
    public R save(@RequestBody CrmWeixinAppidEntity crmWeixinAppid){
		crmWeixinAppidService.save(crmWeixinAppid);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("weixin:crmweixinappid:update")
    public R update(@RequestBody CrmWeixinAppidEntity crmWeixinAppid){
		crmWeixinAppidService.updateById(crmWeixinAppid);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("weixin:crmweixinappid:delete")
    public R delete(@RequestBody String[] ids){
		crmWeixinAppidService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
