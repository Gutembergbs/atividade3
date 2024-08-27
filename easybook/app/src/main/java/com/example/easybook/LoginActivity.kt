package com.example.easybook

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var edtNomeOuEmail: EditText
    private lateinit var edtSenha: EditText
    private lateinit var btnEntrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DBHelper(this)

        edtNomeOuEmail = findViewById(R.id.edtNomeEmail)
        edtSenha = findViewById(R.id.edtSenha)
        btnEntrar = findViewById(R.id.btnLogin)

        btnEntrar.setOnClickListener {
            val nomeOuEmail = edtNomeOuEmail.text.toString()
            val senha = edtSenha.text.toString()

            val userId = validarLogin(nomeOuEmail, senha)
            if (userId != null) {
                // Navega para a tela principal se o login for válido, enviando o ID do usuário
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
                finish() // Finaliza a atividade de login
            } else {
                // Exibe um Toast com a mensagem de erro se o login for inválido
                Toast.makeText(this, "Nome/Email ou senha inválidos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validarLogin(nomeOuEmail: String, senha: String): Int? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DBHelper.TABELA_USUARIO,
            arrayOf(DBHelper.COLUNA_ID_USUARIO, DBHelper.COLUNA_SENHA_USUARIO),
            "${DBHelper.COLUNA_NOME_USUARIO} = ? OR ${DBHelper.COLUNA_EMAIL_USUARIO} = ?",
            arrayOf(nomeOuEmail, nomeOuEmail),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUNA_ID_USUARIO))
            val senhaArmazenada = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUNA_SENHA_USUARIO))
            cursor.close()
            db.close()
            return if (senhaArmazenada == senha) idUsuario else null
        }

        cursor.close()
        db.close()
        return null
    }
}
