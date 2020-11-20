package com.example.recognitiontext.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;

public class Recognition {

    public void runRecognitionText(Context context, Uri uri, StringBuffer result, TextView txtRecog) {
        try {
            txtRecog.setText("Recogniting...");
            FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(context, uri);

            FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            textRecognizer.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                                result.append(block.getText().toString() + "\n");
                            }
                            Log.d("AAA", "onSuccess: "+result);
                            txtRecog.setText(result);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
