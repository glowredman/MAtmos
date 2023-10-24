package eu.ha3.matmos.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;

public class NoOpLogger implements Logger {
    
    public static final NoOpLogger INSTANCE = new NoOpLogger();

    @Override
    public void catching(Level level, Throwable t) {}

    @Override
    public void catching(Throwable t) {}

    @Override
    public void debug(Marker marker, Message msg) {}

    @Override
    public void debug(Marker marker, Message msg, Throwable t) {}

    @Override
    public void debug(Marker marker, Object message) {}

    @Override
    public void debug(Marker marker, Object message, Throwable t) {}

    @Override
    public void debug(Marker marker, String message) {}

    @Override
    public void debug(Marker marker, String message, Object... params) {}

    @Override
    public void debug(Marker marker, String message, Throwable t) {}

    @Override
    public void debug(Message msg) {}

    @Override
    public void debug(Message msg, Throwable t) {}

    @Override
    public void debug(Object message) {}

    @Override
    public void debug(Object message, Throwable t) {}

    @Override
    public void debug(String message) {}

    @Override
    public void debug(String message, Object... params) {}

    @Override
    public void debug(String message, Throwable t) {}

    @Override
    public void entry() {}

    @Override
    public void entry(Object... params) {}

    @Override
    public void error(Marker marker, Message msg) {}

    @Override
    public void error(Marker marker, Message msg, Throwable t) {}

    @Override
    public void error(Marker marker, Object message) {}

    @Override
    public void error(Marker marker, Object message, Throwable t) {}

    @Override
    public void error(Marker marker, String message) {}

    @Override
    public void error(Marker marker, String message, Object... params) {}

    @Override
    public void error(Marker marker, String message, Throwable t) {}

    @Override
    public void error(Message msg) {}

    @Override
    public void error(Message msg, Throwable t) {}

    @Override
    public void error(Object message) {}

    @Override
    public void error(Object message, Throwable t) {}

    @Override
    public void error(String message) {}

    @Override
    public void error(String message, Object... params) {}

    @Override
    public void error(String message, Throwable t) {}

    @Override
    public void exit() {}

    @Override
    public <R> R exit(R result) {
        
        return null;
    }

    @Override
    public void fatal(Marker marker, Message msg) {}

    @Override
    public void fatal(Marker marker, Message msg, Throwable t) {}

    @Override
    public void fatal(Marker marker, Object message) {}

    @Override
    public void fatal(Marker marker, Object message, Throwable t) {}

    @Override
    public void fatal(Marker marker, String message) {}

    @Override
    public void fatal(Marker marker, String message, Object... params) {}

    @Override
    public void fatal(Marker marker, String message, Throwable t) {}

    @Override
    public void fatal(Message msg) {}

    @Override
    public void fatal(Message msg, Throwable t) {}

    @Override
    public void fatal(Object message) {}

    @Override
    public void fatal(Object message, Throwable t) {}

    @Override
    public void fatal(String message) {}

    @Override
    public void fatal(String message, Object... params) {}

    @Override
    public void fatal(String message, Throwable t) {}

    @Override
    public MessageFactory getMessageFactory() {
        
        return null;
    }

    @Override
    public String getName() {
        
        return null;
    }

    @Override
    public void info(Marker marker, Message msg) {}

    @Override
    public void info(Marker marker, Message msg, Throwable t) {}

    @Override
    public void info(Marker marker, Object message) {}

    @Override
    public void info(Marker marker, Object message, Throwable t) {}

    @Override
    public void info(Marker marker, String message) {}

    @Override
    public void info(Marker marker, String message, Object... params) {}

    @Override
    public void info(Marker marker, String message, Throwable t) {}

    @Override
    public void info(Message msg) {}

    @Override
    public void info(Message msg, Throwable t) {}

    @Override
    public void info(Object message) {}

    @Override
    public void info(Object message, Throwable t) {}

    @Override
    public void info(String message) {}

