package kz.iitu.springlab.web;

import kz.iitu.springlab.NotificationService;
import kz.iitu.springlab.lifecycle.LifecycleDemo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/api/lab2/notify")
    public String notify(@RequestParam String message) {
        notificationService.viaPrimary(message);
        return "Notification sent: " + message;
    }

    @GetMapping("/api/lab2/lifecycle")
    public List<String> lifecycle() {
        return lifecycle.events();
    }

    @GetMapping("/api/lab2/custom")
    public Map<String, String> custom(
            @RequestParam(defaultValue = "Hello") String text) {

        return Map.of(
                "channel", "reversed",
                "input", text,
                "output", notificationService.viaReversed(text)
        );
    }
}