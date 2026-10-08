# NexusAuth 项目长期记忆

## 内部服务凭证（Auth → System）

- `InternalServiceCredential`（nexusAuth-common）校验格式 `[A-Za-z0-9_-]{43,128}`，
  即 32 字节随机数的无填充 Base64URL；不满足则在 Bean 构造期直接抛异常、服务拒绝启动。
- 配置项 `nexusauth.internal.system-token`，auth 与 system 必须取同一值；
  prod 通过环境变量 `NEXUSAUTH_SYSTEM_INTERNAL_TOKEN` 注入，dev 允许写默认值。
- 调用方通过 Feign 拦截器写入请求头 `X-Nexus-Service-Token`，System 侧
  `SystemRequestContextFilter` 先校验该凭证，再接受用户/租户上下文。

## 本机协作环境限制（重要）

- 当前会话对 `\\wsl$\Ubuntu\...` 路径下**已存在**的项目文件没有写权限：
  编辑、追加、重命名替换均返回 Permission denied（Windows 侧 python 写入亦然）；
  仅**新建**文件可写入。内置编辑工具另因 UNC 路径无法建备份而直接失败。
- `wsl.exe` 被安全策略列入程序黑名单，禁止调用或变通调用。
- 结论：需要修改仓库既有文件时，先生成补丁（用 `git apply --check` / `patch --dry-run` 校验可应用性）
  交用户在 WSL 内应用；新建文件（如本记忆目录）不受影响。
- `git` 在本机直接对 UNC 路径操作会报 dubious ownership，需 `-c safe.directory=<仓库路径>`。
