package ridho21nov.pml.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SqliteDbHelper(context: Context) : SQLiteOpenHelper(context, "db_penjualan", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val t_transaksi = """
            CREATE TABLE t_transaksi (
                id_tran TEXT PRIMARY KEY,
                id_brg INTEGER NOT NULL,
                jml_brg INTEGER NOT NULL,
                tgl_tran TEXT NOT NULL,
                hrg_brg INTEGER NOT NULL,
                total_hrg INTEGER NOT NULL
            )
        """.trimIndent()
        db?.execSQL(t_transaksi)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Implementasi jika ada perubahan versi database
    }

    fun insert(
        id_tran: String,
        id_brg: Int,
        jml_brg: Int,
        tgl_tran: String,
        hrg_brg: Int
    ): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("id_tran", id_tran)
        cv.put("id_brg", id_brg)
        cv.put("jml_brg", jml_brg)
        cv.put("tgl_tran", tgl_tran)
        cv.put("hrg_brg", hrg_brg)
        cv.put("total_hrg", hrg_brg * jml_brg)

        return db.insert("t_transaksi", null, cv) != -1L
    }
}