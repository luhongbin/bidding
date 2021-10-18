package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallPackingSelectMapper;
import org.linlinjava.litemall.db.domain.LitemallPackingSelect;
import org.linlinjava.litemall.db.domain.LitemallPackingSelectExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallSelectService {
    @Resource
    private LitemallPackingSelectMapper selectMapper;

    LitemallPackingSelect.Column[] columns = new LitemallPackingSelect.Column[]{LitemallPackingSelect.Column.id, LitemallPackingSelect.Column.fileId, LitemallPackingSelect.Column.fileTactics, LitemallPackingSelect.Column.detail};

    public LitemallPackingSelect queryExist(Integer fileId, Integer fileTactics, String detail) {
        LitemallPackingSelectExample example = new LitemallPackingSelectExample();
        example.or().andFileIdEqualTo(fileId).andFileTacticsEqualTo(fileTactics).andDeletedEqualTo(false);
        return selectMapper.selectOneByExample(example);
    }

    public void add(LitemallPackingSelect select) {
        select.setAddTime(LocalDateTime.now());
        select.setUpdateTime(LocalDateTime.now());
        selectMapper.insertSelective(select);
    }

    public int updateById(LitemallPackingSelect select) {
        select.setUpdateTime(LocalDateTime.now());
        return selectMapper.updateByPrimaryKeySelective(select);
    }

//    public List<LitemallPackingSelect> queryByUid(int userId) {
//        LitemallPackingSelectExample example = new LitemallPackingSelectExample();
//        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
//        return selectMapper.selectByExample(example);
//    }
//
//
//    public List<LitemallPackingSelect> queryByUidAndChecked(Integer userId) {
//        LitemallPackingSelectExample example = new LitemallPackingSelectExample();
//        example.or().andUserIdEqualTo(userId).andCheckedEqualTo(true).andDeletedEqualTo(false);
//        return selectMapper.selectByExample(example);
//    }
//
//    public int delete(List<Integer> productIdList, int userId) {
//        LitemallPackingSelectExample example = new LitemallPackingSelectExample();
//        example.or().andUserIdEqualTo(userId).andProductIdIn(productIdList);
//        return selectMapper.logicalDeleteByExample(example);
//    }
//
    public LitemallPackingSelect findById(Integer id) {
        return selectMapper.selectByPrimaryKey(id);
    }
//
//    public LitemallPackingSelect findById(Integer userId, Integer id) {
//        LitemallPackingSelectExample example = new LitemallPackingSelectExample();
//        example.or().andUserIdEqualTo(userId).andIdEqualTo(id).andDeletedEqualTo(false);
//        return selectMapper.selectOneByExample(example);
//    }
//
//    public int updateCheck(Integer userId, List<Integer> idsList, Boolean checked) {
//        LitemallPackingSelectExample example = new LitemallPackingSelectExample();
//        example.or().andUserIdEqualTo(userId).andProductIdIn(idsList).andDeletedEqualTo(false);
//        LitemallPackingSelect select = new LitemallPackingSelect();
//        select.setChecked(checked);
//        select.setUpdateTime(LocalDateTime.now());
//        return selectMapper.updateByExampleSelective(select, example);
//    }
//
//    public void clearGoods(Integer userId) {
//        LitemallPackingSelectExample example = new LitemallPackingSelectExample();
//        example.or().andUserIdEqualTo(userId).andCheckedEqualTo(true);
//        LitemallPackingSelect select = new LitemallPackingSelect();
//        select.setDeleted(true);
//        selectMapper.updateByExampleSelective(select, example);
//    }

    public List<LitemallPackingSelect> querySelective(Integer fileId, Integer fileTactics, String detail, Integer page, Integer limit, String sort, String order) {
        LitemallPackingSelectExample example = new LitemallPackingSelectExample();
        LitemallPackingSelectExample.Criteria criteria = example.createCriteria();


        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return selectMapper.selectByExample(example);
    }

    public void deleteById(Integer id) {
        selectMapper.logicalDeleteByPrimaryKey(id);
    }

//    public boolean checkExist(Integer goodsId) {
//        LitemallPackingSelectExample example = new LitemallPackingSelectExample();
//        example.or().andGoodsIdEqualTo(goodsId).andCheckedEqualTo(true).andDeletedEqualTo(false);
//        return selectMapper.countByExample(example) != 0;
//    }
//
//    public void updateProduct(Integer id, String detail, String goodsName, BigDecimal price, String url) {
//        LitemallPackingSelect select = new LitemallPackingSelect();
//        select.setPrice(price);
//        select.setPicUrl(url);
//        select.setGoodsSn(goodsSn);
//        select.setdetail(goodsName);
//        LitemallPackingSelectExample example = new LitemallPackingSelectExample();
//        example.or().andProductIdEqualTo(id);
//        selectMapper.updateByExampleSelective(select, example);
//    }
}
