package kz.iitu.springlab.lab09;

import java.nio.file.Path;
import java.sql.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.*;

class MigrationLabTest {
 @TempDir Path directory;
 Flyway flyway(String url,String target){return Flyway.configure().dataSource(url,"sa","")
   .locations("classpath:db/migration").target(target).load();}
 @Test void emptyDatabaseRestartAndUpgrade() throws Exception {
  String url="jdbc:h2:file:"+directory.resolve("versioning").toAbsolutePath().toString().replace('\\','/')+";NON_KEYWORDS=YEAR";
  assertThat(flyway(url,"2").migrate().migrationsExecuted).isEqualTo(2);
  System.out.println("LAB09 | MIGRATION empty database: V1 and V2 applied");
  // File-backed DB is closed and reopened by fresh Flyway instances.
  assertThat(flyway(url,"2").migrate().migrationsExecuted).isZero();
  System.out.println("LAB09 | MIGRATION restart: 0 migrations executed");
  assertThat(flyway(url,"3").migrate().migrationsExecuted).isEqualTo(1);
  assertThat(flyway(url,"3").migrate().migrationsExecuted).isZero();
  try(Connection c=DriverManager.getConnection(url,"sa","");Statement s=c.createStatement()){
   try(ResultSet r=s.executeQuery("SELECT \"version\",\"description\",\"success\" FROM \"flyway_schema_history\" WHERE \"version\" IS NOT NULL ORDER BY \"installed_rank\"")){
    int count=0;while(r.next()){count++;assertThat(r.getBoolean(3)).isTrue();
     System.out.println("LAB09 | HISTORY V"+r.getString(1)+" "+r.getString(2)+" success="+r.getBoolean(3));}
    assertThat(count).isEqualTo(3);
   }
   try(ResultSet r=s.executeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.INDEXES WHERE INDEX_NAME='IDX_ACCOUNTS_OWNER_NAME'")){
    r.next();assertThat(r.getInt(1)).isEqualTo(1);
   }
  }
  assertThat(flyway(url,"3").validateWithResult().validationSuccessful).isTrue();
  System.out.println("LAB09 | MIGRATION upgrade: only V3 applied; index verified; checksums valid");
 }
}
