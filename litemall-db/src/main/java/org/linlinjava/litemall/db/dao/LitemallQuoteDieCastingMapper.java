package org.linlinjava.litemall.db.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallQuoteDieCasting;
import org.linlinjava.litemall.db.domain.LitemallQuoteDieCastingExample;

public interface LitemallQuoteDieCastingMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    long countByExample(LitemallQuoteDieCastingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    int deleteByExample(LitemallQuoteDieCastingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    int insert(LitemallQuoteDieCasting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    int insertSelective(LitemallQuoteDieCasting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    LitemallQuoteDieCasting selectOneByExample(LitemallQuoteDieCastingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    LitemallQuoteDieCasting selectOneByExampleSelective(@Param("example") LitemallQuoteDieCastingExample example, @Param("selective") LitemallQuoteDieCasting.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    LitemallQuoteDieCasting selectOneByExampleWithBLOBs(LitemallQuoteDieCastingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    List<LitemallQuoteDieCasting> selectByExampleSelective(@Param("example") LitemallQuoteDieCastingExample example, @Param("selective") LitemallQuoteDieCasting.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    List<LitemallQuoteDieCasting> selectByExampleWithBLOBs(LitemallQuoteDieCastingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    List<LitemallQuoteDieCasting> selectByExample(LitemallQuoteDieCastingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    LitemallQuoteDieCasting selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") LitemallQuoteDieCasting.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    LitemallQuoteDieCasting selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    LitemallQuoteDieCasting selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") LitemallQuoteDieCasting record, @Param("example") LitemallQuoteDieCastingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    int updateByExampleWithBLOBs(@Param("record") LitemallQuoteDieCasting record, @Param("example") LitemallQuoteDieCastingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") LitemallQuoteDieCasting record, @Param("example") LitemallQuoteDieCastingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(LitemallQuoteDieCasting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    int updateByPrimaryKeyWithBLOBs(LitemallQuoteDieCasting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(LitemallQuoteDieCasting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    int logicalDeleteByExample(@Param("example") LitemallQuoteDieCastingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_quote_die_casting
     *
     * @mbg.generated
     */
    int logicalDeleteByPrimaryKey(Integer id);
}