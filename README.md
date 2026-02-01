# 📅 Evently – Modern Event Management Case Study

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Java%2011-orange.svg)](https://www.oracle.com/java/)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-blue.svg)](https://developer.android.com/topic/architecture)

**Evently** is a high-performance event tracking application developed as a technical case study. It demonstrates a robust implementation of modern Android standards, focusing on clean separation of concerns, reactive data management, and lifecycle-aware components.

---

## 🎯 Case Study Requirements & Fulfillment
The project successfully implements all core requirements from the ID3 Android Case Study:
* **Modern UI:** Built with **Material 3** and **Data Binding** for a seamless, declarative UI.
* **Architecture:** Structured using **MVVM** and **Clean Architecture** to ensure testability and scalability.
* **Live Updates:** Real-time "Live Status" monitoring with an automated **30-second polling cycle**.
* **Lifecycle Awareness:** Intelligent resource management where background updates pause strictly when the tab is not visible or the fragment is in the background.

---

## 🏗️ Architectural Deep Dive

The application is strictly partitioned into three layers to ensure business logic remains independent of UI and data providers:

### 1. Domain Layer (Pure Logic)
* **Entities:** Core models such as `Event`, `Comment`, and `Participant`.
* **Use Cases:** Granular, single-responsibility classes like `GetLiveStatusUseCase` and `GetEventDetailUseCase`.
* **Repository Interfaces:** Defined abstractions to decouple domain logic from specific data implementations (Dependency Inversion).

### 2. Data Layer (Infrastructure)
* **Repository Implementation:** `EventRepositoryImpl` leverages **RxJava 3** to provide a reactive data stream from mock sources, architected for effortless transition to remote APIs.

### 3. Presentation Layer (MVVM)
* **ViewModels:** Orchestrates UI state and business logic execution.
* **ViewState Pattern:** Centralized state management handling **Loading**, **Success**, and **Error** states reactively via LiveData.
* **Data Binding:** Optimized interaction between XML layouts and ViewModels, significantly reducing boilerplate code in Fragments.

---

## 🛠️ Tech Stack

| Category | Technology | Implementation Detail |
| :--- | :--- | :--- |
| **Concurrency** | **RxJava 3** | Manages background threading and periodic status polling. |
| **DI** | **Hilt** | Manages dependency lifecycles and injection across the app. |
| **Navigation** | **Safe Args** | Ensures type-safe argument passing (e.g., `eventId`) between screens. |
| **UI** | **Material 3** | Utilizes ViewPager2, TabLayout, and modern Material components. |

---

## 🚀 Technical Highlights: The Live Status Challenge

A key requirement was the **Live Status** feature, which updates every 30 seconds but must stop when the tab is not visible.

**The Solution:**
I implemented a reactive timer using RxJava's `Observable.interval`. To maintain strict lifecycle awareness and resource optimization:
1.  **Subscription Management:** The polling mechanism is active only when the `LiveStatusFragment` is visible to the user.
2.  **Resource Cleanup:** All `Disposable` objects are cleared in the ViewModel's `onCleared()` and managed via fragment lifecycle hooks to prevent memory leaks and unnecessary CPU usage.

---

## 📸 Screenshots

<table>
  <tr>
    <td align="center"><b>Home (Event List)</b></td>
    <td align="center"><b>Participants Tab</b></td>
    <td align="center"><b>Comments Tab</b></td>
    <td align="center"><b>Live Status Tab</b></td>
  </tr>
  <tr>
    <td><img src="images/Ekran%20Resmi%202026-02-01%2022.59.41.png" width="200pt" /></td>
    <td><img src="images/Ekran%20Resmi%202026-02-01%2022.59.50.png" width="200pt" /></td>
    <td><img src="images/Ekran%20Resmi%202026-02-01%2022.59.57.png" width="200pt" /></td>
    <td><img src="images/Ekran%20Resmi%202026-02-01%2023.00.27.png" width="200pt" /></td>
  </tr>
</table>

---

## 📁 Project Structure

```text
app/src/main/java/com/doseyenc/evently/
├── domain/         # Entities, Use Cases, Repository Interfaces
├── data/           # Repository Implementation, Mock Data Sources
├── di/             # Hilt Dependency Injection Modules
├── ui/
│   ├── base/       # ViewState, BaseViewModel, SingleLiveEvent
│   ├── home/       # Event list, filtering, and main navigation
│   └── detail/     # ViewPager2 setup, Participants, Comments, Live Status
└── util/           # DateTime formatters, UI Helpers, Constants
```

## 🏁 Getting Started
1. Clone the repo: `git clone https://github.com/doseyenc/evently.git`
2. Open in Android Studio.
3. Ensure **JDK 11** and **Min SDK 26** are configured.
