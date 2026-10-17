package com.coop.wolf.linkfixerforrevancedyt

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.coop.wolf.linkfixerforrevancedyt.ui.theme.LinkFixerForReVancedYTTheme
import rikka.shizuku.Shizuku
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val SHIZUKU_PERMISSION_REQUEST_CODE = 1001

fun isShizukuAvailableAndGranted(): Boolean {
    return try {
        Shizuku.pingBinder() && (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED)
    } catch (_: Exception) {
        false
    }
}

fun executeShizukuCommands(commands: List<String>): Boolean {
    return try {
        for (cmd in commands) {
            val process = Shizuku.newProcess(arrayOf("sh", "-c", cmd), null, null)
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                android.util.Log.w("Shizuku", "Command failed with exit code $exitCode: $cmd")
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinkFixerForReVancedYTTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var bgUri by remember {
        val prefs = context.getSharedPreferences("link_fixer_prefs", android.content.Context.MODE_PRIVATE)
        val savedUriStr = prefs.getString("bg_uri", null)
        mutableStateOf(savedUriStr?.let { Uri.parse(it) })
    }
    var titleTextColor by remember { mutableStateOf(Color.Unspecified) }
    val imageLoader = remember { coil.ImageLoader(context) }

    LaunchedEffect(bgUri) {
        if (bgUri == null) {
            titleTextColor = Color.Unspecified
        } else {
            withContext(Dispatchers.IO) {
                try {
                    val request = ImageRequest.Builder(context)
                        .data(bgUri)
                        .allowHardware(false)
                        .build()
                    val result = imageLoader.execute(request)
                    val drawable = (result as? SuccessResult)?.drawable
                    val bitmap = when (drawable) {
                        is android.graphics.drawable.BitmapDrawable -> drawable.bitmap
                        else -> {
                            if (drawable != null && drawable.intrinsicWidth > 0 && drawable.intrinsicHeight > 0) {
                                val bmp = android.graphics.Bitmap.createBitmap(
                                    drawable.intrinsicWidth,
                                    drawable.intrinsicHeight,
                                    android.graphics.Bitmap.Config.ARGB_8888
                                )
                                val canvas = android.graphics.Canvas(bmp)
                                drawable.setBounds(0, 0, canvas.width, canvas.height)
                                drawable.draw(canvas)
                                bmp
                            } else {
                                null
                            }
                        }
                    }

                    if (bitmap != null) {
                        val width = bitmap.width
                        val height = bitmap.height
                        var totalLuminance = 0f
                        var count = 0
                        val stepX = maxOf(1, width / 30)
                        val stepY = maxOf(1, height / 30)
                        for (x in 0 until width step stepX) {
                            for (y in 0 until height step stepY) {
                                val pixel = bitmap.getPixel(x, y)
                                val r = android.graphics.Color.red(pixel) / 255f
                                val g = android.graphics.Color.green(pixel) / 255f
                                val b = android.graphics.Color.blue(pixel) / 255f
                                val luminance = 0.299f * r + 0.587f * g + 0.114f * b
                                totalLuminance += luminance
                                count++
                            }
                        }
                        val avgLuminance = if (count > 0) totalLuminance / count else 0.5f
                        val resolvedColor = if (avgLuminance > 0.5f) Color.Black else Color.White
                        withContext(Dispatchers.Main) {
                            titleTextColor = resolvedColor
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    val finalLine1Color = if (bgUri == null) {
        MaterialTheme.colorScheme.onBackground
    } else {
        if (titleTextColor == Color.Unspecified) MaterialTheme.colorScheme.onBackground else titleTextColor
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            bgUri = uri
            val prefs = context.getSharedPreferences("link_fixer_prefs", android.content.Context.MODE_PRIVATE)
            prefs.edit().putString("bg_uri", uri.toString()).apply()
        }
    }

    DisposableEffect(Unit) {
        val listener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == SHIZUKU_PERMISSION_REQUEST_CODE) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "We can fix this now!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "You didn't give it dummy.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        Shizuku.addRequestPermissionResultListener(listener)
        onDispose {
            Shizuku.removeRequestPermissionResultListener(listener)
        }
    }

    // Animated RGB gradient brush for Line 2
    val rainbowColors = listOf(
        Color.Red,
        Color(0xFFFF7F00), // Orange
        Color.Yellow,
        Color.Green,
        Color.Blue,
        Color(0xFF4B0082), // Indigo
        Color(0xFF8B00FF), // Violet
        Color.Red
    )
    val infiniteTransition = rememberInfiniteTransition(label = "rgbTransition")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rgbOffset"
    )
    val animatedRgbBrush = Brush.horizontalGradient(
        colors = rainbowColors,
        startX = offset,
        endX = offset + 1000f,
        tileMode = TileMode.Repeated
    )

    // Vertical rainbow gradient for bottom right button
    val rainbowVerticalGradient = Brush.verticalGradient(
        listOf(
            Color.Red,
            Color(0xFFFF7F00),
            Color.Yellow,
            Color.Green,
            Color.Blue,
            Color(0xFF4B0082),
            Color(0xFF8B00FF),
            Color.Red
        )
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Full screen background image when bgUri != null
        if (bgUri != null) {
            AsyncImage(
                model = bgUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // 1. Title Banner stacked 2 lines at top center
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ReVanced YT/YT Music",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = finalLine1Color
                )
            )
            Text(
                text = "link fixer!",
                style = TextStyle(
                    brush = animatedRgbBrush,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // 2. Center Stacked Buttons
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Top Button: "Fix it! Dummy"
            Button(
                onClick = {
                    if (!isShizukuAvailableAndGranted()) {
                        Toast.makeText(context, "I dont have Shizuku!", Toast.LENGTH_SHORT).show()
                    } else {
                        coroutineScope.launch(Dispatchers.IO) {
                            val fixCommands = listOf(
                                "pm set-app-links-user-selection --user 0 --package app.revanced.android.youtube true youtube.com",
                                "pm set-app-links-user-selection --user 0 --package app.revanced.android.youtube true youtu.be",
                                "pm set-app-links-user-selection --user 0 --package app.revanced.android.apps.youtube.music true music.youtube.com"
                            )
                            executeShizukuCommands(fixCommands)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "I've fixed it!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .width(260.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text(
                    text = "Fix it! Dummy",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Button: "Revert it! Dummy"
            Button(
                onClick = {
                    if (!isShizukuAvailableAndGranted()) {
                        Toast.makeText(context, "I dont have Shizuku!", Toast.LENGTH_SHORT).show()
                    } else {
                        coroutineScope.launch(Dispatchers.IO) {
                            val revertCommands = listOf(
                                "pm set-app-links-user-selection --user 0 --package app.revanced.android.youtube false youtube.com",
                                "pm set-app-links-user-selection --user 0 --package app.revanced.android.youtube false youtu.be",
                                "pm set-app-links-user-selection --user 0 --package app.revanced.android.apps.youtube.music false music.youtube.com"
                            )
                            executeShizukuCommands(revertCommands)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "I've fixed it!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .width(260.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
            ) {
                Text(
                    text = "Revert it! Dummy",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 3. Bottom Left Button: "Shizuku it! Dummy."
        Button(
            onClick = {
                try {
                    if (Shizuku.pingBinder()) {
                        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(context, "We can fix this now!", Toast.LENGTH_SHORT).show()
                        } else {
                            Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
                        }
                    } else {
                        Toast.makeText(context, "You didn't give it dummy.", Toast.LENGTH_SHORT).show()
                    }
                } catch (_: Exception) {
                    Toast.makeText(context, "You didn't give it dummy.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Text(
                text = "Shizuku it! Dummy.",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // 4. Bottom Right Buttons: "Remove BG" (if bgUri != null) and "Your BG!"
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (bgUri != null) {
                Button(
                    onClick = {
                        val prefs = context.getSharedPreferences("link_fixer_prefs", android.content.Context.MODE_PRIVATE)
                        prefs.edit().remove("bg_uri").apply()
                        bgUri = null
                        Toast.makeText(context, "Background removed", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text(
                        text = "Remove BG",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(rainbowVerticalGradient),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box {
                    val outlineColor = Color.Black
                    val fontSize = 14.sp
                    val fontWeight = FontWeight.Bold

                    val offsets = listOf(
                        (-1).dp to 0.dp,
                        1.dp to 0.dp,
                        0.dp to (-1).dp,
                        0.dp to 1.dp,
                        (-1).dp to (-1).dp,
                        1.dp to (-1).dp,
                        (-1).dp to 1.dp,
                        1.dp to 1.dp
                    )

                    for ((xOffset, yOffset) in offsets) {
                        Text(
                            text = "Your BG!",
                            modifier = Modifier.offset(x = xOffset, y = yOffset),
                            style = TextStyle(
                                color = outlineColor,
                                fontSize = fontSize,
                                fontWeight = fontWeight
                            )
                        )
                    }

                    // Main white text on top
                    Text(
                        text = "Your BG!",
                        color = Color.White,
                        fontSize = fontSize,
                        fontWeight = fontWeight
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
fun MainScreenPreview() {
    LinkFixerForReVancedYTTheme {
        MainScreen()
    }
}
