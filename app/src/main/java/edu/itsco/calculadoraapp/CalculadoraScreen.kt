package edu.itsco.calculadoraapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculadoraScreen(
    modifier: Modifier = Modifier
) {
    var result by remember { mutableStateOf("0") }
    var equation by remember { mutableStateOf("") }

    val buttonList = listOf(
        "C", "()", "%", "/",
        "7", "8", "9", "x",
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "+/-", "0", ".", "="
    )

    fun handleButtonClick(button: String) {
        when (button) {
            "C" -> {
                equation = ""
                result = "0"
            }

            "=" -> {
                if (equation.isNotEmpty()) {
                    result = calculateExpression(equation)
                }
            }

            "+/-" -> {
                equation = toggleCurrentNumberSign(equation)
            }

            "()" -> {
                equation += button
            }

            else -> {
                if (result != "0" && button !in listOf("+", "-", "x", "/", "%")) {
                    equation = ""
                    result = "0"
                }
                equation += button
            }
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calculadora",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        val buttonsRow = buttonList.chunked(size = 4)

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            DisplaySection(
                equation = equation,
                result = result,
                modifier = modifier
                    .padding(all = 24.dp)
                    .fillMaxWidth()
                    .height(200.dp)
            )

            buttonsRow.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEach { number ->
                        val buttonShape = RoundedCornerShape(22.dp)

                        Button(
                            modifier = Modifier
                                .size(78.dp)
                                .shadow(
                                    elevation = 12.dp,
                                    shape = buttonShape,
                                    clip = false
                                )
                                .border(
                                    width = 1.dp,
                                    color = setupButtonBorderColor(number),
                                    shape = buttonShape
                                ),
                            shape = buttonShape,
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                focusedElevation = 0.dp,
                                hoveredElevation = 0.dp
                            ),
                            onClick = {
                                handleButtonClick(button = number)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = setupButtonColor(number),
                                contentColor = setupButtonTextColor(number)
                            )
                        ) {
                            Text(
                                text = when (number) {
                                    "+/-" -> "±"
                                    "()" -> "()"
                                    else -> number
                                },
                                textAlign = TextAlign.Center,
                                fontSize = when (number) {
                                    "+/-" -> 18.sp
                                    "()" -> 16.sp
                                    else -> 24.sp
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun calculateExpression(expression: String): String {
    if (expression.isEmpty()) return "0"

    val cleanExpression = expression.replace(" ", "")

    val operatorIndex = findMainOperatorIndex(cleanExpression)
    if (operatorIndex == -1) return cleanExpression

    val operator = cleanExpression[operatorIndex]

    val number1 = cleanExpression.substring(0, operatorIndex)
    val number2 = cleanExpression.substring(operatorIndex + 1)

    val num1 = normalizeNumber(number1).toDoubleOrNull() ?: return "Error"
    val num2 = normalizeNumber(number2).toDoubleOrNull() ?: return "Error"

    val result = when (operator) {
        '+' -> num1 + num2
        '-' -> num1 - num2
        'x' -> num1 * num2
        '/' -> if (num2 != 0.0) num1 / num2 else return "Error"
        '%' -> num1 % num2
        else -> return "Error"
    }

    return if (result % 1.0 == 0.0) {
        result.toInt().toString()
    } else {
        result.toString()
    }
}

private fun findMainOperatorIndex(expression: String): Int {
    for (i in 1 until expression.length) {
        if (expression[i] in listOf('+', '-', 'x', '/', '%')) {
            return i
        }
    }
    return -1
}

private fun normalizeNumber(value: String): String {
    return if (value.startsWith("+")) {
        value.removePrefix("+")
    } else {
        value
    }
}

private fun toggleCurrentNumberSign(expression: String): String {
    if (expression.isBlank()) return expression

    val operators = listOf('+', '-', 'x', '/', '%')
    var lastOperatorIndex = -1

    for (i in 1 until expression.length) {
        if (expression[i] in operators) {
            lastOperatorIndex = i
        }
    }

    return if (lastOperatorIndex == -1) {
        when {
            expression.startsWith("-") -> "+" + expression.removePrefix("-")
            expression.startsWith("+") -> "-" + expression.removePrefix("+")
            else -> "-$expression"
        }
    } else {
        val before = expression.substring(0, lastOperatorIndex + 1)
        val current = expression.substring(lastOperatorIndex + 1)

        if (current.isBlank()) return expression

        val updatedCurrent = when {
            current.startsWith("-") -> "+" + current.removePrefix("-")
            current.startsWith("+") -> "-" + current.removePrefix("+")
            else -> "-$current"
        }

        before + updatedCurrent
    }
}

@Composable
private fun setupButtonColor(number: String): Color =
    when (number) {
        "C", "()", "%" -> Color(0xFFA5A5A5)
        "/", "x", "-", "+", "=" -> Color(0xFFFF9F0A)
        "+/-" -> Color(0xFF333333)
        else -> Color(0xFF333333)
    }

@Composable
private fun setupButtonTextColor(number: String): Color =
    when (number) {
        "C", "()", "%" -> Color.Black
        "/", "x", "-", "+", "=" -> Color.White
        "+/-" -> Color.White
        else -> Color.White
    }

@Composable
private fun setupButtonBorderColor(number: String): Color =
    when (number) {
        "C", "()", "%" -> Color(0xFFD0D0D0)
        "/", "x", "-", "+", "=" -> Color(0xFFFFB347)
        "+/-" -> Color(0xFF4A4A4A)
        else -> Color(0xFF4A4A4A)
    }

@Composable
fun DisplaySection(
    equation: String,
    result: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(size = 20.dp))
            .background(Color(0xFF0B0B0B))
            .padding(all = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = equation,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Text(
                text = result,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalculadoraScreenPreview() {
    CalculadoraScreen()
}