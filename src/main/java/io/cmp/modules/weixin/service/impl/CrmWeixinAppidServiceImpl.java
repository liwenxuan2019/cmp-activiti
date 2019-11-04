package io.cmp.modules.weixin.service.impl;

import io.cmp.common.utils.Constant;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.Query;

import io.cmp.modules.weixin.dao.CrmWeixinAppidDao;
import io.cmp.modules.weixin.entity.CrmWeixinAppidEntity;
import io.cmp.modules.weixin.service.CrmWeixinAppidService;


@Service("crmWeixinAppidService")
public class CrmWeixinAppidServiceImpl extends ServiceImpl<CrmWeixinAppidDao, CrmWeixinAppidEntity> implements CrmWeixinAppidService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String appid = (String)params.get("appid");
        String accessToken = (String)params.get("accessToken");
        String startCreateTime = (String)params.get("startCreateTime");
        String endCreateTime = (String)params.get("endCreateTime");
        IPage<CrmWeixinAppidEntity> page = this.page(
                new Query<CrmWeixinAppidEntity>().getPage(params),
                new QueryWrapper<CrmWeixinAppidEntity>()
                        .eq(StringUtils.isNotBlank(appid),"appid", appid)
                        .eq(StringUtils.isNotBlank(accessToken),"access_token", accessToken)
                        .ge(StringUtils.isNotBlank(startCreateTime),"create_time",startCreateTime)
                        .le(StringUtils.isNotBlank(endCreateTime),"create_time",endCreateTime)
                        .apply(params.get(Constant.SQL_FILTER) != null, (String)params.get(Constant.SQL_FILTER))
        );

        return new PageUtils(page);
    }

}