package top.misec.task;

import com.google.gson.JsonObject;
import top.misec.api.ApiList;
import top.misec.config.ConfigLoader;
import top.misec.utils.BilibiliRuntime;
import top.misec.utils.HttpUtils;

/**
 * 漫画签到.
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 5:22
 */

public class MangaSign implements Task {
    @Override
    public boolean run(BilibiliRuntime bilibiliRuntime) {
        return bilibiliRuntime.runWithL(log -> {
            String platform = ConfigLoader.helperConfig.getTaskConfig().getDevicePlatform();
            String requestBody = "platform=" + platform;
            JsonObject result = HttpUtils.doPost(ApiList.MANGA, requestBody);

            if (result == null) {
                log.pushln("哔哩哔哩漫画已经签到过了");
            } else {
                log.pushln("完成漫画签到");
            }
            return true;
        });
    }

    @Override
    public String getName() {
        return "漫画签到";
    }
}
