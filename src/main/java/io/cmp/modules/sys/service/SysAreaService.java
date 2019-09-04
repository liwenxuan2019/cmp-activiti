package io.cmp.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.cmp.common.utils.PageUtils;
import io.cmp.modules.sys.entity.SysAreaEntity;
import io.cmp.modules.sys.entity.SysMenuEntity;

import java.util.List;
import java.util.Map;

/**
 * 地区表
 *
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-06-25 14:26:47
 */
public interface SysAreaService extends IService<SysAreaEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据父地区，查询子地区
     * @param parentId 父地区ID
     */
    List<SysAreaEntity> queryListParentId(Long parentId);
}

