package gong.server_api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hospital")
public class HospitalNavController {
    @GetMapping("/nav")
    public String hospitalNav() {
        return "page/hospital/nav";
    }
}