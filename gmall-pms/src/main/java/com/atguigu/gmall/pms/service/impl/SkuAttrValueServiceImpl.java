package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrVo;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Service("skuAttrValueService")
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValueEntity> implements SkuAttrValueService {
    @Autowired
    SkuMapper skuMapper;
    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuAttrValueEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuAttrValueEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<SkuSaleAttrVo> getSkuSaleAttrVoList(Long spuId) {
        List<SkuEntity> skuEntities = skuMapper.selectList(new QueryWrapper<SkuEntity>().eq("spu_id", spuId));
        List<Long> ids = skuEntities.stream().map(SkuEntity::getId).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(ids)){
            List<SkuAttrValueEntity> skuAttrValueEntities = this.list(new QueryWrapper<SkuAttrValueEntity>().in("sku_id", ids));
            if(!CollectionUtils.isEmpty(skuAttrValueEntities)){
                List<SkuSaleAttrVo> skuSaleAttrVos = skuAttrValueEntities.stream().map(skuAttrValueEntity -> {
                    SkuSaleAttrVo skuSaleAttrVo = new SkuSaleAttrVo();
                    BeanUtils.copyProperties(skuAttrValueEntity, skuSaleAttrVo);
                    return skuSaleAttrVo;
                }).collect(Collectors.toList());
                return skuSaleAttrVos;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        ExecutorService executorService = new ThreadPoolExecutor(2,
                3,30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
//        executorService.submit(()->{
//            System.out.println("1111");
//            return 1;
//        });
        for (int i = 0; i < 10; i++) {
            executorService.execute(()-> {
                System.out.println(Thread.currentThread().getName());
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        System.out.println(222);
    }


}