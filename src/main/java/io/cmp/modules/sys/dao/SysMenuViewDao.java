package io.cmp.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.cmp.modules.sys.entity.SysMenuViewEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单视图表
 * 
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-07-07 15:52:28
 */
@Mapper
public interface SysMenuViewDao extends BaseMapper<SysMenuViewEntity> {
  List<SysMenuViewEntity> queryMenuViewList(Long menuId);
}
