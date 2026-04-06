package br.edu.utfpr.trabalhofinal.ui.lancamento.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.trabalhofinal.R
import br.edu.utfpr.trabalhofinal.data.TipoLancamentoEnum
import br.edu.utfpr.trabalhofinal.ui.lancamento.form.composables.FormCheckbox
import br.edu.utfpr.trabalhofinal.ui.lancamento.form.composables.FormRadioButton
import br.edu.utfpr.trabalhofinal.ui.lancamento.form.composables.FormTextField
import br.edu.utfpr.trabalhofinal.ui.shared.composables.Carregando
import br.edu.utfpr.trabalhofinal.ui.shared.composables.ErroAoCarregar
import br.edu.utfpr.trabalhofinal.ui.theme.TrabalhoFinalTheme
import br.edu.utfpr.trabalhofinal.utils.toBrazilianDateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun FormularioLancamentoScreen(
    modifier: Modifier = Modifier,
    onVoltarPressed: () -> Unit,
    viewModel: FormularioLancamentoViewModel = viewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    LaunchedEffect(viewModel.state.lancamentoPersistidaOuRemovida) {
        if (viewModel.state.lancamentoPersistidaOuRemovida) {
            onVoltarPressed()
        }
    }
    val errorMessage = viewModel.state.codigoMensagem
        .takeIf { it > 0 }
        ?.let { stringResource(it) }
    LaunchedEffect(snackbarHostState, errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onMensagemExibida()
        }
    }

    val contentModifier: Modifier = modifier.fillMaxSize()
    if (viewModel.state.carregando) {
        Carregando(modifier = contentModifier)
    } else if (viewModel.state.erroAoCarregar) {
        ErroAoCarregar(
            modifier = contentModifier,
            onTryAgainPressed = viewModel::carregarLancamento
        )
    } else {
        Scaffold(
            modifier = contentModifier,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                AppBar(
                    lancamentoNovo = viewModel.state.lancamentoNovo,
                    processando = viewModel.state.salvando || viewModel.state.excluindo,
                    onVoltarPressed = onVoltarPressed,
                    onSalvarPressed = viewModel::salvarLancamento,
                    onExcluirPressed = viewModel::removerLancamento
                )
            }
        ) { paddingValues ->
            FormContent(
                modifier = Modifier.padding(paddingValues),
                processando = viewModel.state.salvando || viewModel.state.excluindo,
                descricao = viewModel.state.descricao,
                data = viewModel.state.data,
                valor = viewModel.state.valor,
                paga = viewModel.state.paga,
                tipo = viewModel.state.tipo,
                onDescricaoAlterada = viewModel::onDescricaoAlterada,
                onDataAlterada = viewModel::onDataAlterada,
                onValorAlterado = viewModel::onValorAlterado,
                onStatusPagamentoAlterado = viewModel::onStatusPagamentoAlterado,
                onTipoAlterado = viewModel::onTipoAlterado
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    modifier: Modifier = Modifier,
    lancamentoNovo: Boolean,
    processando: Boolean,
    onVoltarPressed: () -> Unit,
    onSalvarPressed: () -> Unit,
    onExcluirPressed: () -> Unit
) {
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            Text(if (lancamentoNovo) {
                stringResource(R.string.novo_lancamento)
            } else {
                stringResource(R.string.editar_lancamento)
            })
        },
        navigationIcon = {
            IconButton(onClick = onVoltarPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.voltar)
                )
            }
        },
        actions = {
            if (processando) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(60.dp)
                        .padding(all = 16.dp),
                    strokeWidth = 2.dp
                )
            } else {
                if (!lancamentoNovo) {
                    IconButton(onClick = onExcluirPressed) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.excluir)
                        )
                    }
                }
                IconButton(onClick = onSalvarPressed) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.salvar)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun AppBarPreview() {
    TrabalhoFinalTheme {
        AppBar(
            lancamentoNovo = true,
            processando = false,
            onVoltarPressed = {},
            onSalvarPressed = {},
            onExcluirPressed = {}
        )
    }
}

