# CSNewsletter8 — MOV course project

## Short description

- Course: MOV (Modeling und Verification) at OTH Regensburg.
- Project: example project for Camunda 8 + Spring Boot that implements a simple newsletter subscription workflow (start processes, job workers, and basic persistence).
- This repository is a skeleton suitable for labs and for reuse in real-world projects with small additions (license, CI, production config).

## Key links

- Camunda Java + Spring quickstart: https://docs.camunda.io/docs/8.7/guides/getting-started-java-spring/
- Local self-managed runtime (c8run): https://docs.camunda.io/docs/8.7/self-managed/setup/deploy/local/c8run/

- [CSNewsletter8 — MOV course project](#csnewsletter8--mov-course-project)
  - [Short description](#short-description)
  - [Key links](#key-links)
  - [Quick start (development)](#quick-start-development)
    - [Actions:](#actions)
    - [Running tests](#running-tests)
    - [Important files \& locations](#important-files--locations)
- [License](#license)

## Quick start (development)

Prerequisites:

- Java 21, Maven, Git.
- A local Camunda 8 runtime [c8run](https://docs.camunda.io/docs/8.7/self-managed/setup/deploy/local/c8run/) — required for end-to-end worker subscription and process execution.

### Actions:

1. Start the local Camunda runtime (from the parent folder where `c8run` lives):
   - cd /path/to/c8run
   - ./c8run start
2. Build the app:
   - mvn clean package
3. Run the Spring Boot app:
   - mvn spring-boot:run
   - Default app port: 8081 (doesn't conflict with c8run REST on 8080)
4. Trigger a subscription (example):
   - POST http://localhost:8081/newsletter/subscribe/new-user
   - Body: {"email":"alice@example.com","topic":"Technology"}
5. Inspect Operate/Tasklist (c8run UI) to see started process instances and variables.

### Running tests

- Unit tests use an in-memory H2 DB and a mocked Camunda client:
  - mvn test

### Important files & locations

- Application entry: src/main/java/com/cyrils/csnewsletter/CsNewsletterApplication.java
- REST endpoints: src/main/java/com/cyrils/csnewsletter/NewsletterController.java
- Job workers: src/main/java/com/cyrils/csnewsletter/\*Worker.java (e.g., CheckUserExistsWorker)
- JPA entity & repo: src/main/java/com/cyrils/csnewsletter/User.java and UserRepository.java
- BPMN models and forms: bpmn/ (look for `newsletter_subscription`, `new-user-subscription-process`, and the start form)
- Test config: src/test/java/com/cyrils/csnewsletter/TestCamundaClientConfig.java (mocks CamundaClient for tests)

# License

- Apache License 2.0 (add `LICENSE` file to apply).
