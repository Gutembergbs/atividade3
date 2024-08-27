package com.example.easybook

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class LivroAdapter(private val context: Context, private val livros: List<Livro>) : BaseAdapter() {

    override fun getCount(): Int = livros.size

    override fun getItem(position: Int): Any = livros[position]

    override fun getItemId(position: Int): Long = livros[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_livro, parent, false)
        val livro = livros[position]

        val txtTitulo = view.findViewById<TextView>(R.id.txtTituloLivro)
        val txtEditora = view.findViewById<TextView>(R.id.txtEditoraLivro)
        val txtGenero = view.findViewById<TextView>(R.id.txtGeneroLivro)
        val imgCapa = view.findViewById<ImageView>(R.id.imgCapaLivro)

        txtTitulo.text = livro.titulo
        txtEditora.text = livro.editora
        txtGenero.text = livro.genero

        // Configura a imagem da capa, se disponível
        livro.foto?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            imgCapa.setImageBitmap(bitmap)
        } ?: run {
            // Se a capa não estiver disponível, define uma imagem padrão
            imgCapa.setImageResource(R.drawable.ic_launcher_background)
        }

        return view
    }
}
