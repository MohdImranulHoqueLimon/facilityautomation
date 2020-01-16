package frausas.mobilecomputing.facilityautomation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/iot/")
public class HomePageController {

    @GetMapping("facility")
    public String home() {
        return "home";
    }
}
