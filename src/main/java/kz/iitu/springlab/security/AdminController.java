package kz.iitu.springlab.security;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AppUserRepository userRepository;

    public AdminController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin";
    }
}
