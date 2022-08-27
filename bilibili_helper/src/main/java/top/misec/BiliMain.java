package top.misec;

import java.io.File;

import com.google.gson.JsonSyntaxException;

import top.misec.config.ConfigLoader;
import top.misec.config.HelperConfig;
import top.misec.task.DailyTask;
import top.misec.task.ServerPush;
import top.misec.utils.Log;
import top.misec.utils.VersionInfo;

/**
 * 入口类 .
 *
 * @author JunzhouLiu
 * @since 2020/10/11 2:29
 */

public class BiliMain {

    public static void main(String[] args) {
        Log log = new Log();
        //VersionInfo.printVersionInfo();
        //每日任务65经验

        if (args.length > 0) {
            log.info("使用自定义目录的配置文件");
            ConfigLoader.configInit(args[0]);
        } else {
            log.info("使用同目录下的config.json文件");
            String currentPath = System.getProperty("user.dir") + File.separator + "config.json";
            ConfigLoader.configInit(currentPath);
        }

        if (!Boolean.TRUE.equals(ConfigLoader.helperConfig.getTaskConfig().getSkipDailyTask())) {
            DailyTask dailyTask = new DailyTask();
            dailyTask.doDailyTask(log);
        } else {
            log.info("已开启了跳过本日任务，（不会发起任何网络请求），如果需要取消跳过，请将skipDailyTask值改为false");
            ServerPush.doServerPush();
        }
    }

}
