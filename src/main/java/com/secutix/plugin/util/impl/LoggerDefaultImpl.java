package com.secutix.plugin.util.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.base.Joiner;
import com.secutix.plugin.util.Logger;

public class LoggerDefaultImpl implements Logger {

    private List<String> allMessages = new ArrayList<>();

    @Override
    public void info(String message) {
        log("INFO", message);

    }

    @Override
    public void debug(String message) {
        log("DEBUG", message);

    }

    @Override
    public void error(String message, Throwable e) {
        log("ERROR", message);
        if (e != null) {
            e.printStackTrace();
        }
    }

    @Override
    public void warn(String message) {
        log("WARN", message);

    }

    void log(String level, String message) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss");

        String fullMessage = Joiner.on(" - ").join(level, sdf.format(d), message);

        allMessages.add(fullMessage);

        System.out.println(fullMessage);
    }

    public List<String> getAllMessages() {
        return allMessages;
    }

}
