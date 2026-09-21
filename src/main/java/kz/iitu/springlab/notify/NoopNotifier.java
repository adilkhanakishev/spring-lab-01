package kz.iitu.springlab.notify;

import org.springframework.stereotype.Component;

@Component
public class NoopNotifier implements Notifier {

    @Override
    public void send(String message) {
        // intentionally does nothing
    }
}