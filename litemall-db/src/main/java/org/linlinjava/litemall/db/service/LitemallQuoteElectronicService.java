package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallQuoteElectronicMapper;
import org.linlinjava.litemall.db.domain.LitemallQuoteElectronic;
import org.linlinjava.litemall.db.domain.LitemallQuoteElectronicExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallQuoteElectronicService {
    @Resource
    private LitemallQuoteElectronicMapper quoteElectronicMapper;

    public List<LitemallQuoteElectronic> queryByGid(Integer goodsId,Boolean reId) {
        LitemallQuoteElectronicExample example = new LitemallQuoteElectronicExample();
        example.or().andQuoteIdEqualTo(goodsId).andBillIdEqualTo(reId).andDeletedEqualTo(false);
        return quoteElectronicMapper.selectByExample(example);
    }

    public void add(LitemallQuoteElectronic goodsAttribute) {
        goodsAttribute.setAddTime(LocalDateTime.now());
        goodsAttribute.setUpdateTime(LocalDateTime.now());
        quoteElectronicMapper.insertSelective(goodsAttribute);
    }

    public LitemallQuoteElectronic findById(Integer id) {
        return quoteElectronicMapper.selectByPrimaryKey(id);
    }

    public void deleteByGid(Integer gid) {
        LitemallQuoteElectronicExample example = new LitemallQuoteElectronicExample();
        example.or().andQuoteIdEqualTo(gid);
        quoteElectronicMapper.logicalDeleteByExample(example);
    }

    public void deleteById(Integer id) {
        quoteElectronicMapper.logicalDeleteByPrimaryKey(id);
    }

    public void updateById(LitemallQuoteElectronic attribute) {
        attribute.setUpdateTime(LocalDateTime.now());
        quoteElectronicMapper.updateByPrimaryKeySelective(attribute);
    }
}
