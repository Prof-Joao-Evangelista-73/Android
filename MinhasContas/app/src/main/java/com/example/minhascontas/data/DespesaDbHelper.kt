package com.example.minhascontas.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.minhascontas.model.Despesa

class DespesaDbHelper(context: Context) : SQLiteOpenHelper(context, "MinhasContas.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE despesas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipoDespesa TEXT,
                descricao TEXT,
                tipo TEXT,
                valor TEXT,
                dataCadastro TEXT,
                dataPagamento TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS despesas")
        onCreate(db)
    }

    fun inserirDespesa(despesa: Despesa): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("tipoDespesa", despesa.tipoDespesa)
            put("descricao", despesa.descricao)
            put("tipo", despesa.tipo)
            put("valor", despesa.valor)
            put("dataCadastro", despesa.dataCadastro)
            put("dataPagamento", despesa.dataPagamento)
        }
        return db.insert("despesas", null, values)
    }

    fun deletarDespesa(id: Long): Int {
        val db = writableDatabase
        return db.delete("despesas", "id = ?", arrayOf(id.toString()))
    }
    fun listarDespesas(): List<Despesa> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM despesas", null)
        val despesas = mutableListOf<Despesa>()

        while (cursor.moveToNext()) {
            val despesa = Despesa(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                tipoDespesa = cursor.getString(cursor.getColumnIndexOrThrow("tipoDespesa")),
                descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao")),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                valor = cursor.getString(cursor.getColumnIndexOrThrow("valor")),
                dataCadastro = cursor.getString(cursor.getColumnIndexOrThrow("dataCadastro")),
                dataPagamento = cursor.getString(cursor.getColumnIndexOrThrow("dataPagamento"))
            )
            despesas.add(despesa)
        }

        cursor.close()
        return despesas
    }
    fun atualizarDespesa(despesa: Despesa) {
        writableDatabase.use { db ->
            val values = ContentValues().apply {
                put("tipoDespesa", despesa.tipoDespesa)
                put("descricao", despesa.descricao)
                put("tipo", despesa.tipo)
                put("valor", despesa.valor)
                put("dataCadastro", despesa.dataCadastro)
                put("dataPagamento", despesa.dataPagamento)
            }
            db.update("despesas", values, "id = ?", arrayOf(despesa.id.toString()))
        }
    }

}
