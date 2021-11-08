package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallQuoteDieCastingMapper;
import org.linlinjava.litemall.db.domain.LitemallQuoteDieCasting;
import org.linlinjava.litemall.db.domain.LitemallQuoteDieCastingExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallQuoteDieCastingService {
    @Resource
    private LitemallQuoteDieCastingMapper quoteDieCastingMapper;

    public List<LitemallQuoteDieCasting> queryByGid(Integer quoteId,Boolean reId ) {
        LitemallQuoteDieCastingExample example = new LitemallQuoteDieCastingExample();
        example.or().andQuoteIdEqualTo(quoteId).andBillIdEqualTo(reId).andDeletedEqualTo(false);
        return quoteDieCastingMapper.selectByExampleWithBLOBs(example);
    }

    public void add(LitemallQuoteDieCasting goodsAttribute) {
        goodsAttribute.setAddTime(LocalDateTime.now());
        goodsAttribute.setUpdateTime(LocalDateTime.now());
        quoteDieCastingMapper.insertSelective(goodsAttribute);
    }

    public LitemallQuoteDieCasting findById(Integer id) {
        return quoteDieCastingMapper.selectByPrimaryKey(id);
    }

    public void deleteByGid(Integer gid) {
        LitemallQuoteDieCastingExample example = new LitemallQuoteDieCastingExample();
        example.or().andQuoteIdEqualTo(gid);
        quoteDieCastingMapper.logicalDeleteByExample(example);
    }

    public void deleteById(Integer id) {
        quoteDieCastingMapper.logicalDeleteByPrimaryKey(id);
    }

    public void updateById(LitemallQuoteDieCasting quoteDieCasting) {
        quoteDieCasting.setUpdateTime(LocalDateTime.now());
        quoteDieCastingMapper.updateByPrimaryKeySelective(quoteDieCasting);
    }
}
