package kz.iitu.springlab.lab09;
import jakarta.persistence.*;
@Entity @Table(name="audit_records")
public class AuditRecord {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String message;
 protected AuditRecord(){}
 public AuditRecord(String message){this.message=message;}
 public Long getId(){return id;}
 public String getMessage(){return message;}
}
