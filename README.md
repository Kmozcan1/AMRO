# AMRO - Android Movie App

**AMRO** is a modern, modular Android application built with **Jetpack Compose** and **Clean Architecture**. It showcases a list of Top 100 movies fetched from the [TMDB API](https://www.themoviedb.org/), allowing users to filter by genre, sort results, and view detailed movie information.


## Features

* **Top 100 Movies:** Displays a scrolling list of top-rated movies.
* **Dynamic Filtering:** Filter movies by **Genre** using interactive chips.
* **Sorting:** Sort movies by **Popularity**, **Title**, or **Release Date** (Ascending/Descending).
* **Movie Details:** Rich detail view with backdrop images, release info, revenue, and vote averages.
* **Adaptive UI:** Fully responsive UI built with **Material 3** and **Jetpack Compose**.
* **Splash Screen:** Native splash screen API integration.
* **Offline Handling:** Graceful error states and retry mechanisms.

## Tech Stack

* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
* **Architecture:** Pragmatic Clean Architecture
* **Dependency Injection:** [Hilt](https://dagger.dev/hilt/)
* **Networking:** [Retrofit](https://square.github.io/retrofit/) + [OkHttp](https://square.github.io/okhttp/) + [Gson](https://github.com/google/gson)
* **Concurrency:** [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) + [Flow](https://kotlinlang.org/docs/flow.html)
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
* **Navigation:** [Navigation Compose](https://developer.android.com/guide/navigation/navigation-compose)
* **Build System:** Gradle (Kotlin DSL) + Version Catalogs (`libs.versions.toml`)

## Modular Architecture

The project follows a strict **feature-module** strategy to ensure separation of concerns and build scalability.

```text
:app                 # Application entry point

:core                # Shared infrastructure
 ├── :common         # Base utilities & Result types
 ├── :network        # Retrofit & OkHttp setup
 ├── :ui             # Design System & Compose Theme
 └── :testing        # Shared Test Rules & Dispatchers

:movies              # Feature Module
 ├── :data           # Repository Impl & API sources
 ├── :domain         # Pure Business Logic & Models
 └── :ui             # Screens & ViewModels
```

An example of a new feature-module would be :profile:data, :profile:domain, :profile:ui.

## Testing

The project includes comprehensive unit tests using **JUnit 5**, **Mockk**, and **Turbine**.

* **ViewModels:** Tested using `Turbine` to verify StateFlow emissions and `MainDispatcherExtension` for coroutine control.
* **Mappers:** Verified to ensure correct data transformation from Domain to UI models.
* **Repositories:** (Ready for integration) mock API responses and verify data flow.

To run tests:

```bash
./gradlew testDebugUnitTest
```

## Getting Started

### Prerequisites

* Android Studio Ladybug (or newer)
* JDK 17

### 1. Clone the Repository

```bash
git clone [https://github.com/your-username/AMRO.git](https://github.com/your-username/AMRO.git)
cd AMRO
```

### 2. Configure API Key

This project uses **The Movie Database (TMDB)** API. You must provide your own API key.

1. Get a key from [TMDB Settings](https://www.google.com/search?q=https://www.themoviedb.org/settings/api).
2. Open your **`local.properties`** file (in the project root).
3. Add the following line:
```properties
TMDB_API_KEY=your_api_key_goes_here
```

### 3. Build & Run

Sync Gradle and run the `app` configuration on an emulator or physical device.

## Visual guidelines

Below are some guidelines to follow to have visual consistency between screens when introducing new features.

1. The app relies on the Palette for the dynamic coloring. It takes a little bit of time for the [swatch](https://developer.android.com/reference/androidx/palette/graphics/Palette.Swatch) to be calculated. To make the transition between colors smoother, prefer darker background colors.
2. While the Top 100 screen is open, a gradient is applied to the screen. The header (part until the list) is assigned a solid color that's derived from the top item's palette. A vertical gradient is applied to the rest of the screen. The color changes dynamically as the user scroll through the list. The default color on launch is Teal, as it's similar to the color of the ABN Amro logo in the splash screen.
3. Just before navigating from the Top 100 to the Details screen, the background "atmosphere" changes to the color of swatch derived from the clicked item's poster image. In the detail screen, the entire background switches to the dominant swatch derived from the movie's backdrop image. Poster's swatch color is carried over to the details screen while the backdrop image's palette is being calculated. These images usually have similar colors, so the transition looks smooth.
4. A brush effect is applied to make the parts where image is near a solid color. On the list, this effect between header and the top item is removed when top item is about to change, so that the brush effect doesn't obstruct the next top item.
5. Different swatches are used for light/dark modes, so no one goes blind.

