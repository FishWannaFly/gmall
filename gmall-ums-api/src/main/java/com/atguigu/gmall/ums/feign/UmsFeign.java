package com.atguigu.gmall.ums.feign;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.ums.entity.UserAddressEntity;
import com.atguigu.gmall.ums.entity.UserEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UmsFeign {
    @PostMapping("ums/user/query")
    public ResponseVo<UserEntity> queryUserEntity(@RequestParam("username")String username,
                                                  @RequestParam("password")String password);
    @GetMapping("ums/useraddress/getAddressListByUserId/{userId}")
    public ResponseVo<List<UserAddressEntity>> getAddressListByUserId(@PathVariable Long userId);
    @GetMapping("ums/user/{id}")
    public ResponseVo<UserEntity> queryUserById(@PathVariable("id") Long id);
}
