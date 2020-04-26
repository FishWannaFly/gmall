package com.atguigu.gmall.pms.entity.vo;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.List;

@Data
public class SpuAttrValueVo {
    private Long id;
    private Long spuId;
    private Long attrId;
    private String attrName;
    private String attrValue;

    public void setValueSelected(List<String> valueSelected){
        String join = StringUtils.join(valueSelected, ",");
        attrValue = join;
    }
}
