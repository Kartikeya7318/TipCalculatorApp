package com.kartikeya.tipcalculatorapp

import android.app.assist.AssistContent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kartikeya.tipcalculatorapp.components.InputField
import com.kartikeya.tipcalculatorapp.ui.theme.TipCalculatorAppTheme
import com.kartikeya.tipcalculatorapp.util.calculateTotalPerPerson
import com.kartikeya.tipcalculatorapp.util.calculateTotalTip
import com.kartikeya.tipcalculatorapp.widgets.RoundIconButton

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // A surface container using the 'background' color from the theme
            Myapp {
                MainContent()
            }

        }
    }
}

@Composable
fun Myapp(content: @Composable () -> Unit) {
    TipCalculatorAppTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.background,

            ) {
            content.invoke()
        }
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent() {
    Column() {
        BillForm() { billAmt ->
            Log.d("AMT", "MainContent: ${billAmt.toInt() * 100}")
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    onValChange: (String) -> Unit = {}
) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()
    val totalPerson = remember {
        mutableStateOf(1)
    }
    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    if(totalBillState.value.isNotEmpty()){
        totalPerPersonState.value = calculateTotalPerPerson(
            totalBill = totalBillState.value.toDouble(),
            totalPerson = totalPerson.value,
            tipPercentage = tipPercentage
        )
    }

    TopHeader(totalPerPer = totalPerPersonState.value)
    Surface(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )
//            if (validState) {
//                Text(text = "Valid")
            Row(
                modifier = Modifier.padding(3.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Split",
                    modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    )
                )
                Spacer(modifier = Modifier.width(120.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    RoundIconButton(
                        imageVector = Icons.Default.Remove,
                        onClick = {
                            if (totalPerson.value > 1)
                                totalPerson.value = totalPerson.value - 1
//                            Log.d("Icon", "BillForm: Removed")
                            totalPerPersonState.value = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                totalPerson = totalPerson.value,
                                tipPercentage = tipPercentage
                            )
                        })
                    Text(
                        text = "${totalPerson.value}",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 9.dp, end = 9.dp)
                    )
                    RoundIconButton(
                        imageVector = Icons.Default.Add,
                        onClick = {
//                            Log.d("Icon", "BillForm: Add")
                            totalPerson.value = totalPerson.value + 1
                            totalPerPersonState.value = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                totalPerson = totalPerson.value,
                                tipPercentage = tipPercentage
                            )
                        })

                }
            }

            //Tip row

            Row(
                modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Tip",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)

                )
                Spacer(modifier = Modifier.width(200.dp))
                Text(
                    text = "$ ${tipAmountState.value}",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "$tipPercentage%")
                Spacer(modifier = Modifier.height(14.dp))
                //Slider
                Slider(
                    value = sliderPositionState.value,
                    onValueChange = { newVal ->
                        sliderPositionState.value = newVal
                        tipAmountState.value = calculateTotalTip(
                            totalBill = totalBillState.value.toDouble(),
                            tipPercentage = tipPercentage
                        )
                        totalPerPersonState.value = calculateTotalPerPerson(
                            totalBill = totalBillState.value.toDouble(),
                            totalPerson = totalPerson.value,
                            tipPercentage = tipPercentage
                        )
//                        Log.d("Slider", "BillForm: $newVal")
                    },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    steps = 5,
                    onValueChangeFinished = {
//                        Log.d(TAG, "BillForm: Finished")
                    }
                )
            }

//            }
//            else {
//                Box() {}
//            }
        }
    }
}


@Composable
fun TopHeader(totalPerPer: Double = 134.0) {
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(150.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPer)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5,
            )
            Text(
                text = "$$totalPerPer",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}


//@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipCalculatorAppTheme {
        Myapp {
            TopHeader()
        }
    }
}

