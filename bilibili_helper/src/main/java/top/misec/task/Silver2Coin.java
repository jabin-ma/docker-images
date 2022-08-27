package top.misec.task;

import com.google.gson.JsonObject;
import top.misec.api.ApiList;
import top.misec.api.OftenApi;
import top.misec.config.ConfigLoader;
import top.misec.utils.HttpUtils;
import top.misec.utils.Log;

import java.util.Objects;

import static top.misec.task.TaskInfoHolder.STATUS_CODE_STR;
import static top.misec.task.TaskInfoHolder.userInfo;

/**
 * 银瓜子换硬币.
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 5:25
 */
public class Silver2Coin implements Task {
    Log log;
    @Override
    public boolean run(Log logger) {
        log = logger;
        if (null == ConfigLoader.helperConfig.getTaskConfig().silver2Coin || !ConfigLoader.helperConfig.getTaskConfig().silver2Coin) {
            log.info("未开启银瓜子兑换硬币功能");
            return true;
        }
        JsonObject queryStatus = HttpUtils.doGet(ApiList.GET_SILVER_2_COIN_STATUS);
        if (queryStatus == null || Objects.isNull(queryStatus.get("data"))) {
            log.pushln("获取银瓜子状态失败");
            return true;
        }
        queryStatus = queryStatus.get("data").getAsJsonObject();
        //银瓜子兑换硬币汇率
        final int exchangeRate = 700;
        int silverNum = queryStatus.get("silver").getAsInt();

        if (silverNum < exchangeRate) {
            log.info("当前银瓜子余额为:%s,不足700,不进行兑换", silverNum);
        } else {
            String requestBody = "csrf_token=" + ConfigLoader.helperConfig.getBiliVerify().getBiliJct()
                    + "&csrf=" + ConfigLoader.helperConfig.getBiliVerify().getBiliJct();
            JsonObject resultJson = HttpUtils.doPost(ApiList.SILVER_2_COIN, requestBody);

            int responseCode = resultJson.get(STATUS_CODE_STR).getAsInt();
            if (responseCode == 0) {
                double coinMoneyAfterSilver2Coin = OftenApi.getCoinBalance();
                log.pushln("银瓜子兑换硬币成功 银瓜子余额:%s 硬币:%s",(silverNum - exchangeRate),coinMoneyAfterSilver2Coin);

                //兑换银瓜子后，更新userInfo中的硬币值
                if (userInfo != null) {
                    userInfo.setMoney(coinMoneyAfterSilver2Coin);
                }
            } else {
                log.pushln("银瓜子兑换硬币失败 原因是:%s", resultJson.get("message").getAsString());
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return "银瓜子换硬币";
    }
}
