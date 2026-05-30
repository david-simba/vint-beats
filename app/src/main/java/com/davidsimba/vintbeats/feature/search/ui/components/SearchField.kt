package com.davidsimba.vintbeats.feature.search.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.davidsimba.vintbeats.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGray
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit = {},
    autoFocus: Boolean = false,
    onClick: (() -> Unit)? = null,
    leadingIcon: @Composable () -> Unit = {
        Icon(Icons.Rounded.Search, contentDescription = null, tint = VintageBgDark)
    },
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (autoFocus) focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(VintageBgDark)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(stringResource(R.string.search_placeholder), color = VintageGray) },
            leadingIcon = leadingIcon,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = VintageWhite,
                unfocusedContainerColor = VintageWhite,
                focusedTextColor = VintageBgDark,
                unfocusedTextColor = VintageBgDark,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = VintageBgDark,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { onFocusChanged(it.isFocused) }
                .focusRequester(focusRequester)
        )
        if (onClick != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClick
                    )
            )
        }
    }
}
