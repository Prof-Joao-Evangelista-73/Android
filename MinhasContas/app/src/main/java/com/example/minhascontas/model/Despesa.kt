package com.example.minhascontas.model

data class Despesa(
    val tipoDespesa: String,
    val descricao: String,
    val tipo: String,
    val valor: String,
    val dataCadastro: String,
    val dataPagamento: String,
    val id: Long
)
