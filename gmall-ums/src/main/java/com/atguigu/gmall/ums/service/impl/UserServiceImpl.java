package com.atguigu.gmall.ums.service.impl;

import com.atguigu.gmall.common.exception.GmallException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.ums.mapper.UserMapper;
import com.atguigu.gmall.ums.entity.UserEntity;
import com.atguigu.gmall.ums.service.UserService;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<UserEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<UserEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public Boolean checkData(String data, Integer type) {
        String column;
        switch (type){
            case 1:
                column = "username";
                break;
            case 2:
                column = "phone";
                break;
            case 3:
                column = "email";
                break;
            default:
                return false;
        }
        Integer count = baseMapper.selectCount(new QueryWrapper<UserEntity>().eq(column, data));
        return count == 0;
    }
    @Autowired
    StringRedisTemplate redisTemplate;
    @Override
    public void register(UserEntity userEntity, String code) {
        //判断验证码是否正确
        String phone = userEntity.getPhone();
        if(StringUtils.isEmpty(phone)){
            return;
        }
        String redisCode = redisTemplate.opsForValue().get(phone + ":code");
        if(StringUtils.isEmpty(redisCode)){
            return;
        }
        if(!StringUtils.equals(code,redisCode)){
            throw new GmallException("验证码错误");
        }
        redisTemplate.delete(phone + ":code");


        String password = userEntity.getPassword();
        String sault = UUID.randomUUID().toString().substring(0, 6);
        password = password + sault;
        password = DigestUtils.md5Hex(password);
        userEntity.setPassword(password);
        userEntity.setSault(sault);
        userEntity.setCreateTime(new Date());
        userEntity.setLevelId(1l);
        userEntity.setStatus(1);
        userEntity.setIntegration(0);
        userEntity.setGrowth(0);
        baseMapper.insert(userEntity);
    }

    @Override
    public UserEntity queryUserEntity(String username, String password) {
        UserEntity userEntity = baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("username", username));
        if(userEntity == null){
            throw new GmallException("账号不存在");
        }
        String sault = userEntity.getSault();
        password = password + sault;
        if(!StringUtils.equals(DigestUtils.md5Hex(password),userEntity.getPassword())){
            throw new GmallException("密码错误");
        }
        return userEntity;
    }
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Override
    public void getCode(String phone) {
        Integer count = baseMapper.selectCount(new QueryWrapper<UserEntity>().eq("phone", phone));
        if(count > 0){
            throw new GmallException("手机号已注册");
        }
        rabbitTemplate.convertAndSend("gmall.msg.exchange",
                "send.ums",phone);
    }

}