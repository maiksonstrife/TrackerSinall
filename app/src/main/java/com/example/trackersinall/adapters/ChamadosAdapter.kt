package com.example.trackersinall.adapters

import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.example.trackersinall.R
import com.example.trackersinall.model.ChamadosModel
import com.example.trackersinall.view.DetalhesActivity
import kotlinx.android.synthetic.main.chamados_cardview.view.*

//Essa classe infla a cardview com dados usando o padrão ViewHolder, padrão necessário pra que o adaptador se encaixe na Recycleview
//RecycleView está no view "activity_chamados.xml", é preenchida pelo "chamados_cardview", durante execução do "ListSeparadosActivity"

class ChamadosAdapter (var context: Context) : RecyclerView.Adapter<ChamadosAdapter.ViewHolder>() {

        var list : List<ChamadosModel> = listOf()
        var color : String ?= null
        var chave : String ?= null

        //Este método é acessado para preencher a lista
        fun setSeparadosListItems(list: List<ChamadosModel>, color: String, chave: String){
            this.list = list
            this.color = color
            this.chave = chave
            notifyDataSetChanged()
        }

        //ViewHolder pattern: 4 métodos e uma classe, seu propósito é aumentar a performance do aparelho,
        //-Seu uso é obrigatório para a recycleview -A RecycleView é um substituto e melhoramento da ListView

        //Este método retorna a view de cada item da lista
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.chamados_cardview, parent, false)
            return ViewHolder(
                view
            )
        }
        //Este método retorna o tamanho da lista
        override fun getItemCount(): Int {
            return list.size
        }
        //Este método anexa os dados a lista
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.item1.text = list[position].idchamado + " - " + list[position].cliente + " - " + list[position].classificacao
            holder.item2.text = list[position].endereco
            holder.item3.text = list[position].motivo
            holder.cardView.setBackgroundColor(Color.parseColor(color))

            holder.itemView.setOnClickListener {
                val intent = Intent(context, DetalhesActivity::class.java)
                intent.putExtra("idchamado", list[position].idchamado)
                intent.putExtra("chave", chave)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startActivity(context, intent, null)
                } else {
                    context.startActivity(intent)
                }
            }
        }

        //Classe construtora que segura a listview
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val item1: TextView = itemView.item1
            val item2: TextView = itemView.item2
            val item3: TextView = itemView.item3
            val cardView : RelativeLayout = itemView.cardviewId
        }
}