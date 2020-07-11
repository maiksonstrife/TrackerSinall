package com.example.trackersinall.adapters

import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.trackersinall.R
import com.example.trackersinall.model.DetalhesModel
import kotlinx.android.synthetic.main.detalhes_cardview.view.*

//Essa classe infla a cardview com dados usando o padrão ViewHolder, padrão necessário pra que o adaptador se encaixe na Recycleview
//RecycleView está no view "activity_chamados.xml", é preenchida pelo "chamados_cardview", durante execução do "ListSeparadosActivity"

class DetalhesAdapter (var context: Context) : RecyclerView.Adapter<DetalhesAdapter.ViewHolder>() {

    var list : List<DetalhesModel> = listOf()
    var color : String ?= null

    //Este método é acessado para preencher a lista
    fun setSeparadosListItems(list: List<DetalhesModel>, color: String){
        this.list = list
        this.color = color
        notifyDataSetChanged()
    }

    //ViewHolder pattern: 4 métodos e uma classe, seu propósito é aumentar a performance do aparelho,
    //-Seu uso é obrigatório para a recycleview -A RecycleView é um substituto e melhoramento da ListView

    //Este método retorna a view de cada item da lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.detalhes_cardview, parent, false)
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
        holder.tipocontrato.text = list[position].tipocontrato
        holder.data.text = list[position].data
        holder.idchamado.text = list[position].idchamado
        holder.nrserie.text = list[position].nrserie
        holder.classificacao.text = list[position].classificacao
        holder.modelo.text = list[position].modelo
        holder.cliente.text = list[position].cliente
        holder.endereco.text = list[position].endereco
        holder.atendente.text = list[position].atendente
        holder.motivo.text = list[position].motivo
        holder.obsprox_chamado.text = list[position].obsprox_chamado
        holder.obsappweb.text = list[position].obsappweb
        holder.departamento.text = list[position].departamento
        holder.solicitante.text = list[position].solicitante
        holder.inicioatend.text = list[position].inicioatend
        holder.fimatend.text = list[position].fimatend
        holder.sla.text = list[position].sla
        holder.cardviewcolor.setBackgroundColor(Color.parseColor(color))
        holder.itemView.setOnClickListener {

        }
    }

    //Classe construtora que segura a listview
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tipocontrato: TextView = itemView.tipocontrato
        val data: TextView = itemView.data
        val idchamado: TextView = itemView.idchamado
        val nrserie : TextView = itemView.nrserie
        val classificacao: TextView = itemView.classificacao
        val modelo: TextView = itemView.modelo
        val cliente: TextView = itemView.cliente
        val endereco : TextView = itemView.endereco
        val atendente: TextView = itemView.atendente
        val motivo: TextView = itemView.motivo
        val obsprox_chamado: TextView = itemView.obsprox_chamado
        val obsappweb : TextView = itemView.obsappweb
        val departamento: TextView = itemView.departamento
        val solicitante: TextView = itemView.solicitante
        val inicioatend: TextView = itemView.inicioatend
        val fimatend : TextView = itemView.fimatend
        val sla : TextView = itemView.sla
        val cardviewcolor  : RelativeLayout = itemView.detalhescardviewId
    }
}