package kz.iitu.springlab.notify;

import org.springframework.stereotype.Component;

@Component("noop")
public class NoopNotifier implements Notifier {

    @Override
    public String send(String message) {
        // intentionally does nothing
        return message;
    }

    @Override
    public String channel() {
        return "noop";
    }
}