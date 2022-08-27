package top.misec.utils;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import top.misec.api.ApiList;
import top.misec.config.ConfigLoader;
import top.misec.pojo.userinfobean.UserData;
import top.misec.task.GetVideoId;

import java.io.File;

public class BilibiliRuntime {

    public static final String STATUS_CODE_STR = "code";
    private final Log log = new Log();
    public GetVideoId getVideoId;
    private UserData userInfo;

    public BilibiliRuntime(String[] args) {
        boolean result = false;
        if (args.length > 0) {
            log.info("使用自定义目录的配置文件");
            result = ConfigLoader.configInit(args[0], log);
        } else {
            log.info("使用同目录下的config.json文件");
            String currentPath = System.getProperty("user.dir") + File.separator + "config.json";
            result = ConfigLoader.configInit(currentPath, log);
        }
        if (result) {
            getVideoId = new GetVideoId();
        }
    }

    /**
     * 获取当前投币获得的经验值.
     *
     * @return 本日已经投了几个币
     */
    public int expConfirm() {
        JsonObject resultJson = HttpUtils.doGet(ApiList.NEED_COIN_NEW);
        int getCoinExp = resultJson.get("data").getAsInt();
        return getCoinExp / 10;
    }

    /**
     * 此功能依赖UserCheck.
     *
     * @return 返回会员类型.
     * 0:无会员（会员过期，当前不是会员）. 1:月会员. 2:年会员.
     */
    public int queryVipStatusType() {
        if (userInfo == null) {
            log.info("暂时无法查询会员状态，默认非大会员");
        }
        if (userInfo != null && userInfo.getVipStatus() == 1) {
            //只有VipStatus为1的时候获取到VipType才是有效的。
            return userInfo.getVipType();
        } else {
            return 0;
        }
    }

    public <R> R runWithLU(LogAndUserCall<R> sendTo) {
        return sendTo.notNull(userInfo, log);
    }

    public <R> R runWithU(UserCall<R> sendTo) {
        return sendTo.notNull(userInfo);
    }

    public <R> R runWithL(LogCall<R> sendTo) {
        return sendTo.notNull(log);
    }

    public void setUserInfo(UserData userInfo) {
        this.userInfo = userInfo;
    }

    public interface LogAndUserCall<S> {
        S notNull(UserData userInfo, Log log);
    }

    public interface UserCall<S> {
        S notNull(UserData userInfo);
    }

    public interface LogCall<S> {
        S notNull(Log log);
    }

    @Slf4j
    public static class Log {

        private final StringBuilder message = new StringBuilder();

        private Log() {
        }

        public void info(String msg, Object... args) {
            log.info(String.format(msg, args));
        }

        public void error(String msg, Exception e, Object... args) {
            log.error(String.format(msg, args), e);
        }

        public void error(String msg, String value, Exception e) {
            error(msg, e, value);
        }

        public void push(String msg, Object... args) {
            push(msg, false, args);
        }

        public void pushln(String msg, Object... args) {
            push(msg, true, args);
        }

        private void push(String msg, boolean newline, Object... args) {
            if (newline) {
                message.append("\n");
            }
            log.info("PUSH : " + String.format(msg, args));
            message.append(String.format(msg, args));
        }

        public void warn(String msg, Object... args) {
            log.warn(String.format(msg, args));
        }

        public void debug(String msg, Object... args) {
            log.debug(String.format(msg, args));
        }

    }


}
