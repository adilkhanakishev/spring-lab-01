package kz.iitu.springlab.notify;

import org.springframework.stereotype.Component;

@Component("emailNotifier")
public class EmailNotifier implements Notifier {

    @Override
    public String send(String message) {
        System.out.println("EMAIL: " + message);
        return message;
    }

    @Override
    public String channel() {
        return "email";
    }
}