# NexusAuth

NexusAuth 统一认证授权平台 —— 基于 Spring Boot 3.5 + Spring Cloud 2025 的 Maven 多模块项目，包含网关、系统管理、统一认证（SSO）三大服务与公共模块。

> 项目持续开发中，已实现基础登录、登录日志和租户选择与上下文传递；RBAC、SSO 和网关统一认证尚未实现。当前实现及接口边界见 [文档入口](docs/README.md)，尚未完成企业部署验收。

## 模块说明

| 模块 | 说明 | 端口 |
| --- | --- | --- |
| nexusAuth-gateway | 网关服务（Spring Cloud Gateway） | 8080 |
| nexusAuth-auth | 统一认证、授权、SSO 服务 | 9200 |
| nexusAuth-system | 系统管理服务 | 9201 |
| nexusAuth-common | 公共模块：工具类、公共实体、常量、异常、通用返回对象等 | - |
| nexusAuth-api | 服务间 API 契约与内部传输 DTO | - |

## 技术栈

- JDK 21
- Spring Boot 3.5.16
- Spring Cloud 2025.0.3（Gateway）+ Spring Cloud Alibaba 2025.0.0.0（Nacos）
- MySQL 8（MyBatis-Plus 3.5.17）
- Redis（Spring Data Redis / Lettuce）

## 本地环境依赖（dev 配置）

| 服务 | 本地默认连接 |
| --- | --- |
| MySQL | `localhost:3306`，root / 123456，业务库首次连接自动创建 |
| Redis | `localhost:6379`，密码 123456 |
| Nacos | `localhost:8848`（已关闭鉴权），服务自动注册、配置中心可选 |

默认激活 `dev` 环境；生产启动时使用 `--spring.profiles.active=prod`，并替换 `application-prod.yml` 中的连接信息。

数据库连接可以自动创建数据库，不会自动创建业务表；当前仓库没有受版本管理的建表脚本或迁移工具。System 内部接口要求共享服务凭证；Auth 调用时自动注入。Auth 与 System 必须配置相同的 `nexusauth.internal.system-token`，仍需限制 System 在可信内部网络中使用，详见 [上下文契约](docs/architecture/context-contract.md)。

## 快速开始

```bash
git clone https://github.com/niuren930/NexusAuth.git
cd NexusAuth
mvn clean package -DskipTests
```

编译产物位于各模块 `target/` 目录，服务可独立启动，例如：

```bash
java -jar nexusAuth-gateway/target/nexusAuth-gateway-1.0.0.jar
```

## 目录结构

```text
NexusAuth
├── nexusAuth-gateway    # 网关服务
├── nexusAuth-auth       # 统一认证授权服务（SSO）
├── nexusAuth-system     # 系统管理服务
├── nexusAuth-common     # 公共模块
├── nexusAuth-api        # 服务间接口契约
└── docs                 # 接口与上下文契约文档
```

每个服务的 Java 根包统一为 `com.nexusauth`，包结构如下：

```text
com.nexusauth
├── controller
├── domain
├── mapper
└── service
    └── impl
```

## 开源协议

[Apache License 2.0](LICENSE)
