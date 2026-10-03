package com.nexusauth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexusauth.api.system.SystemUserApi;
import com.nexusauth.api.system.dto.UserAuthInfo;
import com.nexusauth.domain.entity.User;
import com.nexusauth.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * 用户内部接口。
 * <p>
 * 仅供 NexusAuth 内部服务调用
 *
 * @author niuren
 * @date 2026-10-03 21:58
 */
@RestController
public class InternalUserController implements SystemUserApi {

    private final UserMapper userMapper;

    public InternalUserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserAuthInfo getUserAuthInfo(String username) {

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
        );

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户未找到");
        }

        return new UserAuthInfo(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getStatus().getCode()
        );
    }
}
