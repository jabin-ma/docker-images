package top.misec.task;

import com.google.gson.JsonObject;
import top.misec.api.ApiList;
import top.misec.utils.BilibiliRuntime;
import top.misec.utils.HttpUtils;

import static top.misec.utils.BilibiliRuntime.STATUS_CODE_STR;


/**
 * 直播签到.
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 5:42
 */
public class LiveChecking implements Task {
    @Override
    public boolean run(BilibiliRuntime bilibiliRuntime) {
        return bilibiliRuntime.runWithL(log -> {
            JsonObject liveCheckInResponse = HttpUtils.doGet(ApiList.LIVE_CHECKING);
            int code = liveCheckInResponse.get(STATUS_CODE_STR).getAsInt();
            if (code == 0) {
                JsonObject data = liveCheckInResponse.get("data").getAsJsonObject();
                log.pushln("直播签到成功，本次签到获得%s,%s", data.get("text").getAsString(), data.get("specialText").getAsString());
            } else {
                String message = liveCheckInResponse.get("message").getAsString();
                log.pushln("直播签到失败: %s", message);
            }
            return true;
        });
    }

    @Override
    public String getName() {
        return "直播签到";
    }
}
