package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallFileMapper;
import org.linlinjava.litemall.db.domain.LitemallFile;
import org.linlinjava.litemall.db.domain.LitemallFile.Column;
import org.linlinjava.litemall.db.domain.LitemallFileExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallFileService {
    Column[] columns = new Column[]{Column.id, Column.userId, Column.packingFilename, Column.packingFile, Column.cube, Column.weight, Column.quanlity, Column.rowcount};
    @Resource
    private LitemallFileMapper fileMapper;

    public List<LitemallFile> querySelective(Integer userId, String packingFilename,  String packingFile, Integer page, Integer limit, String sort, String order) {
        LitemallFileExample example = new LitemallFileExample();
        LitemallFileExample.Criteria criteria = example.createCriteria();

        if (userId != null) {
            criteria.andIdEqualTo(userId);
        }
        if (!StringUtils.isEmpty(packingFilename)) {
            criteria.andPackingFilenameEqualTo(packingFilename);
        }
//        if (!StringUtils.isEmpty(packingFile)) {
//            criteria.andPackingFileLike("%" + packingFile + "%");
//        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return fileMapper.selectByExample(example);
    }


    /**
     * 获取所有物品总数，包括在售的和下架的，但是不包括已删除的商品
     *
     * @return
     */
    public int count() {
        LitemallFileExample example = new LitemallFileExample();
        example.or().andDeletedEqualTo(false);
        return (int) fileMapper.countByExample(example);
    }

    public List<LitemallFile> queryByOid(Integer userId) {
        LitemallFileExample example = new LitemallFileExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return fileMapper.selectByExampleSelective(example);
    }

    public int updateById(LitemallFile file) {
        file.setUpdateTime(LocalDateTime.now());
        return fileMapper.updateByPrimaryKeySelective(file);
    }

    public void deleteById(Integer id) {
        fileMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallFile file) {
        file.setAddTime(LocalDateTime.now());
        file.setUpdateTime(LocalDateTime.now());
        fileMapper.insertSelective(file);
    }

    public LitemallFile findById(Integer id) {
        return fileMapper.selectByPrimaryKey(id);
    }
}
