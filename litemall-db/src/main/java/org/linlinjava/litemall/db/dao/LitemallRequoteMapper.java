package org.linlinjava.litemall.db.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallRequote;
import org.linlinjava.litemall.db.domain.LitemallRequoteExample;

public interface LitemallRequoteMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    long countByExample(LitemallRequoteExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    int deleteByExample(LitemallRequoteExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    int insert(LitemallRequote record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    int insertSelective(LitemallRequote record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    LitemallRequote selectOneByExample(LitemallRequoteExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    LitemallRequote selectOneByExampleSelective(@Param("example") LitemallRequoteExample example, @Param("selective") LitemallRequote.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    List<LitemallRequote> selectByExampleSelective(@Param("example") LitemallRequoteExample example, @Param("selective") LitemallRequote.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    List<LitemallRequote> selectByExample(LitemallRequoteExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    LitemallRequote selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") LitemallRequote.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    LitemallRequote selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    LitemallRequote selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") LitemallRequote record, @Param("example") LitemallRequoteExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") LitemallRequote record, @Param("example") LitemallRequoteExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(LitemallRequote record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(LitemallRequote record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    int logicalDeleteByExample(@Param("example") LitemallRequoteExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_requote
     *
     * @mbg.generated
     */
    int logicalDeleteByPrimaryKey(Integer id);
}