package com.davidsimba.vintbeats.feature.library.ui.createplaylist

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageOrange
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm
import java.io.File

@Composable
fun CreatePlaylistScreen(
    onBack: () -> Unit,
    onCreated: (Int) -> Unit,
    viewModel: CreatePlaylistViewModel = hiltViewModel(),
) {
    val cropImage = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { viewModel.onImagePicked(it) }
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                cropImage.launch(
                    CropImageContractOptions(
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
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 4.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                    tint = VintageWhite,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = stringResource(R.string.create_playlist_title),
                color = VintageWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
            )
        }

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(420.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(VintageBgDark)
                .border(
                    width = 1.dp,
                    color = if (viewModel.coverImagePath != null) VintageOrange.copy(alpha = 0.4f)
                            else VintageGrayDeep,
                    shape = RoundedCornerShape(14.dp),
                )
                .clickable {
                    imagePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            if (viewModel.coverImagePath != null) {
                AsyncImage(
                    model = File(viewModel.coverImagePath!!),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(14.dp)),
                )
                // Edit overlay badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .clip(RoundedCornerShape(50))
                        .background(VintageOrange),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null,
                        tint = VintageWhite,
                        modifier = Modifier.size(14.dp),
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AddPhotoAlternate,
                        contentDescription = null,
                        tint = VintageGrayDeep,
                        modifier = Modifier.size(36.dp),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.playlist_add_photo),
                        color = VintageGrayDeep,
                        fontSize = 11.sp,
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value = viewModel.name,
            onValueChange = viewModel::onNameChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.playlist_name_hint),
                    color = VintageGrayDeep,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VintageOrange,
                unfocusedBorderColor = VintageGrayDeep,
                focusedTextColor = VintageWhiteWarm,
                unfocusedTextColor = VintageWhiteWarm,
                cursorColor = VintageOrange,
                focusedContainerColor = VintageBgDark,
                unfocusedContainerColor = VintageBgDark,
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { viewModel.create(onCreated) },
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.create(onCreated) },
            enabled = viewModel.name.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VintageWhite,
                contentColor = VintageBgDark,
                disabledContainerColor = VintageGrayDeep.copy(alpha = 0.3f),
                disabledContentColor = VintageGrayMid,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(52.dp),
        ) {
            Text(
                text = stringResource(R.string.create_playlist_action),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
