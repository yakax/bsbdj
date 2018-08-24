package com.yakax.bsbdj.mapper;

import lombok.extern.slf4j.Slf4j;
import net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator;
import net.sf.log4jdbc.sql.Spy;

/**
 * 基于jdbc层面的监听工具
 * 打印执行的SQL的具体情况
 * */
@Slf4j
public class DBSlf4jSpyLogDelegator extends Slf4jSpyLogDelegator {
    @Override
    public void sqlTimingOccurred(Spy spy, long execTime, String methodCall, String sql) {
        log.info("[" + execTime + "ms]" + sql.replaceAll("[\n\r]", ""));
    }
}