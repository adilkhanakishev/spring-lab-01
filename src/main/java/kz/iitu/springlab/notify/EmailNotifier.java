package kz.iitu.springlab.notify;

import org.springframework.stereotype.Component;

@Component("emailNotifier")
public class EmailNotifier implements Notifier {

    @Override
    public void send(String message) {
        System.out.println("EMAIL: " + message);
    }
}
