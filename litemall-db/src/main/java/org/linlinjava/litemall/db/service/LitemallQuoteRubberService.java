package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallQuoteRubberMapper;
import org.linlinjava.litemall.db.domain.LitemallQuoteRubber;
import org.linlinjava.litemall.db.domain.LitemallQuoteRubberExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallQuoteRubberService {
    @Resource
    private LitemallQuoteRubberMapper quoteRubberMapper;

    public List<LitemallQuoteRubber> queryByGid(Integer goodsId,Boolean reId ) {
        LitemallQuoteRubberExample example = new LitemallQuoteRubberExample();
        example.or().andQuoteIdEqualTo(goodsId).andBillIdEqualTo(reId).andDeletedEqualTo(false);
        return quoteRubberMapper.selectByExampleWithBLOBs(example);
    }

    public void add(LitemallQuoteRubber goodsAttribute) {
        goodsAttribute.setAddTime(LocalDateTime.now());
        goodsAttribute.setUpdateTime(LocalDateTime.now());
        quoteRubberMapper.insertSelective(goodsAttribute);
    }

    public LitemallQuoteRubber findById(Integer id) {
        return quoteRubberMapper.selectByPrimaryKey(id);
    }

    public void deleteByGid(Integer gid) {
        LitemallQuoteRubberExample example = new LitemallQuoteRubberExample();
        example.or().andQuoteIdEqualTo(gid);
        quoteRubberMapper.logicalDeleteByExample(example);
    }

    public void deleteById(Integer id) {
        quoteRubberMapper.logicalDeleteByPrimaryKey(id);
    }

    public void updateById(LitemallQuoteRubber attribute) {
        attribute.setUpdateTime(LocalDateTime.now());
        quoteRubberMapper.updateByPrimaryKeySelective(attribute);
    }
}
