package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.GroupAttrVo;
import com.atguigu.gmall.pms.entity.vo.GroupAndAttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;

import java.util.List;

/**
 * 属性分组
 *
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 18:06:45
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<GroupAndAttrVo> getAttrsByCatId(Long catId);

    List<GroupAttrVo> getGroupAttr(Long cId, Long skuId, Long spuId);
}

