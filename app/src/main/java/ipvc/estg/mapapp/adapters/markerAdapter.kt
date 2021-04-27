package ipvc.estg.mapapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.mapapp.R
import ipvc.estg.mapapp.api.marker
import retrofit2.Call

class markerAdapter(val marker: List<marker>) : RecyclerView.Adapter<MarkerViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): MarkerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_marker, parent, false)
        return MarkerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return marker.size
    }

    override fun onBindViewHolder(holder: MarkerViewHolder, position: Int) {
        return holder.bind(marker[position])
    }
}

class MarkerViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
    private val titulo: TextView = itemView.findViewById(R.id.titulo)
    private val descricao : TextView = itemView.findViewById(R.id.descricao)
    private val tipoProb: TextView = itemView.findViewById(R.id.tipoProb)

    fun bind(marker: marker) {
        titulo.text = marker.titulo
        descricao.text = marker.descricao
        tipoProb.text = marker.tipoProb
    }

}