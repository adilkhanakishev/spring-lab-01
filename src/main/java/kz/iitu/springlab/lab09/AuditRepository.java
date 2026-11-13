package kz.iitu.springlab.lab09;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AuditRepository extends JpaRepository<AuditRecord,Long>{}
