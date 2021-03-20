package ipvc.estg.mapapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ipvc.estg.mapapp.adapters.NoteAdapter
import ipvc.estg.mapapp.entities.Note
import ipvc.estg.mapapp.viewModel.NoteViewModel

class Note : AppCompatActivity(), NoteAdapter.OnNoteClickListener {

    private lateinit var notaViewModel: NoteViewModel
    private val newNotaActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        // recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = NoteAdapter(this,this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // view model
        notaViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        notaViewModel.allNote.observe(this, Observer { notas ->
            // Update the cached copy of the words in the adapter.
            notas?.let { adapter.setNotas(it) }
        })

        //Fab
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@Note, MainActivity::class.java)
            startActivityForResult(intent, newNotaActivityRequestCode)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newNotaActivityRequestCode && resultCode == Activity.RESULT_OK) {
            val titulo = data?.getStringExtra(MainActivity.EXTRA_REPLY_titulo)
            val descricao = data?.getStringExtra(MainActivity.EXTRA_REPLY_descricao)


            if (titulo!= null && descricao != null) {
                val nota = Note(titulo = titulo, desc = descricao)
                notaViewModel.insert(nota)
            }

        } else {
            Toast.makeText(
                    applicationContext,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show()
        }
    }

    override fun onItemClick(nota: Note, position: Int) {
        //Toast.makeText(this, nota.titulo, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Editar::class.java)
        intent.putExtra("id", nota.id)
        intent.putExtra("titulo", nota.titulo)
        intent.putExtra("descricao", nota.desc)

        startActivity(intent)
    }



}