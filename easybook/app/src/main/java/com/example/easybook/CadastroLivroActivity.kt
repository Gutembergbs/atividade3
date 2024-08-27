package com.example.easybook

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory

class CadastroLivroActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var edtTitulo: EditText
    private lateinit var edtEditora: EditText
    private lateinit var edtGenero: EditText
    private lateinit var imgCapa: ImageView
    private var imagemCapa: Bitmap? = null
    private var livroId: Int = -1
    private var userId: Int = -1

    companion object {
        const val SELECIONAR_IMAGEM_CAPA = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_book)

        dbHelper = DBHelper(this)

        edtTitulo = findViewById(R.id.edtTitulo)
        edtEditora = findViewById(R.id.edtEditora)
        edtGenero = findViewById(R.id.edtGenero)
        imgCapa = findViewById(R.id.imgCapa)

        imgCapa.setOnClickListener {
            escolherImagemCapa()
        }

        val btnSalvarLivro = findViewById<Button>(R.id.btnSalvarLivro)
        btnSalvarLivro.setOnClickListener {
            salvarLivro()
        }

        // Obter o ID do livro do Intent (se houver) para edição
        livroId = intent.getIntExtra("LIVRO_ID", -1)
        if (livroId != -1) {
            carregarDadosLivro(livroId)
        }

        // Obter o ID do usuário do Intent de entrada
        userId = intent.getIntExtra("USER_ID", -1)
    }

    private fun escolherImagemCapa() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, SELECIONAR_IMAGEM_CAPA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECIONAR_IMAGEM_CAPA && resultCode == Activity.RESULT_OK && data != null) {
            val uriImagem: Uri? = data.data
            val bitmapOriginal = MediaStore.Images.Media.getBitmap(this.contentResolver, uriImagem)
            // Reduzir o tamanho da imagem
            val bitmapReduzido = resizeImage(bitmapOriginal, 300, 300)
            imgCapa.setImageBitmap(bitmapReduzido)
            imagemCapa = bitmapReduzido
        }
    }

    private fun carregarDadosLivro(livroId: Int) {
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

            edtTitulo.setText(titulo)
            edtEditora.setText(editora)
            edtGenero.setText(genero)
            if (foto != null) {
                imagemCapa = BitmapFactory.decodeByteArray(foto, 0, foto.size)
                imgCapa.setImageBitmap(imagemCapa)
            }
        }
        cursor.close()
        db.close()
    }

    private fun salvarLivro() {
        val titulo = edtTitulo.text.toString()
        val editora = edtEditora.text.toString()
        val genero = edtGenero.text.toString()

        if (titulo.isEmpty() || editora.isEmpty() || genero.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        val valores = ContentValues().apply {
            put(DBHelper.COLUNA_TITULO_LIVRO, titulo)
            put(DBHelper.COLUNA_EDITORA_LIVRO, editora)
            put(DBHelper.COLUNA_GENERO_LIVRO, genero)
            if (imagemCapa != null) {
                val resizedImage = resizeImage(imagemCapa!!, 300, 300)
                put(DBHelper.COLUNA_FOTO_LIVRO, bitmapParaByteArray(resizedImage))
            }
            put(DBHelper.COLUNA_USUARIO_ID, userId)  // Adiciona o ID do usuário
        }

        val db = dbHelper.writableDatabase
        if (livroId == -1) {
            db.insert(DBHelper.TABELA_LIVRO, null, valores)
            Toast.makeText(this, "Livro salvo com sucesso!", Toast.LENGTH_SHORT).show()
        } else {
            db.update(DBHelper.TABELA_LIVRO, valores, "${DBHelper.COLUNA_ID_LIVRO} = ?", arrayOf(livroId.toString()))
            Toast.makeText(this, "Livro atualizado com sucesso!", Toast.LENGTH_SHORT).show()
        }
        db.close()

        // Navegar para a tela de detalhes do livro com o ID do livro e ID do usuário
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("LIVRO_ID", livroId)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
        finish()
    }

    private fun bitmapParaByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        // Compressão JPEG para reduzir o tamanho do arquivo
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return stream.toByteArray()
    }

    private fun resizeImage(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height

        if (width > maxWidth) {
            val ratio = maxWidth.toFloat() / width
            width = maxWidth
            height = (height * ratio).toInt()
        }

        if (height > maxHeight) {
            val ratio = maxHeight.toFloat() / height
            height = maxHeight
            width = (width * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}
