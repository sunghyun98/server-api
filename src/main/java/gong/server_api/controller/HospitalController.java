package gong.server_api.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/hospital")
public class HospitalController {


    @GetMapping("/list")
    public String hospitalList(String text, Model model) {
        // 병원 목록 가져오기

        // 페이지 이름 반환
        return "page/hospital/list";
    }

    @GetMapping("/nav")
    public String hospitalNav() {
        // 페이지 이름 반환
        return "page/hospital/nav";
    }
}