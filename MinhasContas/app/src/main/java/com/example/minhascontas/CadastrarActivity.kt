package com.example.minhascontas

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.minhascontas.data.DespesaDbHelper
import com.example.minhascontas.model.Despesa
import com.example.minhascontas.ui.theme.MinhasContasTheme
import java.text.SimpleDateFormat
import java.util.*

class CadastrarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MinhasContasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TelaCadastro()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ContextCastToActivity")
@Composable
fun TelaCadastro() {
    val context = LocalContext.current
    var tipoDespesa by remember { mutableStateOf("") }
    var tipoReceita by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Despesa") }
    var valor by remember { mutableStateOf("") }
    var dataPagamento by remember { mutableStateOf("") }

    val dataCadastro = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    val calendario = Calendar.getInstance()
    val ano = calendario.get(Calendar.YEAR)
    val mes = calendario.get(Calendar.MONTH)
    val dia = calendario.get(Calendar.DAY_OF_MONTH)

    val opcoesDespesas = listOf(
        "Água", "Atividades Crianças", "Cartão", "Combustível", "Consórcio",
        "Escola", "Feira", "Farmácia", "IPVA", "IRRF", "Luz", "Manutenção",
        "Mercado", "Outros", "Prestação Casa", "Ração", "Telefone"
    )

    val opcoesReceitas = listOf(
        "Salário", "Investimentos", "Freelance", "Vendas", "Presente",
        "Reembolso", "Outros"
    )

    var expandedTipo by remember { mutableStateOf(false) }
    var expandedDespesa by remember { mutableStateOf(false) }
    var expandedReceita by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Cadastro de Despesa/Receita", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Menu suspenso para Tipo (Despesa/Receita)
        ExposedDropdownMenuBox(
            expanded = expandedTipo,
            onExpandedChange = { expandedTipo = !expandedTipo }
        ) {
            OutlinedTextField(
                value = tipo,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo (Despesa/Receita)") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedTipo,
                onDismissRequest = { expandedTipo = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Despesa") },
                    onClick = {
                        tipo = "Despesa"
                        expandedTipo = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Receita") },
                    onClick = {
                        tipo = "Receita"
                        expandedTipo = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Menu suspenso para Tipo de Despesa ou Receita (dependendo da seleção anterior)
        if (tipo == "Despesa") {
            ExposedDropdownMenuBox(
                expanded = expandedDespesa,
                onExpandedChange = { expandedDespesa = !expandedDespesa }
            ) {
                OutlinedTextField(
                    value = tipoDespesa,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de Despesa") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedDespesa,
                    onDismissRequest = { expandedDespesa = false }
                ) {
                    opcoesDespesas.forEach { opcao ->
                        DropdownMenuItem(
                            text = { Text(opcao) },
                            onClick = {
                                tipoDespesa = opcao
                                expandedDespesa = false
                            }
                        )
                    }
                }
            }
        } else {
            ExposedDropdownMenuBox(
                expanded = expandedReceita,
                onExpandedChange = { expandedReceita = !expandedReceita }
            ) {
                OutlinedTextField(
                    value = tipoReceita,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de Receita") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedReceita,
                    onDismissRequest = { expandedReceita = false }
                ) {
                    opcoesReceitas.forEach { opcao ->
                        DropdownMenuItem(
                            text = { Text(opcao) },
                            onClick = {
                                tipoReceita = opcao
                                expandedReceita = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Nome/Descrição (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor (R$)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Data de Cadastro: $dataCadastro")

        Spacer(modifier = Modifier.height(8.dp))

        // Campo com seletor de calendário
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = dataPagamento,
                onValueChange = {},
                label = { Text("Data de Pagamento") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        DatePickerDialog(
                            context,
                            { _, selectedYear, selectedMonth, selectedDay ->
                                dataPagamento = "%02d/%02d/%04d".format(
                                    selectedDay,
                                    selectedMonth + 1,
                                    selectedYear
                                )
                            },
                            ano, mes, dia
                        ).show()
                    }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val activity = LocalContext.current as? ComponentActivity

            Button(
                onClick = { activity?.finish() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    val dbHelper = DespesaDbHelper(context)
                    val novaDespesa = Despesa(
                        tipoDespesa = if (tipo == "Despesa") tipoDespesa else tipoReceita,
                        descricao = descricao,
                        tipo = tipo,
                        valor = valor,
                        dataCadastro = dataCadastro,
                        dataPagamento = dataPagamento,
                        id = 0 // Novo registro, ID será gerado automaticamente
                    )
                    dbHelper.inserirDespesa(novaDespesa)
                    activity?.finish()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Salvar")
            }
        }
    }
}