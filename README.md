# TANICO — Project Documentation

TANICO adalah aplikasi Android marketplace yang menghubungkan petani/penjual limbah hasil panen dengan pembeli atau pelaku usaha yang memanfaatkan limbah organik. Aplikasi juga menyediakan materi edukasi dan kalkulator estimasi nilai ekonomi limbah.

## Technology baseline

- Android: Kotlin
- UI: Jetpack Compose + Material Design 3
- Backend: Supabase
- Database: Supabase PostgreSQL
- Authentication: Supabase Auth
- File storage: Supabase Storage
- Database access control: PostgreSQL Row Level Security (RLS)

## User roles

- `buyer`: mencari dan membeli produk limbah pertanian.
- `seller`: mengelola toko, produk, stok, dan pesanan masuk.

## Repository structure

```text
.
├── AGENTS.md
├── README.md
├── docs/
│   ├── architecture/
│   ├── database/
│   ├── design/
│   ├── development/
│   └── features/
├── app/                       # Dibuat oleh Android Studio
└── supabase/
    └── migrations/
```

## Start here

1. Baca `AGENTS.md`.
2. Baca `docs/architecture/system-overview.md`.
3. Baca `docs/architecture/application-architecture.md`.
4. Baca `docs/database/database-design.md` dan `docs/database/table-definitions.md`.
5. Baca spesifikasi fitur terkait sebelum mengimplementasikannya.
6. Periksa status `Proposed`, `Agreed`, dan `Open decision` dalam dokumentasi.

## Status dokumen

Dokumentasi ini merupakan baseline awal berdasarkan laporan proyek dan keputusan teknis yang sudah disampaikan. Detail yang belum diputuskan ditandai secara eksplisit dan tidak boleh diasumsikan oleh programmer atau AI agent.

## Important

Folder `app/` tidak disertakan karena project Android Studio belum dibuat. Setelah project dibuat, pindahkan/merge isi folder dokumentasi ini ke root repository. Jangan mengganti konfigurasi Gradle yang dihasilkan Android Studio tanpa alasan teknis.
