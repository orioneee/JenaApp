<div align="center">

# 🧭 JetNavigation

**Kotlin Multiplatform навигационная библиотека для гибридной маршрутизации внутри помещений и на улице**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.x-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose_Multiplatform-1.7.x-4285F4?logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/compose-multiplatform/)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS%20%7C%20Desktop%20%7C%20Web-blue)]()

*Навигация между зданиями, этажами и улицей — в одном решении*

</div>

---

## 📖 Содержание

- [О проекте](#-о-проекте)
- [Возможности](#-возможности)
- [Архитектура](#-архитектура)
- [Поддерживаемые платформы](#-поддерживаемые-платформы)
- [Установка](#-установка)
- [Быстрый старт](#-быстрый-старт)
- [Конфигурация](#-конфигурация)
- [Запуск Sample-приложения](#-запуск-sample-приложения)
- [Публикация](#-публикация)
- [Стек технологий](#-стек-технологий)
- [Лицензия](#-лицензия)

---

## 🎯 О проекте

**JetNavigation** — это кроссплатформенная Kotlin Multiplatform библиотека, которая решает задачу **гибридной навигации**: бесшовное построение маршрутов внутри зданий (с поддержкой множества этажей и корпусов) и на улице с интеграцией GPS-карт.

### Зачем это нужно?

Представьте университетский кампус, бизнес-центр или торговый комплекс — пользователю нужно попасть из аудитории 305 в корпусе А в аудиторию 112 в корпусе Б. JetNavigation построит маршрут:

1. 🏢 По этажу внутри здания А
2. 🔽 Спуск на 1 этаж
3. 🚶 Выход на улицу
4. 🗺️ Маршрут по улице до здания Б (с учётом погоды)
5. 🏢 Вход в здание Б и путь до нужной аудитории

---

## ✨ Возможности

| Возможность | Описание |
|---|---|
| 🏗️ **Мульти-этажная навигация** | Поддержка зданий с множеством этажей, переходы по лестницам |
| 🏘️ **Мульти-корпусная навигация** | Маршруты между несколькими зданиями через улицу |
| 🔄 **Гибридные переходы** | Бесшовные переходы indoor ↔ outdoor |
| 📍 **Несколько вариантов маршрута** | До 4 альтернативных маршрутов с оценкой времени и расстояния |
| 🌤️ **Интеграция погоды** | Учёт погодных условий для outdoor-участков |
| 🗺️ **Интерактивные карты** | Масштабируемые планы этажей + Google Maps / MapBox |
| 🎨 **Анимированные переходы** | Плавные анимации между этажами и зданиями |
| 📱 **Кроссплатформенность** | Единый код для Android, iOS, Desktop и Web |

---

## 🏛️ Архитектура

Библиотека построена по принципам **Clean Architecture** с разделением на слои:

```
┌─────────────────────────────────────────────┐
│              Presentation Layer              │
│  NavigationApp · Screens · Components · Map  │
├─────────────────────────────────────────────┤
│                Domain Layer                  │
│  NavigationStep · NavigationDirection · Graph │
├─────────────────────────────────────────────┤
│               Engine Layer                   │
│  PathFinder · RouteBuilder · FloorRenderer   │
├─────────────────────────────────────────────┤
│                Data Layer                    │
│  NavigationRemoteRepository · Ktor Client    │
└─────────────────────────────────────────────┘
```

- **Presentation** — Jetpack Compose UI с MVVM (ViewModel + Coroutines)
- **Domain** — модели данных, навигационные шаги и граф зданий
- **Engine** — алгоритм Дейкстры, построение маршрутов, рендеринг этажей
- **Data** — удалённый репозиторий для загрузки навигационных графов

---

## 📱 Поддерживаемые платформы

| Платформа | Статус | Минимальная версия |
|---|---|---|
| 🤖 Android | ✅ | API 21 (Android 5.0) |
| 🍎 iOS | ✅ | iOS 13+ |
| 🖥️ Desktop (JVM) | ✅ | Java 21 |
| 🌐 Web (WASM) | ✅ | Современные браузеры |

---

## 📦 Установка

### Gradle (Kotlin DSL)

```kotlin
// settings.gradle.kts
repositories {
    mavenCentral()
}

// build.gradle.kts
dependencies {
    implementation("com.oriooneee.jet.navigation:JetNavigation:1.0.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'com.oriooneee.jet.navigation:JetNavigation:1.0.0'
}
```

---

## 🚀 Быстрый старт

### Минимальный пример

```kotlin
import com.oriooneee.jet.navigation.presentation.navigation.NavigationApp

@Composable
fun App(isDarkTheme: Boolean = false) {
    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            NavigationApp(isDarkTheme = isDarkTheme)
        }
    }
}
```

`NavigationApp` — это главная точка входа, которая:
- Инициализирует **Koin DI** с изолированным контекстом
- Настраивает **Jetpack Navigation** с экранами
- Управляет состоянием через `NavigationViewModel`

### Ключевые типы

```kotlin
// Навигационный шаг — один сегмент маршрута
sealed class NavigationStep {
    data class ByFlor(...)            // Движение по этажу
    data class TransitionToFlor(...)   // Переход между этажами
    data class TransitionToBuilding(...)// Переход между зданиями
    data class TransitionToOutDoor(...)// Выход на улицу
    data class OutDoorMaps(...)        // Уличный маршрут
    data class TransitionToInDoor(...) // Вход в здание
}

// Полный маршрут с метриками
data class NavigationDirection(
    val steps: List<NavigationStep>,
    val totalDistanceMeters: Double,
    val badge: String?
) {
    val estimatedTimeMinutes: Double   // Оценка времени (80м/мин)
}
```

---

## ⚙️ Конфигурация

Для работы библиотеки необходимо настроить API-ключи. Создайте файл `local.properties` в корне проекта:

```properties
GOOGLE_MAPS_API_KEY=ваш_google_maps_ключ
MAPBOX_API_KEY=ваш_mapbox_ключ
API_KEY=ваш_api_ключ
BASE_URL=https://your-api-url.com
```

Или задайте через переменные окружения:

```bash
export GOOGLE_MAPS_API_KEY=ваш_google_maps_ключ
export MAPBOX_API_KEY=ваш_mapbox_ключ
export API_KEY=ваш_api_ключ
export BASE_URL=https://your-api-url.com
```

---

## 🏃 Запуск Sample-приложения

Проект включает полнофункциональное демо-приложение:

```bash
# 🖥️ Desktop (JVM)
./gradlew :sample:composeApp:run

# 🌐 Web (WASM) — откроется в браузере
./gradlew :sample:composeApp:wasmJsBrowserRun

# 🤖 Android — откройте проект в Android Studio и запустите sample app

# 🍎 iOS — откройте в Xcode:
open sample/iosApp/iosApp.xcodeproj
```

---

## 📤 Публикация

### Публикация в MavenLocal

```bash
./gradlew :JetNavigation:publishToMavenLocal
```

Артефакты будут доступны в `~/.m2/repository/com/oriooneee/jet/navigation/`

### Публикация в MavenCentral

<details>
<summary>📋 Пошаговая инструкция</summary>

1. **Создайте аккаунт** на [Sonatype Central Portal](https://central.sonatype.org/register/central-portal/#create-an-account)

2. **Заполните данные разработчика** в `./JetNavigation/build.gradle.kts` (id, name, email, project url)

3. **Сгенерируйте GPG-ключ:**
   ```bash
   gpg --full-gen-key
   gpg --keyserver keyserver.ubuntu.com --send-keys XXXXXXXX
   gpg --export-secret-key XXXXXXXX > XXXXXXXX.gpg
   ```

4. **Добавьте конфигурацию** в `gradle.properties`:
   ```properties
   signing.keyId=XXXXXXXX
   signing.password=ваш_пароль_ключа
   signing.secretKeyRingFile=../XXXXXXXX.gpg
   mavenCentralUsername=сгенерированный_логин
   mavenCentralPassword=сгенерированный_пароль
   ```

5. **Опубликуйте:**
   ```bash
   ./gradlew :JetNavigation:publishAndReleaseToMavenCentral --no-configuration-cache
   ```

</details>

---

## 🛠️ Стек технологий

| Технология | Назначение |
|---|---|
| [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) | Общий код для всех платформ |
| [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/) | Декларативный UI |
| [Ktor Client](https://ktor.io/docs/client-overview.html) | HTTP-клиент (OkHttp / Darwin / WASM) |
| [Koin](https://insert-koin.io/) | Dependency Injection |
| [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) | Сериализация JSON |
| [Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines) | Асинхронность |
| [Google Maps Compose](https://developers.google.com/maps/documentation/android-sdk/maps-compose) | Карты для Android |
| [MapBox](https://www.mapbox.com/) | Карты для кроссплатформенных сценариев |
| [Coil](https://coil-kt.github.io/coil/) | Загрузка изображений |
| [Jetpack Navigation](https://developer.android.com/jetpack/compose/navigation) | Навигация между экранами |

---

## 📄 Лицензия

```
MIT License

Copyright (c) 2025 oriooneee

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

<div align="center">

**Сделано с ❤️ с использованием Kotlin Multiplatform и Compose**

</div>
