package com.example.utspakdanan

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.Locale
import java.util.UUID

// Ini adalah ViewModel kita. Dia mewarisi kelas ViewModel dari AndroidX.
class AlarmViewModel : ViewModel() {

    // _uiState adalah state internal yang hanya bisa diubah di dalam ViewModel ini.
    // Ini berisi seluruh data yang perlu ditampilkan oleh UI.
    private val _uiState = MutableStateFlow(AlarmUiState())
    // uiState adalah versi read-only yang akan diakses oleh UI (Composable).
    val uiState: StateFlow<AlarmUiState> = _uiState.asStateFlow()

    // Fungsi ini dipanggil saat pengguna menekan tombol OK di TimePicker.
    fun addAlarm(calendar: Calendar) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
        val formattedHour = String.format(Locale.US, "%02d", if (hour % 12 == 0) 12 else hour % 12)
        val formattedMinute = String.format(Locale.US, "%02d", minute)
        val newAlarmTime = "$formattedHour:$formattedMinute $amPm"

        val newAlarm = Alarm(time = newAlarmTime)

        // `update` fungsinya cara aman untuk mengubah state.
        _uiState.update { currentState ->
            currentState.copy(
                alarms = (currentState.alarms + newAlarm).sortedBy { it.time }
            )
        }
    }

    // Fungsi untuk menghapus alarm berdasarkan ID uniknya
    fun deleteAlarm(id: UUID) {
        _uiState.update { currentState ->
            currentState.copy(
                alarms = currentState.alarms.filter { it.id != id }
            )
        }
    }

    // Fungsi untuk mengubah status aktif/nonaktif alarm.
    fun toggleAlarm(id: UUID) {
        _uiState.update { currentState ->
            val updatedAlarms = currentState.alarms.map { alarm ->
                if (alarm.id == id) {
                    alarm.copy(isActive = !alarm.isActive) // Balikkan nilainya
                } else {
                    alarm
                }
            }
            currentState.copy(alarms = updatedAlarms)
        }
    }

    // Fungsi untuk menampilkan atau menyembunyikan TimePicker.
    fun showTimePicker(show: Boolean) {
        _uiState.update { it.copy(showTimePicker = show) }
    }
}

// Kelas ini berguna untuk mengumpulkan semua state UI dalam satu objek.
data class AlarmUiState(
    val alarms: List<Alarm> = listOf(
        Alarm(time = "07:00 AM"),
        Alarm(time = "08:30 AM"),
        Alarm(time = "11:30 AM")
    ),
    val showTimePicker: Boolean = false
)