package kz.iitu.springlab.lab09;

import jakarta.persistence.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.*;
import org.springframework.transaction.support.TransactionTemplate;
import static org.assertj.core.api.Assertions.*;

@org.springframework.test.context.ActiveProfiles("lab09")
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:tests;DB_CLOSE_DELAY=-1;NON_KEYWORDS=YEAR", "spring.jpa.show-sql=false"})
class TransactionLabTest {
 @Autowired TransferService service;
 @Autowired AccountRepository accounts;
 @Autowired AuditRepository audits;
 @Autowired JdbcTemplate jdbc;
 @Autowired PlatformTransactionManager manager;
 @Autowired EntityManagerFactory emf;

 @BeforeEach void reset(){
  jdbc.update("DELETE FROM audit_records");
  jdbc.update("UPDATE accounts SET balance=CASE id WHEN 1 THEN 1000.00 ELSE 500.00 END, version=0,status='ACTIVE'");
 }
 BigDecimal balance(long id){return accounts.findById(id).orElseThrow().getBalance();}
 void unchanged(){assertThat(balance(1)).isEqualByComparingTo("1000.00");assertThat(balance(2)).isEqualByComparingTo("500.00");}
 void evidence(String text){System.out.println("LAB09 | "+text);}
 @Test void successfulTransfer(){
  service.transfer(1L,2L,new BigDecimal("100.00"));
  assertThat(balance(1)).isEqualByComparingTo("900.00");
  assertThat(balance(2)).isEqualByComparingTo("600.00");
  assertThat(accounts.findById(1L).orElseThrow().getVersion()).isEqualTo(1L);
  evidence("TRANSFER Alice=900.00 Bob=600.00 total=1500.00");
 }
 @Test void insufficientFunds(){
  assertThatThrownBy(()->service.transfer(1L,2L,new BigDecimal("2000.00"))).isInstanceOf(IllegalStateException.class);
  unchanged();evidence("INSUFFICIENT FUNDS rejected; Alice=1000.00 Bob=500.00");
 }
 @Test void runtimeFailureRollsBackFlushedDebit(){
  assertThatThrownBy(()->service.failAfterDebit(1L,new BigDecimal("100.00"))).isInstanceOf(IllegalStateException.class);
  unchanged();assertThat(accounts.findById(1L).orElseThrow().getVersion()).isZero();
  evidence("ROLLBACK after SQL flush: Alice=1000.00 Bob=500.00 version=0");
 }
 @Test void checkedExceptionCommitsByDefault(){
  assertThatThrownBy(()->service.checkedDefault(1L,new BigDecimal("100.00"))).isInstanceOf(IOException.class);
  assertThat(balance(1)).isEqualByComparingTo("900.00");
  evidence("CHECKED default: debit committed, Alice=900.00 (intentional unsafe demonstration)");
 }
 @Test void checkedExceptionRollbackFor(){
  assertThatThrownBy(()->service.checkedRollback(1L,new BigDecimal("100.00"))).isInstanceOf(IOException.class);
  unchanged();evidence("CHECKED rollbackFor=IOException: Alice=1000.00");
 }
 @Test void requiredSharesRollback(){
  assertThatThrownBy(()->service.outerFailure(false)).isInstanceOf(IllegalStateException.class);
  unchanged();assertThat(audits.count()).isZero();
  evidence("REQUIRED outer rollback: audits=0 Alice=1000.00");
 }
 @Test void requiresNewSurvivesRollback(){
  assertThatThrownBy(()->service.outerFailure(true)).isInstanceOf(IllegalStateException.class);
  unchanged();assertThat(audits.count()).isEqualTo(1);
  evidence("REQUIRES_NEW outer rollback: audits=1 Alice=1000.00");
 }
 @Test void invalidTransfersDoNotChangeBalances(){
  for(String amount:List.of("0","-1","0.001")){
   assertThatThrownBy(()->service.transfer(1L,2L,new BigDecimal(amount))).isInstanceOf(IllegalArgumentException.class);
  }
  assertThatThrownBy(()->service.transfer(1L,1L,BigDecimal.ONE)).isInstanceOf(IllegalArgumentException.class);
  assertThatThrownBy(()->service.transfer(1L,99L,BigDecimal.ONE)).isInstanceOf(IllegalArgumentException.class);
  unchanged();
 }
 TransactionTemplate tx(int isolation){
  TransactionTemplate t=new TransactionTemplate(manager);
  t.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
  t.setIsolationLevel(isolation);t.setTimeout(10);return t;
 }
 void isolation(int level,String name,String expected){
  // JDBC SELECTs bypass Hibernate's first-level cache. The inner independent
  // transaction commits while the suspended reader transaction remains open.
  tx(level).executeWithoutResult(status->{
   BigDecimal first=jdbc.queryForObject("SELECT balance FROM accounts WHERE id=1",BigDecimal.class);
   tx(TransactionDefinition.ISOLATION_READ_COMMITTED).executeWithoutResult(writer->
    jdbc.update("UPDATE accounts SET balance=balance+100,version=version+1 WHERE id=1"));
   BigDecimal second=jdbc.queryForObject("SELECT balance FROM accounts WHERE id=1",BigDecimal.class);
   assertThat(first).isEqualByComparingTo("1000.00");
   assertThat(second).isEqualByComparingTo(expected);
   evidence(name+" first="+first+" second="+second);
  });
  assertThat(balance(1)).isEqualByComparingTo("1100.00");
 }
 @Test void readCommittedSeesNewCommit(){isolation(TransactionDefinition.ISOLATION_READ_COMMITTED,"READ_COMMITTED","1100.00");}
 @Test void repeatableReadKeepsSnapshot(){isolation(TransactionDefinition.ISOLATION_REPEATABLE_READ,"REPEATABLE_READ","1000.00");}
 @Test void optimisticLockRejectsStaleCopy(){
  EntityManager first=emf.createEntityManager(),second=emf.createEntityManager();
  try{
   first.getTransaction().begin();second.getTransaction().begin();
   Account a=first.find(Account.class,1L),stale=second.find(Account.class,1L);
   assertThat(a.getVersion()).isEqualTo(stale.getVersion());
   a.credit(new BigDecimal("10.00"));first.getTransaction().commit();
   stale.credit(new BigDecimal("20.00"));
   assertThatThrownBy(()->second.getTransaction().commit())
    .isInstanceOf(RollbackException.class).hasRootCauseInstanceOf(org.hibernate.StaleStateException.class);
   assertThat(balance(1)).isEqualByComparingTo("1010.00");
   assertThat(accounts.findById(1L).orElseThrow().getVersion()).isEqualTo(1L);
   evidence("OPTIMISTIC LOCK stale version=0 rejected; Alice=1010.00 version=1; no lost update");
  }finally{
   if(first.getTransaction().isActive())first.getTransaction().rollback();
   if(second.getTransaction().isActive())second.getTransaction().rollback();
   first.close();second.close();
  }
 }
 @Test void databaseVersion(){evidence("DATABASE "+jdbc.queryForObject("SELECT H2VERSION()",String.class));}
}

