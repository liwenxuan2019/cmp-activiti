package io.cmp.modules.sys.dao;

import io.cmp.modules.sys.entity.SysAreaEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.cmp.modules.sys.entity.SysMenuEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 地区表
 * 
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-06-25 14:26:47
 */
@Mapper
public interface SysAreaDao extends BaseMapper<SysAreaEntity> {
    /**
     * 根据父地区，查询子地区
     * @param parentId 父地区ID
     */
    List<SysAreaEntity> queryListParentId(Long parentId);
}
