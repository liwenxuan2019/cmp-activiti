

package io.cmp.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.cmp.common.utils.R;
import io.cmp.modules.sys.entity.SysAreaEntity;
import io.cmp.modules.sys.entity.SysDeptEntity;
import io.cmp.modules.sys.vo.NewEmployee;

import java.util.List;
import java.util.Map;

/**
 * 部门管理
 *
 * @author
 */
public interface SysDeptService extends IService<SysDeptEntity> {

	List<SysDeptEntity> queryList(Map<String, Object> map);

	/**
	 * 查询子部门ID列表
	 * @param parentId  上级部门ID
	 */
	List<Long> queryDetpIdList(Long parentId);

	/**
	 * 获取子部门ID，用于数据过滤
	 */
	List<Long> getSubDeptIdList(Long deptId);

	/**
	 * 根据父部门，查询子部门
	 * @param parentId 父部门ID
	 */
	List<SysDeptEntity> queryListParentId(Long parentId);

	/**
	 * 更新用户的deptId
	 */
    void newEmployee(NewEmployee newEmployee);

	SysDeptEntity queryBydeptCode(String deptCode);

	R toSaveDeptHeaders(SysDeptEntity dept);

	R toupdateHader(SysDeptEntity dept);
	
	R saveDeptHeaders(SysDeptEntity dept);
	R updateHader(SysDeptEntity dept);


	R saveDeptLeader(SysDeptEntity dept);

	R updateLeader(SysDeptEntity dept);
}
