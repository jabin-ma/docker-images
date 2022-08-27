package top.misec.task;


import top.misec.utils.Log;

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
    boolean run(Log log);


    /**
     * 任务名.
     *
     * @return taskName
     */
    String getName();

}