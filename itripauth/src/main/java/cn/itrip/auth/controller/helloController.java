package cn.itrip.auth.controller;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/a")
public class helloController {

    @RequestMapping(value = "/b")
    public String a(){
        return "a";
    }
}
