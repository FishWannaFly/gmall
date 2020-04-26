package com.atguigu.gmall.pms.controller;

import java.util.List;

import com.atguigu.gmall.pms.entity.CategoryVo;
import com.atguigu.gmall.pms.entity.ThreeCategoryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 商品三级分类
 *
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 18:06:45
 */
@Api(tags = "商品三级分类 管理")
@RestController
@RequestMapping("pms/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getThreeCate/{cId}")
    public ResponseVo<List<ThreeCategoryVo>> getThreeCate(@PathVariable Long cId){
        List<ThreeCategoryVo> list = categoryService.getThreeCate(cId);
        return ResponseVo.ok(list);
    }


    @GetMapping("/parent/{parentId}")
    public ResponseVo<List<CategoryEntity>> getParent(@PathVariable Long parentId){
        List<CategoryEntity> list = categoryService.getParent(parentId);
        return ResponseVo.ok(list);
    }

    @GetMapping("/children/{parentId}")
    public ResponseVo<List<CategoryVo>> getChildren(@PathVariable Long parentId){
        List<CategoryVo> list = categoryService.getChildren(parentId);
        return ResponseVo.ok(list);
    }


    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> list(PageParamVo paramVo){
        PageResultVo pageResultVo = categoryService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id){
		CategoryEntity category = categoryService.getById(id);

        return ResponseVo.ok(category);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		categoryService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
