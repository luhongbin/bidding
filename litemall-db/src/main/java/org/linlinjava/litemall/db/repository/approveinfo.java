package org.linlinjava.litemall.db.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import java.util.List;

//@Repository
//public interface approveinfo extends CustomizedRepository<litemall_approve_info, Integer> {
//    @Query("select a from litemall_approve_info a where a.dcRecord.id = :id")
//    List<AcItem> findAllByDcRecordID(@Param("id") int id);
//
//    /**
//     * 更新时先将旧数据删除
//     * @param dcRecord
//     * @return void
//     * @Date 4:22 PM 2/1/2020
//     **/
//    void deleteByDcRecord(DcRecord dcRecord);
//
//}
