package org.linlinjava.litemall.db.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.linlinjava.litemall.db.domain.LitemallRequote;

import java.util.List;
@Mapper
public class RequoteMapper {
//    @Select("select b.dept, b.nickname, c.dept as receDept, c.nickname as receName,action,note,next_action as nextAction,add_time as addTime,receiver, admin_id as adminId,bill_name as billName " +
//            "from litemall_requote a left join litemall_admin b on b.id = a.admin_id left join litemall_admin c on c.id=a.receiver left join litemall_quote_bill d on d.id = a.quoteId" +
//            " where bill_code = #{bid} and source_id = #{sid} and ( admin_id = #{adminId} or  receiver = #{adminId})")
//    public List<LitemallRequote> queryRequote(Integer bid, Integer sid, Integer adminId);
}