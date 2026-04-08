package com.polycampus.android.Attendance;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FaceVerificationHelper {
    private static final String TAG = "FaceVerificationHelper";
    private static final String MODEL_PATH = "facenet.tflite";
    private static final int INPUT_IMAGE_SIZE = 160; // Standard FaceNet input size

    private final Interpreter interpreter;
    private final FaceDetector faceDetector;

    public FaceVerificationHelper(Context context) throws IOException {
        // Initialize TFLite Interpreter
        interpreter = new Interpreter(loadModelFile(context));

        // Initialize ML Kit Face Detector
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL) // For liveness (blinking)
                .build();
        faceDetector = FaceDetection.getClient(options);
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public Task<List<Face>> detectFaces(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        return faceDetector.process(image);
    }

    public float[] getFaceEmbedding(Bitmap fullBitmap, Rect boundingBox) {
        // Crop face from original bitmap
        Bitmap croppedFace = Bitmap.createBitmap(fullBitmap, 
                Math.max(0, boundingBox.left), 
                Math.max(0, boundingBox.top), 
                Math.min(boundingBox.width(), fullBitmap.getWidth() - boundingBox.left), 
                Math.min(boundingBox.height(), fullBitmap.getHeight() - boundingBox.top));

        // Preprocess image for FaceNet
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(INPUT_IMAGE_SIZE, INPUT_IMAGE_SIZE, ResizeOp.ResizeMethod.BILINEAR))
                .add(new NormalizeOp(127.5f, 127.5f)) // Normalize to [-1, 1]
                .build();

        TensorImage tensorImage = new TensorImage(interpreter.getInputTensor(0).dataType());
        tensorImage.load(croppedFace);
        tensorImage = imageProcessor.process(tensorImage);

        // Run inference
        float[][] output = new float[1][128]; // FaceNet output is 128-dimensional embedding
        interpreter.run(tensorImage.getBuffer(), output);

        return output[0];
    }

    public static double calculateCosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public void close() {
        if (interpreter != null) interpreter.close();
        if (faceDetector != null) faceDetector.close();
    }
}
