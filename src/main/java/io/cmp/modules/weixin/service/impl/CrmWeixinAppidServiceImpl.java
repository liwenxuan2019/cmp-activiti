package io.cmp.modules.weixin.service.impl;

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
        IPage<CrmWeixinAppidEntity> page = this.page(
                new Query<CrmWeixinAppidEntity>().getPage(params),
                new QueryWrapper<CrmWeixinAppidEntity>()
        );

        return new PageUtils(page);
    }

}