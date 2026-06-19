# Java-welch-powell-UAS-matematika-diskrit
Repositori aplikasi Desktop (Java Swing &amp; Graphics2D) untuk tugas UAS Matematika Diskrit. Proyek ini mengimplementasikan Algoritma Welch-Powell secara visual dan interaktif untuk memecahkan masalah pewarnaan graf (Graph Coloring).

# Fitur Utama
1. Menggambar nodes dan edges secara langsung di atas kanvas dengan tata letak sirkular yang rapi.
2. Dilengkapi tombol "View Process" untuk melihat detail log eksekusi (Penghitungan Derajat -> Pengurutan Descending -> Pemberian Warna).
3. Menyediakan template graf siap pakai untuk pengujian cepat Zoo Layout (Kasus penempatan kandang hewan), Simple Graph, dan Mini Petersen Graph
4. Mendukung penambahan warna kustom ke dalam palet secara dinamis.
5. Input node dan edge (konflik) yang mudah digunakan dengan tema visual Forest yang memanjakan mata.

# Prasyarat
Untuk menjalankan aplikasi ini, kamu membutuhkan:
Java Development Kit (JDK) versi 8 atau yang lebih baru.

# Cara Menjalanakan Aplikasi
1. Menggunakan VS Code atau Code Editor lainnya
- Melakukan cloning/pengunduhan file .java yang telah disediakan
- Menjalankan code seperti menjalankan kode pada umumnya

2. Menggunakan terminal
- Melakukan clonning repositori
  ```bash
  git clone https://github.com/alannight/Java-welch-powell-UAS-matematika-diskrit
  ```
- Masuk kedalam repository
  ```bash
  cd nama_repository
  ```
- Melakukan Kompilasi file Java
  ```bash
  javac WelchPowellApp.java
  ```
- Menjalankan Aplikasi
  ```bash
  python3 WelchPowellApp.java
  ```

# Cara Kerja Algoritma Welch-Powell (Di dalam Aplikasi)
1. Mencari jumlah koneksi (edge/konflik) untuk setiap node.
2. Mengurutkan semua node berdasarkan derajatnya dari yang terbesar ke terkecil (descending).
3. Mengambil warna pertama, memberikannya ke node dengan derajat tertinggi.
4. Menyusuri daftar node yang belum diwarnai, dan memberikan warna yang sama jika node tersebut tidak bertetangga dengan node yang sudah memiliki warna tersebut.
5. Mengulangi proses dengan warna baru untuk sisa node yang belum diwarnai sampai semua node memiliki warna.
