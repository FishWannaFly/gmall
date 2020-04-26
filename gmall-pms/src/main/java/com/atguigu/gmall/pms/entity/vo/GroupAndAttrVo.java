package com.atguigu.gmall.pms.entity.vo;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class GroupAndAttrVo extends AttrGroupEntity implements Serializable {
    List<AttrEntity> attrEntities;
}
