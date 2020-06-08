package com.tom.crawler.constant;

import us.codecraft.webmagic.Task;

public class RedisConstant {

    private RedisConstant() {

    }

    private static final String BIT_PREFIX = "bit_";

    private static final String QUEUE_PREFIX = "queue_";

    private static final String ITEM_PREFIX = "item_";

    private static final String RUN_PREFIX = "run_";


    public static String getQueueKey(Task task) {
        return QUEUE_PREFIX + task.getUUID();
    }

    public static String getItemKey(Task task) {
        return ITEM_PREFIX + task.getUUID();
    }

    public static String getRunKey(Task task) {
        return RUN_PREFIX + task.getUUID();
    }

    public static String getBitKey(Task task) {
        return BIT_PREFIX + task.getUUID();
    }

}
