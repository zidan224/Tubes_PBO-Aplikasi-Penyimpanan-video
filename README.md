# 🎬 MyWatchlist - Video Watchlist Application

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![Swing](https://img.shields.io/badge/Java_Swing-GUI-orange?style=for-the-badge)

**Tugas Besar Pemrograman Berorientasi Objek (PBO)** Aplikasi manajemen video dan _personal watchlist_ berbasis desktop yang dibangun menggunakan Java Swing dan MySQL dengan penerapan _Design Patterns_.

---

## 👥 Tim Pengembang

| NIM           | Nama Lengkap                      |
| :------------ | :-------------------------------- |
| **241511015** | **Marrely Salsa Kasih**           |
| **241511017** | **Muhammad Faliq Shiddiq Azzaki** |
| **241511032** | **Zidan Taufiqurahman**           |

---

## 📖 Deskripsi Program

**MyWatchlist** adalah aplikasi desktop yang memungkinkan pengguna untuk menjelajahi koleksi video dan menyimpannya ke dalam daftar tontonan pribadi (_Watchlist_). Aplikasi ini memisahkan hak akses antara **Admin** (pengelola konten) dan **User** (penikmat konten).

### Fitur Utama:

- 🔐 **Authentication**: Login & Register dengan pemisahan role (User/Admin).
- 👨‍💼 **Admin Dashboard**: CRUD (Create, Read, Update, Delete) data video.
- 👤 **User Dashboard**: Melihat library video, menambahkan ke watchlist.
- ⭐ **Personal Watchlist**: Halaman khusus untuk video yang disimpan user.
- ▶️ **Playback Simulation**: Fitur simulasi pemutaran video menggunakan _Iterator Pattern_.

---

## 🏗️ Arsitektur & Design Patterns

Aplikasi ini dirancang tidak hanya sekadar jalan, tetapi juga menerapkan prinsip _Software Engineering_ yang baik:

1.  **Singleton Pattern** \* Digunakan pada koneksi **Database**. Memastikan hanya ada satu instance koneksi yang aktif untuk menghemat resource.
2.  **Facade Pattern**
    - Digunakan pada **`AppFacade`**. Menyederhanakan akses UI ke logika bisnis yang rumit (DAO, Model, Auth) melalui satu pintu gerbang yang bersih.
3.  **Iterator Pattern** _(In Progress)_
    - Digunakan pada fitur **Player**. Memungkinkan navigasi video (_next/prev_) dalam playlist tanpa mengekspos struktur data internalnya.
4.  **MVC (Model-View-Controller)**
    - Pemisahan yang jelas antara data (`model`), tampilan (`ui`), dan logika (`dao`/`facade`).

---

## 🗄️ Struktur Database (SQL)

Aplikasi ini sebenarnya memiliki fitur **Auto-Create Table** saat dijalankan. Namun, jika Anda ingin membuatnya secara manual di phpMyAdmin, berikut adalah query-nya:

### 1. Buat Database

```sql
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `videos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `genre` varchar(100) DEFAULT NULL,
  `duration` double DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `watchlist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `video_id` int(11) NOT NULL,
  `status` varchar(20) DEFAULT 'QUEUED',
  `last_watch_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `video_id` (`video_id`),
  CONSTRAINT `fk_watchlist_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_watchlist_video` FOREIGN KEY (`video_id`) REFERENCES `videos` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
