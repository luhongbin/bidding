package org.linlinjava.litemall.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.vo.CatVo;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallKnowledge;
import org.linlinjava.litemall.db.domain.LitemallXmcatalog;
import org.linlinjava.litemall.db.service.LitemallKnowledgeService;
import org.linlinjava.litemall.db.service.LitemallXmCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.linlinjava.litemall.admin.util.AdminResponseCode.GOODS_NAME_EXIST;

@Service
public class AdminKnowledgeService {
    private final Log logger = LogFactory.getLog(AdminKnowledgeService.class);

    @Autowired
    private LitemallKnowledgeService KnowledgeService;
    @Autowired
    private LitemallXmCategoryService xmCategoryService;

    public Object list(Integer Id, String title, Integer readCount, Integer status,Integer isPopular,
                       Integer page, Integer limit, String sort, String order) {
        List<LitemallKnowledge> goodsList = KnowledgeService.querySelective(Id, title,readCount, status, isPopular,page, limit, sort, order);
        return ResponseUtil.okList(goodsList);
    }

    private Object validate(LitemallKnowledge litemallKnowledge) {
        String name = litemallKnowledge.getTitle();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }

        // 分类可以不设置，如果设置则需要验证分类存在
        Integer categoryId = litemallKnowledge.getKnowledgeCatalogId();
        if (categoryId != null && categoryId != 0) {
            if (xmCategoryService.findById(categoryId) == null) {
                return ResponseUtil.badArgumentValue();
            }
        }

        return null;
    }

    /**
     * 编辑商品
     *
     * NOTE：
     * 由于商品涉及到四个表，特别是litemall_goods_product表依赖litemall_goods_specification表，
     * 这导致允许所有字段都是可编辑会带来一些问题，因此这里商品编辑功能是受限制：
     * （1）litemall_goods表可以编辑字段；
     * （2）litemall_goods_specification表只能编辑pic_url字段，其他操作不支持；
     * （3）litemall_goods_product表只能编辑price, number和url字段，其他操作不支持；
     * （4）litemall_goods_attribute表支持编辑、添加和删除操作。
     *
     * NOTE2:
     * 前后端这里使用了一个小技巧：
     * 如果前端传来的update_time字段是空，则说明前端已经更新了某个记录，则这个记录会更新；
     * 否则说明这个记录没有编辑过，无需更新该记录。
     *
     * NOTE3:
     * （1）购物车缓存了一些商品信息，因此需要及时更新。
     * 目前这些字段是goods_sn, goods_name, price, pic_url。
     * （2）但是订单里面的商品信息则是不会更新。
     * 如果订单是未支付订单，此时仍然以旧的价格支付。
     */
    @Transactional
    public Object update(LitemallKnowledge litemallKnowledge) {
        Object error = validate(litemallKnowledge);
        if (error != null) {
            return error;
        }
        // 商品基本信息表litemall_goods
        if (KnowledgeService.updateById(litemallKnowledge) == 0) {
            throw new RuntimeException("更新数据失败");
        }

        Integer gid = litemallKnowledge.getId();

        return ResponseUtil.ok();
    }

    @Transactional
    public Object delete(LitemallKnowledge litemallKnowledge) {
        Integer id = litemallKnowledge.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }

        Integer gid = litemallKnowledge.getId();
        KnowledgeService.deleteById(gid);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(LitemallKnowledge litemallKnowledge) {
        Object error = validate(litemallKnowledge);
        if (error != null) {
            return error;
        }
        String name = litemallKnowledge.getTitle();
        if (KnowledgeService.checkExistByTitle(name)) {
            return ResponseUtil.fail(GOODS_NAME_EXIST, "商品名已经存在");
        }

        // 商品基本信息表litemall_goods
        KnowledgeService.add(litemallKnowledge);
        return ResponseUtil.ok();
    }

    public Object list2() {
        // http://element-cn.eleme.io/#/zh-CN/component/cascader
        // 管理员设置“所属分类”
        List<LitemallXmcatalog> l1CatList = xmCategoryService.queryL1();
        List<CatVo> categoryList = new ArrayList<>(l1CatList.size());

        for (LitemallXmcatalog l1 : l1CatList) {
            CatVo l1CatVo = new CatVo();
            l1CatVo.setValue(l1.getId());
            l1CatVo.setLabel(l1.getName());

            List<LitemallXmcatalog> l2CatList = xmCategoryService.queryByPid(l1.getId());
            List<CatVo> children = new ArrayList<>(l2CatList.size());
            for (LitemallXmcatalog l2 : l2CatList) {
                CatVo l2CatVo = new CatVo();
                l2CatVo.setValue(l2.getId());
                l2CatVo.setLabel(l2.getName());
                children.add(l2CatVo);
            }
            l1CatVo.setChildren(children);

            categoryList.add(l1CatVo);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("categoryList", categoryList);
         return ResponseUtil.ok(data);
    }

    public Object detail(Integer id) {
        LitemallKnowledge knowledge = KnowledgeService.findById(id);

        Integer categoryId = knowledge.getKnowledgeCatalogId();
        LitemallXmcatalog category = xmCategoryService.findById(categoryId);
        Integer[] categoryIds = new Integer[]{};
        if (category != null) {
            Integer parentCategoryId = category.getPid();
            categoryIds = new Integer[]{parentCategoryId, categoryId};
        }

        Map<String, Object> data = new HashMap<>();
        data.put("knowledge", knowledge);
        data.put("categoryIds", categoryIds);

        return ResponseUtil.ok(data);
    }

}
