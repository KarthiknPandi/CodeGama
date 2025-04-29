# CodeGama
# 📰 News App (Android - Kotlin)

This is a modern Android News application built with Kotlin, MVVM architecture, and Jetpack libraries. It fetches top headlines and allows users to search news articles using the [NewsAPI](https://newsapi.org/). It supports offline caching using Room and periodic background updates via WorkManager.

---

## 📱 Features

-  Firebase Email SignUp/Login
-  View Top Headlines (ViewPager2 with swipe-to-delete/open)
-  Search news articles (with live results)
-  User Profile 
-  MVVM Architecture using ViewModel, LiveData, and Repository pattern
-  Room Database for offline caching
-  WorkManager: Refreshes news every 30 minutes
-  Shimmer loading animation
-  Retrofit for API calls with Paging 3 support

---

## 📁 Project Structure & Flow

├── ui
│   ├── home/              # HomeFragment - Top headlines
|   ├── details/           # Individual News Detail
│   ├── search/            # SearchFragment - Search news
│   ├── profile/           # ProfileFragment - Profile info and logout
│   └── auth/              # SignUpActivity / LoginActivity
│
├── data
│   ├── model/             # Article data model
│   ├── api/               # Retrofit setup and API interface
│   ├── repository/        # NewsRepository
│   ├── db/                # Room DB, DAO
│   └── paging/            # NewsPagingSource
│
├── workers               # NewsRefreshWorker for background sync
├── utils                 # Utility classes (PrefManager, Swipe gestures)
└── ...




 Dependencies Used

Core
Kotlin Coroutines
AndroidX Lifecycle (ViewModel, LiveData)
Jetpack Navigation
ViewBinding
ViewPager2
ShimmerLayout
Glide
SwipeRefreshLayout
Retrofit
OkHttp + Logging Interceptor
Gson
Architecture
MVVM + Repository pattern
Paging 3
Room Database (with Paging support)
Firebase Authentication
WorkManager (Periodic sync)

