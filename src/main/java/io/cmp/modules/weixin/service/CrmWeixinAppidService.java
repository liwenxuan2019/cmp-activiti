package io.cmp.modules.weixin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.cmp.common.utils.PageUtils;
import io.cmp.modules.weixin.entity.CrmWeixinAppidEntity;

import java.util.Map;

/**
 * 微信appidtoken表
 *
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-11-04 11:20:33
 */
public interface CrmWeixinAppidService extends IService<CrmWeixinAppidEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

