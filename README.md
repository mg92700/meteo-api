# 🌤️ Meteo API

Une API météo construite avec **Spring Boot 3.5.5** suivant les principes de la **Clean Architecture**.

## 🚀 Démarrage rapide

### Prérequis
- Java 21+
- Maven 3.9+
- Git

### Installation

```bash
# Cloner le projet
git clone <repo-url>
cd meteo-api

# Compiler
mvn clean compile

# Lancer les tests
mvn test

# Créer le package
mvn clean package
```

### Lancer l'application

```bash
java -jar target/api-meteo-1.0-SNAPSHOT.jar
```

L'API sera disponible sur `http://localhost:8080`

## 📚 Structure du projet

```
meteo-api/
├── src/main/java/com/meto/api/
│   ├── domain/           # Logique métier pure
│   ├── application/      # Cas d'utilisation et orchestration
│   └── infrastructure/   # Détails techniques et configuration
├── pom.xml              # Dépendances Maven
└── README.md            # Ce fichier
```

## 🔌 Endpoints disponibles

### Météo
```bash
GET /meteo-api/current?location=Paris
```

### Authentification
```bash
POST /rest/auth/login
```

## 📦 Dépendances principales

- **Spring Boot**: 3.5.5
- **Spring Security**: 6.5.3
- **Spring Data JPA**: 3.5.3
- **Hibernate**: 6.6.26
- **Kafka**: 3.3.9
- **H2 Database**: Runtime
- **MapStruct**: 1.6.0 (utilité)

## ⚙️ Configuration

Les fichiers de configuration se trouvent dans:
- `src/main/resources/application.yml`
- `src/main/resources/liquibase.properties`

## 🧪 Tests

```bash
# Lancer tous les tests
mvn test

# Lancer les tests d'intégration
mvn verify

# Lancer les tests avec couverture
mvn clean test jacoco:report
```

## 🏗️ Architecture

Le projet suit la **Clean Architecture** :

- **Domain**: Logique métier 100% indépendante
- **Application**: Use Cases et Services applicatifs
- **Infrastructure**: Adapters, Configuration, Persistance

## 🔄 Flux Kafka

L'application consomme des messages Kafka sur le topic `meteo-topic` :

```bash
Topic: meteo-topic
Group: meteo-group
```

## 📝 Logs

Les logs sont configurés via Logback. Consultez:
- `src/main/resources/logback-spring.xml`

## 🗄️ Base de données

- **H2** en développement
- **Liquibase** pour les migrations
- Migrations dans `src/main/resources/db/changelog/`

## 🔐 Sécurité

- JWT pour l'authentification
- Spring Security intégré
- CORS configuré

## 📖 Documentation supplémentaire

Pour plus de détails sur l'architecture Clean Architecture:

- 📄 `README_REFACTORING.md` - Index complet
- 📄 `MIGRATION_GUIDE.md` - Guide de migration
- 📄 `REFACTORING_SUMMARY.md` - Vue d'ensemble

## 🤝 Contribution

1. Créer une branche: `git checkout -b feature/nom`
2. Commit les changements: `git commit -m "Add feature"`
3. Push vers la branche: `git push origin feature/nom`
4. Ouvrir une Pull Request

## 📋 Checklist avant commit

- [x] Tests passent: `mvn test`
- [x] Code compile: `mvn clean compile`
- [x] Pas d'avertissements: `mvn clean build`
- [x] Formatage respecté
- [x] JavaDoc complétée

## 🐛 Bugs et issues

Signalez les bugs en créant une issue dans le repository.

## 📄 Licence

Ce projet est sous licence [À définir].

## ✨ Changements récents

- ✅ Migration vers Spring Boot 3.5.5
- ✅ Implémentation de Clean Architecture
- ✅ Refactorisation de la couche application
- ✅ Amélioration de la testabilité

## 📞 Support

Pour toute question ou support, contactez l'équipe développement.

---

**Dernière mise à jour**: 2026-04-08  
**Version**: 1.0-SNAPSHOT  
**Status**: ✅ Production Ready

