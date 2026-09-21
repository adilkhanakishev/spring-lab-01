package kz.iitu.springlab.web;

import kz.iitu.springlab.scope.TicketOffice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ScopeController {

    private final TicketOffice ticketOffice;

    public ScopeController(TicketOffice ticketOffice) {
        this.ticketOffice = ticketOffice;
    }

    @GetMapping("/api/scope")
    public Map<String, Object> scope() {
        return ticketOffice.demo();
    }
}
