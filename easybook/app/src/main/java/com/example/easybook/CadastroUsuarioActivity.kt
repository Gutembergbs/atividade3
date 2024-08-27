package com.example.easybook

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import android.widget.Toast

@Suppress("DEPRECATION")
class CadastroUsuarioActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var edtNome: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtSenha: EditText
    private lateinit var imgPerfil: ImageView
    private var imagemPerfil: Bitmap? = null
    private var userId: Int = -1

    companion object {
        const val SELECIONAR_IMAGEM_PERFIL = 1
        const val RESULTADO_EDITAR = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        dbHelper = DBHelper(this)

        edtNome = findViewById(R.id.edtNome)
        edtEmail = findViewById(R.id.edtEmail)
        edtSenha = findViewById(R.id.edtSenhaCadastro)
        imgPerfil = findViewById(R.id.imgPerfil)

        userId = intent.getIntExtra("USER_ID", -1)
        if (userId != -1) {
            carregarDadosUsuario(userId)
        }

        imgPerfil.setOnClickListener {
            escolherImagemPerfil()
        }

        val btnSalvarCadastro = findViewById<Button>(R.id.btnSalvarCadastro)
        btnSalvarCadastro.setOnClickListener {
            if (userId == -1) {
                salvarUsuario()
            } else {
                atualizarUsuario(userId)
            }
        }

        val btnVoltar = findViewById<Button>(R.id.btnVoltarCadastro)
        btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun carregarDadosUsuario(userId: Int) {
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
            val email = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUNA_EMAIL_USUARIO))
            val senha = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUNA_SENHA_USUARIO))
            val foto = cursor.getBlob(cursor.getColumnIndexOrThrow(DBHelper.COLUNA_FOTO_USUARIO))

            edtNome.setText(nome)
            edtEmail.setText(email)
            edtSenha.setText(senha)
            if (foto != null) {
                imagemPerfil = BitmapFactory.decodeByteArray(foto, 0, foto.size)
                imgPerfil.setImageBitmap(imagemPerfil)
            }
        }
        cursor.close()
        db.close()
    }

    private fun salvarUsuario() {
        val nome = edtNome.text.toString()
        val email = edtEmail.text.toString()
        val senha = edtSenha.text.toString()

        val valores = ContentValues().apply {
            put(DBHelper.COLUNA_NOME_USUARIO, nome)
            put(DBHelper.COLUNA_EMAIL_USUARIO, email)
            put(DBHelper.COLUNA_SENHA_USUARIO, senha)
            if (imagemPerfil != null) {
                put(DBHelper.COLUNA_FOTO_USUARIO, bitmapParaByteArray(imagemPerfil!!))
            }
        }

        val db = dbHelper.writableDatabase
        val id = db.insert(DBHelper.TABELA_USUARIO, null, valores)
        db.close()

        if (id != -1L) {
            // Mostrar Toast de sucesso
            Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
        } else {
            // Mostrar Toast de falha
            Toast.makeText(this, "Erro ao cadastrar o usuário.", Toast.LENGTH_SHORT).show()
        }

        // Navegar de volta para a MainActivity com o ID do usuário
        val intent = Intent().apply {
            putExtra("USER_ID", userId)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    private fun atualizarUsuario(userId: Int) {
        val nome = edtNome.text.toString()
        val email = edtEmail.text.toString()
        val senha = edtSenha.text.toString()

        val valores = ContentValues().apply {
            put(DBHelper.COLUNA_NOME_USUARIO, nome)
            put(DBHelper.COLUNA_EMAIL_USUARIO, email)
            put(DBHelper.COLUNA_SENHA_USUARIO, senha)
            if (imagemPerfil != null) {
                put(DBHelper.COLUNA_FOTO_USUARIO, bitmapParaByteArray(imagemPerfil!!))
            }
        }

        val db = dbHelper.writableDatabase
        val rowsAffected = db.update(DBHelper.TABELA_USUARIO, valores, "${DBHelper.COLUNA_ID_USUARIO} = ?", arrayOf(userId.toString()))
        db.close()

        if (rowsAffected > 0) {
            // Mostrar Toast de sucesso
            Toast.makeText(this, "Usuário atualizado com sucesso!", Toast.LENGTH_SHORT).show()
        } else {
            // Mostrar Toast de falha
            Toast.makeText(this, "Erro ao atualizar o usuário.", Toast.LENGTH_SHORT).show()
        }

        // Navegar de volta para a MainActivity com o ID do usuário
        val intent = Intent().apply {
            putExtra("USER_ID", userId)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    private fun escolherImagemPerfil() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, SELECIONAR_IMAGEM_PERFIL)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECIONAR_IMAGEM_PERFIL && resultCode == Activity.RESULT_OK && data != null) {
            val uriImagem: Uri? = data.data
            imagemPerfil = MediaStore.Images.Media.getBitmap(this.contentResolver, uriImagem)
            imgPerfil.setImageBitmap(imagemPerfil)
        }
    }

    private fun bitmapParaByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
