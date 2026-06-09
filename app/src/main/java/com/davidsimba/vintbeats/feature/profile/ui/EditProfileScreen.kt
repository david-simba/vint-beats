package com.davidsimba.vintbeats.feature.profile.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.davidsimba.vintbeats.core.util.VintCropImageContract
import com.davidsimba.vintbeats.core.util.VintCropImageContractOptions
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.feature.profile.ui.components.ProfileAvatar
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm
import com.davidsimba.vintbeats.shared.theme.vintageBgGradient

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    val photoPath by viewModel.photoPath.collectAsStateWithLifecycle()
    val photoVersion by viewModel.photoVersion.collectAsStateWithLifecycle()

    var editName by remember(name) { mutableStateOf(name) }
    val focusManager = LocalFocusManager.current

    val cropImage = rememberLauncherForActivityResult(VintCropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { viewModel.onImagePicked(it) }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(vintageBgGradient)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // App bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = VintageWhite,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = stringResource(R.string.edit_profile_title),
                color = VintageWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = {
                viewModel.saveName(editName)
                focusManager.clearFocus()
                onBack()
            }) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = stringResource(R.string.edit_profile_save),
                    tint = VintageWhite,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        ProfileAvatar(
            photoPath = photoPath,
            photoVersion = photoVersion,
            size = 100.dp,
            showCameraBadge = true,
            onClick = {
                imagePicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
        )

        Spacer(Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            Text(
                text = stringResource(R.string.edit_profile_label_name),
                color = VintageGrayMid,
                fontSize = 12.sp,
            )
            BasicTextField(
                value = editName,
                onValueChange = { editName = it },
                singleLine = true,
                textStyle = TextStyle(
                    color = VintageWhiteWarm,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                ),
                cursorBrush = SolidColor(VintageWhite),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    viewModel.saveName(editName)
                    focusManager.clearFocus()
                    onBack()
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
            )
            HorizontalDivider(color = VintageGrayDeep)
        }
    }
}
