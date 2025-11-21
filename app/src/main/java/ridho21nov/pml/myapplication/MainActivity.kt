package ridho21nov.pml.myapplication

import android.os.Bundle
import android.os.SystemClock.sleep
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ridho21nov.pml.myapplication.ui.theme.PML27NovemberTheme
import java.time.Instant
import kotlin.random.Random
import java.util.UUID

// Helper class untuk menghitung data secara thread-safe
class DataCounter {
    var value = 0
    val mutex = Mutex()

    suspend fun increment() {
        mutex.withLock { value++ }
    }

    suspend fun get(): Int {
        mutex.withLock { return value }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inisialisasi DB Helper
        val dbHelper = SqliteDbHelper(this)

        setContent {
            PML27NovemberTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(dbHelper)
                }
            }
        }
    }
}

@Composable
fun MainScreen(dbHelper: SqliteDbHelper) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Scope untuk menjalankan coroutine
    val scrollState = rememberScrollState()

    // --- STATE VARIABLES (Pengganti TextView & ProgressBar) ---

    // State untuk Bagian 1 (Coroutine)
    var firebaseCountThread by remember { mutableIntStateOf(0) }
    var sqliteCountThread by remember { mutableIntStateOf(0) }
    var isProcessThreadRunning by remember { mutableStateOf(false) }

    // State untuk Bagian 2 (Tanpa Thread)
    var firebaseCountNoThread by remember { mutableIntStateOf(0) }
    var sqliteCountNoThread by remember { mutableIntStateOf(0) }
    var isProcessNoThreadRunning by remember { mutableStateOf(false) }

    // Flag Cancel
    var cancelProcess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Judul Utama
        Text(
            text = "Kirim data ke Firebase dan SQLite",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // ==========================================
        // BAGIAN 1: MENGGUNAKAN THREAD (COROUTINE)
        // ==========================================
        SectionHeader(text = "Menggunakan Thread (Coroutine)", color = Color(0xFF4CAF50)) // Hijau

        Spacer(modifier = Modifier.height(8.dp))

        ProgressItem(
            label = "Firebase",
            progress = firebaseCountThread / 100f,
            count = "$firebaseCountThread / 100"
        )
        ProgressItem(
            label = "SQLite",
            progress = sqliteCountThread / 100f,
            count = "$sqliteCountThread / 100"
        )

        Spacer(modifier = Modifier.height(16.dp))

        ActionButton(
            text = if (isProcessThreadRunning) "Sedang Mengirim..." else "Kirim Data",
            backgroundColor = Color(0xFF388E3C), // Hijau Tua
            enabled = !isProcessThreadRunning && !isProcessNoThreadRunning,
            onClick = {
                isProcessThreadRunning = true
                cancelProcess = false

                // Reset counter
                firebaseCountThread = 0
                sqliteCountThread = 0

                // Jalankan Coroutine
                scope.launch {
                    val firebaseCounter = DataCounter()
                    val sqliteCounter = DataCounter()

                    for (i in 1..100) {
                        if (cancelProcess) break
                        delay(300L) // Non-blocking delay

                        // Jalankan proses berat di IO Thread
                        launch(Dispatchers.IO) {
                            val id_tran = UUID.randomUUID().toString()
                            val id_brg = Random.nextInt(1, 100)
                            val jml_brg = Random.nextInt(1, 20)
                            val hrg_brg = Random.nextInt(1000, 100000)
                            val tgl_tran = Instant.now().toString()

                            // Insert SQLite (Async)
                            val sqliteDeferred = async {
                                try {
                                    dbHelper.insert(id_tran, id_brg, jml_brg, tgl_tran, hrg_brg)
                                } catch (e: Exception) { false }
                            }

                            // Insert Firebase (Async)
                            val firebaseDeferred = async {
                                try {
                                    FirebaseRealtimeHelper.insert(id_tran, id_brg, jml_brg, tgl_tran, hrg_brg)
                                } catch (e: Exception) { false }
                            }

                            // Tunggu hasil SQLite
                            if (sqliteDeferred.await()) {
                                sqliteCounter.increment()
                                // Update UI di Main Thread
                                withContext(Dispatchers.Main) {
                                    sqliteCountThread = sqliteCounter.get()
                                }
                            }

                            // Tunggu hasil Firebase
                            if (firebaseDeferred.await()) {
                                firebaseCounter.increment()
                                // Update UI di Main Thread
                                withContext(Dispatchers.Main) {
                                    firebaseCountThread = firebaseCounter.get()
                                }
                            }
                        }
                    }

                    // Selesai
                    isProcessThreadRunning = false
                    Toast.makeText(context, "Proses Thread Selesai", Toast.LENGTH_SHORT).show()
                }
            }
        )

        Divider(modifier = Modifier.padding(vertical = 24.dp))

        // ==========================================
        // BAGIAN 2: TANPA MENGGUNAKAN THREAD
        // ==========================================
        SectionHeader(text = "Tanpa Menggunakan Thread", color = Color(0xFFFF5722)) // Oranye

        Spacer(modifier = Modifier.height(8.dp))

        ProgressItem(
            label = "Firebase",
            progress = firebaseCountNoThread / 100f,
            count = "$firebaseCountNoThread / 100"
        )
        ProgressItem(
            label = "SQLite",
            progress = sqliteCountNoThread / 100f,
            count = "$sqliteCountNoThread / 100"
        )

        Spacer(modifier = Modifier.height(16.dp))

        ActionButton(
            text = if (isProcessNoThreadRunning) "Sedang Mengirim..." else "Kirim Data",
            backgroundColor = Color(0xFFD32F2F), // Merah
            enabled = !isProcessThreadRunning && !isProcessNoThreadRunning,
            onClick = {
                // PERINGATAN: Logic ini akan membekukan UI (ANR) sesuai demo modul
                isProcessNoThreadRunning = true
                cancelProcess = false
                firebaseCountNoThread = 0
                sqliteCountNoThread = 0

                // Kita gunakan scope.launch tapi menjalankan logika blocking di dalamnya
                // agar onClick handler selesai, tetapi efek 'hang' tetap terasa jika di main thread
                // Namun dalam Compose, 'sleep' di main thread benar-benar menghentikan recomposition.

                val frDb = FirebaseDatabase.getInstance().getReference("transaksi")

                // Simulasi blocking main thread
                // Note: Di Compose, UI tidak akan ter-update per iterasi jika loop ini berjalan
                // di Main Thread tanpa yield/delay suspend. Anda akan melihat UI freeze lalu tiba-tiba 100%.
                try {
                    for (i in 1..100) {
                        if (cancelProcess) break
                        sleep(300L) // Blocking sleep (Membekukan UI)

                        val id_tran = UUID.randomUUID().toString()
                        val id_brg = Random.nextInt(1, 100)
                        val jml_brg = Random.nextInt(1, 20)
                        val hrg_brg = Random.nextInt(1000, 100000)
                        val tgl_tran = Instant.now().toString()

                        // Insert SQLite
                        dbHelper.insert(id_tran, id_brg, jml_brg, tgl_tran, hrg_brg)
                        sqliteCountNoThread = i // Update state (tidak akan ter-render sampai loop selesai)

                        // Insert Firebase (Direct SDK call, asynchronous by nature but loop is blocking)
                        val payload = mapOf(
                            "id_tran" to id_tran,
                            "id_brg" to id_brg,
                            "jml_brg" to jml_brg,
                            "tgl_tran" to tgl_tran,
                            "hrg_brg" to hrg_brg,
                            "total_hrg" to hrg_brg * jml_brg
                        )
                        frDb.child(id_tran).setValue(payload)
                        firebaseCountNoThread = i
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isProcessNoThreadRunning = false
                    Toast.makeText(context, "Proses Non-Thread Selesai", Toast.LENGTH_SHORT).show()
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // ==========================================
        // BAGIAN 3: TOMBOL BAWAH
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Tombol Cancel
            Button(
                onClick = { cancelProcess = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("CANCEL", color = Color.White)
            }

            // Tombol Info
            Button(
                onClick = {
                    Toast.makeText(context, "Demo penggunaan thread pada aplikasi", Toast.LENGTH_LONG).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
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

// --- COMPONENT REUSABLE ---

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
                color = Color.Blue,
                trackColor = Color.LightGray,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = count, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionButton(text: String, backgroundColor: Color, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(50),
        modifier = Modifier.height(48.dp)
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    PML27NovemberTheme {
        MainScreen(SqliteDbHelper(LocalContext.current))
    }
}