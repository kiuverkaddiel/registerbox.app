package kiu.business.registerboxapp.view;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import core.model.product.IProduct;
import kiu.business.registerboxapp.view.activity.NotifierCurrentTicketChange;
import kiu.business.registerboxapp.view.activity.NotifierIpsProductListChange;
import kiu.business.registerboxapp.view.listener.ClickIpsProductListItem;

public class MyBarcodeDetector {

    private final PreviewView previewView;

    private CameraSelector cameraSelector;
    private ProcessCameraProvider cameraProvider;
    private Preview previewUseCase;
    private ImageAnalysis analysisUseCase;

    private NotifierCurrentTicketChange notifierCurrentTicketChange;
    private NotifierIpsProductListChange notifierIpsProductListChange;

    private List<IProduct> products;

    private Fragment fragment;

    public MyBarcodeDetector(Fragment fragment, PreviewView previewView, List<IProduct> products) {
        this.previewView = previewView;
        this.fragment = fragment;
        this.products = products;

        if (fragment instanceof NotifierCurrentTicketChange)
            notifierCurrentTicketChange = (NotifierCurrentTicketChange) fragment;

        if (fragment instanceof NotifierIpsProductListChange)
            notifierIpsProductListChange = (NotifierIpsProductListChange) fragment;
    }

    public void setupCamera() {


        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(fragment.requireContext());

        int lensFacing = CameraSelector.LENS_FACING_BACK;
        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindAllCameraUseCases();
                previewView.setVisibility(View.VISIBLE);
                previewView.setOnClickListener(v -> {
                    cameraProvider.unbindAll();
                    previewView.setVisibility(View.GONE);
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(fragment.requireContext()));
    }

    private void bindAllCameraUseCases() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            bindPreviewUseCase();
            bindAnalysisUseCase();
        }
    }

    private void bindPreviewUseCase() {
        if (cameraProvider == null) {
            return;
        }

        if (previewUseCase != null) {
            cameraProvider.unbind(previewUseCase);
        }

        Preview.Builder builder = new Preview.Builder();
        builder.setTargetRotation(getRotation());

        previewUseCase = builder.build();
        previewUseCase.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            cameraProvider
                    .bindToLifecycle(fragment, cameraSelector, previewUseCase);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return;
        }

        if (analysisUseCase != null) {
            cameraProvider.unbind(analysisUseCase);
        }

        Executor cameraExecutor = Executors.newSingleThreadExecutor();

        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
        builder.setTargetRotation(getRotation());

        analysisUseCase = builder.build();
        analysisUseCase.setAnalyzer(cameraExecutor, this::analyze);

        try {
            cameraProvider
                    .bindToLifecycle(fragment, cameraSelector, analysisUseCase);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected int getRotation() throws NullPointerException {
        return previewView.getDisplay().getRotation();
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void analyze(@NonNull ImageProxy image) {
        if (image.getImage() == null) return;

        InputImage inputImage = InputImage.fromMediaImage(
                image.getImage(),
                image.getImageInfo().getRotationDegrees()
        );

        BarcodeScanner barcodeScanner = BarcodeScanning.getClient();


        barcodeScanner.process(inputImage)
                .addOnSuccessListener(this::onSuccessListener)
                .addOnFailureListener(Throwable::printStackTrace)
                .addOnCompleteListener(task -> image.close());


    }

    private void onSuccessListener(List<Barcode> barcodes) {
        if (barcodes.size() > 0) {

            cameraProvider.unbindAll();
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
            toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP, 150);

            String productId = barcodes.get(0).getDisplayValue();

            int index = -1;
            IProduct product = null;
            for (int i = 0; i < products.size(); i++) {
                product = products.get(i);
                if (product.getProductId().equals(productId)) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                ClickIpsProductListItem clickIpsProductListItem = new ClickIpsProductListItem(
                        product,
                        index,
                        notifierCurrentTicketChange,
                        notifierIpsProductListChange
                );
                clickIpsProductListItem.setContext(fragment.getContext());
                clickIpsProductListItem.click();
            } else {
                // TODO extract text
                Toast.makeText(fragment.requireContext(),
                        "Producto no entontrado", Toast.LENGTH_SHORT)
                        .show();
            }

            setupCamera();
        }
    }
}
