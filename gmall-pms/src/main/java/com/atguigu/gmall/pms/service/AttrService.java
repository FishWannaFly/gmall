package com.atguigu.gmall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.AttrEntity;

import java.util.List;

/**
 * 商品属性
 *
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 18:06:45
 */
public interface AttrService extends IService<AttrEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<AttrEntity> getAttrEntityListByCid(Long cid, Integer type, Integer searchType);
}

