package org.linlinjava.litemall.db.dao;

import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallQuoteBill;

import java.time.LocalDateTime;

public interface QuoteMapper {
    int updateWithOptimisticLocker(@Param("lastUpdateTime") LocalDateTime lastUpdateTime, @Param("quoteBill") LitemallQuoteBill quoteBill);

}
