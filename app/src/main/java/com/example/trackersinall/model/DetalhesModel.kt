package com.example.trackersinall.model

data class DetalhesModel(var idchamado: String, var data: String, var idfuncionario: String, var IdStatusChamado: String,
                         var motivo: String, var usuario: String, var cliente: String, var endereco: String,
                         var inicioatend: String, var fimatend: String, var dataatend: String, var departamento: String,
                         var classificacao: String, var finalizado: String, var idcontrato: String, var solicitante: String, var nrserie: String,
                         var modelo: String, var obsappweb: String, var obsprox_chamado: String, var sla: String,
                         var tipocontrato: String, var atendente: String, var latitude: String, var longitude: String,
                         var distanciaminima: String
                        )
