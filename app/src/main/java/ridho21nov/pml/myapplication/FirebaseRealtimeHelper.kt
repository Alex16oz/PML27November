package ridho21nov.pml.myapplication

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object FirebaseRealtimeHelper {
    // Menggunakan by lazy agar inisialisasi dilakukan saat pertama kali dipanggil
    private val fbDb: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val ref: DatabaseReference by lazy { fbDb.getReference("transaksi") }

    suspend fun insert(
        id_tran: String,
        id_brg: Int,
        jml_brg: Int,
        tgl_tran: String,
        hrg_brg: Int
    ): Boolean = suspendCoroutine { cont ->
        val payload = mapOf(
            "id_tran" to id_tran,
            "id_brg" to id_brg,
            "jml_brg" to jml_brg,
            "tgl_tran" to tgl_tran,
            "hrg_brg" to hrg_brg,
            "total_hrg" to hrg_brg * jml_brg
        )

        ref.child(id_tran).setValue(payload)
            .addOnSuccessListener { cont.resume(true) }
            .addOnFailureListener { except -> cont.resumeWithException(except) }
    }
}