# Evently – Modern Event Management Case Study

**Java • Android • Clean Architecture • Material 3**

Evently is a task-focused event tracking application built to demonstrate modern Android development
practices. It emphasizes clean code, clear separation of concerns, and a solid architectural base
using **Clean Architecture** and **MVVM**, with use cases and ViewModels split by responsibility.

---

## 🚀 Key Features

- **Reactive Data Flow:** Event list and detail data driven by RxJava `Single`/`Observable` with
  repository abstraction.
- **Smart Filtering:** Filter events by **All**, **Today**, **Upcoming**, and **Past** via chip
  selection with instant list updates.
- **Unified State Management:** Centralized **Loading**, **Success**, **Error**, and **Empty**
  states using the `ViewState` pattern.
- **Event Detail Tabs:** **Participants**, **Comments**, and **Live Status** in a single screen with
  ViewPager2; title/date overlay on event image, comments-only header on Comments tab.
- **Comments & Participants:** Add comments, like/unlike, reply with icon; search participants; live
  status with wave animation and countdown.
- **Modern UI/UX:** Material 3, DrawerLayout, FAB on home, white toolbar/status bar, rounded event
  cards, Light/Dark mode support.
- **Type-Safe Navigation:** Safe Args for passing `eventId` between Event List and Event Detail.

---

## 🏗️ Architectural Stack

The project follows **Clean Architecture**, keeping business logic independent of UI and data
sources:

### 1. Domain Layer 

- **Entities:** Core models (`Event`, `Comment`, `Participant`, `FilterType`).
- **Use Cases:** One responsibility per operation (event list/detail, participants, comments, live
  status).
- **Repository Interfaces:** Abstractions so domain does not depend on data implementations.

### 2. Data Layer

- **Repository Implementation:** `EventRepositoryImpl` implements event, participant, comment, and
  live-status logic (mock data; Room or remote can be plugged in later).
- **No data entities in domain:** Domain models are used end-to-end; mapping can be added when
  persisting to Room/API.

### 3. Presentation Layer (MVVM)

- **ViewModels:** One ViewModel per screen/tab; UI state and use-case orchestration.
- **State & Events:** `LiveData` for UI state; `SingleLiveEvent` for one-time events (navigation,
  toasts).
- **Data Binding & View Binding:** Layouts bound to ViewModels; clicks via handlers for cleaner
  fragments.

---

## 🛠️ Tech Stack

| Category             | Technologies                                                  |
|----------------------|---------------------------------------------------------------|
| Language             | Java 11                                                       |
| Async / Streams      | RxJava 3                                                      |
| Dependency Injection | Hilt                                                          |
| Jetpack              | Navigation (Safe Args), ViewModel, Data Binding, View Binding |
| UI                   | Material 3, ViewPager2, DrawerLayout, Chips, FAB              |
| Architecture         | Clean Architecture, MVVM, ViewState pattern                   |
| Date/Time            | `java.time` (minSdk 26)                                       |

---

---

## 📸 Screenshots

| Event List                                                                               | Event Detail (Participants)                                                              | Event Detail (Comments)                                                                  |
|------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| ![Ekran Resmi 2026-02-01 22.59.41.png](images/Ekran%20Resmi%202026-02-01%2022.59.41.png) | ![Ekran Resmi 2026-02-01 22.59.50.png](images/Ekran%20Resmi%202026-02-01%2022.59.50.png) | ![Ekran Resmi 2026-02-01 22.59.57.png](images/Ekran%20Resmi%202026-02-01%2022.59.57.png) |

| Event Detail (Live Status)                                                               | Drawer                                                                                   | 
|------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| ![Ekran Resmi 2026-02-01 23.00.27.png](images/Ekran%20Resmi%202026-02-01%2023.00.27.png) | ![Ekran Resmi 2026-02-01 23.03.19.png](images/Ekran%20Resmi%202026-02-01%2023.03.19.png) |

---

## 🏁 Getting Started

1. **Clone the repository:**
   ```bash
   git clone https://github.com/doseyenc/evently.git
   ```

2. Open the project in **Android Studio**.

3. Wait for **Gradle sync** to finish.

4. Run the app on an **emulator or device** (min SDK **26**).

---

## 📁 Project Structure (High Level)

```
app/src/main/java/com/doseyenc/evently/
├── domain/           # Entities, use cases, repository interfaces
├── data/             # Repository implementations
├── di/               # Hilt modules
├── ui/
│   ├── base/         # ViewState, SingleLiveEvent, BaseViewModel
│   ├── home/         # Event list, FAB, chips, drawer
│   └── detail/       # Event detail, tabs, participants, comments, live status
└── util/             # Constants, DateTimeUtils
```

---

