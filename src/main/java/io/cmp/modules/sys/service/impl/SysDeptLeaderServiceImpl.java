package io.cmp.modules.sys.service.impl;

import io.cmp.modules.sys.entity.SysDeptEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.Query;

import io.cmp.modules.sys.dao.SysDeptLeaderDao;
import io.cmp.modules.sys.entity.SysDeptLeaderEntity;
import io.cmp.modules.sys.service.SysDeptLeaderService;


@Service("sysDeptLeaderService")
public class SysDeptLeaderServiceImpl extends ServiceImpl<SysDeptLeaderDao, SysDeptLeaderEntity> implements SysDeptLeaderService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysDeptLeaderEntity> page = this.page(
                new Query<SysDeptLeaderEntity>().getPage(params),
                new QueryWrapper<SysDeptLeaderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void deleteBatchs(Long[] deptIds) {
        baseMapper.deleteBatch(deptIds);
    }

    @Override
    public List<SysDeptLeaderEntity> findByUserId(Long userId) {
        return baseMapper.findByUserId(userId);
    }

    @Override
    public List<SysDeptLeaderEntity> findUserByDeptId(Long deptId) {
        return baseMapper.findUserByDeptId(deptId);
    }

    @Override
    public void deleteByUserId(Long userId) {
        baseMapper.deleteByUserId(userId);
    }

}