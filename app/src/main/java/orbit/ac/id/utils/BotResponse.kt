package orbit.ac.id.utils

/**
 * BotResponse — chatbot sederhana untuk aplikasi Orbit.
 * Semua konten telah disesuaikan dengan konteks Indonesia.
 */
object BotResponse {

    fun basicResponses(message: String): String {
        val random = (0..2).random()
        val msg = message.lowercase().trim()

        return when {

            // Salam
            msg.contains("halo") || msg.contains("hai") || msg.contains("hello") -> {
                when (random) {
                    0 -> "Halo! Ada yang bisa saya bantu seputar pemesanan tiket bus?"
                    1 -> "Hai! Selamat datang di Orbit. Mau pesan tiket ke mana hari ini?"
                    else -> "Halo! Saya siap membantu perjalananmu. Ketik '1' untuk melihat menu bantuan."
                }
            }

            // Kabar
            msg.contains("apa kabar") || msg.contains("how are you") -> {
                when (random) {
                    0 -> "Baik dan siap melayani! Ada yang bisa saya bantu?"
                    1 -> "Siap 100%! Mau tanya soal tiket, rute, atau pembayaran?"
                    else -> "Alhamdulillah baik! Yuk, rencanakan perjalananmu bersama Orbit"
                }
            }

            // Menu bantuan
            msg.contains("menu") || msg.contains("bantuan") || msg.contains("help") || msg == "1" -> {
                "Menu Bantuan Orbit:\n" +
                "1 → Tentang Orbit\n" +
                "2 → Cara Pesan Tiket\n" +
                "3 → Metode Pembayaran\n" +
                "4 → Hubungi Kami\n" +
                "5 → Cara Membatalkan Tiket\n\n" +
                "Ketik angka untuk info lebih lanjut."
            }

            // Tentang Orbit
            msg.contains("tentang") || msg.contains("orbit") || msg == "1" && msg.contains("orbit") -> {
                "Tentang Orbit:\n" +
                "Orbit adalah aplikasi pemesanan tiket bus yang memudahkan perjalananmu.\n\n" +
                "Fitur utama:\n" +
                "• Pilih kursi favoritmu\n" +
                "• Berbagai operator bus (Sinar Jaya, Murni Jaya, Agra Mas, Restu Express)\n" +
                "• Pembayaran QR Code atau Cash\n" +
                "• Riwayat pemesanan lengkap"
            }

            // Cara pesan
            msg.contains("cara pesan") || msg.contains("pesan tiket") || msg.contains("booking") || msg == "2" -> {
                "Cara Pesan Tiket di Orbit:\n" +
                "1. Masuk ke tab 'Cari Tiket'\n" +
                "2. Pilih kota asal dan tujuan\n" +
                "3. Tentukan tanggal keberangkatan\n" +
                "4. Pilih bus yang sesuai\n" +
                "5. Pilih kursi\n" +
                "6. Konfirmasi dan bayar\n\n" +
                "Mudah kan?"
            }

            // Pembayaran
            msg.contains("bayar") || msg.contains("payment") || msg.contains("qr") || msg.contains("transfer") || msg == "3" -> {
                "Metode Pembayaran Orbit:\n" +
                "• QR Code — Scan dan bayar langsung\n" +
                "• Cash — Bayar tunai ke petugas bus\n\n" +
                "Pembayaran aman dan terkonfirmasi otomatis."
            }

            // Kontak
            msg.contains("kontak") || msg.contains("hubungi") || msg.contains("contact") || msg == "4" -> {
                "Hubungi Orbit:\n" +
                "• Email: orbit.support@gmail.com\n" +
                "• WhatsApp: 0812-ORBIT-APP\n" +
                "• Jam operasional: 06.00 – 22.00 WIB\n\n" +
                "Kami siap membantu dalam 1×24 jam!"
            }

            // Batalkan tiket
            msg.contains("batal") || msg.contains("cancel") || msg == "5" -> {
                "Pembatalan Tiket:\n" +
                "Saat ini pembatalan tiket dilakukan melalui:\n" +
                "1. Hubungi CS kami via WhatsApp atau Email\n" +
                "2. Sertakan nomor tiket dan alasan pembatalan\n\n" +
                "Perhatikan kebijakan refund dari masing-masing operator."
            }

            // Hitung / matematika
            msg.contains("hitung") || msg.contains("solve") -> {
                val equation = msg.substringAfterLast("hitung").trim()
                    .ifEmpty { msg.substringAfterLast("solve").trim() }
                try {
                    val answer = SolveMath.solveMath(equation)
                    "Hasilnya: $answer"
                } catch (e: Exception) {
                    "Hmm, saya tidak bisa menghitung itu. Coba format: 'hitung 5 + 3'"
                }
            }

            // Flip coin
            msg.contains("flip") && msg.contains("coin") || msg.contains("koin") -> {
                val result = if ((0..1).random() == 0) "Kepala (Heads)" else "Ekor (Tails)"
                "Saya melempar koin dan hasilnya: $result"
            }

            // Waktu
            msg.contains("jam") || msg.contains("waktu") || msg.contains("time") -> {
                val time = java.text.SimpleDateFormat("HH:mm, dd/MM/yyyy", java.util.Locale("id", "ID"))
                    .format(java.util.Date())
                "Sekarang pukul $time WIB"
            }

            // Rute populer
            msg.contains("rute") || msg.contains("trayek") || msg.contains("tujuan") -> {
                "Rute Populer di Orbit:\n" +
                "• Jakarta → Bandung\n" +
                "• Jakarta → Yogyakarta\n" +
                "• Jakarta → Surabaya\n" +
                "• Jakarta → Semarang\n" +
                "• Bandung → Surabaya\n\n" +
                "Buka tab 'Cari Tiket' untuk melihat jadwal & harga!"
            }

            // Tidak dimengerti
            else -> {
                when (random) {
                    0 -> "Maaf, saya kurang mengerti. Ketik 'menu' untuk melihat topik yang bisa saya bantu."
                    1 -> "Hmm, coba ulangi pertanyaanmu ya. Atau ketik '1' untuk menu bantuan."
                    else -> "Saya belum paham maksudnya. Coba tanya tentang: pesan tiket, pembayaran, atau rute."
                }
            }
        }
    }
}
