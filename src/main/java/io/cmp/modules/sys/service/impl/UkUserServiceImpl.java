package io.cmp.modules.sys.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.Query;

import io.cmp.modules.sys.dao.UkUserDao;
import io.cmp.modules.sys.entity.UkUserEntity;
import io.cmp.modules.sys.service.UkUserService;
import org.springframework.transaction.annotation.Transactional;


@Service("ukUserService")
public class UkUserServiceImpl extends ServiceImpl<UkUserDao, UkUserEntity> implements UkUserService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UkUserEntity> page = this.page(
                new Query<UkUserEntity>().getPage(params),
                new QueryWrapper<UkUserEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUkUser(UkUserEntity ukUserEntity) {
        baseMapper.insertUkUser(ukUserEntity);
    }
}