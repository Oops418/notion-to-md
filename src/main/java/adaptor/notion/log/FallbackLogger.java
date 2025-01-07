package adaptor.notion.log;

import org.slf4j.Logger;
import org.slf4j.Marker;

public class FallbackLogger implements Logger {
    private final Class<?> logger;

    public FallbackLogger(Class<?> name) {
        this.logger = name;
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String msg) {

    }

    @Override
    public void trace(String format, Object arg) {

    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {

    }

    @Override
    public void trace(String format, Object... arguments) {

    }

    @Override
    public void trace(String msg, Throwable t) {

    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    public void trace(Marker marker, String msg) {

    }

    @Override
    public void trace(Marker marker, String format, Object arg) {

    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {

    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {

    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String msg) {

    }

    @Override
    public void debug(String format, Object arg) {

    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {

    }

    @Override
    public void debug(String format, Object... arguments) {

    }

    @Override
    public void debug(String msg, Throwable t) {

    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return false;
    }

    @Override
    public void debug(Marker marker, String msg) {

    }

    @Override
    public void debug(Marker marker, String format, Object arg) {

    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {

    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String msg) {
        String message = msg == null ? "null" : msg;
        System.out.println("[INFO] " + logger.getName() + ": " + message);
    }

    @Override
    public void info(String format, Object arg) {
        String safeFormat = format == null ? "null" : format;
        String formattedFormat = safeFormat.replace("{}", "%s");

        String argString = arg == null ? "null" : String.valueOf(arg);
        System.out.println("[INFO] " + logger.getName() + ": " + String.format(formattedFormat, argString));
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        String safeFormat = format == null ? "null" : format;
        String formattedFormat = safeFormat.replace("{}", "%s");
        
        String arg1String = arg1 == null ? "null" : arg1.toString();
        String arg2String = arg2 == null ? "null" : arg2.toString();
        System.out.println("[INFO] " + logger.getName() + ": " + String.format(formattedFormat, arg1String, arg2String));
    }

    @Override
    public void info(String format, Object... arguments) {
        String safeFormat = format == null ? "null" : format;
        Object[] safeArgs = arguments == null ? new Object[0] : arguments;
        
        Object[] argsString = new Object[safeArgs.length];
        for (int i = 0; i < safeArgs.length; i++) {
            argsString[i] = safeArgs[i] == null ? "null" : safeArgs[i].toString();
        }
        
        try {
            String formattedFormat = safeFormat.replace("{}", "%s");
            String message = String.format(formattedFormat, argsString);
            System.out.println("[INFO] " + logger.getName() + ": " + message);
        } catch (Exception e) {
            System.err.println("[ERROR] " + logger.getName() + ": " + "log message format error: " + e.getMessage());
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        System.out.println("[INFO] " + logger.getName() + ": " + msg);
        t.printStackTrace(System.out);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return false;
    }

    @Override
    public void info(Marker marker, String msg) {

    }

    @Override
    public void info(Marker marker, String format, Object arg) {

    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {

    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String msg) {
        String message = msg == null ? "null" : msg;
        System.out.println("[WARN] " + logger.getName() + ": " + message);
    }

    @Override
    public void warn(String format, Object arg) {
        String safeFormat = format == null ? "null" : format;
        String formattedFormat = safeFormat.replace("{}", "%s");

        String argString = arg == null ? "null" : String.valueOf(arg);
        System.out.println("[WARN] " + logger.getName() + ": " + String.format(formattedFormat, argString));
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        String safeFormat = format == null ? "null" : format;
        String formattedFormat = safeFormat.replace("{}", "%s");

        String arg1String = arg1 == null ? "null" : arg1.toString();
        String arg2String = arg2 == null ? "null" : arg2.toString();
        System.out.println("[WARN] " + logger.getName() + ": " + String.format(formattedFormat, arg1String, arg2String));
    }

    @Override
    public void warn(String format, Object... arguments) {
        String safeFormat = format == null ? "null" : format;
        Object[] safeArgs = arguments == null ? new Object[0] : arguments;

        Object[] argsString = new Object[safeArgs.length];
        for (int i = 0; i < safeArgs.length; i++) {
            argsString[i] = safeArgs[i] == null ? "null" : safeArgs[i].toString();
        }

        try {
            String formattedFormat = safeFormat.replace("{}", "%s");
            String message = String.format(formattedFormat, argsString);
            System.out.println("[WARN] " + logger.getName() + ": " + message);
        } catch (Exception e) {
            System.err.println("[ERROR] " + logger.getName() + ": " + "log message format error: " + e.getMessage());
        }}

    @Override
    public void warn(String msg, Throwable t) {
        System.err.println("[WARN] " + logger.getName() + ": " + msg);
        t.printStackTrace(System.err);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return false;
    }

    @Override
    public void warn(Marker marker, String msg) {

    }

    @Override
    public void warn(Marker marker, String format, Object arg) {

    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {

    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(String msg) {
        String message = msg == null ? "null" : msg;
        System.out.println("[ERROR] " + logger.getName() + ": " + message);
    }

    @Override
    public void error(String format, Object arg) {
        String safeFormat = format == null ? "null" : format;
        String formattedFormat = safeFormat.replace("{}", "%s");

        String argString = arg == null ? "null" : String.valueOf(arg);
        System.out.println("[ERROR] " + logger.getName() + ": " + String.format(formattedFormat, argString));
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        String safeFormat = format == null ? "null" : format;
        String formattedFormat = safeFormat.replace("{}", "%s");

        String arg1String = arg1 == null ? "null" : arg1.toString();
        String arg2String = arg2 == null ? "null" : arg2.toString();
        System.out.println("[ERROR] " + logger.getName() + ": " + String.format(formattedFormat, arg1String, arg2String));
    }

    @Override
    public void error(String format, Object... arguments) {
        String safeFormat = format == null ? "null" : format;
        Object[] safeArgs = arguments == null ? new Object[0] : arguments;

        Object[] argsString = new Object[safeArgs.length];
        for (int i = 0; i < safeArgs.length; i++) {
            argsString[i] = safeArgs[i] == null ? "null" : safeArgs[i].toString();
        }

        try {
            String formattedFormat = safeFormat.replace("{}", "%s");
            String message = String.format(formattedFormat, argsString);
            System.out.println("[ERROR] " + logger.getName() + ": " + message);
        } catch (Exception e) {
            System.err.println("[ERROR] " + logger.getName() + ": " + "log message format error: " + e.getMessage());
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        System.err.println("[ERROR] " + logger.getName() + ": " + msg);
        t.printStackTrace(System.err);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return false;
    }

    @Override
    public void error(Marker marker, String msg) {

    }

    @Override
    public void error(Marker marker, String format, Object arg) {

    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {

    }
}
