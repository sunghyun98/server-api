package gong.server_api.controller;
import gong.server_api.service.HospitalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/hospital")
public class HospitalController {

    @GetMapping("/list")
    public String hospitalGo(){
        return "/page/hospital/list";
    }
    private final HospitalService hospitalService;


    public HospitalController(HospitalService hospitalService) {
        log.info("HospitalController");
        this.hospitalService = hospitalService;
    }


}