@Composable
private fun FormContent(
    modifier: Modifier = Modifier,
    processando: Boolean,
    descricao: CampoFormulario,
    data: CampoFormulario,
    valor: CampoFormulario,
    paga: CampoFormulario,
    tipo: CampoFormulario,
    onDescricaoAlterada: (String) -> Unit,
    onDataAlterada: (String) -> Unit,
    onValorAlterado: (String) -> Unit,
    onStatusPagamentoAlterado: (String) -> Unit,
    onTipoAlterado: (String) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    var showDatePickerDialog by remember {
        mutableStateOf(false)
    }

    val focusManager = LocalFocusManager.current

    if(showDatePickerDialog){
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState
                            .selectedDateMillis?.let { millis ->
                                onDataAlterada (millis.toBrazilianDateFormat())
                            }
                        showDatePickerDialog = false
                    }) {
                    Text(text = "Escolher data")
                }
            }) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .padding(all = 16.dp)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        val formTextFieldModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Notes,
                contentDescription = null,
                modifier = Modifier.size(40.dp).padding(end = 12.dp)
            )
            FormTextField(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.descricao),
                value = descricao.valor,
                onValueChanged = onDescricaoAlterada,
                enabled = !processando
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.AttachMoney,
                contentDescription = null,
                modifier = Modifier.size(40.dp).padding(end = 12.dp)
            )
            FormTextField(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.valor),
                value = valor.valor,
                onValueChanged = onValorAlterado,
                keyboardType = KeyboardType.Number,
                enabled = !processando
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(40.dp))

            FormTextField(
                modifier = Modifier
                    .weight(1f)
                    .onFocusEvent {
                        if (it.isFocused) {
                            showDatePickerDialog = true
                            focusManager.clearFocus()
                        }
                    },
                label = stringResource(R.string.data),
                value = data.valor,
                onValueChanged = {},
                readOnly = true,
                enabled = !processando,
                trailingIcon = {
                    Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = null)
                }
            )
        }
        val checkOptionsModifier = Modifier.padding(vertical = 8.dp)
        FormCheckbox(
            modifier = checkOptionsModifier,
            label = stringResource(R.string.paga),
            checked = paga.valor.toBoolean(),
            onCheckChanged = { newValue ->
                onStatusPagamentoAlterado(newValue.toString())
            },
            enabled = !processando
        )
        Row {
            FormRadioButton(
                modifier = checkOptionsModifier,
                value = TipoLancamentoEnum.DESPESA,
                groupValue = TipoLancamentoEnum.valueOf(tipo.valor),
                onValueChanged = { newValue ->
                    onTipoAlterado(newValue.toString())
                },
                label = stringResource(R.string.despesa),
                enabled = !processando
            )
            FormRadioButton(
                modifier = checkOptionsModifier,
                value = TipoLancamentoEnum.RECEITA,
                groupValue = TipoLancamentoEnum.valueOf(tipo.valor),
                onValueChanged = { newValue ->
                    onTipoAlterado(newValue.toString())
                },
                label = stringResource(R.string.receita),
                enabled = !processando
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun FormContentPreview() {
    TrabalhoFinalTheme {
        FormContent(
            processando = false,
            descricao = CampoFormulario(),
            data = CampoFormulario(),
            valor = CampoFormulario(),
            paga = CampoFormulario(),
            tipo = CampoFormulario(TipoLancamentoEnum.RECEITA.toString()),
            onDescricaoAlterada = {},
            onDataAlterada = {},
            onValorAlterado = {},
            onStatusPagamentoAlterado = {},
            onTipoAlterado = {}
        )
    }
}

@Composable
fun DataPicker(modifier: Modifier = Modifier) {

    val datePickerState = rememberDatePickerState()
    DatePicker(state = datePickerState)
}

@Preview(showSystemUi = true)
@Composable
private fun DataPickerPreview(){
    DataPicker()
}