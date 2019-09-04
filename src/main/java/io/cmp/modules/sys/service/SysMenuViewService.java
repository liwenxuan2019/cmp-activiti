package io.cmp.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.cmp.common.utils.PageUtils;
import io.cmp.modules.sys.entity.SysMenuViewEntity;

import java.util.List;
import java.util.Map;

/**
 * 菜单视图表
 *
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-07-07 15:52:28
 */
public interface SysMenuViewService extends IService<SysMenuViewEntity> {

    PageUtils queryPage(Map<String, Object> params);
    List<SysMenuViewEntity> queryMenuViewList(Long menuId);

}

