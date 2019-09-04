

package io.cmp.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.cmp.modules.sys.entity.SysAreaEntity;
import io.cmp.modules.sys.entity.SysDeptEntity;
import io.cmp.modules.sys.vo.NewEmployee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 部门管理
 *
 * @author
 */
@Mapper
public interface SysDeptDao extends BaseMapper<SysDeptEntity> {

    List<SysDeptEntity> queryList(Map<String, Object> params);

    /**
     * 查询子部门ID列表
     * @param parentId  上级部门ID
     */
    List<Long> queryDetpIdList(Long parentId);


    /**
     * 根据父部门，查询子部门
     * @param parentId 父部门ID
     */
    List<SysDeptEntity> queryListParentId(Long parentId);


    void newEmployee(NewEmployee newEmployee);

    SysDeptEntity queryBydeptCode(String deptCode);
}
