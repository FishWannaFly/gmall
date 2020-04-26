package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.sms.api.entity.CouponSpuEntity;

/**
 * 优惠券与产品关联
 *
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 18:15:52
 */
public interface CouponSpuService extends IService<CouponSpuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

