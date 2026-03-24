# TrackerConnect - Technical Solution Document

This document provides a comprehensive technical overview of the TrackerConnect application, covering architecture decisions, implementation details, and rationale behind key choices.

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Models & Data Structures](#models--data-structures)
3. [Layer-by-Layer Breakdown](#layer-by-layer-breakdown)
4. [Key Features Implementation](#key-features-implementation)
5. [Design Decisions](#design-decisions)
6. [Module Strategy: Single vs Multi-Module](#module-strategy-single-vs-multi-module)
7. [Data Flow & State Management](#data-flow--state-management)
8. [Testing Strategy](#testing-strategy)
9. [Future Improvements](#future-improvements)

---

## Architecture Overview

### Clean Architecture Principles

TrackerConnect follows **Clean Architecture** principles with clear separation of concerns across three primary layers:

```
┌─────────────────────────────────────────────────────┐
│             Presentation Layer (UI)                 │
│  • Jetpack Compose screens                          │
│  • ViewModels with StateFlow                        │
│  • UI state models                                  │
└────────────────┬────────────────────────────────────┘
                 │
                 │ UI Events & State
                 ▼
┌─────────────────────────────────────────────────────┐
│              Domain Layer                           │
│  • Repository interfaces                            │
│  • Business logic contracts                         │
│  • Domain models (Shipment, Status, etc.)           │
└────────────────┬────────────────────────────────────┘
                 │
                 │ Data Operations
                 ▼
┌─────────────────────────────────────────────────────┐
│               Data Layer                            │
│  • Repository implementations                       │
│  • Room Database (local cache)                      │
│  • JSON Data Source (mock API)                      │
│  • Entity-Domain mappers                            │
└─────────────────────────────────────────────────────┘
```

### Benefits of This Architecture

1. **Testability**: Each layer can be tested independently with mocks/fakes
2. **Maintainability**: Clear boundaries make code easier to understand and modify
3. **Scalability**: New features can be added without affecting existing code
4. **Flexibility**: Data sources can be swapped without changing business logic

---

## Models & Data Structures

### Domain Models (`core/model/`)

Domain models represent business entities used throughout the application. They are independent of any data source or UI framework.

#### Shipment

The core domain model representing a tracked shipment.

**Design Decisions**:
- Uses `@Serializable` for JSON parsing and navigation
- `isFavorite` is local-only, not from API
- `checkpoints` is nullable (only available in details view)
- Immutable data class for thread-safety

#### ShipmentStatus

Enum representing possible shipment states.

**Design Decision**: Used property instead of function for display name
- More idiomatic Kotlin
- Direct property access: `status.displayName` vs `status.getDisplayName()`

#### Location

#### Checkpoint

Represents a tracking event in the shipment's journey.

#### Result Wrapper

Generic wrapper for operation results with loading and error states.

**Benefits**:
- Type-safe error handling
- Eliminates exceptions in Flow
- Built-in loading state

### Data Models (`core/data/local/entity/`)

Data models represent database entities. They are separate from domain models to maintain layer independence.

#### ShipmentEntity

Room entity for persisting shipments locally.

**Design Decisions**:
- Flattened structure (no nested objects) for Room compatibility
- Separate fields for origin/destination instead of Location objects
- Checkpoints not persisted (loaded from JSON on-demand)
- Snake_case column names for SQL convention

#### Entity-Domain Mappers

Extension functions convert models between layers.

### UI State Models

#### ShipmentsUiState

State model for the shipments list screen.

**Key Design**: Filtering happens in computed property, not database
- **Benefit**: Instant filtering without DB queries
- **Trade-off**: Requires all shipments in memory (acceptable for typical usage)

#### ShipmentDetailsUiState

State model for shipment details screen.

**Design**: Sealed interface for mutually exclusive states
- Loading OR Success OR Error (never multiple simultaneously)
- Forces exhaustive when expressions in UI

---

## Layer-by-Layer Breakdown

### 1. Presentation Layer (`feature/`)

**Purpose**: Handle user interactions and render UI

**Components**:

#### ShipmentsScreen (`feature/shipments/`)

**ViewModel**: `ShipmentsViewModel`
- Manages list of tracked shipments
- Handles search query and favorites filter in-memory
- Exposes `ShipmentsUiState` via `StateFlow`

**Decision**: Load from DB once, filter in UI state
- Search and favorites filtering happens client-side via computed property
- No database queries on every keystroke
- Instant UI updates

#### ShipmentDetailsScreen (`feature/details/`)

**ViewModel**: `ShipmentDetailsViewModel`
- Loads shipment details with checkpoints from JSON
- Uses `SavedStateHandle` to receive `shipmentId` from navigation

**Navigation**: Type-safe routes using `@Serializable`

### 2. Domain Layer (`core/domain/`)

**Purpose**: Define business rules and contracts

#### Repository Interface (`core/domain/repository/ShipmentRepository.kt`)

**Design**: Repository returns `Flow<Result<T>>` for reactive updates
- Flow enables automatic UI updates when database changes
- Result wrapper provides type-safe error handling

### 3. Data Layer (`core/data/`)

**Purpose**: Manage data sources and persistence

#### Repository Implementation (`core/data/repository/ShipmentRepositoryImpl.kt`)

**Responsibilities**:
1. Coordinate between JSON source and Room database
2. Map between entities and domain models
3. Handle data synchronization

**Key Methods**:

**getShipments()**: Load all tracked shipments from database

**addTracking()**: Validate against JSON, then save to database

**refreshShipments()**: Sync tracked shipments with JSON

**Decision**: Preserve user customizations during sync
- User's custom title preserved
- Favorite status maintained
- Only API-driven fields updated (status, location, ETA)

#### Local Database (`core/data/local/`)

**Room Database**: `TrackerDatabase.kt`

**DAO**: `ShipmentDao.kt`

**Key Decision**: DAO returns `Flow` for reactive updates
- Room automatically emits when database changes
- UI updates automatically without manual refresh

#### Data Source (`core/data/source/ShipmentDataSource.kt`)

**Purpose**: Read shipment data from JSON assets (mock API)

**Decision**: JSON files act as "remote API"
- `shipments.json`: All available shipments
- `shipment_<id>.json`: Detailed shipment with checkpoints
- Simulates real-world API structure

---

## Key Features Implementation

### 1. Add Tracking Workflow

```
User enters tracking number
         ↓
ViewModel validates input
         ↓
Repository.addTracking()
         ↓
Check if exists in DB (prevent duplicates)
         ↓
Search shipments.json for tracking number
         ↓
   Found? ──No──> Return Error("Not found")
     │
     Yes
     ↓
Save to Room database
     ↓
Room emits Flow update
     ↓
ViewModel updates allShipments
     ↓
UI recomposes with new shipment
```

### 2. Search & Filter Implementation

**Decision**: Filter in-memory, not in database

**Rejected Approach** (Database queries):

**Chosen Approach** (In-memory filtering):

**Benefits**:
- Instant filtering without database overhead
- No debouncing needed (local filtering is instantaneous)
- Simpler code

**Trade-off**: Requires loading all shipments into memory (acceptable for typical usage)

### 3. Favorites Toggle

```
User taps favorite icon
         ↓
ViewModel.toggleFavorite(id, isFavorite)
         ↓
Repository updates database
         ↓
Room emits updated Flow
         ↓
ViewModel receives updated allShipments
         ↓
UI recomposes (heart icon + filter updates)
```

**No manual refresh needed**: Room's Flow observes database changes

### 4. Refresh Mechanism

**Purpose**: Sync tracked shipments with latest data from JSON

**Process**:
1. Fetch all data from `shipments.json`
2. For each tracked shipment in database:
   - Find matching shipment in JSON
   - Update status, location, ETA
   - Preserve user-modified fields (title, favorite)
3. Room Flow emits updates
4. UI refreshes automatically

---

## Design Decisions

### 1. Type-Safe Navigation with @Serializable

**Traditional Approach** (String-based):
```kotlin
sealed class Screen(val route: String) {
    object Shipments : Screen("shipments")
    object ShipmentDetails : Screen("shipment_details/{shipmentId}") {
        fun createRoute(id: String) = "shipment_details/$id"
    }
}

composable(
    route = Screen.ShipmentDetails.route,
    arguments = listOf(navArgument("shipmentId") { type = NavType.StringType })
) { ... }
```

**Modern Approach** (Type-safe):
```kotlin
@Serializable
object Shipments

@Serializable
data class ShipmentDetails(val shipmentId: String)

composable<ShipmentDetails> { ... }
navController.navigate(ShipmentDetails(shipmentId))
```

**Benefits**:
- Compile-time route validation
- Automatic argument serialization
- No manual string building
- Refactoring-safe
- Type safety

### 2. Result Wrapper for Error Handling

Using sealed class instead of exceptions in Flow:

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: String? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}
```

**Benefits**:
- Type-safe error handling
- Loading state built-in
- UI can pattern match with `when`

### 3. Dependency Injection with Hilt

**Modules**:
- `DatabaseModule`: Provides `TrackerDatabase` and DAOs
- `RepositoryModule`: Binds repository implementations

**Example**:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TrackerDatabase {
        return Room.databaseBuilder(
            context,
            TrackerDatabase::class.java,
            "tracker_database"
        ).build()
    }

    @Provides
    fun provideShipmentDao(database: TrackerDatabase): ShipmentDao {
        return database.shipmentDao()
    }
}
```

**Benefits**:
- Compile-time dependency graph validation
- Easy testing (swap implementations)
- Scoped instances

### 4. StateFlow for UI State

**Pattern**:
```kotlin
class ShipmentsViewModel {
    private val _uiState = MutableStateFlow(ShipmentsUiState())
    val uiState: StateFlow<ShipmentsUiState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}
```

**In UI**:
```kotlin
val uiState by viewModel.uiState.collectAsState()
```

**Benefits**:
- Always has a value (no null states)
- Survives configuration changes
- Composables recompose automatically

---

## Module Strategy: Single vs Multi-Module

### Why I Chose Single Module

For TrackerConnect, I opted for a **single-module architecture**. Here's why:

#### Advantages of Single Module for This Project

1. **Simplicity**
   - One `build.gradle.kts` file to maintain
   - No module dependency management
   - Faster initial setup

2. **Build Performance**
   - For small projects, build time difference is negligible
   - No inter-module compilation overhead
   - Simpler Gradle cache management

3. **Development Speed**
   - Refactoring across layers is seamless
   - No module boundary friction during prototyping
   - Easy to reorganize packages

4. **Project Scope**
   - 2 feature screens (Shipments, Details)
   - Minimal shared code
   - No need for strict module isolation

#### When to Use Multi-Module

Multi-module becomes beneficial when:
- Large codebase
- Multiple teams working in parallel
- Reusable libraries across apps
- Strict encapsulation requirements
- Build optimization critical
- Dynamic feature modules needed

---

### How Multi-Module Would Look

If TrackerConnect were to scale to multi-module, here's the structure:

#### Module Structure

```
TrackerConnect/
├── app/                                # Main application module
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── MainActivity.kt
│   │   └── TrackerApplication.kt
│   └── build.gradle.kts
│
├── feature/
│   ├── shipments/                      # Shipments feature module
│   │   ├── src/main/
│   │   └── build.gradle.kts
│   │
│   └── details/                        # Shipment details feature module
│       ├── src/main/
│       └── build.gradle.kts
│
├── core/
│   ├── data/                           # Data layer module
│   │   ├── src/main/
│   │   └── build.gradle.kts
│   │
│   ├── domain/                         # Domain layer module
│   │   ├── src/main/
│   │   └── build.gradle.kts
│   │
│   ├── model/                          # Shared models module
│   │   ├── src/main/
│   │   └── build.gradle.kts
│   │
│   ├── ui/                             # Shared UI module
│   │   ├── src/main/
│   │   └── build.gradle.kts
│   │
│   └── common/                         # Common utilities
│       ├── src/main/
│       └── build.gradle.kts
│
├── build-logic/                        # Convention plugins
│   ├── convention/
│   │   ├── src/main/kotlin/
│   │   │   ├── AndroidApplicationConventionPlugin.kt
│   │   │   ├── AndroidLibraryConventionPlugin.kt
│   │   │   ├── AndroidFeatureConventionPlugin.kt
│   │   │   ├── AndroidComposeConventionPlugin.kt
│   │   │   └── AndroidHiltConventionPlugin.kt
│   │   └── build.gradle.kts
│   └── settings.gradle.kts
│
└── settings.gradle.kts
```

#### Convention Plugins with build-logic

**Purpose**: Share build configuration across modules without duplication


#### Benefits of Multi-Module with Convention Plugins

1. **No Configuration Duplication**: All modules use shared convention plugins
2. **Consistent Setup**: Every feature module has same structure
3. **Easy Updates**: Change SDK version in one place
4. **Type-Safe**: Gradle's Kotlin DSL catches errors at compile time
5. **Modular Features**: Each feature is independently buildable
6. **Parallel Builds**: Gradle can build modules concurrently
7. **Incremental Compilation**: Only changed modules rebuild

#### Drawbacks

1. **Initial Setup Complexity**: More boilerplate upfront
2. **Longer CI Builds**: More modules = more overhead (for small projects)
3. **Cognitive Load**: Developers must understand module boundaries
4. **Refactoring Friction**: Moving code between modules requires dependency updates

---

### My Decision

For **TrackerConnect**, single module is the right choice because:

1. Small scope (2 features)
2. Solo/small team development
3. No need for strict layer isolation
4. Build time not a bottleneck
5. Simplicity over premature optimization

If the app grows to **10+ features** or **multiple teams**, migrating to multi-module with build-logic convention plugins would make sense.

---

## Future Improvements

### 1. Real API Integration

**Current**: JSON files in assets
**Future**: Integrate with real shipment tracking API

```kotlin
interface ShipmentApi {
    @GET("shipments")
    suspend fun getShipments(): List<Shipment>

    @POST("shipments")
    suspend fun addTracking(@Body request: AddTrackingRequest): Shipment
}
```

### 2. Push Notifications

- Notify when shipment status changes
- WorkManager for periodic sync
- Firebase Cloud Messaging for real-time updates

### 3. Widgets

- Home screen widget showing active shipments
- Glance API for Jetpack Compose widgets

### 4. Multi-Account Support

- Track shipments across multiple carriers/accounts
- Account switcher UI

### 5. Export/Import

- Export tracking history as CSV/JSON
- Import bulk tracking numbers

### 6. Analytics

- Track user behavior
- Monitor API errors and performance

### 7. Advanced Filtering

- Filter by date range
- Filter by carrier
- Sort by ETA, status

---

## Conclusion

TrackerConnect demonstrates modern Android development best practices:

- **Clean Architecture** with clear layer separation
- **Reactive programming** with Kotlin Flow
- **Type-safe navigation** with Kotlin Serialization
- **Dependency injection** with Hilt
- **Offline-first** architecture with Room
- **Declarative UI** with Jetpack Compose

The decision to use a **single-module** architecture was deliberate and appropriate for the project's scope. However, the code is structured with modularity in mind, making a future migration to **multi-module with build-logic convention plugins** straightforward if the project scales.

This solution balances **simplicity** (single module) with **scalability** (clean architecture) and **performance** (in-memory filtering, reactive streams), resulting in a maintainable and extensible codebase.
