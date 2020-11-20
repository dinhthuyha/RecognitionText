package com.example.recognitiontext.MainActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.recognitiontext.R;
import com.example.recognitiontext.util.DrawingView;
import com.example.recognitiontext.util.ImageUtils;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class DrawActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DrawActivity";
    private ImageView ivColor;
    private ImageView ivDone;
    private RadioGroup radioGroup;

    public static int currentColor = 0xFFFF8F00;
    public static int currentSize = 10;

    DrawingView drawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        setupUI();

        if (getIntent().getBooleanExtra("camera_mode", false)) {
            //openCamera();
        } else {
            addDrawingView(null);
        }

    }

//    private void openCamera() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        Uri uri = ImageUtils.getUri(this);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//
//        startActivityForResult(intent, 1);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1) {
////            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
////            Log.d(TAG, "onActivityResult: " + bitmap.getHeight() + " " + bitmap.getWidth());
//            Bitmap bitmap = ImageUtils.getBitmap(this);
//            addDrawingView(bitmap);
//        }
//    }

    private void addDrawingView(Bitmap bitmap) {
        LinearLayout linearLayout = findViewById(R.id.ll_draw);

        if (bitmap == null) {
            drawingView = new DrawingView(this, null);

            drawingView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            drawingView = new DrawingView(this, bitmap);

            drawingView.setLayoutParams(new LinearLayout.LayoutParams(
                    bitmap.getWidth(),
                    bitmap.getHeight()));
        }

        linearLayout.addView(drawingView);
    }

    private void setupUI() {
        ivColor = findViewById(R.id.iv_color);
        ivDone = findViewById(R.id.iv_done);
        radioGroup = findViewById(R.id.gr_pen_size);
        radioGroup.check(R.id.rb_medium);

        ivColor.setOnClickListener(this);
        ivDone.setOnClickListener(this);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.rb_thin: {
                        currentSize = 5;
                        break;
                    }
                    case R.id.rb_medium: {
                        currentSize = 10;
                        break;
                    }
                    case R.id.rb_strong: {
                        currentSize = 15;
                        break;
                    }
                }
            }
        });

        ivColor.setColorFilter(currentColor);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_color: {
                ColorPickerDialogBuilder
                        .with(this)
                        .setTitle("Choose color")
                        .initialColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {

                            }
                        })
                        .setPositiveButton("Ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                currentColor = selectedColor;
                                ivColor.setColorFilter(currentColor);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
                break;
            }
            case R.id.iv_done: {
                saveImage();
                break;
            }
        }
    }

    ProgressDialog progressDialog;

    private void saveImage() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        //1. get bitmap
        drawingView.setDrawingCacheEnabled(true);
        drawingView.buildDrawingCache();
        Bitmap bitmap = drawingView.getDrawingCache();

        //2. save bitmap to phone
        String path=ImageUtils.saveImage(bitmap, this);
        // DrawingUtil.saveImage(bitmap, this);

        Intent patch = new Intent( DrawActivity.this, MainActivity.class);
        patch.putExtra("path_file", path);
        startActivity(patch);
        //3. close this activity
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        progressDialog.dismiss();
    }
}
