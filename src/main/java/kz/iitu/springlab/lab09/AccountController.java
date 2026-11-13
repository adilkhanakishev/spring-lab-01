package kz.iitu.springlab.lab09;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
@RestController @RequestMapping("/api/accounts")
public class AccountController {
 private final AccountRepository repository;private final TransferService service;
 public AccountController(AccountRepository repository,TransferService service){this.repository=repository;this.service=service;}
 public record Transfer(Long fromId,Long toId,BigDecimal amount){}
 @GetMapping public List<Account> list(){return repository.findAll();}
 @PostMapping("/transfer") public Map<String,String> transfer(@RequestBody Transfer request){
  if(request.fromId()==null||request.toId()==null)throw new IllegalArgumentException("Both account ids are required");
  service.transfer(request.fromId(),request.toId(),request.amount());return Map.of("result","committed");
 }
 @ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class})
 public ResponseEntity<Map<String,String>> invalid(RuntimeException ex){return ResponseEntity.badRequest().body(Map.of("error",ex.getMessage()));}
 @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
 public ResponseEntity<Map<String,String>> conflict(RuntimeException ex){return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","Concurrent update: refresh account data and try again"));}
}
