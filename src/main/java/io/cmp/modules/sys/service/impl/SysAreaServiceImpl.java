package io.cmp.modules.sys.service.impl;

import io.cmp.modules.sys.entity.SysMenuEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.Query;

import io.cmp.modules.sys.dao.SysAreaDao;
import io.cmp.modules.sys.entity.SysAreaEntity;
import io.cmp.modules.sys.service.SysAreaService;


@Service("areaService")
public class SysAreaServiceImpl extends ServiceImpl<SysAreaDao, SysAreaEntity> implements SysAreaService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysAreaEntity> page = this.page(
                new Query<SysAreaEntity>().getPage(params),
                new QueryWrapper<SysAreaEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据父地区，查询子地区
     * @param parentId 父地区ID
     */
    @Override
    public List<SysAreaEntity> queryListParentId(Long parentId) {
        return baseMapper.queryListParentId(parentId);
    }
}