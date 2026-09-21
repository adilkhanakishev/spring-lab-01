package kz.iitu.springlab;

import kz.iitu.springlab.notify.Notifier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final Notifier notifier;

    public NotificationService(
            @Qualifier("noopNotifier") Notifier notifier) {
        this.notifier = notifier;
    }

    public void notifyUser(String message) {
        notifier.send(message);
    }
}
