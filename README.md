# TaskFlow — Android Final Project

**TaskFlow** არის Kotlin-ზე დაწერილი Android აპლიკაცია პირადი დავალებების მართვისთვის. მომხმარებელი რეგისტრირდება, შედის სისტემაში და მართავს საკუთარ დავალებებს — ამატებს, ეძებს, ასრულებს და შლის მათ. ყველა მონაცემი ინახება ლოკალურად **Room** ბაზაში.

**GitHub:** [https://github.com/Ts1po/Final_in_Android_Dev_UNI](https://github.com/Ts1po/Final_in_Android_Dev_UNI)

---

## რა აკეთებს აპი

| ფუნქცია | აღწერა |
|--------|--------|
| **რეგისტრაცია / შესვლა** | მომხმარებელი ქმნის ანგარიშს ან შედის username/password-ით |
| **დავალებების სია** | აქტიური დავალებები `RecyclerView`-ში |
| **დასრულებული** | ცალკე ჩანართი დასრულებული დავალებებისთვის |
| **დამატება / რედაქტირება** | დიალოგი: სათაური, აღწერა, პრიორიტეტი (High / Medium / Low) |
| **ძებნა** | რეალურ დროში ფილტრაცია სათაურით და აღწერით |
| **Swipe to delete** | მარცხნივ გადაფურცვლით წაშლა + Undo |
| **პრიორიტეტის badge** | ფერადი ეტიკეტი და გვერდითი ინდიკატორი |
| **პარამეტრები** | პროფილი, About, გასვლა |
| **ქართული ენა** | UI სტრინგები `values-ka`-ში |

---

## მომხმარებლის ნაკადი

```
აპის გახსნა
    │
    ├─► არ არის შესული → Login / Sign Up
    │                        │
    │                        └─► წარმატებული შესვლა
    │
    └─► შესულია → Tasks ეკრანი
                      │
                      ├─► + → ახალი დავალება
                      ├─► ძებნა → ფილტრაცია
                      ├─► Checkbox → დასრულება
                      ├─► Swipe → წაშლა
                      ├─► Completed ჩანართი
                      └─► Settings → Logout
```

1. **პირველი გაშვება** — ჩანს Login ეკრანი.
2. **რეგისტრაცია** — `SignUpFragment` ინახავს მომხმარებელს Room ბაზაში (`users` ცხრილი).
3. **შესვლა** — `AuthViewModel` ამოწმებს მონაცემებს; `SessionManager` (SharedPreferences) ინახავს `userId`-ს და `username`-ს.
4. **დავალებები** — `TaskViewModel` აჩვენებს მხოლოდ მიმდინარე მომხმარებლის დავალებებს.
5. **ცვლილებები** — UI → ViewModel → Repository → Room → SQLite.

---

## არქიტექტურა: MVVM

აპი **MVVM** (Model – View – ViewModel) პატერნს იყენებს.

```
┌─────────────────────────────────────────┐
│              View (UI)                  │
│  MainActivity, Fragments, TaskAdapter   │
└──────────────────┬──────────────────────┘
                   │ observe LiveData
                   │ user actions
┌──────────────────▼──────────────────────┐
│            ViewModel                    │
│  TaskViewModel  │  AuthViewModel        │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│           Repository                    │
│  TaskRepository  │  UserRepository      │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         Room (DAO + Database)           │
│  TaskDao │ UserDao → SQLite ფაილი       │
└─────────────────────────────────────────┘
```

| ფენა | კომპონენტები |
|------|-------------|
| **View** | `MainActivity`, `TasksFragment`, `CompletedTasksFragment`, `LoginFragment`, `SignUpFragment`, `SettingsFragment`, `TaskAdapter`, `AddEditTaskDialogFragment` |
| **ViewModel** | `TaskViewModel`, `AuthViewModel` — `LiveData`, Coroutines |
| **Model** | `Task`, `User`, `TaskDao`, `UserDao`, `TaskDatabase`, `TaskRepository`, `UserRepository`, `SessionManager` |

**მთავარი წესი:** UI პირდაპირ ბაზას არ ეხება. Fragment-ები `LiveData`-ს უკვე უყურებენ ViewModel-იდან, ხოლო ყველა ცვლილება ViewModel → Repository → DAO გზით მიდის.

---

## მონაცემთა ბაზა: Room

აპი **Room**-ს იყენებს — Android-ის ოფიციალურ ORM-ს, რომელიც Kotlin ობიექტებს SQLite ცხრილებად გარდაქმნის.

### ცხრილები

**`users`** — მომხმარებლები (რეგისტრაცია/ავტორიზაცია)

| ველი | ტიპი | აღწერა |
|------|------|--------|
| id | Long | პირველადი გასაღები (auto) |
| username | String | მომხმარებლის სახელი (უნიკალური) |
| password | String | პაროლი |

**`tasks`** — დავალებები

| ველი | ტიპი | აღწერა |
|------|------|--------|
| id | Long | პირველადი გასაღები (auto) |
| userId | Long | მომხმარებლის ID (foreign key ლოგიკით) |
| title | String | სათაური |
| description | String | აღწერა |
| isCompleted | Boolean | დასრულებულია თუ არა |
| priority | Enum | HIGH / MEDIUM / LOW |
| createdAt | Long | შექმნის დრო (timestamp) |

### Room-ის კავშირის ჯაჭვი

```
Task / User (Entity)
       ↓
TaskDao / UserDao (SQL queries)
       ↓
TaskDatabase (RoomDatabase singleton)
       ↓
SQLite ფაილი: taskflow_database
```

**მაგალითი — დავალების დამატება:**

```kotlin
// 1. UI იძახებს ViewModel-ს
viewModel.addTask("ფინალური პროექტი", "README დაწერა", Priority.HIGH)

// 2. ViewModel coroutine-ში Repository-ს უძახებს
repository.insert(Task(userId = 1, title = "...", ...))

// 3. Room DAO წერს SQLite-ში
taskDao.insert(task)

// 4. LiveData ავტომატურად აახლებს RecyclerView-ს
```

### Room-ის უპირატესობები

- **Compile-time შემოწმება** — არასწორი SQL compile-ზე დაიჭერება (KSP)
- **LiveData** — ბაზის ცვლილება ავტომატურად აისახება UI-ში
- **Coroutines** — `suspend` ფუნქციები main thread-ს არ ბლოკავენ
- **ოფლაინ მუშაობა** — ინტერნეტის გარეშეც მუშაობს

---

## ეკრანები და ნავიგაცია

აპი **Navigation Component**-ით მუშაობს:

| ეკრანი | ფაილი | როლი |
|--------|-------|------|
| Login | `LoginFragment` | შესვლა |
| Sign Up | `SignUpFragment` | რეგისტრაცია |
| Tasks | `TasksFragment` | აქტიური დავალებები + ძებნა + FAB |
| Completed | `CompletedTasksFragment` | დასრულებული დავალებები |
| Settings | `SettingsFragment` | პროფილი და გასვლა |

**Bottom Navigation** — Tasks / Completed / Settings ჩანართებს შორის გადასვლა.

**Options Menu** (`main_menu.xml`) — Tasks ეკრანზე: Add, Clear completed, About.

---

## ახალი ფუნქციები (წინა დავალებებში არ გამოყენებული)

| ფუნქცია | იმპლემენტაცია |
|---------|--------------|
| **Login / SignUp** | `AuthViewModel` + `UserRepository` + Room |
| **Navigation Component** | `nav_graph.xml` + Bottom Navigation |
| **Swipe-to-delete + Undo** | `ItemTouchHelper` + Snackbar |
| **ძებნა** | `TextWatcher` → `TaskViewModel.setSearchQuery()` |
| **პრიორიტეტის badge** | ფერადი ინდიკატორი `TaskAdapter`-ში |
| **Celebration animation** | `CelebrationHelper` — დავალების დასრულებისას |
| **მულტიენური შეყვანა** | `InputUtils` — ქართული და სხვა ენების მხარდაჭერა |

---

## პროექტის სტრუქტურა

```
app/src/main/java/com/example/final_project/
├── MainActivity.kt                 # Toolbar, Navigation, Menu
├── data/
│   ├── Task.kt                     # Entity — tasks ცხრილი
│   ├── User.kt                     # Entity — users ცხრილი
│   ├── TaskDao.kt                  # SQL queries დავალებებისთვის
│   ├── UserDao.kt                  # SQL queries მომხმარებლებისთვის
│   ├── TaskDatabase.kt             # Room Database singleton
│   ├── TaskRepository.kt           # Repository ფენა
│   ├── UserRepository.kt
│   └── SessionManager.kt           # SharedPreferences სესია
└── ui/
    ├── TaskViewModel.kt
    ├── AuthViewModel.kt
    ├── AppViewModelFactory.kt
    ├── TasksFragment.kt
    ├── CompletedTasksFragment.kt
    ├── LoginFragment.kt
    ├── SignUpFragment.kt
    ├── SettingsFragment.kt
    ├── TaskAdapter.kt
    ├── AddEditTaskDialogFragment.kt
    ├── SwipeToDeleteCallback.kt
    ├── InputUtils.kt
    └── CelebrationHelper.kt
```

---

## ტექნოლოგიები

| ტექნოლოგია | დანიშნულება |
|-----------|------------|
| **Kotlin** | პროგრამირების ენა |
| **MVVM** | არქიტექტურული პატერნი |
| **Room 2.7.1** | ლოკალური SQLite ORM |
| **ViewBinding** | type-safe layout binding (`findViewById` არ გამოიყენება) |
| **LiveData + Coroutines** | ასინქრონული მონაცემები |
| **Navigation Component** | Fragment-ებს შორის ნავიგაცია |
| **Material Design 3** | UI კომპონენტები |
| **KSP** | Room-ის compile-time კოდგენერაცია |
| **SharedPreferences** | სესიის შენახვა (SessionManager) |

---

## როგორ გავუშვათ

### მოთხოვნები

- Android Studio (Ladybug ან უფრო ახალი)
- JDK 11+
- Android SDK (API 24–36)
- ემულატორი ან ფიზიკური მოწყობილობა (Android 7.0+)

### ნაბიჯები

1. კлонირება:
   ```bash
   git clone https://github.com/Ts1po/Final_in_Android_Dev_UNI.git
   ```
2. Android Studio-ში გახსენი პროექტი
3. **File → Sync Project with Gradle Files**
4. ემულატორი აირჩიე და დააჭირე **Run ▶**
5. შექმენი ანგარიში → შედი → დაამატე დავალებები

---

## ფინალური გამოცდის მოთხოვნები

| მოთხოვნა | იმპლემენტაცია |
|---------|--------------|
| Menu | `main_menu.xml` + `bottom_nav_menu.xml` |
| List | `RecyclerView` + `TaskAdapter` |
| MVVM | ViewModel + Repository + LiveData |
| Database | **Room** (SQLite) |
| ახალი ფუნქცია | Login/SignUp, Navigation, Swipe-to-delete, ძებნა, პრიორიტეტის badge |
| ViewBinding | `findViewById` არ გამოიყენება |
| README | ეს ფაილი |
| Git | GitHub რეპოზიტორია |

---

## ავტორი

ფინალური გამოცდა — Kotlin Android Development
#   A n d r o i d _ f i n a l  
 