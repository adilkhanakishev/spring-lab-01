package kz.iitu.springlab.web;

import kz.iitu.springlab.NotificationService;
import kz.iitu.springlab.lifecycle.LifecycleDemo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final LifecycleDemo lifecycle;

    public NotificationController(
            NotificationService notificationService,
            LifecycleDemo lifecycle) {

        this.notificationService = notificationService;
        this.lifecycle = lifecycle;
    }

    @GetMapping("/api/notify")
    public String notify(@RequestParam String message) {
        notificationService.notifyUser(message);
        return "Notification sent: " + message;
    }

    @GetMapping("/api/lifecycle")
    public List<String> lifecycle() {
        return lifecycle.events();
    }
}