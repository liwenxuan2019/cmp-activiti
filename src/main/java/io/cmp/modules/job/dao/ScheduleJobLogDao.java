

package io.cmp.modules.job.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.cmp.modules.job.entity.ScheduleJobLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务日志
 *
 * @author
 */
@Mapper
public interface ScheduleJobLogDao extends BaseMapper<ScheduleJobLogEntity> {
	
}
