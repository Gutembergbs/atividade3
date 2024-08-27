package com.example.easybook

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var btnCadastrarLivro: Button
    private lateinit var btnLogoff: Button
    private lateinit var btnVerUser: Button
    private lateinit var txtNomeUsuario: TextView
    private lateinit var listaLivros: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        btnCadastrarLivro = findViewById(R.id.btnCadastrarLivro)
        btnLogoff = findViewById(R.id.btnLogoff)
        btnVerUser = findViewById(R.id.btnVerUser)
        txtNomeUsuario = findViewById(R.id.txtNomeUsuario)
        listaLivros = findViewById(R.id.listaLivros) // Certifique-se de que o ID corresponda ao ListView no layout XML

        dbHelper = DBHelper(this)

        // Setup button click listeners
        btnCadastrarLivro.setOnClickListener {
            // Navigate to CadastroLivroActivity
            val userId = intent.getIntExtra("USER_ID", -1)
            if (userId != -1) {
                carregarDetalhesUsuario(userId)
            }

            val intent = Intent(this, CadastroLivroActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)

        }

        btnLogoff.setOnClickListener {
            // Navigate back to SplashActivity (or LoginActivity as per flow)
            val intent = Intent(this, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Close current activity
        }

        btnVerUser.setOnClickListener {
            val userId = intent.getIntExtra("USER_ID", -1)
            if (userId != -1) {
                // Navigate to DetalhesUsuarioActivity with user ID
                val intent = Intent(this, DetalhesUsuarioActivity::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "ID do usuário não encontrado.", Toast.LENGTH_SHORT).show()
            }
        }

        // Load user info and display
        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId != -1) {
            carregarDetalhesUsuario(userId)
        }

        // Carrega a lista de livros cadastrados
        carregarLivros()
    }

    private fun carregarDetalhesUsuario(userId: Int) {
        val dbHelper = DBHelper(this)
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
            txtNomeUsuario.text = nome
        }
        cursor.close()
        db.close()
    }

    private fun carregarLivros() {
        try {
            val idUsuario = intent.getIntExtra("USER_ID", -1)
            val livros = dbHelper.getAllLivros(idUsuario.toString())
            // Obtenha todos os livros do banco de dados

            if (livros.isNotEmpty()) {
                val listaLivros = findViewById<ListView>(R.id.listaLivros) // Certifique-se de usar o ID correto do ListView

                val adapter = LivroAdapter(this, livros) // Use o adaptador personalizado
                listaLivros.adapter = adapter // Defina o adaptador para o ListView

                // Adiciona o onItemClickListener para o ListView
                listaLivros.setOnItemClickListener { _, _, position, _ ->
                    val livroSelecionado = livros[position]
                    val userId = intent.getIntExtra("USER_ID", -1)
                    val intent = Intent(this, DetalhesLivroActivity::class.java)
                    intent.putExtra("LIVRO_ID", livroSelecionado.id) // Supondo que cada livro tenha um ID
                    intent.putExtra("USER_ID", userId)

                    startActivity(intent)
                }
            } else {
                // Mostrar um Toast se não houver livros cadastrados
                Toast.makeText(this, "Nenhum livro encontrado.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Mostrar um Toast em caso de erro ao carregar os livros
            Toast.makeText(this, "Erro ao carregar os livros: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }



}
