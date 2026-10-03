# NexusAuth

NexusAuth 统一认证授权平台 —— 基于 Spring Boot 3.5 + Spring Cloud 2025 的 Maven 多模块项目，包含网关、系统管理、统一认证（SSO）三大服务与公共模块。

> 项目当前处于骨架初始化阶段，业务功能持续开发中。

## 模块说明

| 模块 | 说明 | 端口 |
| --- | --- | --- |
| nexusAuth-gateway | 网关服务（Spring Cloud Gateway） | 8080 |
| nexusAuth-auth | 统一认证、授权、SSO 服务 | 9200 |
| nexusAuth-system | 系统管理服务 | 9201 |
| nexusAuth-common | 公共模块：工具类、公共实体、常量、异常、通用返回对象等 | - |

## 技术栈

- JDK 21
- Spring Boot 3.5.16
- Spring Cloud 2025.0.3（Gateway）

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
└── nexusAuth-common     # 公共模块
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
