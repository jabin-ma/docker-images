package top.misec.task;

import top.misec.utils.BilibiliRuntime;

public class Summary implements Task{

    @Override
    public boolean run(BilibiliRuntime bilibiliRuntime) {
        return bilibiliRuntime.runWithLU((userInfo, log) ->{
            if (userInfo == null) {
                log.info("未请求到用户信息，暂无法计算等级相关数据");
                return true;
            }
            int todayExp = 15;
            todayExp += bilibiliRuntime.expConfirm() * 10;
            log.info("今日获得的总经验值为: %s", todayExp);

            int needExp = userInfo.getLevel_info().getNextExpAsInt() - userInfo.getLevel_info().getCurrent_exp();

            if (userInfo.getLevel_info().getCurrent_level() < 6) {
                log.info("按照当前进度，升级到升级到Lv%s还需要: %s天", userInfo.getLevel_info().getCurrent_level() + 1, needExp / todayExp);
            } else {
                log.info("当前等级Lv6，经验值为：%s", userInfo.getLevel_info().getCurrent_exp());
            }
            return true;
        });
    }






    @Override
    public String getName() {
        return "统计信息";
    }
}
