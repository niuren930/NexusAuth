package com.nexusauth.service.impl;

import com.nexusauth.domain.dto.LoginClientInfo;
import com.nexusauth.domain.entity.LoginLog;
import com.nexusauth.domain.enums.LoginStatus;
import com.nexusauth.domain.enums.LoginType;
import com.nexusauth.mapper.LoginLogMapper;
import com.nexusauth.service.LoginLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 登录日志实现
 *
 * @author niuren
 * @date 2026-10-04 11:01
 */
@Service
@Slf4j
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogMapper loginLogMapper;

    public LoginLogServiceImpl(
            LoginLogMapper loginLogMapper) {
        this.loginLogMapper = loginLogMapper;
    }

    @Override
    public void recordSuccess(
            Long userId,
            String username,
            LoginClientInfo clientInfo) {

        save(
                userId,
                username,
                LoginStatus.SUCCESS,
                null,
                clientInfo
        );
    }

    @Override
    public void recordFailure(
            Long userId,
            String username,
            String failureReason,
            LoginClientInfo clientInfo) {

        save(
                userId,
                username,
                LoginStatus.FAILURE,
                failureReason,
                clientInfo
        );
    }

    private void save(
            Long userId,
            String username,
            LoginStatus status,
            String failureReason,
            LoginClientInfo clientInfo) {

        try {

            LoginLog loginLog = new LoginLog();

            loginLog.setUserId(userId);
            loginLog.setUsername(username);
            loginLog.setLoginType(
                    LoginType.PASSWORD
            );
            loginLog.setLoginStatus(status);
            loginLog.setIp(clientInfo.ip());
            loginLog.setUserAgent(
                    clientInfo.userAgent()
            );
            loginLog.setFailureReason(
                    failureReason
            );
            loginLog.setLoginTime(
                    LocalDateTime.now()
            );

            loginLogMapper.insert(loginLog);

        } catch (Exception exception) {

            log.error(
                    "保存登录日志失败, username={}",
                    username,
                    exception
            );
        }
    }
}