import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ECloudSigner {

    private static final String SIGN_URL = "https://api.cloud.189.cn/mkt/userSign.action";
    private static final String DRAW_URL_1 = "https://m.cloud.189.cn/v2/drawPrizeMarketDetails.action?taskId=TASK_SIGNIN&activityId=ACT_SIGNIN";
    private static final String DRAW_URL_2 = "https://m.cloud.189.cn/v2/drawPrizeMarketDetails.action?taskId=TASK_SIGNIN_PHOTOS&activityId=ACT_SIGNIN";
    private static final String DRAW_URL_3 = "https://m.cloud.189.cn/v2/drawPrizeMarketDetails.action?taskId=TASK_2022_FLDFS_KJ&activityId=ACT_SIGNIN";

    // 模拟手机端 User-Agent，天翼云对移动端接口支持较好
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 5.1.1; SM-G930K Build/NRD90M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/74.0.3729.136 Mobile Safari/537.36 Ecloud/8.6.3 Android/22 clientId/355325117317828 clientModel/SM-G930K imsi/460071114317824 clientChannelId/qq proVersion/1.0.6";

    public static void main(String[] args) {
        // 1. 从环境变量获取 Cookie
        String cookie = System.getenv("TYYP_COOKIE");
        if (cookie == null || cookie.isEmpty()) {
            System.err.println("错误: 未找到环境变量 TYYP_COOKIE。请在 GitHub Secrets 中配置。");
            System.exit(1);
        }

        // 2. 创建 OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        try {
            // 3. 执行签到
            boolean signSuccess = performSign(client, cookie);

            if (signSuccess) {
                // 4. 如果签到成功（或已签到），尝试抽奖
                performDraw(client, cookie, "第一次抽奖", DRAW_URL_1);
                performDraw(client, cookie, "第二次抽奖", DRAW_URL_2);
                performDraw(client, cookie, "第三次抽奖", DRAW_URL_3);
            } else {
                System.out.println("签到失败，跳过抽奖。");
            }

        } catch (Exception e) {
            System.err.println("运行出错: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * 执行签到
     */
    private static boolean performSign(OkHttpClient client, String cookie) throws IOException {
        long timestamp = System.currentTimeMillis();
        String url = SIGN_URL + "?rand=" + timestamp + "&clientType=TELEANDROID&version=8.6.3&model=SM-G930K";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Referer", "https://m.cloud.189.cn/zhuanti/2016/sign/index.jsp?albumBackupOpened=1")
                .addHeader("Host", "m.cloud.189.cn")
                .addHeader("Accept", "application/json;charset=UTF-8")
                .addHeader("Cookie", cookie)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("签到请求失败，状态码: " + response.code());
            }
            ResponseBody body = response.body();
            if(body == null){
                System.out.println("performSign:响应体为空");
                return false;
            }
            String responseBody =body.string();
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

            // 检查是否有 netdiskBonus 字段，这是签到成功的标志
            if (json.has("netdiskBonus")) {
                String bonus = json.get("netdiskBonus").getAsString();
                String isSign = json.has("isSign") ? json.get("isSign").getAsString() : "unknown";

                if ("false".equals(isSign)) {
                    System.out.println("签到成功！获得空间: " + bonus + "M");
                } else {
                    System.out.println("今日已签到。获得空间: " + bonus + "M");
                }
                return true;
            } else {
                System.err.println("签到响应异常: " + responseBody);
                return false;
            }
        }
    }

    /**
     * 执行抽奖
     */
    private static void performDraw(OkHttpClient client, String cookie, String drawName, String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Referer", "https://m.cloud.189.cn/zhuanti/2016/sign/index.jsp?albumBackupOpened=1")
                .addHeader("Host", "m.cloud.189.cn")
                .addHeader("Accept", "application/json;charset=UTF-8")
                .addHeader("Cookie", cookie)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println(drawName + " 请求失败，状态码: " + response.code());
                return;
            }

            ResponseBody body = response.body();
            if(body == null){
                System.out.println("performSign:响应体为空");
                return;
            }
            String responseBody = body.string();

            // 简单判断是否包含 errorCode
            if (responseBody.contains("errorCode")) {
                System.out.println(drawName + " 跳过或失败: " + responseBody);
            } else {
                JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
                if (json.has("description")) {
                    String description = json.get("description").getAsString();
                    System.out.println(drawName + " 结果: " + description);
                } else {
                    System.out.println(drawName + " 响应无描述字段: " + responseBody);
                }
            }
        } catch (IOException e) {
            System.err.println(drawName + " 出错: " + e.getMessage());
        }
    }
}
