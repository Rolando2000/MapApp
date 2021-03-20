package ipvc.estg.mapapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.mapapp.R
import ipvc.estg.mapapp.entities.Note

class NoteAdapter  internal constructor(
    context: Context, var clickListener: OnNoteClickListener
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notas = emptyList<Note>() // Cached copy of cities

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notaItemView: TextView = itemView.findViewById(R.id.textView)
        val descricaoItemView: TextView = itemView.findViewById(R.id.desc)

        fun initialize(nota: Note, action:OnNoteClickListener){
            notaItemView.text=nota.titulo
            descricaoItemView.text=nota.desc

            itemView.setOnClickListener{
                action.onItemClick(nota, adapterPosition)
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val current = notas[position]
        holder.notaItemView.text =  current.titulo
        holder.descricaoItemView.text= current.desc

        holder.initialize(notas.get(position),clickListener)

    }

    internal fun setNotas(notas: List<Note>) {
        this.notas = notas
        notifyDataSetChanged()
    }

    override fun getItemCount() =notas.size

    interface OnNoteClickListener{
        fun onItemClick(nota:Note, position: Int)
    }
}