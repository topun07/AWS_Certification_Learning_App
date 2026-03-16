✅ Current Objectives Completed
Full-Stack Authentication: Implemented a secure Email/Password authentication flow using Spring Security and Vue.js.

Database Persistence: Successfully migrated from a volatile in-memory H2 database to a file-based H2 storage (examdb.mv.db), ensuring user accounts and scores survive server restarts.

JPA Repository Layer: Architected a robust Data Access Object (DAO) pattern using Spring Data JPA, including custom query methods for email-based lookups.

Frontend-Backend Integration: Established a clean API communication layer using the Fetch API with reactive state management in Vue 3 (Composition API).

Hall of Fame Engine: Built the backend logic to aggregate top scores by user, ready for real-time leaderboard rendering.

🧠 Challenges Faced (The "Challenge" Log)
The "In-Memory" Wipe: * Challenge: Losing all test data every time a code change triggered a backend restart.

Solution: Configured spring.datasource.url to use jdbc:h2:file, providing a persistent local data store.

Dependency Management: * Challenge: Encountered Cannot load driver class: org.h2.Driver runtime errors.

Solution: Identified missing H2 dependencies in pom.xml and performed a manual Maven sync to align the IDE with the project runtime.

Java Type Safety & Optionals: * Challenge: Faced incompatible types errors when mapping database entities to Controller responses.

Solution: Refactored UserRepository to utilize java.util.Optional<User>, enforcing better null-safety and preventing potential NullPointerExceptions during login.

Schema Evolution: * Challenge: Transitioned from a "Nickname" system to a "Unique Email" system, which initially caused database conflicts with existing records.

Solution: Performed a clean data migration by purging legacy DB files and redefining the @Column(unique = true) constraints in the User entity.

🛠️ Next Up on the Roadmap
[ ] Leaderboard UI: Finalize the CSS/Vue loop to display the "Hall of Fame" data currently stored in the backend.

[ ] Score Correlation: Ensure quiz results are linked to the currentUser.id upon completion.

[ ] UI Personalization: Implement a conditional header to display the user's name and a logout function.