package top.misec.task;


import com.google.gson.JsonObject;
import top.misec.api.ApiList;
import top.misec.api.OftenApi;
import top.misec.config.ConfigLoader;
import top.misec.utils.BilibiliRuntime;
import top.misec.utils.HelpUtil;
import top.misec.utils.HttpUtils;

import java.util.Random;

import static top.misec.utils.BilibiliRuntime.STATUS_CODE_STR;

/**
 * 观看分享视频.
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 5:13
 */
public class VideoWatch implements Task {
    @Override
    public boolean run(BilibiliRuntime bilibiliRuntime) {
        return bilibiliRuntime.runWithL(log -> {
            JsonObject dailyTaskStatus = getDailyTaskStatus(log);
            String bvid = bilibiliRuntime.getVideoId.getRegionRankingVideoBvid();
            if (!dailyTaskStatus.get("watch").getAsBoolean()) {
                watchVideo(bvid, log);
            } else {
                log.pushln("本日观看视频任务已经完成了，不需要再观看视频了");
            }

            if (!dailyTaskStatus.get("share").getAsBoolean()) {
                dailyAvShare(bvid, log);
            } else {
                log.pushln("本日分享视频任务已经完成了，不需要再分享视频了");
            }
            return true;
        });
    }

    @Override
    public String getName() {
        return "观看分享视频";
    }

    public void watchVideo(String bvid, BilibiliRuntime.Log log) {
        int playedTime = new Random().nextInt(90) + 1;
        String postBody = "bvid=" + bvid
                + "&played_time=" + playedTime;
        JsonObject resultJson = HttpUtils.doPost(ApiList.VIDEO_HEARTBEAT, postBody);
        String videoTitle = OftenApi.getVideoTitle(bvid);
        int responseCode = resultJson.get(STATUS_CODE_STR).getAsInt();
        if (responseCode == 0) {
            log.pushln("视频: %s播放成功,已观看到第%s秒", videoTitle, playedTime);
        } else {
            log.pushln("视频: %s播放失败,原因: %s", videoTitle, resultJson.get("message").getAsString());
        }
    }

    /**
     * @param bvid 要分享的视频bvid.
     */
    public void dailyAvShare(String bvid, BilibiliRuntime.Log log) {
        String requestBody = "aid=" + HelpUtil.bv2av(bvid) + "&csrf=" + ConfigLoader.helperConfig.getBiliVerify().getBiliJct();
        JsonObject result = HttpUtils.doPost(ApiList.AV_SHARE, requestBody);
        String videoTitle = OftenApi.getVideoTitle(bvid);
        if (result.get(STATUS_CODE_STR).getAsInt() == 0) {
            log.pushln("视频: %s 分享成功", videoTitle);
        } else {
            log.pushln("视频分享失败，原因: %s", result.get("message").getAsString());
            log.info("如果出现 csrf 校验失败 提示，请查看常用浏览器中的cookie并替换");
        }
    }

    /**
     * get daily task status.
     *
     * @return jsonObject 返回status对象
     * @value {"login":true,"watch":true,"coins":50,"share":true,"email":true,"tel":true,"safe_question":true,"identify_card":false}
     * @author @srcrs
     */
    public JsonObject getDailyTaskStatus(BilibiliRuntime.Log log) {
        JsonObject jsonObject = HttpUtils.doGet(ApiList.REWARD);
        int responseCode = jsonObject.get(STATUS_CODE_STR).getAsInt();
        if (responseCode == 0) {
            log.info("请求本日任务完成状态成功");
            return jsonObject.get("data").getAsJsonObject();
        } else {
            log.warn(jsonObject.get("message").getAsString());
            return HttpUtils.doGet(ApiList.REWARD).get("data").getAsJsonObject();
            //偶发性请求失败，再请求一次。
        }
    }
}
