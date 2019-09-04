package io.cmp.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.cmp.common.utils.Constant;
import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.Query;
import io.cmp.modules.sys.dao.SysMenuViewDao;
import io.cmp.modules.sys.entity.SysMenuViewEntity;
import io.cmp.modules.sys.service.SysMenuViewService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("sysMenuViewService")
public class SysMenuViewServiceImpl extends ServiceImpl<SysMenuViewDao, SysMenuViewEntity> implements SysMenuViewService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String menuId = (String)params.get("menuId");
        String viewName = (String)params.get("viewName");
        String isView = (String)params.get("isView");
        String orderNum = (String)params.get("orderNum");
        String createCode = (String)params.get("createCode");
        String createName = (String)params.get("createName");
        String startCreateTime = (String)params.get("startCreateTime");
        String endCreateTime = (String)params.get("endCreateTime");
        String updateCode = (String)params.get("updateCode");
        String updateName = (String)params.get("updateName");
        String startUpdateTime = (String)params.get("startUpdateTime");
        String endUpdateTime = (String)params.get("endUpdateTime");
        IPage<SysMenuViewEntity> page = this.page(
                new Query<SysMenuViewEntity>().getPage(params),
                new QueryWrapper<SysMenuViewEntity>()
                .eq(StringUtils.isNotBlank(menuId),"menu_id", menuId)
                .like(StringUtils.isNotBlank(viewName),"view_name", viewName)
                .eq(StringUtils.isNotBlank(isView),"is_view", isView)
                .eq(StringUtils.isNotBlank(orderNum),"order_num", orderNum)
                .eq(StringUtils.isNotBlank(createCode),"create_code", createCode)
                .like(StringUtils.isNotBlank(createName),"create_name", createName)
                .ge(StringUtils.isNotBlank(startCreateTime),"create_time",startCreateTime)
                .le(StringUtils.isNotBlank(endCreateTime),"create_time",endCreateTime)
                .eq(StringUtils.isNotBlank(updateCode),"update_code", updateCode)
                .like(StringUtils.isNotBlank(updateName),"update_name", updateName)
                .ge(StringUtils.isNotBlank(startUpdateTime),"update_time",startUpdateTime)
                .le(StringUtils.isNotBlank(endUpdateTime),"update_time",endUpdateTime)
                .apply(params.get(Constant.SQL_FILTER) != null, (String)params.get(Constant.SQL_FILTER))
        );

        return new PageUtils(page);
    }

    @Override
    public List<SysMenuViewEntity> queryMenuViewList(Long menuId) {



        return baseMapper.queryMenuViewList(menuId);
    }

}