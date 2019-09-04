package io.cmp.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.cmp.common.utils.PageUtils;
import io.cmp.modules.sys.entity.SysMenuOperationEntity;

import java.util.List;
import java.util.Map;

/**
 * 菜单操作配置表
 *
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-07-07 15:52:28
 */
public interface SysMenuOperationService extends IService<SysMenuOperationEntity> {

    PageUtils queryPage(Map<String, Object> params);
    List<SysMenuOperationEntity> queryOperationViewList(Long menuId);
}

