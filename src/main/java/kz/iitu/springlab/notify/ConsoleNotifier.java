package kz.iitu.springlab.notify;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ConsoleNotifier implements Notifier {

    @Override
    public void send(String message) {
        System.out.println(message);
    }
}
