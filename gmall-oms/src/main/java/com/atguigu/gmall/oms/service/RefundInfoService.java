package com.atguigu.gmall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.oms.entity.RefundInfoEntity;

import java.util.Map;

/**
 * 退款信息
 *
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 17:35:45
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

