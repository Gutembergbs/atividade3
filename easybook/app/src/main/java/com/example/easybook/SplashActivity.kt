package com.example.easybook

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val btnEntrar: Button = findViewById(R.id.btnEntrar)
        val btnCadastrar: Button = findViewById(R.id.btnCadastrar)

        btnEntrar.setOnClickListener {
            // Navega para a tela de login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnCadastrar.setOnClickListener {
            // Navega para a tela de cadastro de usuário
            val intent = Intent(this, CadastroUsuarioActivity::class.java)
            startActivity(intent)
        }
    }
}
