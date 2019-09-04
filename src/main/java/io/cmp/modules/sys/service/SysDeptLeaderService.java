package io.cmp.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.cmp.common.utils.PageUtils;
import io.cmp.modules.sys.entity.SysDeptEntity;
import io.cmp.modules.sys.entity.SysDeptLeaderEntity;

import java.util.List;
import java.util.Map;

/**
 * 部门负责人
 *
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-07-16 15:40:26
 */
public interface SysDeptLeaderService extends IService<SysDeptLeaderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void deleteBatchs(Long [] deptIds);

    List<SysDeptLeaderEntity> findByUserId(Long userId);

    List<SysDeptLeaderEntity> findUserByDeptId(Long deptId);

    void deleteByUserId(Long userId);
}

