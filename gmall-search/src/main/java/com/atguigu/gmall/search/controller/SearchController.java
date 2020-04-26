package com.atguigu.gmall.search.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.search.entity.ResponseParamVo;
import com.atguigu.gmall.search.entity.SearchParam;
import com.atguigu.gmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("search")
public class SearchController {
    @Autowired
    SearchService searchService;
    @GetMapping
    public ResponseVo<ResponseParamVo> search(SearchParam searchParam){
        try {
            ResponseParamVo responseParamVo = searchService.search(searchParam);
            return ResponseVo.ok(responseParamVo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseVo.fail();
    }
}
