package org.linlinjava.litemall.admin.constant;

/**
 * 项目中的常量定义类
 */
public class Constant {


    public static class BpmConstant{

        /**
         * 用于获取相对应工厂
         */
        public static final String BPMS = "bpms";

        /**
         * 事件类型 审批任务开始，结束，转交。
         */
        public static final String BPMS_TASK_CHANGE = "bpms_task_change";

        /**
         * 事件类型 审批实例开始，结束。
         */
        public static final String BPMS_INSTANCE_CHANGE = "bpms_instance_change";
    }

    public static class CheckUrl{
        /**
         * 用于获取相对应工厂
         */
        public static final String CHECK_URL = "check_url";
    }
}
