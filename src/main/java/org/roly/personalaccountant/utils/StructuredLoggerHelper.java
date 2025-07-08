package org.roly.personalaccountant.utils;

import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;

public final class StructuredLoggerHelper {

    public static final String ACTION_0_PARAMS = "{};";
    public static final String ACTION_1_PARAMS = "{}; {}";
    public static final String ACTION_2_PARAMS = "{}; {} {}";
    public static final String ACTION_3_PARAMS = "{}; {} {} {}";

    private StructuredLoggerHelper() {
    }

    public static StructuredArgument action(String action) {
        return StructuredArguments.v("action", action);
    }

    public static <T> StructuredArgument key(T key) {
        return StructuredArguments.kv("key", String.valueOf(key));
    }

}