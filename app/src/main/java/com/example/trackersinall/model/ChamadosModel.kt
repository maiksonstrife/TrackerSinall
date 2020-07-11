package com.example.trackersinall.model

    //Esse model será um objeto <List> que armazenará essas variaveis Json
    //É usado no adaptador: que recebe o modelo e faz a conexão dos itens visuais no android com o método "setSeparadosListItems"
    //Ele também é usado pra fazer a chamada na conexão com o php, avisando dos itens que deverão ser recebidos
  //original  data class ThreeItemsList(var idchamado: String, var cliente: String, var endereco: String)
data class ChamadosModel(var idchamado: String, var cliente: String, var endereco: String, var motivo: String, var traco: String, var classificacao: String)

