package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.sms.api.entity.CouponSpuCategoryEntity;

/**
 * 优惠券分类关联
 *
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 18:15:52
 */
public interface CouponSpuCategoryService extends IService<CouponSpuCategoryEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

