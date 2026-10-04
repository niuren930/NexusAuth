package com.nexusauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexusauth.domain.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志 Mapper
 *
 * @author niuren
 * @date 2026-10-04 10:17
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
}
