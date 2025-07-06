package com.sky.context;

//import cn.dev33.satoken.stp.StpUtil;

public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
//        return StpUtil.getLoginIdAsLong();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
