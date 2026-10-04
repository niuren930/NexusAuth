package com.nexusauth.service;

import com.nexusauth.domain.dto.LoginClientInfo;

public interface LoginLogService {

    void recordSuccess(
            Long userId,
            String username,
            LoginClientInfo clientInfo
    );

    void recordFailure(
            Long userId,
            String username,
            String failureReason,
            LoginClientInfo clientInfo
    );
}