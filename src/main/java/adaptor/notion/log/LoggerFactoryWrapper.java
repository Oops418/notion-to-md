package adaptor.notion.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

public class LoggerFactoryWrapper {
    private static boolean isCalled = false;

    public static Logger getLogger(Class<?> name) {
        Logger log = LoggerFactory.getLogger(name);
        if (log instanceof NOPLogger) {
            if (!isCalled) {
                System.out.println("SLF4J implementation not found. Using fallback logger.");
                isCalled = true;
            }
            return new FallbackLogger(name);
        }else {
            return log;
        }
    }
}
