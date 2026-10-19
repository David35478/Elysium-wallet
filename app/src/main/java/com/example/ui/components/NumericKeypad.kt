package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumKeyBg
import com.example.ui.theme.ElysiumKeyPressed
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary

@Composable
fun NumericKeypad(
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(text = "1", onClick = { onDigitClick("1") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "2", onClick = { onDigitClick("2") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "3", onClick = { onDigitClick("3") }, modifier = Modifier.weight(1f))
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(text = "4", onClick = { onDigitClick("4") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "5", onClick = { onDigitClick("5") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "6", onClick = { onDigitClick("6") }, modifier = Modifier.weight(1f))
        }

        // Row 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(text = "7", onClick = { onDigitClick("7") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "8", onClick = { onDigitClick("8") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "9", onClick = { onDigitClick("9") }, modifier = Modifier.weight(1f))
        }

        // Row 4
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(text = "·", onClick = { onDigitClick(".") }, modifier = Modifier.weight(1f), isDot = true)
            KeypadButton(text = "0", onClick = { onDigitClick("0") }, modifier = Modifier.weight(1f))
            KeypadIconButton(onClick = onBackspaceClick, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDot: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        label = "key_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isPressed) ElysiumKeyPressed else ElysiumKeyBg)
            .border(
                width = 1.dp,
                color = ElysiumCardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(if (isDot) "keypad_dot_button" else "keypad_${text}_button"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = if (isDot) 22.sp else 19.sp,
            fontWeight = FontWeight.Medium,
            color = ElysiumTextPrimary
        )
    }
}

@Composable
private fun KeypadIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        label = "key_icon_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isPressed) ElysiumKeyPressed else ElysiumKeyBg)
            .border(
                width = 1.dp,
                color = ElysiumCardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("keypad_backspace_button"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.Backspace,
            contentDescription = "Backspace",
            tint = ElysiumTextSecondary
        )
    }
}
