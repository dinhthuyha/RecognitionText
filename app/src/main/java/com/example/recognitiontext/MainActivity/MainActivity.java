package com.example.recognitiontext.MainActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.recognitiontext.R;
import com.example.recognitiontext.util.IndentifyAndTranslate;
import com.example.recognitiontext.util.Recognition;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int WRITE_EXTERNAL = 1;
    private static final int ACTION_CAMERA = 2;
    private static final int SELECT_IMAGE = 3;
    Uri uri;
    byte[] byteArray;
    IndentifyAndTranslate indentifyAndTranslate = new IndentifyAndTranslate();
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    ImageView image;
    Bitmap bitmap;
    Recognition recognition;
    String target;
    ProgressDialog progressDialog;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.camera)
    FloatingActionButton camera;
    @BindView(R.id.brush)
    FloatingActionButton brush;
    @BindView(R.id.grabelly)
    FloatingActionButton grabelly;
    @BindView(R.id.fb_menu)
    FloatingActionMenu fbMenu;
    @BindView(R.id.btnRecog)
    Button btnRecog;
    @BindView(R.id.txtRecog)
    TextView txtRecog;
    @BindView(R.id.btnTranslate)
    Button btnTranslate;


    @BindView(R.id.txt)
    TextView txt;
    @BindView(R.id.layout_re)
    RelativeLayout layoutRe;
    @BindView(R.id.ll_main)
    LinearLayout llMain;
    StringBuffer result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initPermission();
        Intent intent = getIntent();
        String path = intent.getStringExtra("path_file");
        if (path != null) {
            uri = Uri.fromFile(new File(path));
            imageView.setImageURI(uri);
        }
        // llMain.setMovementMethod(new ScrollingMovementMethod());
        txtRecog.setMovementMethod(new ScrollingMovementMethod());
        txtRecog.setText("get Text");
        recognition = new Recognition();

    }

    @OnClick({R.id.camera, R.id.brush, R.id.grabelly, R.id.fb_menu, R.id.btnRecog, R.id.btnTranslate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, ACTION_CAMERA);
                fbMenu.close(false);
                break;
            case R.id.brush:
                Intent draw = new Intent(this, DrawActivity.class);
                startActivity(draw);
                fbMenu.close(false);
                break;
            case R.id.grabelly:
                selectImage();
                fbMenu.close(false);
                break;
            case R.id.fb_menu:
                break;
            case R.id.btnRecog:
                txtRecog.setText("");
                result = new StringBuffer("");
                Log.d(TAG, "onViewClicked: " + uri);

                recognition.runRecognitionText(this, uri, result, txtRecog);
                break;
            case R.id.btnTranslate:
                if (uri == null) {
                    Toast.makeText(MainActivity.this, "Please choose image or drawer", Toast.LENGTH_SHORT).show();
                } else {
                    if (result.equals("")) {
                        recognition.runRecognitionText(MainActivity.this, uri, result, txtRecog);
                    }
                }
                openDialog();
                break;
        }
    }

    private boolean initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted1");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted1");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL
                );
            }
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ACTION_CAMERA | requestCode == SELECT_IMAGE) && data != null && resultCode == RESULT_OK) {
            try {
                Log.d(TAG, "onActivityResult data: " + data);
                uri = data.getData();
                Log.d(TAG, "onActivityResult: " + uri);
                InputStream i = getContentResolver().openInputStream(data.getData());
                imageView.setImageURI(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
    }

    private void openDialog() {
        final int check = 0;
        Dialog dialog = new Dialog(this);
        dialog.setTitle("Language to translate");
        dialog.setContentView(R.layout.translate_language);
        Button submit = dialog.findViewById(R.id.submit);
        dialog.setCancelable(false);
        dialog.show();

        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioId = radioGroup.getCheckedRadioButtonId();
                switch (radioId) {
                    case R.id.radio_en:
                        target = "en";
                        Toast.makeText(MainActivity.this, "translate english", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radio_hi:
                        target = "hi";
                        Toast.makeText(MainActivity.this, "translate hindi", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radio_ko:
                        target = "ko";
                        Toast.makeText(MainActivity.this, "translate korea", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radio_vi:
                        target = "vi";
                        Toast.makeText(MainActivity.this, "translate Vietnamese", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        target = "und";
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "target: " + target);
                if (!target.equals("")) {
                    Toast.makeText(MainActivity.this, "" + target, Toast.LENGTH_SHORT).show();
                    dialog.cancel();

                    Log.d(TAG, "taret:" + target);
                    if (uri == null) {
                        Toast.makeText(MainActivity.this, "Please choose image or drawer", Toast.LENGTH_SHORT).show();
                    } else {
                        if (result.equals("")) {
                            recognition.runRecognitionText(MainActivity.this, uri, result, txtRecog);
                        }
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Translating ...");
                        progressDialog.show();
                        openDialogLoad();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please choose language", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openDialogLoad() {

        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.translate_success, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        TextView txtResult = dialogView.findViewById(R.id.txt_result);
        txtResult.setText("translating....");
        indentifyAndTranslate.identifyLanguage(result, target, txtResult, this);
        Log.d(TAG, "openDialogLoad: "+txtResult);
        alertDialog.show();
        progressDialog.dismiss();


        Button ok = dialogView.findViewById(R.id.buttonOk);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }


}

