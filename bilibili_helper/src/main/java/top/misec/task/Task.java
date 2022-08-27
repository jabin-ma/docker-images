package top.misec.task;


import top.misec.utils.BilibiliRuntime;

/**
 * Task Interface.
 *
 * @author @Kurenai
 * @since 2020-11-22 5:22
 */
public interface Task {

    /**
     * task接口.
     */
    boolean run(BilibiliRuntime bilibiliRuntime);


    /**
     * 任务名.
     *
     * @return taskName
     */
    String getName();

}