package com.example.Scrumvest.util;

import com.example.Scrumvest.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RoleCheckInterceptor implements HandlerInterceptor {
    @Autowired
    private AccountService accountService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        
        if (path.startsWith("/admin") && !accountService.isCurrentUserInRole("PO")) {
            response.sendRedirect("/access-denied");
            return false;
        }
        
        if (path.startsWith("/project/edit") && !accountService.isCurrentUserInRole("PO")) {
            response.sendRedirect("/access-denied");
            return false;
        }
        
        return true;
    }
}