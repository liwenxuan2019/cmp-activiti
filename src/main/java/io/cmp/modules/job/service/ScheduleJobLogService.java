

package io.cmp.modules.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.cmp.common.utils.PageUtils;
import io.cmp.modules.job.entity.ScheduleJobLogEntity;

import java.util.Map;

/**
 * 定时任务日志
 *
 * @author
 */
public interface ScheduleJobLogService extends IService<ScheduleJobLogEntity> {

	PageUtils queryPage(Map<String, Object> params);
	
}
