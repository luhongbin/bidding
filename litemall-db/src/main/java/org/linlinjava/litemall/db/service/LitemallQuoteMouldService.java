package org.linlinjava.litemall.db.service;

//import org.linlinjava.litemall.db.dao.LitemallQuoteMouldMapper;
//import org.linlinjava.litemall.db.domain.LitemallQuoteMould;
//import org.linlinjava.litemall.db.domain.LitemallQuoteMouldExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallQuoteMouldService {
//    @Resource
//    private LitemallQuoteMouldMapper quoteMouldMapper;
//
//    public List<LitemallQuoteMould> queryByGid(Integer goodsId) {
//        LitemallQuoteMouldExample example = new LitemallQuoteMouldExample();
//        example.or().andQuoteIdEqualTo(goodsId).andDeletedEqualTo(false);
//        return quoteMouldMapper.selectByExample(example);
//    }
//
//    public void add(LitemallQuoteMould goodsAttribute) {
//        goodsAttribute.setAddTime(LocalDateTime.now());
//        goodsAttribute.setUpdateTime(LocalDateTime.now());
//        quoteMouldMapper.insertSelective(goodsAttribute);
//    }
//
//    public LitemallQuoteMould findById(Integer id) {
//        return quoteMouldMapper.selectByPrimaryKey(id);
//    }
//
//    public void deleteByGid(Integer gid) {
//        LitemallQuoteMouldExample example = new LitemallQuoteMouldExample();
//        example.or().andQuoteIdEqualTo(gid);
//        quoteMouldMapper.logicalDeleteByExample(example);
//    }
//
//    public void deleteById(Integer id) {
//        quoteMouldMapper.logicalDeleteByPrimaryKey(id);
//    }
//
//    public void updateById(LitemallQuoteMould attribute) {
//        attribute.setUpdateTime(LocalDateTime.now());
//        quoteMouldMapper.updateByPrimaryKeySelective(attribute);
//    }
}
