package com.example.minhascontas

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.minhascontas.data.DespesaDbHelper
import com.example.minhascontas.model.Despesa
import com.example.minhascontas.ui.theme.MinhasContasTheme
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState

class RelatorioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MinhasContasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TelaRelatorio { finish() }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRelatorio(onVoltar: () -> Unit) {
    val context = LocalContext.current
    val dbHelper = remember { DespesaDbHelper(context) }
    var despesas by remember { mutableStateOf(listOf<Despesa>()) }
    var dataInicio by remember { mutableStateOf("") }
    var dataFim by remember { mutableStateOf("") }

    var receitas by remember { mutableStateOf(0.0) }
    var despesasTotal by remember { mutableStateOf(0.0) }

    // Formatação de data para exibição
    val sdfDisplay = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    // Formatação de data para o banco de dados (padrão ISO)
    val sdfDatabase = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    fun carregarDadosFiltrados() {
        val lista = mutableListOf<Despesa>()

        dbHelper.readableDatabase.use { db ->
            val cursor = db.rawQuery("SELECT * FROM despesas", null)

            cursor.use {
                while (it.moveToNext()) {
                    val despesa = Despesa(
                        id = it.getLong(it.getColumnIndexOrThrow("id")),
                        tipoDespesa = it.getString(it.getColumnIndexOrThrow("tipoDespesa")),
                        descricao = it.getString(it.getColumnIndexOrThrow("descricao")),
                        tipo = it.getString(it.getColumnIndexOrThrow("tipo")),
                        valor = it.getString(it.getColumnIndexOrThrow("valor")),
                        dataCadastro = it.getString(it.getColumnIndexOrThrow("dataCadastro")),
                        dataPagamento = it.getString(it.getColumnIndexOrThrow("dataPagamento"))
                    )

                    // Converter datas para Date para comparação
                    val dataCadastroDate = try {
                        sdfDatabase.parse(despesa.dataCadastro)
                    } catch (e: Exception) {
                        null
                    }

                    val dataPagamentoDate = try {
                        if (despesa.dataPagamento.isNotEmpty()) {
                            sdfDatabase.parse(despesa.dataPagamento)
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    // Usar data de pagamento se existir, senão usar data de cadastro
                    val dataParaFiltro = dataPagamentoDate ?: dataCadastroDate

                    val dentroDoPeriodo = try {
                        val inicio = if (dataInicio.isNotEmpty()) sdfDisplay.parse(dataInicio) else null
                        val fim = if (dataFim.isNotEmpty()) {
                            val date = sdfDisplay.parse(dataFim)
                            // Ajustar para o final do dia
                            val cal = Calendar.getInstance()
                            cal.time = date
                            cal.set(Calendar.HOUR_OF_DAY, 23)
                            cal.set(Calendar.MINUTE, 59)
                            cal.set(Calendar.SECOND, 59)
                            cal.time
                        } else null

                        (inicio == null || dataParaFiltro == null || !dataParaFiltro.before(inicio)) &&
                                (fim == null || dataParaFiltro == null || !dataParaFiltro.after(fim))
                    } catch (e: Exception) {
                        Log.e("FiltroData", "Erro ao filtrar por data", e)
                        true
                    }

                    if (dentroDoPeriodo) {
                        lista.add(despesa)
                    }
                }
            }
        }

        despesas = lista
        receitas = lista.filter { it.tipo.equals("receita", ignoreCase = true) }
            .sumOf { it.valor.toDoubleOrNull() ?: 0.0 }
        despesasTotal = lista.filter { it.tipo.equals("despesa", ignoreCase = true) }
            .sumOf { it.valor.toDoubleOrNull() ?: 0.0 }
    }

    LaunchedEffect(Unit, dataInicio, dataFim) {
        carregarDadosFiltrados()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Relatório Financeiro",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Filtros de data
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CampoDataCalendario(
                label = "Data Início",
                valor = dataInicio,
                modifier = Modifier.weight(1f),
                onDataSelecionada = { dataInicio = it }
            )
            CampoDataCalendario(
                label = "Data Fim",
                valor = dataFim,
                modifier = Modifier.weight(1f),
                onDataSelecionada = { dataFim = it }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botões de ação
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { carregarDadosFiltrados() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text("Aplicar")
            }
            Button(
                onClick = {
                    dataInicio = ""
                    dataFim = ""
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336),
                    contentColor = Color.White
                )
            ) {
                Text("Limpar")
            }
            Button(
                onClick = {
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.DAY_OF_MONTH, 1)
                    dataInicio = sdfDisplay.format(cal.time)
                    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                    dataFim = sdfDisplay.format(cal.time)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9E9E9E),
                    contentColor = Color.White
                )
            ) {
                Text("Mês Atual")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resumo Financeiro
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Receitas: R$ %.2f".format(receitas),
                    color = Color(0xFF388E3C)
                )
                Text(
                    text = "Despesas: R$ %.2f".format(despesasTotal),
                    color = Color(0xFFD32F2F)
                )
                Text(
                    text = "Saldo: R$ %.2f".format(receitas - despesasTotal),
                    style = MaterialTheme.typography.titleMedium,
                    color = if ((receitas - despesasTotal) >= 0) Color(0xFF388E3C) else Color(0xFFD32F2F)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Registros (${despesas.size})",
            style = MaterialTheme.typography.titleSmall
        )

        // Lista de registros
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(despesas) { despesa ->
                val backgroundColor = when {
                    despesa.tipo.equals("receita", ignoreCase = true) -> Color(0xFFC8E6C9)
                    despesa.dataPagamento.isBlank() -> Color(0xFFFFCDD2)
                    else -> Color(0xFFFFEBEE)
                }

                var showDeleteDialog by remember { mutableStateOf(false) }

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Confirmar exclusão") },
                        text = { Text("Tem certeza que deseja excluir esta conta?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    dbHelper.deletarDespesa(despesa.id)
                                    carregarDadosFiltrados()
                                    showDeleteDialog = false
                                }
                            ) {
                                Text("Excluir")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showDeleteDialog = false }
                            ) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = backgroundColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Tipo: ${despesa.tipoDespesa}")
                                Text("Descrição: ${despesa.descricao}")
                                Text("Valor: R$ ${despesa.valor}")
                                Text("Data Pagamento: ${
                                    if (despesa.dataPagamento.isNotEmpty()) {
                                        try {
                                            sdfDisplay.format(sdfDatabase.parse(despesa.dataPagamento))
                                        } catch (e: Exception) {
                                            despesa.dataPagamento
                                        }
                                    } else {
                                        "Não pago"
                                    }
                                }")
                                Text("Data Cadastro: ${
                                    try {
                                        sdfDisplay.format(sdfDatabase.parse(despesa.dataCadastro))
                                    } catch (e: Exception) {
                                        despesa.dataCadastro
                                    }
                                }")
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        // TODO: Implementar edição
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = Color(0xFF2196F3)
                                    )
                                }
                                IconButton(
                                    onClick = { showDeleteDialog = true },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Deletar",
                                        tint = Color(0xFFF44336)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onVoltar,
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            Text("Voltar")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoDataCalendario(
    label: String,
    valor: String,
    modifier: Modifier = Modifier,
    onDataSelecionada: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    // Atualizar calendário se valor mudar
    if (valor.isNotEmpty()) {
        try {
            val date = sdf.parse(valor)
            if (date != null) {
                calendar.time = date
            }
        } catch (e: Exception) {
            Log.e("CampoDataCalendario", "Erro ao parsear data", e)
        }
    }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var showDatePicker by remember { mutableStateOf(false) }

    // Mostrar DatePickerDialog quando showDatePicker for true
    if (showDatePicker) {
        AndroidView(
            factory = { ctx ->
                DatePickerDialog(
                    ctx,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        val formattedDate = "%02d/%02d/%04d".format(
                            selectedDay,
                            selectedMonth + 1,
                            selectedYear
                        )
                        onDataSelecionada(formattedDate)
                        showDatePicker = false
                    },
                    year,
                    month,
                    day
                ).apply {
                    setTitle("Selecione a $label")
                    setOnDismissListener { showDatePicker = false }
                    show()
                }
                // Retornar uma view vazia pois o dialog já está sendo mostrado
                android.widget.FrameLayout(ctx)
            }
        )
    }

    OutlinedTextField(
        value = valor,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = modifier.clickable { showDatePicker = true },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Abrir calendário"
            )
        },
        singleLine = true
    )
}