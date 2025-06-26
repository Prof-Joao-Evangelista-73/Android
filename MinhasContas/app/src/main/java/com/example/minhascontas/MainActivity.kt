package com.example.minhascontas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.minhascontas.ui.theme.MinhasContasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinhasContasTheme {
                Formulario()
            }
        }
    }
}

@Composable
fun Formulario() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Menu no topo
        Text(text = "Sistema de Controle de Gastos")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BotaoMenu("Cadastrar", Color(0xFF4CAF50)) {
                context.startActivity(Intent(context, CadastrarActivity::class.java))
            }
            BotaoMenu("Relatorio", Color(0xFF4CAF50)) {
                context.startActivity(Intent(context, RelatorioActivity::class.java))
            }
            BotaoMenu("Grafico", Color(0xFF4CAF50)) {
              //  context.startActivity(Intent(context, GraficoActivity::class.java))
            }
        }





        Spacer(modifier = Modifier.height(24.dp))


    }
}

@Composable
fun BotaoMenu(texto: String, cor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = cor)
    ) {
        Text(text = texto, color = Color.White)
    }
}

@Composable
fun BotaoPersonalizado(texto: String, cor: Color) {
    Button(
        onClick = { /* ação do botão */ },
        colors = ButtonDefaults.buttonColors(containerColor = cor)
    ) {
        Text(text = texto, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun FormularioPreview() {
    MinhasContasTheme {
        Formulario()
    }
}
