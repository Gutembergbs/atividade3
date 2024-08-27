package com.example.easybook

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, NOME_BANCO, null, VERSAO_BANCO) {

    companion object {
        private const val VERSAO_BANCO = 1
        private const val NOME_BANCO = "EasyBook.db"

        const val TABELA_USUARIO = "Usuario"
        const val COLUNA_ID_USUARIO = "id"
        const val COLUNA_NOME_USUARIO = "nome"
        const val COLUNA_EMAIL_USUARIO = "email"
        const val COLUNA_SENHA_USUARIO = "senha"
        const val COLUNA_FOTO_USUARIO = "foto"

        const val TABELA_LIVRO = "Livro"
        const val COLUNA_ID_LIVRO = "id"
        const val COLUNA_TITULO_LIVRO = "titulo"
        const val COLUNA_EDITORA_LIVRO = "editora"
        const val COLUNA_GENERO_LIVRO = "genero"
        const val COLUNA_FOTO_LIVRO = "foto"
        const val COLUNA_LIDO_LIVRO = "lido"
        const val COLUNA_NOTA_LIVRO = "nota"
        const val COLUNA_USUARIO_ID = "usuario_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val criarTabelaUsuario = ("CREATE TABLE $TABELA_USUARIO (" +
                "$COLUNA_ID_USUARIO INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUNA_NOME_USUARIO TEXT," +
                "$COLUNA_EMAIL_USUARIO TEXT," +
                "$COLUNA_SENHA_USUARIO TEXT," +
                "$COLUNA_FOTO_USUARIO BLOB)")
        db.execSQL(criarTabelaUsuario)

        val criarTabelaLivro = ("CREATE TABLE $TABELA_LIVRO (" +
                "$COLUNA_ID_LIVRO INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUNA_TITULO_LIVRO TEXT," +
                "$COLUNA_EDITORA_LIVRO TEXT," +
                "$COLUNA_GENERO_LIVRO TEXT," +
                "$COLUNA_FOTO_LIVRO BLOB," +
                "$COLUNA_LIDO_LIVRO INTEGER DEFAULT 0," +
                "$COLUNA_NOTA_LIVRO INTEGER DEFAULT 0," +
                "$COLUNA_USUARIO_ID INTEGER," +
                "FOREIGN KEY($COLUNA_USUARIO_ID) REFERENCES $TABELA_USUARIO($COLUNA_ID_USUARIO))")
        db.execSQL(criarTabelaLivro)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABELA_USUARIO")
        db.execSQL("DROP TABLE IF EXISTS $TABELA_LIVRO")
        onCreate(db)
    }


    fun getAllLivros(idUsuario: String): List<Livro> {
        val livroList = mutableListOf<Livro>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABELA_LIVRO WHERE $COLUNA_USUARIO_ID = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(idUsuario))

        if (cursor.moveToFirst()) {
            do {
                val livro = Livro(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUNA_ID_LIVRO)),
                    titulo = cursor.getString(cursor.getColumnIndexOrThrow(COLUNA_TITULO_LIVRO)),
                    editora = cursor.getString(cursor.getColumnIndexOrThrow(COLUNA_EDITORA_LIVRO)),
                    genero = cursor.getString(cursor.getColumnIndexOrThrow(COLUNA_GENERO_LIVRO)),
                    foto = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUNA_FOTO_LIVRO)), // Pega a imagem como Blob
                    lido = cursor.getInt(cursor.getColumnIndexOrThrow(COLUNA_LIDO_LIVRO)) == 1, // Converte 1/0 para Boolean
                    nota = cursor.getInt(cursor.getColumnIndexOrThrow(COLUNA_NOTA_LIVRO))
                )
                livroList.add(livro)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return livroList
    }


}
