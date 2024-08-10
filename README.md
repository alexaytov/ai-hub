# ai-hub

## Requirements
### 12 Web Pages <span style="color:green;">&#x2714;</span>
1.  Home
2. Chat Models List
3. Chat Models Created
4. System Messages List
5. System Messages Create
6. Agents List
7. Agents Create
8. Chat List
9. Chat
10. User Login
11. User Register
12. User Settings

### 5 Controllers <span style="color:green;">&#x2714;</span>
1. Agents Controller
2. AI Model Controller
3. Chat tController
4. System Messages Controller
5. Users Controller

### 1 REST Controller <span style="color:green;">&#x2714;</span>

Currently all controllers are rest controllers.

### 5 Services <span style="color:green;">&#x2714;</span>
1. Chat Service
2. Model Service
3. User Service
4. Agent Service
5. System Messages Service

### 5 Repositories <span style="color:green;">&#x2714;</span>
1. Agent Repository
2. Chat Repository
3. Message Type Repository
4. Model Repository
5. Model Type Repository
6. System Message Repository
7. User Repository
8. User Role Repository

### Implement a separate service in a distinct project, which will be consumed by your main project through a REST API TODO
- This service must include at least four endpoints: GET, POST, DELETE, and PUT or PATCH.

### Database
- Use MySQL, Oracle, PostgreSQL, or MariaDB. <span style="color:green;">&#x2714;</span>
- Access the database using Spring Data. <span style="color:green;">&#x2714;</span>
- Use Hibernate or any other JPA provider. <span style="color:green;">&#x2714;</span>

### Security
- Use standard Spring Security for managing users and roles. <span style="color:green;">&#x2714;</span>
- Roles: user and administrator. <span style="color:green;">&#x2714;</span>
- Ensure role management is secured and error-safe. <span style="color:green;">&#x2714;</span>
- Users and administrators should be able to edit their usernames. <span style="color:green;">&#x2714;</span>

### Validation and Error Handling TODO
- Implement client-side and server-side validation. <span style="color:green;">&#x2714;</span>
- Display appropriate validation messages to the user. <span style="color:green;">&#x2714;</span>

### Internationalization (i18n) TODO
- Support multiple languages.

### Scheduling TODO
- Implement scheduled jobs affecting the application, e.g., once/twice a day.

### Mapping
- Use MapStruct, ModelMapper or another mapping library <span style="color:green;">&#x2714;</span>

### Testing TODO
- Write Unit & Integration tests for logic, services, repository query methods, helpers, etc.
- Achieve at least 70% coverage on business logic (Line Coverage).

### Front-end Design
- Ensure a visually appealing and intuitive front-end design for an good user experience (UX). <span style="color:green;">&#x2714;</span>
- Use the Thymeleaf template engine or a JavaScript framework/library such as React, Angular, or Vue.js, consuming REST services from a Web API. <span style="color:green;">&#x2714;</span>

### Additional Requirements

- Object-Oriented Design and Best Practices
  - Data encapsulation <span style="color:green;">&#x2714;</span>
  - Proper exception handling <span style="color:green;">&#x2714;</span>
  - Appropriate use of inheritance, abstraction, and polymorphism <span style="color:green;">&#x2714;</span>
  - Strong cohesion and loose coupling principles <span style="color:green;">&#x2714;</span>
  - Well-formatted and structured code with readable identifiers <span style="color:green;">&#x2714;</span>
  - Thin controllers concept <span style="color:green;">&#x2714;</span>
- User Interface (UI)
  - Ensure a well-designed UI <span style="color:green;">&#x2714;</span>
- User Experience (UX)
  - Ensure a good UX <span style="color:green;">&#x2714;</span>

### Source Control
- Submit a link to your public source code repository. <span style="color:green;">&#x2714;</span>
- Commit on at least 5 different days. <span style="color:green;">&#x2714;</span>
- Make at least 20 commits. <span style="color:green;">&#x2714;</span>

### Bonuses
- Use Angular for the front-end. <span style="color:green;">&#x2714;</span>
