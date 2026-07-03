 📱 TaskFlow — Android Final Project
 
**TaskFlow** არის დავალებების (Task) მართვის Android აპლიკაცია, რომელიც აგებულია თანამედროვე არქიტექტურული პრინციპების (MVVM) და Android Jetpack-ის საუკეთესო პრაქტიკების გამოყენებით. აპლიკაცია საშუალებას აძლევს მომხმარებელს დარეგისტრირდეს/გაიაროს ავტორიზაცია, შექმნას, დაარედაქტიროს, მონიშნოს დასრულებულად და წაშალოს საკუთარი დავალებები, თითოეულს პრიორიტეტის მინიჭებით.
 
---
 
## 🚀 ფუნქციონალი (Features)
 
- **ავტორიზაცია და რეგისტრაცია** — მომხმარებელი ქმნის ანგარიშს (username + password) და შედის სისტემაში; სესია ინახება `SharedPreferences`-ში (`SessionManager`), რაც უზრუნველყოფს, რომ თითოეულ მომხმარებელს მხოლოდ საკუთარი დავალებები დაენახოს.
- **დავალებების მართვა (CRUD)** — დავალების დამატება, რედაქტირება, დასრულებულად მონიშვნა და წაშლა.
- **პრიორიტეტები** — თითოეულ დავალებას აქვს პრიორიტეტი (High / Medium / Low), რომლის მიხედვითაც ხდება სორტირება.
- **დინამიური ძებნა** — აქტიურ დავალებებში რეალურ დროში ძებნა სათაურის/აღწერის მიხედვით.
- **სვაიპით წაშლა** — `ItemTouchHelper`-ზე დაფუძნებული `SwipeToDeleteCallback`, Undo (Snackbar) ფუნქციით.
- **ორი ცალკეული სია** — აქტიური და დასრულებული დავალებები ცალ-ცალკე ეკრანზე (Bottom Navigation).
- **მენიუები** — ზედა Toolbar მენიუ (დამატება / დასრულებულების გასუფთავება / About) და ქვედა ნავიგაციის მენიუ (Tasks / Completed / Settings).
- **🎉 ანიმირებული "Celebration" ეფექტი (ახალი, აქამდე გამოუყენებელი ფუნქციონალი)** — დავალების დასრულებისას `CelebrationHelper`-ის მეშვეობით ეკრანზე იშლება ემოჯების ანიმაცია (`ObjectAnimator` + `AnimatorSet`, custom radial burst ეფექტი), თან თან ახლავს შემთხვევითი მოტივაციური შეტყობინება და Snackbar. ასევე გამოიყენება FAB-ის და ეკრანის ელემენტების fade-in/scale ანიმაციები ეკრანზე შესვლისას.
- **Empty state-ები** — როცა სია ცარიელია, ნაცვლად ცარიელი ეკრანისა მომხმარებელს უჩვენებს შესაბამის ილუსტრაციასა და ტექსტს.
- **ქართული ლოკალიზაცია** — `values-ka/strings.xml`, ანუ აპი მხარს უჭერს ქართულ ენას.
- **Dark theme მხარდაჭერა** — `values-night/themes.xml`.
---
 
## 🛠️ ტექნოლოგიური სტეკი და არქიტექტურა
 
პროექტი აგებულია **MVVM (Model–View–ViewModel)** არქიტექტურული პატერნით:
 
- **Model** — `Task`, `User` (Room Entity კლასები) და `Priority` enum.
- **ViewModel** — `TaskViewModel`, `AuthViewModel` (`AndroidViewModel` + `LiveData` + `viewModelScope`), რომლებიც UI-სგან მალავენ ბიზნეს ლოგიკას და მონაცემთა წყაროს.
- **Repository** — `TaskRepository`, `UserRepository` — შუალედური შრე, რომელიც `ViewModel`-ს პირდაპირ Room-ის Dao-ებთან კავშირისგან იზოლირებს.
- **View** — `Fragment`-ები (`LoginFragment`, `SignUpFragment`, `TasksFragment`, `CompletedTasksFragment`, `SettingsFragment`) + `MainActivity`, `TaskAdapter`, `AddEditTaskDialogFragment`.
**გამოყენებული ტექნოლოგიები:**
 
