package top.misec.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Log {

    public void info(String msg, Object... args) {
        log.info(String.format(msg, args));
    }

    public void error(String msg, Exception e, Object... args) {
        log.error(String.format(msg, args), e);
    }

    public void error(String msg, String value, Exception e) {
        error(msg, e, value);
    }

    private final StringBuilder message = new StringBuilder();

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
        log.info("PUSH : "+String.format(msg, args));
        message.append(String.format(msg, args));
    }

    public void warn(String msg, Object... args) {
        log.warn(String.format(msg, args));
    }

    public void debug(String msg, Object... args) {
        log.debug(String.format(msg, args));
    }
}
