package adaptor.notion.log;

import notion.api.v1.logging.NotionLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NotionLoggerWrapper implements NotionLogger {
    private static final Logger log = LoggerFactoryWrapper.getLogger(NotionLoggerWrapper.class);

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public void debug(@NotNull String s, @Nullable Throwable throwable) {
        log.debug(s, throwable);
    }

    @Override
    public void info(@NotNull String s, @Nullable Throwable throwable) {
        log.info(s, throwable);
    }

    @Override
    public void warn(@NotNull String s, @Nullable Throwable throwable) {
        log.warn(s, throwable);
    }

    @Override
    public void error(@NotNull String s, @Nullable Throwable throwable) {
        log.error(s, throwable);
    }

    @Override
    public void debug(@NotNull String s) {
        log.debug(s);
    }

    @Override
    public void info(@NotNull String s) {
        log.info(s);
    }

    @Override
    public void warn(@NotNull String s) {
        log.warn(s);
    }

    @Override
    public void error(@NotNull String s) {
        log.error(s);
    }
}