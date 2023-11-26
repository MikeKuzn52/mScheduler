package com.mikekuzn.mscheduler.features.editTask

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.mikekuzn.mscheduler.R

@Composable
fun <T>CustomDropdownMenu(repeat: T, array: ArrayList<T>, set: (repeat: T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var menuSize by remember { mutableStateOf(Size.Zero) }

    Box {
        Row(modifier = Modifier
            .clickable { expanded = !expanded }
            .padding(5.dp)
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                //This value is used to assign to the DropDown the same width
                menuSize = coordinates.size.toSize()
            },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = repeat.toString(),
                fontSize = dimensionResource(R.dimen.normalFontSize).value.sp,
            )
            Icon(
                painter =
                if (expanded) painterResource(id = R.drawable.i_up)
                else painterResource(id = R.drawable.i_down),
                "",
                Modifier.clickable { expanded = !expanded }
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { menuSize.width.toDp() })
        ) {
            array.forEach {
                DropdownMenuItem(
                    onClick = { expanded = false; set(it) },
                    text = {
                        Text(
                            text = it.toString(),
                            fontSize = dimensionResource(R.dimen.miniFontSize).value.sp
                        )
                    }
                )
            }
        }
    }
}
