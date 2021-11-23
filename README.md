
## Requirements

* Java 17
* Docker

## Build

```bash
./gradlew clean build
```

## BDD Cucumber tests

```bash
./gradlew cucumber
```

## Run Locally

```bash
docker run --name some-postgres -p 5432:5432 -e POSTGRES_PASSWORD=testpassword -e POSTGRES_USER=testuser -e POSTGRES_DB=school-db -d postgres
./gradlew bootRun
```

## Comments on Hibernate
* In general is a good tool to speed up development, although it can get messy really quickly, the best way  to go about it is to keep it contained in the data access layer and not let leak into other parts of the application e.g: web layer.
* Performance can be affected if relationships are not handled carefully, especially if sessions are kept open and multiple queries are being executed behind the scenes without our control, for that case is better to write your own queries with any option available, `JPQL` `Native queries` `Criteria API` to mention a few also the use of `EntityGraphs` can mitigate this problem
* Another point in performance is to make use of the 2nd level cache it can be backed by `Ehcache` `Hazelcast` `Redis` or whatever is available in the infrastructure
* Also in some cases prefer the use of `Optimistic Locks` if it makes sense for the use case. 