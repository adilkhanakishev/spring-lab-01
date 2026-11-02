# Lab 08: running and checking the application

The project requires Java 25, Maven (or `bash ./mvnw`) and PostgreSQL. For a disposable database, start PostgreSQL 16, for example:

```sh
docker run --name pg-springlab -e POSTGRES_DB=springlab \
  -e POSTGRES_USER=springlab -e POSTGRES_PASSWORD=springlab \
  -p 5432:5432 -d postgres:16
bash ./mvnw test
bash ./mvnw spring-boot:run
```

The default URL is `jdbc:postgresql://localhost:5432/springlab`; credentials are `springlab` / `springlab`. The development profile uses port 8081; the default profile uses port 8080. The lab uses `ddl-auto: create-drop`, which recreates and drops the schema. Use only a disposable database; tests also use this configuration.

Useful examples after startup:

```sh
curl -s 'http://localhost:8080/api/books?page=1&size=3&sort=year,desc'
curl -s 'http://localhost:8080/api/books/slice?page=0&size=3'
curl -s 'http://localhost:8080/api/demo/n-plus-one/without-fetch-join'
curl -s 'http://localhost:8080/api/demo/n-plus-one/with-fetch-join'
curl -s 'http://localhost:8080/api/demo/variant2/book-tags'
```

The integration tests measure SQL on the initial eight-book dataset: accessing authors lazily executes four statements, while the fetch join executes one. For a full three-book page the API executes three statements (page, count, and one batch to load tags); the slice executes two (slice and tags). A count query can be omitted when Spring Data can infer the total from a partial page. `BookTag` is the association entity required by variant 2, and stores `addedAt` along with both foreign keys.
