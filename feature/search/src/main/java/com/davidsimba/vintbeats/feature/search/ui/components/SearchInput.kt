package com.davidsimba.vintbeats.feature.search.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.designsystem.theme.Fondo
import com.davidsimba.vintbeats.feature.search.R

private val PlaceholderColor = Color(0xFF71717A) // contraste >= 4.5:1 sobre blanco

/**
 * Campo de búsqueda de la pantalla Buscar: 48dp de alto, radio 10dp, blanco.
 * Con [onClick] es un acceso que no se escribe; sin él es un campo editable.
 */
@Composable
fun SearchInput(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    autoFocus: Boolean = false,
    onClick: (() -> Unit)? = null,
    leading: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = Fondo,
            modifier = Modifier.size(20.dp),
        )
    },
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        if (autoFocus && query.isEmpty()) focusRequester.requestFocus()
    }
    val description = stringResource(R.string.search_field_description)
    val hint = stringResource(R.string.search_hint)
    val shape = RoundedCornerShape(10.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(Color.White)
            .then(
                if (onClick != null) {
                    Modifier
                        .semantics { contentDescription = description }
                        .clickable(role = Role.Button, onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading()
        Spacer(Modifier.width(10.dp))
        if (onClick != null) {
            Text(text = hint, color = PlaceholderColor, fontSize = 16.sp, maxLines = 1)
        } else {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(color = Fondo, fontSize = 16.sp),
                cursorBrush = SolidColor(Fondo),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) {
                            Text(text = hint, color = PlaceholderColor, fontSize = 16.sp, maxLines = 1)
                        }
                        inner()
                    }
                },
            )
        }
    }
}
