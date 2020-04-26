package com.atguigu.gmall.search.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

@Data
@Document(indexName = "goods",type = "info",shards = 3,replicas = 2)
public class Goods {
    //需要查询显示的数据
    @Id
    private Long skuId;
    @Field(type = FieldType.Double)
    private Double price;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.Keyword,index = false)
    private String pic;
    @Field(type = FieldType.Long)
    private Long sales;
    //聚合字段
    //任何一个商品都有品牌这个聚合
    @Field(type = FieldType.Long)
    private Long brandId;
    @Field(type = FieldType.Keyword)
    private String brandName;
    @Field(type = FieldType.Keyword,index = false)
    private String logo;
    @Field(type = FieldType.Date)
    private Date createTime;//新品
    @Field(type = FieldType.Boolean)
    private Boolean store; //是否有库存

    //如果搜索的结果有子分类，则会把子分类作为聚合，例如京东搜索手机
    @Field(type = FieldType.Long)
    private Long categoryId;
    @Field(type = FieldType.Keyword)
    private String categoryName;
    @Field(type = FieldType.Nested)
    List<SearchAttrValue> attrs;


}
