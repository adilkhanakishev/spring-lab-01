package kz.iitu.springlab.notify;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component("console")
@Primary
public class ConsoleNotifier implements Notifier {

    @Override
    public String send(String message) {
        System.out.println(message);
        return message;
    }

    @Override
    public String channel() {
        return "console";
    }
}