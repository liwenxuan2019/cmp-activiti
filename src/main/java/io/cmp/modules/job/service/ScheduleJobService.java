

package io.cmp.modules.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.cmp.common.utils.PageUtils;
import io.cmp.modules.job.entity.ScheduleJobEntity;

import java.util.Map;

/**
 * 定时任务
 *
 * @author
 */
public interface ScheduleJobService extends IService<ScheduleJobEntity> {

	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 保存定时任务
	 */
	void saveJob(ScheduleJobEntity scheduleJob);
	
	/**
	 * 更新定时任务
	 */
	void update(ScheduleJobEntity scheduleJob);
	
	/**
	 * 批量删除定时任务
	 */
	void deleteBatch(String[] jobIds);
	
	/**
	 * 批量更新定时任务状态
	 */
	int updateBatch(String[] jobIds, int status);
	
	/**
	 * 立即执行
	 */
	void run(String[] jobIds);
	
	/**
	 * 暂停运行
	 */
	void pause(String[] jobIds);
	
	/**
	 * 恢复运行
	 */
	void resume(String[] jobIds);
}
