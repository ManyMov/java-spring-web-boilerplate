package com.leknarm.boilerplate.util;


import jakarta.servlet.http.HttpServletRequest;

public class WebUtils {
    public static boolean isActuatorEndpoints(HttpServletRequest request) {
        return String.valueOf(request.getRequestURL()).contains("actuator");
    }
}
