package com.atguigu.gmall.search.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.entity.ResponseAttr;
import com.atguigu.gmall.search.entity.ResponseParamVo;
import com.atguigu.gmall.search.entity.SearchParam;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import springfox.documentation.spring.web.json.Json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SearchService {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    public ResponseParamVo search(SearchParam searchParam) throws Exception {


        SearchRequest searchRequest = new SearchRequest(new String[]{"goods"},getSourceBuilder(searchParam));
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        ResponseParamVo responseParamVo = getResponseParamVo(searchParam,searchResponse);

        return responseParamVo;
    }

    private ResponseParamVo getResponseParamVo(SearchParam searchParam,SearchResponse searchResponse) {
        //设置页面数据
        ResponseParamVo responseParamVo = new ResponseParamVo();
        responseParamVo.setTotal(searchResponse.getHits().getTotalHits());
        responseParamVo.setPageNum(searchParam.getPageNum());
        responseParamVo.setPageSize(searchParam.getPageSize());
        SearchHit[] hits = searchResponse.getHits().getHits();
        //设置商品信息
        List<Goods> goodsList = Stream.of(hits).map(hit -> {
            Goods goods = JSON.parseObject(hit.getSourceAsString(), Goods.class);
            HighlightField highlightField = hit.getHighlightFields().get("title");
            Text fragments = highlightField.getFragments()[0];
            goods.setTitle(fragments.string());
            return goods;
        }).collect(Collectors.toList());
        responseParamVo.setGoodsList(goodsList);
        //设置过滤条件信息
        Map<String, Aggregation> aggregationMap = searchResponse.getAggregations().getAsMap();
        //设置品牌
        ParsedLongTerms brandIdAgg = (ParsedLongTerms)aggregationMap.get("brandIdAgg");
        List<? extends Terms.Bucket> buckets = brandIdAgg.getBuckets();
        if(!CollectionUtils.isEmpty(buckets)){
            ResponseAttr responseAttr = new ResponseAttr();
            responseAttr.setAttrId(null);
            responseAttr.setAttrName("品牌");
            buckets.stream().forEach(bucket->{
                long brandId = ((Terms.Bucket) bucket).getKeyAsNumber().longValue();
                ParsedStringTerms brandNameAgg = (ParsedStringTerms)(((Terms.Bucket) bucket).getAggregations().getAsMap().get("brandNameAgg"));
                String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
                ParsedStringTerms logoAgg = (ParsedStringTerms)(((Terms.Bucket) bucket).getAggregations().getAsMap().get("logoAgg"));
                String logo = logoAgg.getBuckets().get(0).getKeyAsString();

                Map<String, Object> map = new HashMap<>();
                map.put("brandId",brandId);
                map.put("brandName",brandName);
                map.put("logo",logo);
                String s = JSON.toJSONString(map);
                responseAttr.getAttrValues().add(s);
                responseParamVo.setBrandAttr(responseAttr);
            });
        }
        //设置分类
        ParsedLongTerms cateIdAgg = (ParsedLongTerms)aggregationMap.get("cateIdAgg");
        List<? extends Terms.Bucket> cateIdAggBuckets = cateIdAgg.getBuckets();
        if(!CollectionUtils.isEmpty(cateIdAggBuckets)){
            ResponseAttr responseAttr = new ResponseAttr();
            responseAttr.setAttrName("分类");
            cateIdAggBuckets.forEach(bucket->{
                long categoryId = ((Terms.Bucket) bucket).getKeyAsNumber().longValue();
                ParsedStringTerms cateNameAgg = (ParsedStringTerms)((Terms.Bucket) bucket).getAggregations().getAsMap().get("cateNameAgg");
                String categoryName = cateNameAgg.getBuckets().get(0).getKeyAsString();
                Map<String, Object> map = new HashMap<>();
                map.put("categoryId",categoryId);
                map.put("categoryName",categoryName);
                String s = JSON.toJSONString(map);
                responseAttr.getAttrValues().add(s);
                responseParamVo.setCategoryAttr(responseAttr);
            });
        }
        //设置过滤属性
        ParsedNested attrsAgg = (ParsedNested)aggregationMap.get("attrsAgg");
        ParsedLongTerms attrIdAgg = (ParsedLongTerms)attrsAgg.getAggregations().getAsMap().get("attrIdAgg");
        List<? extends Terms.Bucket> attrIdAggBuckets = attrIdAgg.getBuckets();
        if(!CollectionUtils.isEmpty(attrIdAggBuckets)){
            List<ResponseAttr> attrArrayList = attrIdAggBuckets.stream().map(bucket -> {
                ResponseAttr responseAttr = new ResponseAttr();
                long attrId = ((Terms.Bucket) bucket).getKeyAsNumber().longValue();
                ParsedStringTerms attrNameAgg = (ParsedStringTerms) ((Terms.Bucket) bucket).getAggregations().getAsMap().get("attrNameAgg");
                List<? extends Terms.Bucket> attrNameBuckets = attrNameAgg.getBuckets();
                String attrName = attrNameBuckets.get(0).getKeyAsString();
                responseAttr.setAttrId(attrId);
                responseAttr.setAttrName(attrName);
                ParsedStringTerms attrValueAgg = (ParsedStringTerms)((Terms.Bucket) bucket).getAggregations().getAsMap().get("attrValueAgg");
                List<? extends Terms.Bucket> attrValueAggBuckets = attrValueAgg.getBuckets();
                attrValueAggBuckets.forEach(attrValueBucket->{
                    String attrValue = attrValueBucket.getKeyAsString();
                    responseAttr.getAttrValues().add(attrValue);
                });
                return responseAttr;
            }).collect(Collectors.toList());
            responseParamVo.setAttrList(attrArrayList);
        }

        return responseParamVo;
    }

    private SearchSourceBuilder getSourceBuilder(SearchParam searchParam) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //关键字匹配
        String keyword = searchParam.getKeyword();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(StringUtils.isEmpty(keyword))
            return null;
        sourceBuilder.query(boolQueryBuilder.must(QueryBuilders.matchQuery("title",keyword).operator(Operator.AND)));
        //过滤分类、品牌
        Long categoryId = searchParam.getCategoryId();
        if(categoryId != null)
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryId",categoryId));
        List<Long> brandId = searchParam.getBrandId();
        if(brandId != null)
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",brandId));
        //价格范围
        Double priceFrom = searchParam.getPriceFrom();
        Double priceTo = searchParam.getPriceTo();
        if(priceFrom != null || priceTo != null){
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
            if(priceFrom != null)
                rangeQueryBuilder.gte(priceFrom);
            if(priceTo != null)
                rangeQueryBuilder.lte(priceTo);
            boolQueryBuilder.filter(rangeQueryBuilder);
        }
        //是否有货
        Boolean store = searchParam.getStore();
        if(store != null)
            boolQueryBuilder.filter(QueryBuilders.termsQuery("store",store));

        //过滤属性
        List<String> props = searchParam.getProps();
        if(!CollectionUtils.isEmpty(props)){
            props.forEach(prop->{
                BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
                String[] attrs = StringUtils.split(prop, ":");
                if(attrs != null && attrs.length == 2){
                    queryBuilder.must(QueryBuilders.termQuery("attrs.attrId",attrs[0]));
                    String[] attrValues = StringUtils.split(attrs[1], "-");
                    if(attrValues != null){
                        queryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
                    }
                    //嵌套过滤属性，不对评分产生影响
                    boolQueryBuilder.filter(QueryBuilders.nestedQuery("attrs", queryBuilder, ScoreMode.None));
                }
            });
        }

        //分页
        Integer pageNum = searchParam.getPageNum();
        sourceBuilder.from((pageNum-1)*searchParam.getPageSize());
        sourceBuilder.size(searchParam.getPageSize());

        //排序
        String order = searchParam.getOrder();
        if(StringUtils.isEmpty(order)){
            String[] orders = StringUtils.split(order, ":");
            if(orders != null && orders.length == 2){
                String field = "";
                switch (orders[0]){
                    case "1":
                        field = "sales";
                        break;
                    case "2":
                        field = "createTime";
                        break;
                    case "3":
                        field = "price";
                        break;
                }
                sourceBuilder.sort(field, SortOrder.ASC.equals(orders[1])?SortOrder.ASC : SortOrder.DESC);
            }
        }
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").preTags("<front style='color:red'>").postTags("</front>");
        sourceBuilder.highlighter(highlightBuilder);


        //聚合品牌
        sourceBuilder.aggregation(AggregationBuilders.terms("brandIdAgg").field("brandId")
                .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName"))
                .subAggregation(AggregationBuilders.terms("logoAgg").field("logo")));
        //聚合分类
        sourceBuilder.aggregation(AggregationBuilders.terms("cateIdAgg").field("categoryId")
                .subAggregation(AggregationBuilders.terms("cateNameAgg").field("categoryName")));

        //聚合属性
        sourceBuilder.aggregation(AggregationBuilders.nested("attrsAgg", "attrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"))));

        sourceBuilder.fetchSource(new String[]{"skuId","price","title","pic","sales"},null);

        System.out.println(sourceBuilder.toString());
        return sourceBuilder;
    }
}
