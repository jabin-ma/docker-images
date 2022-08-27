package top.misec;

import top.misec.config.ConfigLoader;
import top.misec.task.DailyTask;
import top.misec.task.ServerPush;
import top.misec.utils.BilibiliRuntime;

/**
 * 入口类 .
 *
 * @author JunzhouLiu
 * @since 2020/10/11 2:29
 */

public class BiliMain {

    public static void main(String[] args) {
        BilibiliRuntime bilibiliRuntime = new BilibiliRuntime(args);
        //VersionInfo.printVersionInfo();
        //每日任务65经验
        bilibiliRuntime.runWithL(log -> {
                    if (!Boolean.TRUE.equals(ConfigLoader.helperConfig.getTaskConfig().getSkipDailyTask())) {
                        DailyTask dailyTask = new DailyTask();
                        dailyTask.doDailyTask(bilibiliRuntime);
                    } else {
                        log.info("已开启了跳过本日任务，（不会发起任何网络请求），如果需要取消跳过，请将skipDailyTask值改为false");
                        ServerPush.doServerPush();
                    }
                    return null;
                }
        );
    }
}
