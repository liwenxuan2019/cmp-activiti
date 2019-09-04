

package io.cmp.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.cmp.common.annotation.DataFilter;
import io.cmp.common.exception.RRException;
import io.cmp.common.utils.R;
import io.cmp.modules.sys.dao.SysDeptDao;
import io.cmp.modules.sys.entity.SysAreaEntity;
import io.cmp.modules.sys.entity.SysDeptEntity;
import io.cmp.modules.sys.entity.SysDeptLeaderEntity;
import io.cmp.modules.sys.entity.SysUserEntity;
import io.cmp.modules.sys.service.SysDeptLeaderService;
import io.cmp.modules.sys.service.SysDeptService;
import io.cmp.modules.sys.service.SysUserService;
import io.cmp.modules.sys.vo.NewEmployee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("sysDeptService")
public class SysDeptServiceImpl extends ServiceImpl<SysDeptDao, SysDeptEntity> implements SysDeptService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysDeptLeaderService sysDeptLeaderService;

    @Override
    @DataFilter(subDept = true, user = false, tableAlias = "t1")
    public List<SysDeptEntity> queryList(Map<String, Object> params) {
        return baseMapper.queryList(params);
    }

    @Override
    public List<Long> queryDetpIdList(Long parentId) {
        return baseMapper.queryDetpIdList(parentId);
    }

    @Override
    public List<Long> getSubDeptIdList(Long deptId) {
        //部门及子部门ID列表
        List<Long> deptIdList = new ArrayList<>();

        //获取子部门ID
        List<Long> subIdList = queryDetpIdList(deptId);
        getDeptTreeList(subIdList, deptIdList);

        return deptIdList;
    }

    /**
     * 递归
     */
    private void getDeptTreeList(List<Long> subIdList, List<Long> deptIdList) {
        for (Long deptId : subIdList) {
            List<Long> list = queryDetpIdList(deptId);
            if (list.size() > 0) {
                getDeptTreeList(list, deptIdList);
            }

            deptIdList.add(deptId);
        }
    }

    /**
     * 根据父部门，查询子部门
     *
     * @param parentId 父部门D
     */
    @Override
    public List<SysDeptEntity> queryListParentId(Long parentId) {
        return baseMapper.queryListParentId(parentId);
    }

    @Override
    public void newEmployee(NewEmployee newEmployee) {
        for (Integer i : newEmployee.getUserId()) {
            SysUserEntity s = sysUserService.getById(i);
            if (s != null) {
                s.setDeptId(newEmployee.getDeptId());

            }
            sysUserService.update(s);
        }
    }

    @Override
    public SysDeptEntity queryBydeptCode(String deptCode) {
        return baseMapper.queryBydeptCode(deptCode);
    }

    @Override
    public R toSaveDeptHeaders(SysDeptEntity dept) {
//        通过机构代码查询是否存在该机构代码
        if (dept.getDeptCode() != null && !(dept.getDeptCode().trim().equals(""))) {
            if (sysDeptService.queryBydeptCode(dept.getDeptCode()) != null) {
                throw new RRException("机构代码重复");
            } else {

//                初始化集合用来储存不存在的负责人
                List<Long> deptIds = new ArrayList<>();
                if (dept.getManyDepartHeads() != null) {
//                    先判断该负责人是否属于该机构
                    for (Long userId : dept.getManyDepartHeads()) {

                        if(userId!=0){
                            deptIds.add(userId);
                        }


                    }
//                    继续判断deptIds是否为空，如果为空就修改

                    if (deptIds != null && deptIds.size() != 0) {
                        return R.ok().put("notExist", deptIds);
                    } else {
//                先保存部门
                        sysDeptService.save(dept);
                        //				保存负责人


                        for (Long userId : dept.getManyDepartHeads()) {
                            if(userId!=0){
                            SysDeptLeaderEntity sysDeptLeaderEntity = new SysDeptLeaderEntity();
                            sysDeptLeaderEntity.setDeptId(dept.getDeptId());
                            sysDeptLeaderEntity.setUserId(userId);
                            sysDeptLeaderService.save(sysDeptLeaderEntity);

                        }
                    }
                    }

                }
            }

        } else {

            throw new RRException("机构代码不能为空");
        }
        return R.ok();
    }


    @Override
    public R toupdateHader(SysDeptEntity dept) {

        if (dept.getDeptCode() != null && !(dept.getDeptCode().trim().equals(""))) {

            if (dept.getManyDepartHeads() != null) {
                //               初始化集合用来储存不存在的负责人
                List<Long> deptIds = new ArrayList<>();
//                先判断负责人是否在本机构下
                for (Long userId : dept.getManyDepartHeads()) {
                    SysUserEntity sys = sysUserService.getById(userId);
                    if (sys != null) {
                        if (sys.getDeptId() != null) {
                            if (dept.getDeptId() != sys.getDeptId()) {
//                                 添加不在该部门的负责人到集合中返回前端判断
                                if(userId!=0){
                                    deptIds.add(userId);
                                }

                            }
                        }
                    }

                }
//                判断是否为空
                if (deptIds != null && deptIds.size() != 0) {

                    return R.ok().put("notExist", deptIds);
                } else {

                    List<Long> userIds = new ArrayList();
//                先删除中间表中的关系
                    sysDeptLeaderService.deleteBatchs(new Long[]{dept.getDeptId()});
//                先修改
                    sysDeptService.updateById(dept);


                    for (Long userId : dept.getManyDepartHeads()) {
                        //                 重新插入数据
                        if(userId!=0) {
                            SysDeptLeaderEntity sysDeptLeaderEntity = new SysDeptLeaderEntity();
                            sysDeptLeaderEntity.setDeptId(dept.getDeptId());
                            sysDeptLeaderEntity.setUserId(userId);
                            sysDeptLeaderService.save(sysDeptLeaderEntity);
                        }
                    }

                }


            } else {
//                入参为空的时候删除关系
                sysDeptLeaderService.deleteBatchs(new Long[]{dept.getDeptId()});
                //				保存部门
                sysDeptService.updateById(dept);
            }


        } else {
            throw new RRException("机构代码不能为空");
        }
        return R.ok();
    }

    /**
     * 确认后直接添加
     *
     * @param dept
     * @return
     */

    @Override
    public R saveDeptHeaders(SysDeptEntity dept) {

 /*        //        用来储存用户负责人
        StringBuilder sb = new StringBuilder();*/

//        通过机构代码查询是否存在该机构代码
        if (dept.getDeptCode() != null && !(dept.getDeptCode().trim().equals(""))) {
            if (sysDeptService.queryBydeptCode(dept.getDeptCode()) != null) {
                throw new RRException("机构代码重复");
            } else {

                if(dept.getManyDepartHeads()!=null){
                for (int i=0;i<dept.getManyDepartHeads().size();i++){
                    if(dept.getManyDepartHeads().contains(0)){
                        dept.getManyDepartHeads().remove(i);
                    }
                }
                }

//				先保存部门
                sysDeptService.save(dept);
                if (dept.getManyDepartHeads() != null) {
//                     确定保存负责人之前先把负责人以前负责的机构删除
                    for (Long userId : dept.getManyDepartHeads()) {

                  //      sysDeptLeaderService.deleteByUserId(userId);
//                        把部门表中的数据修改了
                        SysUserEntity sys = sysUserService.getById(userId);
                        if(sys!=null) {
                            sys.setDeptId(dept.getDeptId());
                            sysUserService.update(sys);
                        }

                    }

                    //				保存负责人
                    for (Long userId : dept.getManyDepartHeads()) {
                        if(userId!=0) {
                            SysDeptLeaderEntity sysDeptLeaderEntity = new SysDeptLeaderEntity();
                            sysDeptLeaderEntity.setDeptId(dept.getDeptId());
                            sysDeptLeaderEntity.setUserId(userId);
                            sysDeptLeaderService.save(sysDeptLeaderEntity);
                        }
                    }

                }
            }

        } else {

            throw new RRException("机构代码不能为空");
        }
        return R.ok();
    }

    /**
     * 确认后直接修改
     *
     * @param dept
     * @return
     */


    @Override
    public R updateHader(SysDeptEntity dept) {


        if (dept.getDeptCode() != null && !(dept.getDeptCode().trim().equals(""))) {

            if (dept.getManyDepartHeads() != null) {
                //               初始化集合用来储存不存在的负责人
                List<Long> deptIds = new ArrayList<>();
//                先把不在该部门下的人的部门更新成当前部门
                for (Long userId : dept.getManyDepartHeads()) {
                    SysUserEntity sys = sysUserService.getById(userId);
                    if (sys != null) {
                        if (sys.getDeptId() != null) {
                            if (sys.getDeptId() != dept.getDeptId()) {
                                if(userId!=0){
                                    deptIds.add(userId);
                                    sys.setDeptId(dept.getDeptId());
                                    sysUserService.update(sys);
                                }


                            }

                        }
                    }

                }
//                在去删除sysDeptLeader中

                if (deptIds != null && deptIds.size() != 0) {
                    for (Long userId : deptIds) {
                        sysDeptLeaderService.deleteByUserId(userId);
                    }
                }


                //List<Long> userIds = new ArrayList();
//                先删除中间表中的关系
                sysDeptLeaderService.deleteBatchs(new Long[]{dept.getDeptId()});
//                先修改
                sysDeptService.updateById(dept);
                for (Long userId : dept.getManyDepartHeads()) {
                    logger.debug("userId="+userId);
//                 重新插入数据
                    if(userId!=0) {
                        SysDeptLeaderEntity sysDeptLeaderEntity = new SysDeptLeaderEntity();
                        sysDeptLeaderEntity.setDeptId(dept.getDeptId());
                        sysDeptLeaderEntity.setUserId(userId);
                        sysDeptLeaderService.save(sysDeptLeaderEntity);
                    }
                }

            } else {
//                入参为空的时候删除关系
                sysDeptLeaderService.deleteBatchs(new Long[]{dept.getDeptId()});
                //				保存部门
                sysDeptService.updateById(dept);
            }


        } else {
            throw new RRException("机构代码不能为空");
        }
        return R.ok();
    }

    @Override
    public R saveDeptLeader(SysDeptEntity dept) {

//        通过机构代码查询是否存在该机构代码
        if (dept.getDeptCode() != null && !(dept.getDeptCode().trim().equals(""))) {
            if (sysDeptService.queryBydeptCode(dept.getDeptCode()) != null) {
                throw new RRException("机构代码重复");
            } else {
//				先保存部门
                sysDeptService.save(dept);
                if(dept.getManyDepartHeads()!=null){
//				保存负责人
                for (Long userId : dept.getManyDepartHeads()) {
                    if(userId!=0) {
                        SysDeptLeaderEntity sysDeptLeaderEntity = new SysDeptLeaderEntity();
                        sysDeptLeaderEntity.setDeptId(dept.getDeptId());
                        sysDeptLeaderEntity.setUserId(userId);
                        sysDeptLeaderService.save(sysDeptLeaderEntity);
                    }
                }
                }
            }

        } else {

            throw new RRException("机构代码不能为空");
        }
        return R.ok();
    }

    @Override
    public R updateLeader(SysDeptEntity dept) {
        if (dept.getDeptCode() != null && !(dept.getDeptCode().trim().equals(""))) {

            if (dept.getManyDepartHeads() != null&&dept.getManyDepartHeads().size()!=0 ) {
                //               初始化集合用来储存不存在的负责人
                List<Long> deptIds = new ArrayList<>();

                //List<Long> userIds = new ArrayList();
//                先删除中间表中的关系
                sysDeptLeaderService.deleteBatchs(new Long[]{dept.getDeptId()});
//                先修改


                    sysDeptService.updateById(dept);


                for (Long userId : dept.getManyDepartHeads()) {
                    logger.debug("userId="+userId);
//                 重新插入数据
                    if(userId!=0) {
                        SysDeptLeaderEntity sysDeptLeaderEntity = new SysDeptLeaderEntity();
                        sysDeptLeaderEntity.setDeptId(dept.getDeptId());
                        sysDeptLeaderEntity.setUserId(userId);
                        sysDeptLeaderService.save(sysDeptLeaderEntity);
//                        再去更新User中的数据
                        SysUserEntity sysUser=   sysUserService.getById(userId);
                        sysUser.setIsDeptLeader(1);
                        sysUserService.updateById(sysUser);
                    }
                }

            } else {
logger.debug("dept.getManyDepartHeads()="+dept.getManyDepartHeads());

                List<SysDeptLeaderEntity> list = sysDeptLeaderService.findUserByDeptId(dept.getDeptId());
                logger.debug(""+list);
                if (list!=null) {

                    for (SysDeptLeaderEntity s:list){
                        //                        再去更新User中的数据
                        if(s.getUserId()!=null) {
                            logger.debug("s.getUserId()="+s.getUserId());
                            SysUserEntity sysUser = sysUserService.getById(s.getUserId());
                            sysUser.setIsDeptLeader(0);
                            sysUserService.updateById(sysUser);
                        }
                    }
                }
//                入参为空的时候删除关系
               // sysDeptLeaderService.deleteBatchs(new Long[]{dept.getDeptId()});
                //				保存部门

                    sysDeptService.updateById(dept);



            }

        } else {
            throw new RRException("机构代码不能为空");
        }
        return R.ok();
    }
}
