package com.atguigu.gmall.ums.mapper;

import com.atguigu.gmall.ums.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表
 * 
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 18:26:02
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
	
}
