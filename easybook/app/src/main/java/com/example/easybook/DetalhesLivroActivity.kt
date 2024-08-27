package com.example.easybook

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory

class DetalhesLivroActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var imgUsuario: ImageView
    private lateinit var txtNomeUsuario: TextView
    private lateinit var imgCapa: ImageView
    private lateinit var txtTitulo: TextView
    private lateinit var txtEditora: TextView
    private lateinit var txtGenero: TextView
    private lateinit var checkBoxLido: CheckBox
    private lateinit var ratingBar: RatingBar
    private lateinit var btnEditarLivro: Button
    private lateinit var btnVoltarPrincipal: Button

    private var livroId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        dbHelper = DBHelper(this)

        imgUsuario = findViewById(R.id.imgUsuarioDetalhe)
        txtNomeUsuario = findViewById(R.id.txtNomeUsuarioDetalhe)
        imgCapa = findViewById(R.id.imgCapaLivro)
        txtTitulo = findViewById(R.id.txtTituloLivro)
        txtEditora = findViewById(R.id.txtEditoraLivro)
        txtGenero = findViewById(R.id.txtGeneroLivro)
        checkBoxLido = findViewById(R.id.chkLido)
        ratingBar = findViewById(R.id.ratingBar)
        btnEditarLivro = findViewById(R.id.btnEditarLivro)
        btnVoltarPrincipal = findViewById(R.id.btnVoltarPrincipal)

        livroId = intent.getIntExtra("LIVRO_ID", -1)
        val userId = intent.getIntExtra("USER_ID", -1)

        if (userId != -1) {
            carregarDetalhesUsuario(userId)
        }

        if (livroId != -1) {
            carregarDetalhesLivro(livroId)
        }

        checkBoxLido.setOnCheckedChangeListener { _, isChecked ->
            marcarLivroComoLido(livroId, isChecked)
        }

        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            atribuirNotaAoLivro(livroId, rating.toInt())
        }

        btnEditarLivro.setOnClickListener {
            editarLivro(livroId, userId)
        }

        btnVoltarPrincipal.setOnClickListener {
            voltarParaMain(userId)
        }
    }

    private fun carregarDetalhesUsuario(userId: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DBHelper.TABELA_USUARIO,
            null,
            "${DBHelper.COLUNA_ID_USUARIO} = ?",
            arrayOf(userId.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUNA_NOME_USUARIO))
            val foto = cursor.getBlob(cursor.getColumnIndexOrThrow(DBHelper.COLUNA_FOTO_USUARIO))
            txtNomeUsuario.text = nome
            if (foto != null) {
                imgUsuario.setImageBitmap(BitmapFactory.decodeByteArray(foto, 0, foto.size))
            }
        }
        cursor.close()
        db.close()
    }

    private fun carregarDetalhesLivro(livroId: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DBHelper.TABELA_LIVRO,
            null,
            "${DBHelper.COLUNA_ID_LIVRO} = ?",
            arrayOf(livroId.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val titulo = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUNA_TITULO_LIVRO))
            val editora = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUNA_EDITORA_LIVRO))
            val genero = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUNA_GENERO_LIVRO))
            val foto = cursor.getBlob(cursor.getColumnIndexOrThrow(DBHelper.COLUNA_FOTO_LIVRO))
            val lido = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUNA_LIDO_LIVRO)) == 1
            val nota = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUNA_NOTA_LIVRO))

            txtTitulo.text = titulo
            txtEditora.text = editora
            txtGenero.text = genero
            checkBoxLido.isChecked = lido
            ratingBar.rating = nota.toFloat()
            if (foto != null) {
                imgCapa.setImageBitmap(BitmapFactory.decodeByteArray(foto, 0, foto.size))
            }
        }
        cursor.close()
        db.close()
    }

    private fun marcarLivroComoLido(livroId: Int, lido: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COLUNA_LIDO_LIVRO, if (lido) 1 else 0)
        }
        db.update(DBHelper.TABELA_LIVRO, values, "${DBHelper.COLUNA_ID_LIVRO} = ?", arrayOf(livroId.toString()))
        db.close()
        Toast.makeText(this, if (lido) "Livro marcado como lido!" else "Livro marcado como não lido!", Toast.LENGTH_SHORT).show()
    }

    private fun atribuirNotaAoLivro(livroId: Int, nota: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COLUNA_NOTA_LIVRO, nota)
        }
        db.update(DBHelper.TABELA_LIVRO, values, "${DBHelper.COLUNA_ID_LIVRO} = ?", arrayOf(livroId.toString()))
        db.close()
        Toast.makeText(this, "Nota atribuída ao livro!", Toast.LENGTH_SHORT).show()
    }



    private fun editarLivro(livroId: Int, userId: Int) {
        val intent = Intent(this, CadastroLivroActivity::class.java)
        intent.putExtra("LIVRO_ID", livroId)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun voltarParaMain(userId: Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
        finish()
    }
}
