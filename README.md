# 天翼云盘自动签到

[![天翼云盘签到](https://github.com/qsf728999746/Cloud189_Action/actions/workflows/ecloud-sign.yml/badge.svg)](https://github.com/qsf728999746/Cloud189_Action/actions/workflows/ecloud-sign.yml)

基于 Java 开发的天翼云盘自动签到工具，通过 GitHub Actions 实现每日定时自动签到，领取免费存储空间。

## ✨ 功能特性

- 🕐 **定时签到**：每天北京时间 9:00 自动执行签到任务
- 🎁 **领取空间**：自动获取签到奖励的存储空间
- 🔄 **GitHub Actions**：无需本地运行，云端自动执行
- 📱 **模拟移动端**：使用 Android 客户端接口，提高成功率
- 🔒 **安全存储**：Cookie 信息存储在 GitHub Secrets 中，保障账号安全

## 🚀 快速开始

### 1. Fork 本仓库

点击右上角的 **Fork** 按钮，将本仓库复制到你的 GitHub 账号下。

### 2. 获取天翼云盘 Cookie

#### 方法一：从 Network 面板获取（推荐）

1. 浏览器打开 [天翼云盘网页版](https://cloud.189.cn/) 并登录
2. 按 `F12` 打开开发者工具，切换到 **Network** (网络) 标签
3. 刷新页面（按 F5）
4. 在左侧请求列表中，找到第一个请求（通常是 `index.jsp` 或域名是 `cloud.189.cn` 的请求）
5. 点击该请求，在右侧面板选择 **Headers** (标头)
6. 向下滚动找到 **Request Headers** (请求标头) 区域
7. 找到 **`Cookie:`** 这一行，复制冒号后面的**所有内容**

#### 方法二：使用浏览器插件

1. 安装 Chrome/Edge 扩展：**EditThisCookie** 或 **Cookie Editor**
2. 在天翼云盘页面，点击浏览器右上角的插件图标
3. 点击导出/复制按钮，获取完整的 Cookie 字符串

> ⚠️ **重要提示**：
> - `document.cookie` 无法获取 `HttpOnly` 属性的关键 Cookie，请勿使用此方法
> - Cookie 包含敏感信息，**切勿**直接提交到代码仓库
> - Cookie 有有效期（通常几天到几周），过期后需重新获取

### 3. 配置 GitHub Secrets

1. 进入你的仓库页面
2. 点击 **Settings** → **Secrets and variables** → **Actions**
3. 点击 **New repository secret**
4. 填写以下信息：
   - **Name**: `TYYP_COOKIE`
   - **Value**: 粘贴你刚才复制的完整 Cookie 字符串
5. 点击 **Add secret** 保存

### 4. 手动触发测试

1. 进入仓库的 **Actions** 标签页
2. 选择 **天翼云盘签到** 工作流
3. 点击 **Run workflow** → **Run workflow** 按钮
4. 等待执行完成，查看日志确认签到结果

### 5. 查看签到结果

执行成功后，你会在日志中看到类似输出：
```
签到成功！获得空间: 91M
```
或
```
今日已签到。获得空间: 91M
```

> 💡 **注意**：由于天翼云抽奖接口存在频率限制和次数限制，目前代码中已注释掉抽奖功能，仅保留签到功能。

## 🔧 本地运行（可选）

如果你想在本地测试运行：

### 环境要求

- JDK 17 或更高版本
- Maven 3.6+

### 运行步骤

1. **克隆仓库**
   ```bash
   git clone https://github.com/qsf728999746/Cloud189_Action.git
   cd Cloud189_Action
   ```

2. **设置环境变量**
   
   Windows PowerShell:
   ```powershell
   $env:TYYP_COOKIE="你的完整Cookie字符串"
   ```
   
   Linux/macOS:
   ```bash
   export TYYP_COOKIE="你的完整Cookie字符串"
   ```

3. **编译打包**
   ```bash
   mvn clean package -DskipTests
   ```

4. **运行程序**
   ```bash
   java -jar target/ecloud-sign-1.0.0-jar-with-dependencies.jar
   ```

## 📅 定时任务配置

GitHub Actions 工作流配置为每天 UTC 1:00（北京时间 9:00）自动执行。

如需修改执行时间，编辑 `.github/workflows/ecloud-sign.yml` 文件中的 cron 表达式：

```yaml
schedule:
  - cron: '0 1 * * *'  # 每天 UTC 1:00 (北京时间 9:00)
```

Cron 表达式格式：`分 时 日 月 周`（UTC 时间）

常用示例：
- `0 1 * * *` - 每天 UTC 1:00（北京时间 9:00）
- `0 0 * * *` - 每天 UTC 0:00（北京时间 8:00）
- `30 1 * * *` - 每天 UTC 1:30（北京时间 9:30）

## ❓ 常见问题

### 1. 签到失败，提示"未找到环境变量 TYYP_COOKIE"

**原因**：未在 GitHub Secrets 中配置 Cookie 或配置名称错误。

**解决**：
- 检查 Settings → Secrets and variables → Actions 中是否有名为 `TYYP_COOKIE` 的 Secret
- 确认拼写完全一致（区分大小写）

### 2. 签到响应异常或返回未登录错误

**原因**：Cookie 已过期或复制不完整。

**解决**：
- 重新按照步骤 2 获取最新的 Cookie
- 确保从 Network 面板复制的是完整的 Cookie 字符串
- 更新 GitHub Secrets 中的 `TYYP_COOKIE` 值

### 3. Cookie 多久需要更新一次？

**答**：Cookie 有效期通常为几天到几周不等，取决于天翼云的安全策略。建议：
- 当签到突然失败时，检查是否需要更新 Cookie

### 4. 能否增加抽奖功能？

**答**：代码中已包含抽奖逻辑，但默认被注释掉。原因：
- 抽奖需要完成任务才能获得次数（如上传照片、分享文件等）
- 连续请求会被服务器判定为频繁操作而拦截
- 抽奖收益不稳定，且可能触发风控

如需启用，取消 `ECloudSigner.java` 第 36-38 行的注释即可。

## ⚠️ 免责声明

1. 本项目仅供学习交流使用，请勿用于商业用途
2. 使用本项目可能导致账号被限制或封禁，风险由使用者自行承担
3. 请遵守天翼云盘用户协议，合理使用签到功能
4. 本项目作者不对因使用本项目造成的任何损失负责

## 📝 更新日志

### v1.0.0 (2026-04-26)
- ✅ 实现天翼云盘自动签到功能
- ✅ 集成 GitHub Actions 定时执行
- ✅ 支持环境变量配置 Cookie
- ✅ 优化移动端请求头模拟
- ℹ️ 暂时禁用抽奖功能（受限于次数和频率）

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的改动 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启一个 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 🙏 致谢

- [OkHttp](https://square.github.io/okhttp/) - HTTP 客户端库
- [Gson](https://github.com/google/gson) - JSON 解析库
- [GitHub Actions](https://github.com/features/actions) - 自动化工作流平台

---

**如果这个项目对你有帮助，请给个 ⭐ Star 支持一下！**
