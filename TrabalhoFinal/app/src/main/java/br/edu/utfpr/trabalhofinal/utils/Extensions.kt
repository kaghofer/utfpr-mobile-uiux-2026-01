package br.edu.utfpr.trabalhofinal.utils

import br.edu.utfpr.trabalhofinal.data.Lancamento
import br.edu.utfpr.trabalhofinal.data.TipoLancamentoEnum
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.text.DecimalFormatSymbols
import java.util.Locale

fun List<Lancamento>.calcularSaldo(): BigDecimal = map {
    if (it.paga) {
        if (it.tipo == TipoLancamentoEnum.DESPESA) {
            it.valor.negate()
        } else {
            it.valor
        }
    } else {
        BigDecimal.ZERO
    }
}.sumOf { it }

fun List<Lancamento>.calcularProjecao(): BigDecimal = map {
    if (it.tipo == TipoLancamentoEnum.DESPESA) it.valor.negate() else it.valor
}.sumOf { it }

fun BigDecimal.formatar(): String {
    val symbols = DecimalFormatSymbols(Locale("pt", "BR"))

    val pattern = "R$#,##0.00;-R$#,##0.00"

    val formatter = DecimalFormat(pattern, symbols)
    return formatter.format(this)
}

fun LocalDate.formatar(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return format(formatter)
}