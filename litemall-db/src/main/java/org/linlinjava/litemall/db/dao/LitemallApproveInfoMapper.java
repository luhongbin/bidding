package org.linlinjava.litemall.db.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallApproveInfo;
import org.linlinjava.litemall.db.domain.LitemallApproveInfoExample;

public interface LitemallApproveInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    long countByExample(LitemallApproveInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    int deleteByExample(LitemallApproveInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    int insert(LitemallApproveInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    int insertSelective(LitemallApproveInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    LitemallApproveInfo selectOneByExample(LitemallApproveInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    LitemallApproveInfo selectOneByExampleSelective(@Param("example") LitemallApproveInfoExample example, @Param("selective") LitemallApproveInfo.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    List<LitemallApproveInfo> selectByExampleSelective(@Param("example") LitemallApproveInfoExample example, @Param("selective") LitemallApproveInfo.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    List<LitemallApproveInfo> selectByExample(LitemallApproveInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    LitemallApproveInfo selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") LitemallApproveInfo.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    LitemallApproveInfo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    LitemallApproveInfo selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") LitemallApproveInfo record, @Param("example") LitemallApproveInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") LitemallApproveInfo record, @Param("example") LitemallApproveInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(LitemallApproveInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(LitemallApproveInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    int logicalDeleteByExample(@Param("example") LitemallApproveInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_approve_info
     *
     * @mbg.generated
     */
    int logicalDeleteByPrimaryKey(Integer id);
}