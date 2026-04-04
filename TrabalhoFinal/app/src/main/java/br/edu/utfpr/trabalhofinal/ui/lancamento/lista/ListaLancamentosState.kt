package br.edu.utfpr.trabalhofinal.ui.lancamento.lista

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import br.edu.utfpr.trabalhofinal.data.Lancamento

data class ListaLancamentosState(
    val carregando: Boolean = false,
    val erroAoCarregar: Boolean = false,
    val lancamentos: List<Lancamento> = listOf()
)

data class LancamentoEstilo(
    val icon: ImageVector,
    val iconColor: Color,
    val textColor: Color
)