package top.misec.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Data;
import top.misec.utils.BilibiliRuntime;
import top.misec.utils.GsonUtils;
import top.misec.utils.HttpUtils;
import top.misec.utils.ReadFileUtils;

/**
 * Auto-generated: 2020-10-13 17:10:40.
 *
 * @author Junzhou Liu
 * @since 2020/10/13 17:11
 */
@Data
public class ConfigLoader {
    public static HelperConfig helperConfig;
    private static String defaultHelperConfig;

    static {
        defaultHelperConfig = ReadFileUtils.loadJsonFromAsset("config.json");
        helperConfig = buildHelperConfig(defaultHelperConfig);
    }


    /**
     * 优先从jar包同级目录读取.
     */
    public static boolean configInit(String filePath, BilibiliRuntime.Log log) {
        String customConfig = ReadFileUtils.readFile(filePath);
        if (customConfig != null) {
            mergeConfig(GsonUtils.fromJson(customConfig, HelperConfig.class));
            log.info("读取自定义配置文件成功,若部分配置项不存在则会采用默认配置.");
        } else {
            log.info("未在 ：%s 目录读取到配置文件", filePath);
            return false;
        }
        if (!validationConfig(log)) {
            return false;
        }
        helperConfig.getBiliVerify().initCookiesMap();
        HttpUtils.setUserAgent(helperConfig.getTaskConfig().getUserAgent());
        return true;
    }

    /**
     * 使用自定义文件时校验相关值.
     */
    private static boolean validationConfig(BilibiliRuntime.Log log) {

        if (helperConfig.getBiliVerify().getBiliCookies().length() < 1) {
            log.info("未在配置文件中配置cookies");
            return false;
        }

        TaskConfig taskConfig = helperConfig.getTaskConfig();

        taskConfig.setChargeDay(taskConfig.getChargeDay() > 28 || taskConfig.getChargeDay() < 1 ? 28 : taskConfig.getChargeDay())
                .setTaskIntervalTime(taskConfig.getTaskIntervalTime() <= 0 ? 1 : taskConfig.getTaskIntervalTime())
                .setPredictNumberOfCoins(taskConfig.getPredictNumberOfCoins() > 10 ? 10 : taskConfig.getPredictNumberOfCoins() <= 0 ? 1 : taskConfig.getPredictNumberOfCoins());

        helperConfig.setTaskConfig(taskConfig);
        return true;
    }

    /**
     * override config .
     *
     * @param sourceConfig sourceConfig
     */
    private static void mergeConfig(HelperConfig sourceConfig) {
        BeanUtil.copyProperties(sourceConfig, helperConfig, new CopyOptions().setIgnoreNullValue(true));
    }

    private static HelperConfig buildHelperConfig(String configJson) {
        return GsonUtils.fromJson(configJson, HelperConfig.class);
    }
}