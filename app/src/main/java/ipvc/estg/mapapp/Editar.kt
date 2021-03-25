package ipvc.estg.mapapp

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ipvc.estg.mapapp.viewModel.NoteViewModel

class Editar : AppCompatActivity() {


    private lateinit var notaViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val id = getIntent().getIntExtra("id", 0)
        val titulo = getIntent().getStringExtra("titulo")
        val descricao = getIntent().getStringExtra("descricao")

        val tituloText = findViewById<TextView>(R.id.titulo)
        tituloText.text = titulo

        val descricaoText = findViewById<TextView>(R.id.descricao)
        descricaoText.text = descricao


        notaViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)


        val button = findViewById<Button>(R.id.guardar)
        button.setOnClickListener {
            val titulofinal = tituloText.text.toString()
            val descricaofinal = descricaoText.text.toString()

            notaViewModel.update(id, titulofinal, descricaofinal)

            finish()
        }

        val Apagar = findViewById<Button>(R.id.apagar)
        Apagar.setOnClickListener {
            val AlertaApagar = AlertDialog.Builder(this)
            AlertaApagar.setTitle(getString(R.string.apagar_nota))
            AlertaApagar.setMessage(getString(R.string.mensagem_apagar))
            AlertaApagar.setPositiveButton(getString(R.string.sim)){ dialog: DialogInterface?, which: Int ->
                notaViewModel.delete(id)
                finish()
            }

            AlertaApagar.setNegativeButton(getString(R.string.nao)){ dialog, id ->
                dialog.dismiss()
            }
            AlertaApagar.show()

        }

        val back = findViewById<Button>(R.id.back)
        back.setOnClickListener{
            finish()
        }

    }
}