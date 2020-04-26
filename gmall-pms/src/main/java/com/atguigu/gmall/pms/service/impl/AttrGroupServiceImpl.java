package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.entity.vo.GroupAndAttrVo;
import com.atguigu.gmall.pms.service.AttrService;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.AttrGroupMapper;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrService attrService;
    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<AttrGroupEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<GroupAndAttrVo> getAttrsByCatId(Long catId) {
        List<AttrGroupEntity> attrGroupEntityList = baseMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("category_id", catId));
        ArrayList<GroupAndAttrVo> andAttrVoArrayList = attrGroupEntityList.stream().map(attrGroupEntity -> {
            GroupAndAttrVo groupAndAttrVo = new GroupAndAttrVo();
            BeanUtils.copyProperties(attrGroupEntity, groupAndAttrVo);
            List<AttrEntity> attrEntities = attrService.list(new QueryWrapper<AttrEntity>().eq("group_id", groupAndAttrVo.getId()).eq("type",1));
            groupAndAttrVo.setAttrEntities(attrEntities);
            return groupAndAttrVo;
        }).collect(Collectors.toCollection(ArrayList::new));
        return andAttrVoArrayList;
    }
    @Autowired
    SkuAttrValueService skuAttrValueService;
    @Autowired
    SpuAttrValueService spuAttrValueService;

    @Override
    public List<GroupAttrVo> getGroupAttr(Long cId, Long skuId, Long spuId) {
        List<AttrGroupEntity> attrGroupEntities = baseMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("category_id", cId));
        if(!CollectionUtils.isEmpty(attrGroupEntities)){
            return attrGroupEntities.stream().map(attrGroupEntity -> {
                GroupAttrVo groupAttrVo = new GroupAttrVo();
                BeanUtils.copyProperties(attrGroupEntity,groupAttrVo);
                List<AttrEntity> attrEntities = attrService.list(new QueryWrapper<AttrEntity>().eq("group_id", attrGroupEntity.getId()));
                if(!CollectionUtils.isEmpty(attrEntities)){
                    List<Long> ids = attrEntities.stream().map(AttrEntity::getId).collect(Collectors.toList());
                    List<SkuAttrValueEntity> skuAttrValueEntities = skuAttrValueService.list(new QueryWrapper<SkuAttrValueEntity>().in("attr_id", ids).eq("sku_id", skuId));
                    if(!CollectionUtils.isEmpty(skuAttrValueEntities)){
                        List<SkuSaleAttrVo> skuSaleAttrVos = skuAttrValueEntities.stream().map(skuAttrValueEntity -> {
                            SkuSaleAttrVo skuSaleAttrVo = new SkuSaleAttrVo();
                            BeanUtils.copyProperties(skuAttrValueEntity, skuSaleAttrVo);
                            return skuSaleAttrVo;
                        }).collect(Collectors.toList());
                        groupAttrVo.setAllAttrVos(skuSaleAttrVos);
                    }
                    List<SpuAttrValueEntity> spuAttrValueEntities = spuAttrValueService.list(new QueryWrapper<SpuAttrValueEntity>().in("attr_id", ids).eq("spu_id", spuId));
                    if(!CollectionUtils.isEmpty(spuAttrValueEntities)){
                        List<SkuSaleAttrVo> skuSaleAttrVos = spuAttrValueEntities.stream().map(spuAttrValueEntity -> {
                            SkuSaleAttrVo skuSaleAttrVo = new SkuSaleAttrVo();
                            BeanUtils.copyProperties(spuAttrValueEntity, skuSaleAttrVo);
                            return skuSaleAttrVo;
                        }).collect(Collectors.toList());
                        groupAttrVo.getAllAttrVos().addAll(skuSaleAttrVos);
                    }
                }
                return groupAttrVo;
            }).collect(Collectors.toList());
        }
        return null;
    }

}