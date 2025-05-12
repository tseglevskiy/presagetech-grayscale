package com.jollydroid.imageutildemo

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jollydroid.imageutildemo.ui.theme.ImageUtilDemoTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageUtilDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WithPermissions(
                        modifier = Modifier.padding(innerPadding),
                        permissions = listOf("android.permission.CAMERA")
                    ) { modifier ->
                        CameraPreview(
                            modifier = modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier, viewModel: ImageViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    // Grayscale image from ViewModel state
    val grayscaleBitmap by viewModel.grayscaleBitmap

    // Bind camera when preview is ready
    LaunchedEffect(previewView) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        val preview = Preview.Builder().build().apply {
            surfaceProvider = previewView.surfaceProvider
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, imageCapture
        )
    }

    Column(modifier = modifier.fillMaxSize()) {

        // Display the final grayscale bitmap (converted and ready)
        grayscaleBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Grayscale Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Black)
            )
        }

        // Live camera preview
        AndroidView(
            factory = { previewView }, modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        // Button to capture photo from camera
        CaptureButton(
            imageCapture = imageCapture,
            context = context,
            // Pass the captured image to the ViewModel for grayscale conversion
            onBitmapCaptured = { viewModel.onCapturedBitmap(it) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
    }
}


@Composable
private fun CaptureButton(
    imageCapture: ImageCapture,
    context: Context,
    onBitmapCaptured: (Bitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            imageCapture.takePicture(
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageCapturedCallback() {

                    // Image is captured from camera
                    override fun onCaptureSuccess(image: ImageProxy) {
                        val bitmap = image.toBitmap()
                        image.close()

                        Log.d("ImageUtilDemo", "Captured: ${bitmap.height}x${bitmap.width}")

                        onBitmapCaptured(bitmap) // Hand off to conversion handler
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            context, "Error: ${exception.message}", Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }, modifier = modifier.padding(16.dp)
    ) {
        Text("Take Picture")
    }
}


