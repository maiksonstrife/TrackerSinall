package com.example.trackersinall.view

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersinall.R
import com.example.trackersinall.util.SharedPreference
import com.example.trackersinall.adapters.ChamadosAdapter
import com.example.trackersinall.interfaces.*
import com.example.trackersinall.model.ChamadosModel
import kotlinx.android.synthetic.main.activity_chamados.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChamadosActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: ChamadosAdapter
    lateinit var apiInterface: Call<List<ChamadosModel>>
    private var chave : String?=null
    var sharedPreference: SharedPreference?=null
    private var color : String ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chamados)
        recyclerView = findViewById(R.id.listFiles)
        recyclerAdapter = ChamadosAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerAdapter
        sharedPreference = SharedPreference(this)

        //Pegando chave da Intent.putExtra()
        val extras = intent.extras
        if (extras != null) {
            chave = extras.getString("Chave")
        }
        checkSeparados()
    }

    fun checkSeparados () {

        progressBar.visibility = View.VISIBLE
        //pega a idfuncionario pra enviar pra query da api php
        val idfuncionario = sharedPreference!!.getValueString("idfuncionario")!!
        //Manda pra api a query idfuncionario

        when (chave) {
            "SP" -> {apiInterface = ISeparados.create().getSeparados(idfuncionario)
                title = "Separados"
                color = "#3F51B5"}
            "FN" -> {apiInterface = IFechados.create().getSeparados(idfuncionario)
                title = "Fechados"
                color = "#2e7d32"}
            "AP" -> {apiInterface = IAguardandoPecas.create().getSeparados(idfuncionario)
                title = "Aguardando Peças"
                color = "#212121"}
            "NI" -> {apiInterface = INaoIniciados.create().getSeparados(idfuncionario)
                title = "Nao Iniciados"
                color = "#dd2c00"}
            "PD" -> {apiInterface = IPendentes.create().getSeparados(idfuncionario)
                title = "Pendentes"
                color = "#ff6f00"}
            else -> {
                Toast.makeText(this@ChamadosActivity,"Chave inválida", Toast.LENGTH_SHORT).show()
            }
        }

        apiInterface.enqueue( object : Callback<List<ChamadosModel>>{
            override fun onResponse(call: Call<List<ChamadosModel>>?, response: Response<List<ChamadosModel>>?) {
                if(response != null){
                    recyclerAdapter.setSeparadosListItems(response.body()!!, color!!, chave!!)
                    progressBar.visibility = View.GONE
                    //O tratamento de cada chamado é tratado no Adapter
                    Log.d("chamadosResponse", response!!.body().toString()) //apresenta resultado no logcat se der certo
                }
            }

            override fun onFailure(call: Call<List<ChamadosModel>>?, t: Throwable?) {
                Log.d("chamadosResponseError", t.toString()) //apresenta resultado no logcat se falhar
                progressBar.visibility = View.GONE
                Toast.makeText(this@ChamadosActivity,"Dados não encontrados", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@ChamadosActivity, MenuSelectionActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this@ChamadosActivity, MenuSelectionActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startActivity(this@ChamadosActivity, intent, null)
            finish()
        } else {
            startActivity(intent)
            finish()
        }
    }
}

