

package io.cmp.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.cmp.common.utils.PageUtils;
import io.cmp.modules.sys.entity.SysMenuEntity;
import io.cmp.modules.sys.entity.SysUserEntity;
import io.cmp.modules.sys.vo.BatchAddUser;
import io.swagger.models.auth.In;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


/**
 * 系统用户
 *
 * @author
 */
public interface SysUserService extends IService<SysUserEntity> {

	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 查询用户的所有权限
	 * @param userId  用户ID
	 */
	List<String> queryAllPerms(Long userId);
	
	/**
	 * 查询用户的所有菜单ID
	 */
	List<Long> queryAllMenuId(Long userId);

	/**
	 * 根据用户名，查询系统用户
	 */
	SysUserEntity queryByUserName(String username);

	/**
	 * 保存用户
	 */
	void saveUser(SysUserEntity user);
	
	/**
	 * 修改用户
	 */
	void update(SysUserEntity user);
	
	/**
	 * 删除用户
	 */
	void deleteBatch(Long[] userIds);

	/**
	 * 修改密码
	 * @param userId       用户ID
	 * @param password     原密码
	 * @param newPassword  新密码
	 */
	boolean updatePassword(Long userId, String password, String newPassword);

	void createDefaultPassword(SysUserEntity user);


	/**
	 * 管理员重置密码
	 * @param userId
	 * @return
	 */
	boolean resetresetPass(Long userId);

	boolean userIsValid(Long userId);

	List<Long> userRoleListByUserid(Long userId);


    void batchAddUsers(BatchAddUser batchAddUser);


	/**
	 * 通过角色Id分页查询用户
	 */

	PageUtils queryPageByrUserId(Map<String, Object> params,List list);

	/**
	 * 通过用户ID查询菜单ID
	 * @param userId
	 * @return
	 */

	List<Long> findMenuIdByUserId(long userId);

	/**
	 * 通过父级Id查询菜单
	 */

	List<SysMenuEntity> findMenuByParentId(long menuId,List<Long> menuIds);
	List<SysMenuEntity> findMenuByParentId1(Long menuId,Integer toolBar);
	/**
	 * 过滤掉当前角色下面的用户
	 */
	PageUtils notExistCurrRole(Map<String, Object> params,List list);

	List<SysMenuEntity> queryLevelOneMenu(Integer userId, Integer toolBar,Integer parentId);
	List<SysMenuEntity> queryLevelTwoMenu(Integer userId, Integer toolBar,Integer parentId);


	List<SysUserEntity> userList();

	List<SysUserEntity> findByDeptId(long deptId);



	void upload(SysUserEntity user);
}