| კატეგორია | ინსტრუმენტი |
|---|---|
| ენა | Kotlin |
| UI | XML Layouts, ViewBinding, Material Design Components, ConstraintLayout |
| ნავიგაცია | Jetpack Navigation Component (`nav_graph.xml`) + Bottom Navigation |
| არქიტექტურა | MVVM — `ViewModel`, `LiveData`, `Repository` |
| ბაზა | **Room (SQLite)** — `TaskDatabase`, `TaskDao`, `UserDao` |
| ასინქრონულობა | Kotlin Coroutines (`suspend` ფუნქციები Insert/Update/Delete-სთვის) |
| ლისტები | `RecyclerView` + `TaskAdapter` |
| სესია | `SharedPreferences` (`SessionManager`) |
| ანიმაცია | `ObjectAnimator`, `AnimatorSet`, `OvershootInterpolator` (Celebration ფუნქციონალი) |
| სვაიპ-ჟესტები | `ItemTouchHelper` (`SwipeToDeleteCallback`) |
| დამოკიდებულებების მართვა | Gradle Version Catalog (`libs.versions.toml`), KSP |
 
> **შენიშვნა:** მონაცემთა ბაზისთვის გამოყენებულია **Room**, რომელიც სავალდებულო მოთხოვნის ("Firebase Realtime Database / Retrofit / Room") ერთ-ერთი მისაღები ვარიანტია.
 
---
 
## 📂 პროექტის სტრუქტურა
 
```
app/src/main/java/com/example/final_project/
├── data/
│   ├── Task.kt              # Room Entity — დავალება
│   ├── User.kt               # Room Entity — მომხმარებელი
│   ├── TaskDao.kt            # Room DAO — დავალებებზე queries
│   ├── UserDao.kt            # Room DAO — მომხმარებლებზე queries
│   ├── TaskDatabase.kt       # Room Database instance
│   ├── TaskRepository.kt     # Repository layer (Task)
│   ├── UserRepository.kt     # Repository layer (User/Auth)
│   └── SessionManager.kt     # SharedPreferences სესიის მართვა
└── ui/
    ├── MainActivity.kt
    ├── TaskViewModel.kt
    ├── AuthViewModel.kt
    ├── AppViewModelFactory.kt
    ├── LoginFragment.kt
    ├── SignUpFragment.kt
    ├── TasksFragment.kt
    ├── CompletedTasksFragment.kt
    ├── SettingsFragment.kt
    ├── AddEditTaskDialogFragment.kt
    ├── TaskAdapter.kt
    ├── SwipeToDeleteCallback.kt
    ├── CelebrationHelper.kt   # ანიმაციური "Celebration" ფუნქციონალი
    └── InputUtils.kt
```
 
---
 
## ⚙️ როგორ გავუშვათ პროექტი
 
1. დააკლონირეთ რეპოზიტორია:
```bash
   git clone https://github.com/nikatanda/Android_final.git
```
2. გახსენით პროექტი **Android Studio**-ში.
3. დაელოდეთ Gradle sync-ის დასრულებას (KSP საჭიროებს Room-ის annotation processing-ისთვის).
4. გაუშვით აპლიკაცია ემულატორზე ან რეალურ მოწყობილობაზე (`minSdk 24`, `targetSdk 36`).
---
 
## 📸 აპლიკაციის ინტერფეისი
 
| ავტორიზაცია | დავალებების სია | პარამეტრები |
| :---: | :---: | :---: |
| <img width="240" alt="Login" src="https://github.com/user-attachments/assets/0f580e1d-c87e-4d33-bbff-04712a3dc15f" /> | <img width="240" alt="Tasks" src="https://github.com/user-attachments/assets/b2fe9b4a-ba3b-40c7-8ede-50f52e6d578e" /> | <img width="240" alt="Settings" src="https://github.com/user-attachments/assets/44d0a951-3389-4f36-bd77-1c741ae1bbe5" /> |
 
---
 

 
