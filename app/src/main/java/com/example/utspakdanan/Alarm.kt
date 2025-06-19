package com.example.utspakdanan

import java.util.UUID

// Ini adalah Model kita.
// Sebuah data class sederhana yang merepresentasikan satu buah alarm.
data class Alarm(
    val id: UUID = UUID.randomUUID(), // ID unik untuk setiap alarm, penting untuk menghapus
    val time: String,
    val isActive: Boolean = true
)