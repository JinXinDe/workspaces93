package cn.itcast.cas.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/user")
@RestController
public class UserController {

    /**
     * 获取到用户名
     * @param request
     * @return
     */
    @GetMapping("/getUsername")
    public String getUsername(HttpServletRequest request){
        System.out.println(request.getRemoteUser());

        //从security中获取
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return username;
    }
}