    @Override
    public void info(String message, Object... params) {}

    @Override
    public void info(String message, Throwable t) {}

    @Override
    public boolean isDebugEnabled() {
        
        return false;
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        
        return false;
    }

    @Override
    public boolean isEnabled(Level level) {
        
        return false;
    }

    @Override
    public boolean isEnabled(Level level, Marker marker) {
        
        return false;
    }

    @Override
    public boolean isErrorEnabled() {
        
        return false;
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        
        return false;
    }

    @Override
    public boolean isFatalEnabled() {
        
        return false;
    }

    @Override
    public boolean isFatalEnabled(Marker marker) {
        
        return false;
    }

    @Override
    public boolean isInfoEnabled() {
        
        return false;
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        
        return false;
    }

    @Override
    public boolean isTraceEnabled() {
        
        return false;
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        
        return false;
    }

    @Override
    public boolean isWarnEnabled() {
        
        return false;
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        
        return false;
    }

    @Override
    public void log(Level level, Marker marker, Message msg) {}

    @Override
    public void log(Level level, Marker marker, Message msg, Throwable t) {}

    @Override
    public void log(Level level, Marker marker, Object message) {}

    @Override
    public void log(Level level, Marker marker, Object message, Throwable t) {}

    @Override
    public void log(Level level, Marker marker, String message) {}

    @Override
    public void log(Level level, Marker marker, String message, Object... params) {}

    @Override
    public void log(Level level, Marker marker, String message, Throwable t) {}

    @Override
    public void log(Level level, Message msg) {}

    @Override
    public void log(Level level, Message msg, Throwable t) {}

    @Override
    public void log(Level level, Object message) {}

    @Override
    public void log(Level level, Object message, Throwable t) {}

    @Override
    public void log(Level level, String message) {}

    @Override
    public void log(Level level, String message, Object... params) {}

    @Override
    public void log(Level level, String message, Throwable t) {}

    @Override
    public void printf(Level level, Marker marker, String format, Object... params) {}

    @Override
    public void printf(Level level, String format, Object... params) {}

    @Override
    public <T extends Throwable> T throwing(Level level, T t) {
        
        return null;
    }

    @Override
    public <T extends Throwable> T throwing(T t) {
        
        return null;
    }

    @Override
    public void trace(Marker marker, Message msg) {}

    @Override
    public void trace(Marker marker, Message msg, Throwable t) {}

    @Override
    public void trace(Marker marker, Object message) {}

    @Override
    public void trace(Marker marker, Object message, Throwable t) {}

    @Override
    public void trace(Marker marker, String message) {}

    @Override
    public void trace(Marker marker, String message, Object... params) {}

    @Override
    public void trace(Marker marker, String message, Throwable t) {}

    @Override
    public void trace(Message msg) {}

    @Override
    public void trace(Message msg, Throwable t) {}

    @Override
    public void trace(Object message) {}

    @Override
    public void trace(Object message, Throwable t) {}

    @Override
    public void trace(String message) {}

    @Override
    public void trace(String message, Object... params) {}

    @Override
    public void trace(String message, Throwable t) {}

    @Override
    public void warn(Marker marker, Message msg) {}

    @Override
    public void warn(Marker marker, Message msg, Throwable t) {}

    @Override
    public void warn(Marker marker, Object message) {}

    @Override
    public void warn(Marker marker, Object message, Throwable t) {}

    @Override
    public void warn(Marker marker, String message) {}

    @Override
    public void warn(Marker marker, String message, Object... params) {}

    @Override
    public void warn(Marker marker, String message, Throwable t) {}

    @Override
    public void warn(Message msg) {}

    @Override
    public void warn(Message msg, Throwable t) {}

    @Override
    public void warn(Object message) {}

    @Override
    public void warn(Object message, Throwable t) {}

    @Override
    public void warn(String message) {}

    @Override
    public void warn(String message, Object... params) {}

    @Override
    public void warn(String message, Throwable t) {}

}
