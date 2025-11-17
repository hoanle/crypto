# Crypto Currency App

An Android application for browsing and managing cryptocurrency and fiat currency data. Built with Jetpack Compose, following Clean Architecture principles and MVVM pattern.

## 📱 Overview

Crypto Currency App is an Android application that allows users to:

- View cryptocurrency and fiat currency lists
- Search and filter currencies
- Manage local database (insert/clear data)
- Browse currencies with efficient pagination

## ✨ Features

- **Currency Management**
  - View cryptocurrencies (e.g., Bitcoin, Ethereum)
  - View fiat currencies (e.g., USD, EUR, SGD)
  - Combined view of both currency types

- **Search & Filter**
  - Real-time search functionality
  - Search by currency name or symbol
  - Smart matching algorithms

- **Database Operations**
  - Seed database from JSON asset files
  - Clear database functionality
  - Batch insert with transaction support

- **Modern UI**
  - Material Design 3
  - Jetpack Compose UI
  - Paging3 for efficient list rendering
  - Empty states and loading indicators
  - Smooth navigation

## 🏗️ Architecture

The project follows **Clean Architecture** with clear separation of concerns:

### Architecture Layers

1. **Presentation Layer**
   - `CurrencyViewModel` - Manages currency list state
   - `DemoViewModel` - Handles database operations
   - `CurrencyListScreen` - Compose UI components
   - `DemoActivity` - Main activity with navigation

2. **Domain Layer**
   - `CurrencyInfo` - Domain model
   - `CryptoRepository` / `FiatRepository` / `CombinedCurrencyRepository` - Repository interfaces
   - `GetAllCryptosUseCase` / `GetAllFiatsUseCase` - Business logic

3. **Data Layer**
   - `CryptoRepositoryImpl` / `FiatRepositoryImpl` / `CombinedCurrencyRepositoryImpl` - Repository implementations
   - `CryptoDao` / `FiatDao` / `CombinedCurrencyDao` - Room database access objects
   - `CryptoEntity` / `FiatEntity` - Database entities
   - `CombinedCurrencyView` - Database view for UNION queries
   - `SeedDatabase` - Data seeding from JSON assets
   - Room PagingSource for efficient pagination

## 🛠️ Tech Stack

### Core Technologies
- **Kotlin** 2.0.21 - Modern Kotlin with latest features
- **Android Gradle Plugin** 8.13.1 - Latest build tools
- **Jetpack Compose** BOM 2024.09.00 - Modern declarative UI
- **Material Design 3** - Latest Material components

### Architecture Components
- **Hilt** 2.51.1 - Dependency injection
- **Room** 2.6.1 - Local database
- **Paging3** 3.3.0 - Efficient pagination
- **Kotlin Coroutines** 1.7.3 - Asynchronous programming
- **Flow** - Reactive data streams

### Testing
- **JUnit** 4.13.2 - Unit testing framework
- **MockK** 1.13.10 - Mocking library
- **Turbine** 1.1.0 - Flow testing
- **Hilt Android Testing** - Integration testing
- **Compose UI Testing** - UI component testing

## 📁 Project Structure

```
app/src/main/java/com/example/demoactivity/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt              # Room database
│   │   ├── CryptoDao.kt                # Crypto data access
│   │   ├── FiatDao.kt                  # Fiat data access
│   │   ├── CombinedCurrencyDao.kt      # Combined currency DAO (UNION queries)
│   │   ├── CombinedCurrencyView.kt     # Database view for combined currencies
│   │   ├── CryptoEntity.kt              # Crypto entity
│   │   ├── FiatEntity.kt               # Fiat entity
│   │   └── DatabaseMigrations.kt       # Database migrations
│   ├── repository/
│   │   ├── CryptoRepositoryImpl.kt     # Crypto repository implementation
│   │   ├── FiatRepositoryImpl.kt       # Fiat repository implementation
│   │   └── CombinedCurrencyRepositoryImpl.kt  # Combined currency repository
│   └── seed/
│       └── SeedDatabase.kt              # Data seeding from JSON assets
├── domain/
│   ├── model/
│   │   └── CurrencyInfo.kt              # Domain model
│   ├── repository/
│   │   ├── CryptoRepository.kt          # Crypto repository interface
│   │   ├── FiatRepository.kt            # Fiat repository interface
│   │   └── CombinedCurrencyRepository.kt # Combined currency repository interface
│   └── usecase/
│       ├── GetAllCryptosUseCase.kt      # Get cryptos use case
│       └── GetAllFiatsUseCase.kt        # Get fiats use case
├── presentation/
│   ├── CurrencyViewModel.kt              # Currency list ViewModel
│   ├── DemoViewModel.kt                  # Database operations ViewModel
│   └── CurrencyListScreen.kt            # Currency list Compose UI
├── di/
│   ├── DatabaseModule.kt                # Database dependency injection
│   └── RepositoryModule.kt              # Repository dependency injection
└── ui/
    └── theme/                            # Material Design 3 theme
```

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK** 17 or later
- **Android SDK** API 24+ (minimum), API 36 (target)
- **Gradle** 8.0+ (wrapper included)

