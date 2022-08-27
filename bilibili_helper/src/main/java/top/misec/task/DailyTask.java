package top.misec.task;

import com.google.gson.JsonObject;
import top.misec.api.ApiList;
import top.misec.utils.HttpUtils;
import top.misec.utils.Log;
import top.misec.utils.SleepTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static top.misec.task.TaskInfoHolder.STATUS_CODE_STR;
import static top.misec.task.TaskInfoHolder.calculateUpgradeDays;

/**
 * 日常任务 .
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020/10/11 20:44
 */
public class DailyTask {

    private final List<Task> dailyTasks;

    public DailyTask() {
        dailyTasks = new ArrayList<>();
        dailyTasks.add(new VideoWatch());
        dailyTasks.add(new MangaSign());
        dailyTasks.add(new CoinAdd());
        dailyTasks.add(new Silver2Coin());
        dailyTasks.add(new LiveChecking());
        dailyTasks.add(new GiveGift());
        dailyTasks.add(new ChargeMe());
        dailyTasks.add(new GetVipPrivilege());
        dailyTasks.add(new MatchGame());
        dailyTasks.add(new MangaRead());
        Collections.shuffle(dailyTasks);
        dailyTasks.add(0, new UserCheck());
        dailyTasks.add(1, new CoinLogs());
    }


    public void doDailyTask(Log log) {
        try {
            for (Task task : dailyTasks) {
                log.pushln("%s",task.getName());
                log.info("------%s开始------", task.getName());
                try {
                    if (!task.run(log)){
                        log.info("------%s失败 结束运行------\n", task.getName());
                        break;
                    }
                } catch (Exception e) {
                    log.error("任务%s运行失败", task.getName(), e);
                }
                log.info("------%s结束------\n", task.getName());
                new SleepTime().sleepDefault();
            }
            log.info("本日任务已全部执行完毕");
            calculateUpgradeDays();
        } catch (Exception e) {
            log.error("任务运行异常", e);
        } finally {
            ServerPush.doServerPush();
        }
    }
}

