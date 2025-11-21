package ridho21nov.pml.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ridho21nov.pml.myapplication.ui.theme.PML27NovemberTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PML27NovemberTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Agar bisa discroll jika layar kecil
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Judul Utama
        Text(
            text = "Kirim data ke Firebase dan SQLite",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- BAGIAN 1: Menggunakan Thread ---
        SectionHeader(text = "Menggunakan Thread (Coroutine)", color = Color(0xFF4CAF50)) // Hijau

        Spacer(modifier = Modifier.height(8.dp))

        ProgressItem(label = "Firebase", progress = 0f, count = "0 / 0")
        ProgressItem(label = "SQLite", progress = 0f, count = "0 / 0")

        Spacer(modifier = Modifier.height(16.dp))

        ActionButton(
            text = "Kirim Data",
            backgroundColor = Color(0xFF388E3C), // Hijau Tua
            onClick = { /* TODO: Handle Click */ }
        )

        Divider(modifier = Modifier.padding(vertical = 24.dp))

        // --- BAGIAN 2: Tanpa Thread ---
        SectionHeader(text = "Tanpa Menggunakan Thread", color = Color(0xFFFF5722)) // Oranye/Merah

        Spacer(modifier = Modifier.height(8.dp))

        ProgressItem(label = "Firebase", progress = 0f, count = "0 / 0")
        ProgressItem(label = "SQLite", progress = 0f, count = "0 / 0")

        Spacer(modifier = Modifier.height(16.dp))

        ActionButton(
            text = "Kirim Data",
            backgroundColor = Color(0xFFD32F2F), // Merah
            onClick = { /* TODO: Handle Click */ }
        )

        Spacer(modifier = Modifier.weight(1f)) // Dorong tombol bawah ke dasar layout

        // --- BAGIAN 3: Tombol Bawah (Cancel & Info) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Tombol Cancel
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)), // Oranye
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("CANCEL", color = Color.White)
            }

            // Tombol Info
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)), // Biru
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("INFO", color = Color.White)
            }
        }
    }
}

// --- COMPONENT REUSABLE (Dibuat terpisah agar kode rapi) ---

@Composable
fun SectionHeader(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ProgressItem(label: String, progress: Float, count: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.weight(1f).height(8.dp),
                color = Color.Blue, // Bisa disesuaikan
                trackColor = Color.LightGray,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = count, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionButton(text: String, backgroundColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(50), // Membuat tombol bulat lonjong
        modifier = Modifier.height(48.dp)
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    PML27NovemberTheme {
        MainScreen()
    }
}