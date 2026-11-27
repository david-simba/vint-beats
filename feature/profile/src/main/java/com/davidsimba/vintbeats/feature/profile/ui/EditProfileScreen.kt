package com.davidsimba.vintbeats.feature.profile.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.davidsimba.vintbeats.core.common.VintCropImageContract
import com.davidsimba.vintbeats.core.common.VintCropImageContractOptions
import com.davidsimba.vintbeats.core.designsystem.components.inputs.TextField
import com.davidsimba.vintbeats.feature.profile.R
import com.davidsimba.vintbeats.feature.profile.ui.components.ProfileAvatar

private const val NAME_MAX_LENGTH = 30

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    val photoPath by viewModel.photoPath.collectAsStateWithLifecycle()
    val photoVersion by viewModel.photoVersion.collectAsStateWithLifecycle()

    var editName by remember(name) { mutableStateOf(name) }
    // La foto se guarda al elegirla; aquí solo se recuerda que cambió para activar "Guardar".
    var photoPicked by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val cropImage = rememberLauncherForActivityResult(VintCropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let {
                viewModel.onImagePicked(it)
                photoPicked = true
            }
        }
    }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                cropImage.launch(
                    VintCropImageContractOptions(
                        uri = it,
                        cropImageOptions = CropImageOptions(
                            aspectRatioX = 1,
                            aspectRatioY = 1,
                            fixAspectRatio = true,
                            guidelines = CropImageView.Guidelines.ON,
                            activityBackgroundColor = 0xFF121212.toInt(),
                            toolbarColor = 0xFF121212.toInt(),
                            toolbarTitleColor = 0xFFFFFFFF.toInt(),
                            toolbarBackButtonColor = 0xFFFFFFFF.toInt(),
                            borderLineColor = 0xFFDD7733.toInt(),
                            borderCornerColor = 0xFFDD7733.toInt(),
                            guidelinesColor = 0x44DD7733,
                        ),
                    )
                )
            }
        },
    )

    val pickPhoto = {
        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    val nameValid = editName.isNotBlank()
    val nameChanged = editName.trim() != name.trim()
    val canSave = nameValid && (nameChanged || photoPicked)
    val save = {
        viewModel.saveName(editName)
        focusManager.clearFocus()
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E10))
            .statusBarsPadding()
            .imePadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 12.dp, top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.edit_profile_cancel),
                    tint = Color(0xFFF4F4F5),
                    modifier = Modifier.size(24.dp),
                )
            }
            Text(
                text = stringResource(R.string.edit_profile_title),
                color = Color(0xFFF4F4F5),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f).padding(start = 4.dp),
            )
            val pillColor by animateColorAsState(
                if (canSave) Color(0xFFF4F4F5) else Color(0xFF27272A),
                tween(200),
                label = "save_pill",
            )
            val pillText by animateColorAsState(
                if (canSave) Color(0xFF0E0E10) else Color(0xFF71717A),
                tween(200),
                label = "save_pill_text",
            )
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .clickable(enabled = canSave, role = Role.Button) { save() },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(CircleShape)
                        .background(pillColor)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.edit_profile_save),
                        color = pillText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 32.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfileAvatar(
                photoPath = photoPath,
                photoVersion = photoVersion,
                size = 120.dp,
                name = editName.ifBlank { name },
                showCameraBadge = true,
                clickLabel = stringResource(R.string.edit_profile_change_photo_description),
                onClick = pickPhoto,
            )
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .clickable(role = Role.Button) { pickPhoto() }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.edit_profile_change_photo),
                    color = Color(0xFFD4D4D8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                val borderColor = when {
                    !nameValid -> Color(0xFFE5484D)
                    nameChanged -> Color(0xFFF4F4F5)
                    else -> Color(0xFF27272A)
                }
                TextField(
                    value = editName,
                    onValueChange = { editName = it.take(NAME_MAX_LENGTH) },
                    label = stringResource(R.string.edit_profile_label_name),
                    placeholder = stringResource(R.string.edit_profile_name_hint),
                    borderColor = borderColor,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { if (canSave) save() }),
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(
                            if (nameValid) R.string.edit_profile_name_help else R.string.edit_profile_name_help_empty
                        ),
                        color = Color(0xFF71717A),
                        fontSize = 12.sp,
                    )
                    Text(
                        text = "${editName.length}/$NAME_MAX_LENGTH",
                        color = Color(0xFF71717A),
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}
