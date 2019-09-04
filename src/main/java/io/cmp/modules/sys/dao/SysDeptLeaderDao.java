package io.cmp.modules.sys.dao;

import io.cmp.modules.sys.entity.SysDeptEntity;
import io.cmp.modules.sys.entity.SysDeptLeaderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 部门负责人
 * 
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-07-16 15:40:26
 */
@Mapper
public interface SysDeptLeaderDao extends BaseMapper<SysDeptLeaderEntity> {

    void deleteBatch(Long[] deptIds);


    List<SysDeptLeaderEntity> findByUserId(Long userId);

    List<SysDeptLeaderEntity> findUserByDeptId(Long deptId);

    void deleteByUserId(Long userId);
}
