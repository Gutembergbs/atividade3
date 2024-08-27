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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream

@Suppress("DEPRECATION")
class DetalhesUsuarioActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var imgPerfil: ImageView
    private lateinit var txtNome: TextView
    private lateinit var txtEmail: TextView
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        // Inicialização das views
        imgPerfil = findViewById(R.id.imgFotoUsuario)
        txtNome = findViewById(R.id.txtNomeUsuarioDetalhes)
        txtEmail = findViewById(R.id.txtEmailUsuarioDetalhes)

        val btnEditarUsuario = findViewById<Button>(R.id.btnEditarUsuario)
        val btnVoltar = findViewById<Button>(R.id.btnVoltarPrincipalUsuario)

        dbHelper = DBHelper(this)

        userId = intent.getIntExtra("USER_ID", -1)
        if (userId != -1) {
            carregarDetalhesUsuario(userId)
        }

        btnEditarUsuario.setOnClickListener {
            // Navegar para CadastroUsuarioActivity com os detalhes do usuário
            val intent = Intent(this, CadastroUsuarioActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        btnVoltar.setOnClickListener {
            // Voltar para a MainActivity
            finish()
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
            val email = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUNA_EMAIL_USUARIO))
            val foto = cursor.getBlob(cursor.getColumnIndexOrThrow(DBHelper.COLUNA_FOTO_USUARIO))

            txtNome.text = nome
            txtEmail.text = email
            if (foto != null) {
                imgPerfil.setImageBitmap(BitmapFactory.decodeByteArray(foto, 0, foto.size))
            }
        }
        cursor.close()
        db.close()
    }
}
