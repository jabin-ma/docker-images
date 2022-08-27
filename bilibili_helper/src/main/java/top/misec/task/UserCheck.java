package top.misec.task;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import top.misec.api.ApiList;
import top.misec.pojo.userinfobean.UserData;
import top.misec.utils.BilibiliRuntime;
import top.misec.utils.HelpUtil;
import top.misec.utils.HttpUtils;

import static top.misec.utils.BilibiliRuntime.STATUS_CODE_STR;

/**
 * 登录检查.
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 4:57
 */
public class UserCheck implements Task {

    @Override
    public boolean run(BilibiliRuntime bilibiliRuntime) {
        return bilibiliRuntime.runWithL(log -> {
            JsonObject userJson = HttpUtils.doGet(ApiList.LOGIN);
            if (userJson == null) {
                log.pushln("用户信息请求失败，如果是412错误，请在config.json中更换UA，412问题仅影响用户信息确认，不影响任务");
            } else {
                userJson = HttpUtils.doGet(ApiList.LOGIN);
                //判断Cookies是否有效
                if (userJson.get(STATUS_CODE_STR).getAsInt() == 0
                        && userJson.get("data").getAsJsonObject().get("isLogin").getAsBoolean()) {
                    bilibiliRuntime.setUserInfo(new Gson().fromJson(userJson
                            .getAsJsonObject("data"), UserData.class));
                    log.info("Cookies有效，登录成功");
                } else {
                    log.debug(String.valueOf(userJson));
                    log.pushln("Cookies可能失效了,请仔细检查配置中的Cookies是否有效");
                    return false;
                }
                log.pushln("用户名称:%s 硬币余额:%s", HelpUtil.userNameEncode(bilibiliRuntime.runWithU(UserData::getUname)), bilibiliRuntime.runWithU(UserData::getMoney));
            }
            return true;
        });
    }

    @Override
    public String getName() {
        return "登录检查";
    }
}