### Setup Instructions

1. **Clone the repository**

2. **Open in Android Studio**

3. **Sync Gradle**

4. **Run the app**

### Build Configuration

The project uses Gradle Version Catalog (`gradle/libs.versions.toml`) for dependency management.

**Key Configuration:**
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 15)
- **Compile SDK**: 36
- **Java Version**: 11

## 🧪 Testing

### Running Tests

**Unit Tests:**
```
./gradlew test
```

**Instrumented Tests:**
```
./gradlew connectedAndroidTest
```

**All Tests:**
```
./gradlew test connectedAndroidTest
```

### Test Coverage

The project includes comprehensive test coverage:

- **Unit Tests**: 41 tests
  - UseCase tests (4 tests)
    - GetAllCryptosUseCaseTest (2 tests)
    - GetAllFiatsUseCaseTest (2 tests)
  - Repository tests (25 tests)
    - CryptoRepositoryImplTest (9 tests)
    - FiatRepositoryImplTest (9 tests)
    - CombinedCurrencyRepositoryImplTest (7 tests)
  - ViewModel tests (12 tests)
    - CurrencyViewModelTest (3 tests)
    - DemoViewModelTest (9 tests)

- **Instrumented Tests**: 24 tests
  - DAO tests (18 tests)
    - CryptoDaoTest (9 tests)
    - FiatDaoTest (9 tests)
  - Compose UI tests (6 tests)
    - CurrencyListScreenTest (6 tests)

**Total**: 65 tests across 10 test classes

### Test Structure

```
app/src/
├── test/                                    # Unit tests
│   └── java/com/example/demoactivity/
│       ├── domain/usecase/
│       │   ├── GetAllCryptosUseCaseTest.kt
│       │   └── GetAllFiatsUseCaseTest.kt
│       ├── data/repository/
│       │   ├── CryptoRepositoryImplTest.kt
│       │   ├── FiatRepositoryImplTest.kt
│       │   └── CombinedCurrencyRepositoryImplTest.kt
│       └── presentation/
│           ├── CurrencyViewModelTest.kt
│           └── DemoViewModelTest.kt
└── androidTest/                             # Instrumented tests
    └── java/com/example/demoactivity/
        ├── data/local/
        │   ├── CryptoDaoTest.kt
        │   └── FiatDaoTest.kt
        ├── presentation/
        │   └── CurrencyListScreenTest.kt
        └── di/
            └── TestDatabaseModule.kt
```

## 🏗️ Building

### Debug Build
```
./gradlew assembleDebug
```

### Release Build
```
./gradlew assembleRelease
```

### Clean Build
```
./gradlew clean build
```

## 📊 CI/CD

The project includes GitHub Actions workflow (`.github/workflows/ci.yml`) that:

- Runs unit tests on every push/PR
- Runs instrumented tests on Android emulator
- Builds debug and release APKs
- Runs lint checks

**Workflow Jobs:**
1. **test-and-build** - Unit tests and APK builds
2. **instrumented-tests** - Android instrumented tests

## 📦 Dependencies

All dependencies are managed via Gradle Version Catalog. Key dependencies:

- **Compose**: UI, Material3, Navigation
- **Hilt**: Dependency injection
- **Room**: Database with KTX support
- **Paging3**: Pagination library
- **Coroutines**: Async operations
- **Testing**: JUnit, MockK, Turbine, Espresso

See `gradle/libs.versions.toml` for complete dependency list.

## 📄 License

This project is a demonstration application. 

## 📚 Additional Documentation

- **Test Cases**: See `test_cases.txt` for detailed test descriptions
- **Test Coverage**: See `TEST_COVERAGE_REPORT.md` for coverage analysis

## 🐛 Known Issues

None currently. 

## 🔮 Future Enhancements

## 👨‍💻 Development

### Code Quality
- ✅ No linter errors
- ✅ No deprecated APIs
- ✅ Follows Kotlin conventions
- ✅ Clean Architecture compliance
- ✅ Comprehensive test coverage

### Best Practices
- ✅ Dependency Injection with Hilt
- ✅ Reactive programming with Flow
- ✅ Proper error handling
- ✅ String resources externalization
- ✅ Logging for debugging

---

