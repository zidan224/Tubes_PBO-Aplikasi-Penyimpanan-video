# 🎬 MyWatchlist - Video Watchlist Application

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![Swing](https://img.shields.io/badge/Java_Swing-GUI-orange?style=for-the-badge)

**Tugas Besar Pemrograman Berorientasi Objek (PBO)** Aplikasi manajemen video dan *personal watchlist* berbasis desktop yang dibangun menggunakan Java Swing dan MySQL dengan penerapan *Design Patterns*.

---

## 👥 Tim Pengembang

| NIM | Nama Lengkap | Role / Fokus Pengerjaan |
| :--- | :--- | :--- |
| **241511015** | **Marrely Salsa Kasih** |
| **241511017** | **Muhammad Faliq Shiddiq Azzaki** |
| **241511032** | **Zidan Taufiqurahman** |

---

## 📖 Deskripsi Program

**MyWatchlist** adalah aplikasi desktop yang memungkinkan pengguna untuk menjelajahi koleksi video dan menyimpannya ke dalam daftar tontonan pribadi (*Watchlist*). Aplikasi ini memisahkan hak akses antara **Admin** (pengelola konten) dan **User** (penikmat konten).

### Fitur Utama:
* 🔐 **Authentication**: Login & Register dengan pemisahan role (User/Admin).
* 👨‍💼 **Admin Dashboard**: CRUD (Create, Read, Update, Delete) data video.
* 👤 **User Dashboard**: Melihat library video, menambahkan ke watchlist.
* ⭐ **Personal Watchlist**: Halaman khusus untuk video yang disimpan user.
* ▶️ **Playback Simulation**: Fitur simulasi pemutaran video menggunakan *Iterator Pattern*.

---

## 🏗️ Arsitektur & Design Patterns

Aplikasi ini dirancang tidak hanya sekadar jalan, tetapi juga menerapkan prinsip *Software Engineering* yang baik:

1.  **Singleton Pattern** * Digunakan pada koneksi **Database**. Memastikan hanya ada satu instance koneksi yang aktif untuk menghemat resource.
2.  **Facade Pattern**
    * Digunakan pada **`AppFacade`**. Menyederhanakan akses UI ke logika bisnis yang rumit (DAO, Model, Auth) melalui satu pintu gerbang yang bersih.
3.  **Iterator Pattern** *(In Progress)*
    * Digunakan pada fitur **Player**. Memungkinkan navigasi video (*next/prev*) dalam playlist tanpa mengekspos struktur data internalnya.
4.  **MVC (Model-View-Controller)**
    * Pemisahan yang jelas antara data (`model`), tampilan (`ui`), dan logika (`dao`/`facade`).

---

## 🗄️ Struktur Database (SQL)

Aplikasi ini sebenarnya memiliki fitur **Auto-Create Table** saat dijalankan. Namun, jika Anda ingin membuatnya secara manual di phpMyAdmin, berikut adalah query-nya:

### 1. Buat Database
```sql
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, 
    role VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS videos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    creator VARCHAR(255),
    category VARCHAR(255),
    year INT,
    genre VARCHAR(100),
    duration DOUBLE
);

CREATE TABLE IF NOT EXISTS watchlist (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    video_id INT NOT NULL,
    status VARCHAR(20) DEFAULT 'QUEUED',
    last_watch_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE CASCADE
);

```

## 🚀 Cara Menjalankan Program
Prasyarat
1. **Java JDK 8 atau lebih baru.**
2. **MySQL Server (via XAMPP/WAMP/MAMP).**
3. **Library MySQL Connector J (sudah disertakan di folder lib).**

Langkah-langkah :
1. Clone Repository
2. Setup database
    - Nyalakan MySQL di XAMPP.
    - Buat database kosong bernama watchlistdb di phpMyAdmin.
    - (Opsional) Jalankan query SQL di atas jika ingin manual, tapi aplikasi bisa membuatnya otomatis.
3. Jalankan Aplikasi
    - Buka project di IDE (VS Code / IntelliJ / NetBeans).
    - Pastikan library di folder lib sudah ditambahkan ke Classpath/Dependency.
    - Run file src/main/java/com/myplaylist/main.java.
```