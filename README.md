🏗️ Tech Stack
Frontend: Vue.js 3 (Composition API), HTML5, Tailwind CSS

Backend: Java 24, Spring Boot 3

Security: Spring Security, JSON Web Tokens (JWT), BCrypt Password Hashing

Database: H2 Database (File-Based Persistence), Spring Data JPA, Hibernate

Tools: IntelliJ IDEA, Maven, Chrome DevTools

✅ Current Objectives Completed
Enterprise JWT Authentication: Upgraded from a basic login to a highly secure authentication flow. The backend now issues encrypted JSON Web Tokens (JWT) that the Vue frontend stores and attaches to protected API calls (acting as a "VIP Pass").

Gamification Engine: Engineered a custom AppUser entity that actively tracks xp, rankTitle, currentStreak, and lastStudyDate.

Database Persistence & Security: Successfully migrated from a volatile in-memory DB to a file-based H2 storage (awshub_db.mv.db). Passwords are never stored in plain text; they are actively hashed using BCrypt.

Modernized JPA Repository Layer: Architected a robust Data Access Object (DAO) pattern using Spring Data JPA. Utilized java.util.Optional<AppUser> for strict null-safety and crash prevention.

Frontend-Backend API Integration: Established a clean API communication layer using the Fetch API. Built an interactive, dynamic UI Modal in Vue that handles user registration, login, and error catching.

Real-Time Hall of Fame: Built the backend logic and custom Native SQL queries to aggregate top scores by user, rendering a real-time leaderboard on the frontend.

🧠 Challenges Faced & Overcome (The "Challenge" Log)
1. The "Ghost Files" Refactor & Cache Wars

Challenge: Transitioning from an older User architecture to a secure AppUser architecture caused severe clashes. Legacy code, cached IDE files, and browser caches caused "Frankenstein" payloads (sending old variables like passwordhash alongside new ones).

Solution: Performed a systematic purge of legacy code. Cleaned the Vue reactive state variables, executed Hard Refreshes to break browser caching, and ruthlessly deleted orphaned Java classes to prevent IntelliJ auto-import conflicts.

2. Database Schema Evolution & DDL Crashes

Challenge: Upgrading the database blueprint to include primitives (like int xp) caused Spring Boot to throw 500 Internal Server Errors because Hibernate couldn't retroactively apply NOT NULL columns to old test data. We also faced Hibernate duplicate column mapping errors (current_streak vs currentStreak).

Solution: Cleaned up rogue @Column annotations in the entity models and performed a controlled database wipe (dropping the data folder), allowing Hibernate to auto-generate a fresh, perfectly mapped schema.

3. Native SQL Desync

Challenge: After migrating to the new username-only system, the Leaderboard crashed (Column "U.FULL_NAME" not found) because the custom @Query was still searching for a deprecated column.

Solution: Refactored the native SQL query in the ExamHistoryRepository to dynamically target the new username column while maintaining the alias expected by the Vue frontend.

4. Dependency & Type Safety Integrity

Challenge: Encountered missing H2 driver errors and incompatible type mapping errors during early development.

Solution: Performed a manual Maven sync to align the IDE with the project runtime and enforced strict Java typing using modern Optional wrappers instead of manual null checks.

🛠️ Next Up on the Roadmap
[ ] Cloud Deployment: Containerize the application and deploy the Spring Boot backend and Vue frontend to the cloud (AWS/Render).

[ ] User Dashboard: Build a dedicated profile page where users can view their current rank, XP progress bar, and study streak calendar.

[ ] Expanded Module Library: Integrate more robust AWS question banks and dynamically track which specific modules a user has mastered.