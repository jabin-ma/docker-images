package top.misec.task;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import top.misec.api.ApiList;
import top.misec.config.PushConfig;
import top.misec.utils.HelpUtil;
import top.misec.utils.HttpUtils;
import top.misec.utils.Log;
import top.misec.utils.PushData;

import static top.misec.task.TaskInfoHolder.userInfo;

/**
 * 硬币日志.
 *
 * @author junzhou
 */
public class CoinLogs implements Task {
    @Override
    public boolean run(Log log) {

        JsonObject jsonObject = HttpUtils.doGet(ApiList.GET_COIN_LOG);

        if (jsonObject.get("code").getAsInt() == 0) {
            JsonObject data = jsonObject.getAsJsonObject("data");
            log.pushln("最近一周共计%s条硬币记录", String.valueOf(data.get("count").getAsInt()));
//            log.info("最近一周共计%s条硬币记录", data.get("count").getAsInt());
            JsonArray coinList = data.getAsJsonArray("list");

            double income = 0.0;
            double expend = 0.0;
            for (JsonElement jsonElement : coinList) {
                double delta = jsonElement.getAsJsonObject().get("delta").getAsDouble();
                //  String reason = jsonElement.getAsJsonObject().get("reason").getAsString();
                if (delta > 0) {
                    income += delta;
                } else {
                    expend += delta;
                }
            }
            log.pushln("最近一周收入:%s 支出:%s",String.valueOf(income),String.valueOf(expend));
//            log.info("最近一周收入%s个硬币", income);
//            log.info("最近一周支出%s个硬币", expend);
        }
        return true;
    }

    @Override
    public String getName() {
        return "硬币情况统计";
    }
}
