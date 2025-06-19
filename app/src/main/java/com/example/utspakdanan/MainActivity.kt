package com.example.utspakdanan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val alarmViewModel: AlarmViewModel = viewModel()
                val uiState by alarmViewModel.uiState.collectAsState()

                AlarmClockUI(
                    uiState = uiState,
                    onAddAlarm = { alarmViewModel.addAlarm(it) },
                    onDeleteAlarm = { alarmViewModel.deleteAlarm(it) },
                    onToggleAlarm = { alarmViewModel.toggleAlarm(it) },
                    onShowTimePicker = { alarmViewModel.showTimePicker(it) }
                )
            }
        }
    }
}

@Composable
fun AlarmClockUI(
    uiState: AlarmUiState,
    onAddAlarm: (Calendar) -> Unit,
    onDeleteAlarm: (java.util.UUID) -> Unit,
    onToggleAlarm: (java.util.UUID) -> Unit,
    onShowTimePicker: (Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.background), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        IconButton(onClick = { }, modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp)) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options", tint = Color(0xFFDCD7C9))
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(top = 100.dp, start = 24.dp, end = 24.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Alarm", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDCD7C9))
            Spacer(modifier = Modifier.height(32.dp))
            AlarmList(
                alarms = uiState.alarms,
                onDeleteAlarm = onDeleteAlarm,
                onToggleAlarm = onToggleAlarm,
                onShowTimePicker = { onShowTimePicker(true) }
            )
        }

        if (uiState.showTimePicker) {
            TimePickerDialog(
                onDismiss = { onShowTimePicker(false) },
                onConfirm = {
                    onAddAlarm(it)
                    onShowTimePicker(false)
                }
            )
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavigation()
        }
    }
}

@Composable
fun AlarmList(
    alarms: List<Alarm>,
    onDeleteAlarm: (java.util.UUID) -> Unit,
    onToggleAlarm: (java.util.UUID) -> Unit,
    onShowTimePicker: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items = alarms, key = { it.id }) { alarm ->
            AlarmItem(
                alarm = alarm,
                onDelete = { onDeleteAlarm(alarm.id) },
                onToggle = { onToggleAlarm(alarm.id) }
            )
        }
        item {
            Button(
                onClick = onShowTimePicker,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA27B5C))
            ) {
                Text("Tambah Alarm", color = Color(0xFFDCD7C9))
            }
        }
    }
}

@Composable
fun AlarmItem(
    alarm: Alarm,
    onDelete: () -> Unit,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFFA27B5C), shape = MaterialTheme.shapes.medium).padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = alarm.time, fontSize = 20.sp, color = Color.White)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus Alarm", tint = Color.White)
            }
            Switch(
                checked = alarm.isActive,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(onDismiss: () -> Unit, onConfirm: (Calendar) -> Unit) {
    val timePickerState = rememberTimePickerState(is24Hour = false)
    val calendar = Calendar.getInstance()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                calendar.set(Calendar.MINUTE, timePickerState.minute)
                onConfirm(calendar)
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(state = timePickerState)
            }
        }
    )
}

@Composable
fun BottomNavigation() {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFFA27B5C)).navigationBarsPadding().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconTint = Color(0xFFF2E7D5)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = { }) { Icon(painter = painterResource(id = R.drawable.ic_alarm), "Alarm", tint = iconTint) }
            Text("Alarm", color = iconTint, fontSize = 12.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = { }) { Icon(painter = painterResource(id = R.drawable.ic_stopwatch), "Clock", tint = iconTint) }
            Text("Clock", color = iconTint, fontSize = 12.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = { }) { Icon(painter = painterResource(id = R.drawable.ic_world_clock), "Timer", tint = iconTint) }
            Text("Timer", color = iconTint, fontSize = 12.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = { }) { Icon(painter = painterResource(id = R.drawable.ic_timer), "Stopwatch", tint = iconTint) }
            Text("Stopwatch", color = iconTint, fontSize = 12.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        AlarmClockUI(
            uiState = AlarmUiState(),
            onAddAlarm = {},
            onDeleteAlarm = {},
            onToggleAlarm = {},
            onShowTimePicker = {}
        )
    }
}