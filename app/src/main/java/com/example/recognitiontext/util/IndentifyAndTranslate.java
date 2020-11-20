package com.example.recognitiontext.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;

import org.w3c.dom.Text;


public class IndentifyAndTranslate {
    private static final String TAG = "IndentifyAndTranslate";


    public void identifyLanguage(StringBuffer result, String target, TextView txt, Context context) {

        String sourceText = result.toString();

        FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance()
                .getLanguageIdentification();

        //  mSourceLang.setText("Detecting..");
        identifier.identifyLanguage(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s.equals("und")) {
                    Log.d(TAG, "onSuccess: Language Not Identified");

                } else {
                    getLanguageCode(sourceText, s, target, txt);
                }
            }
        });

    }

    public void getLanguageCode(String result, String language, String target, TextView txt) {
        Log.d(TAG, "laguage:" + language);
        //  Log.d(TAG, "target:" + target);
        int langCode;
        switch (language) {
            case "hi":
                langCode = FirebaseTranslateLanguage.HI;
                break;
            case "vi":
                langCode = FirebaseTranslateLanguage.VI;
                break;
            case "en":
                langCode = FirebaseTranslateLanguage.EN;
                break;
            case "ko":
                langCode = FirebaseTranslateLanguage.KO;
                break;
            case "ar":
                langCode = FirebaseTranslateLanguage.AR;
                //  mSourceLang.setText("Arabic");

                break;
            case "ur":
                langCode = FirebaseTranslateLanguage.UR;
                break;
            // mSourceLang.setText("Urdu");
            default:
                langCode = 0;
        }
        Log.d(TAG, "getLanguageCode: " + langCode);
        int targetCode;
        switch (target) {
            case "hi":
                targetCode = FirebaseTranslateLanguage.HI;
                break;
            case "vi":
                targetCode = FirebaseTranslateLanguage.VI;
                break;
            case "en":
                targetCode = FirebaseTranslateLanguage.EN;
                break;
            case "ko":
                targetCode = FirebaseTranslateLanguage.KO;
                break;
            default:
                targetCode = 0;
        }
        Log.d(TAG, "getLanguageCode: " + targetCode);
        translateText(langCode, result.toString(), targetCode, txt);
    }

    public void translateText(int langCode, String sourceText, int targetCode, TextView txt) {
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                //from language
                .setSourceLanguage(langCode)
                // to language
                .setTargetLanguage(targetCode)
                .build();
        Log.d(TAG, "translateText: "+langCode+targetCode);
        final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
                .getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .build();


        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translator.translate(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.d(TAG, "onSuccess translate: " + s);

                        txt.setText(s);
                    }
                });
            }
        });
    }
}
