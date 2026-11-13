package kz.iitu.springlab.lab09;
import java.math.BigDecimal;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class TransferService {
 private final AccountRepository repository;
 private final AuditService audit;
 public TransferService(AccountRepository repository,AuditService audit){this.repository=repository;this.audit=audit;}
 private Account account(Long id){return repository.findById(id).orElseThrow(()->new IllegalArgumentException("Account not found: "+id));}
 @Transactional
 public void transfer(Long fromId,Long toId,BigDecimal amount){
  if(fromId.equals(toId))throw new IllegalArgumentException("Accounts must differ");
  Account from=account(fromId),to=account(toId);
  from.debit(amount);to.credit(amount);
  // Both managed entities are persisted by dirty checking at commit.
 }
 @Transactional
 public void failAfterDebit(Long id,BigDecimal amount){
  account(id).debit(amount);repository.flush();
  throw new IllegalStateException("Simulated failure after flushed debit");
 }
 @Transactional
 public void checkedDefault(Long id,BigDecimal amount) throws IOException {
  account(id).debit(amount);throw new IOException("Checked exception uses default commit rule");
 }
 @Transactional(rollbackFor=IOException.class)
 public void checkedRollback(Long id,BigDecimal amount) throws IOException {
  account(id).debit(amount);repository.flush();throw new IOException("Explicit rollbackFor");
 }
 @Transactional
 public void outerFailure(boolean independent){
  account(1L).debit(new BigDecimal("10.00"));
  if(independent) audit.requiresNew("REQUIRES_NEW audit");
  else audit.required("REQUIRED audit");
  throw new IllegalStateException("Outer operation failed");
 }
}
