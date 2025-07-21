# Ragebait Monorepo

A fullstack project featuring a Kotlin Spring Boot backend and a Flutter mobile frontend for generating and managing ragebait content.

---

## Project Structure

```
.
├── backend/    # Kotlin Spring Boot backend
├── mobile/     # Flutter mobile frontend
├── README.md   # Project documentation (this file)
├── .gitignore  # Git ignore rules for the whole monorepo
└── ...         # Build scripts, configs, etc.
```

---

## Getting Started

### Backend (Kotlin/Spring Boot)

1. **Configure Secrets:**
   - Copy `backend/src/main/resources/application.yml.template` to `application.yml` and fill in your secrets.
   - Ensure you have a valid service account JSON if using Gemini/Google APIs.
2. **Run the Backend:**
   ```sh
   cd backend
   ./gradlew bootRun
   ```
3. **API Docs:**
   - Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - Prometheus metrics: [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)

### Mobile (Flutter)

1. **Install dependencies:**
   ```sh
   cd mobile
   flutter pub get
   ```
2. **Run the app:**
   ```sh
   flutter run
   ```
3. **Run tests & check coverage:**
   ```sh
   flutter test --coverage
   # See coverage/html/index.html for report
   ```

---

## Contributing

1. Fork the repository and create your branch from `main`.
2. Follow the code style and commit message guidelines.
3. Add tests for new features and bug fixes.
4. Open a pull request and describe your changes.

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details. 