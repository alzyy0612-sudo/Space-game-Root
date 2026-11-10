SPACE GAME ROOT X1 — Android Studio source project
==================================================

Project ini adalah sumber aplikasi Android (belum berupa APK terkompilasi).
Lingkungan pembuatan ini tidak memiliki Android SDK/Gradle yang diperlukan untuk menghasilkan APK.

Fitur:
- Floating bubble yang dapat digeser.
- Dashboard popup di atas aplikasi lain.
- Pilihan Free Fire Global / Free Fire MAX.
- Meminta Android Game Mode performance atau standard jika didukung ROM.
- Pemeriksaan status root (su).
- Pemeriksaan RAM tersedia.
- GPU Cooler hanya berupa pengingat penggunaan aman; tidak mengubah clock/voltase.
- Tidak mematikan thermal protection dan tidak memaksa overclock.

Target:
- Android 8.0+; disusun untuk perangkat Android modern seperti Android 16.
- Package: com.spacegame.rootx1
- Target SDK 35; compile SDK 35.

Cara build APK:
1. Ekstrak ZIP.
2. Buka folder SPACE_GAME_ROOT_X1 melalui Android Studio.
3. Tunggu Gradle sync dan unduh Android SDK Platform 35 jika diminta.
4. Build > Build Bundle(s) / APK(s) > Build APK(s).
5. Izinkan "Display over other apps" untuk SPACE GAME ROOT X1.
6. Jalankan aplikasi lalu tekan AKTIFKAN FLOATING POPUP.

Catatan:
- Root tidak diwajibkan untuk floating panel atau Android Game Mode.
- Fitur Android Game Mode bergantung pada dukungan ROM; tidak semua perangkat menyediakan mode performance.
- Mode performance bisa meningkatkan panas dan konsumsi baterai.
- Tombol GPU Cooler tidak mengubah pengaturan kernel. Pendinginan nyata memerlukan penurunan beban, suhu ruangan lebih rendah, dan jeda bermain.
