package com.example.harfi.imageprocessing;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.image_edit) RelativeLayout imgEdit;
    @BindView(R.id.image) ImageView first;
    TextView dynamicTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        imgEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, final MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN){
                Log.d("Test", String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));

                AlertDialog.Builder alertDialog  = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Masukan notes kerusakan : ");
                alertDialog.setCancelable(true);

                final EditText input = new EditText(MainActivity.this);

                FrameLayout container = new FrameLayout(MainActivity.this);
                FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin= 40; // remember to scale correctly
                params.rightMargin= 40;
                input.setLayoutParams(params);
                container.addView(input);
                alertDialog.setView(container);

                int[] location = new int[2];
                view.getLocationOnScreen(location);
                float screenX = event.getRawX();
                float screenY = event.getRawY();
                final float viewX = screenX - location[0];
                final float viewY = screenY - location[1];

                // Set up the buttons
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dynamicTextView = new TextView(MainActivity.this);
                        dynamicTextView.setText(input.getText().toString());
                        dynamicTextView.setX(viewX);
                        dynamicTextView.setY(viewY);
                        dynamicTextView.setPadding(10,10,10,10);
                        dynamicTextView.setTypeface(null, Typeface.BOLD);
                        dynamicTextView.setTextColor(Color.WHITE);
                        dynamicTextView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        Log.d("test", ""+dynamicTextView.getX()+", "+ dynamicTextView.getY());

                        imgEdit.addView(dynamicTextView);

                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.create().show();
            }
            return true;
            }
        });

        if(dynamicTextView != null) {
            dynamicTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @OnClick(R.id.save)
    public void onClick(View v) {
        imgEdit.setDrawingCacheEnabled(true);
        imgEdit.buildDrawingCache();
        Bitmap finalBitmap = imgEdit.getDrawingCache();

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image.jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            Drawable verticalImage = new BitmapDrawable(getResources(), finalBitmap);
            first.setImageDrawable(verticalImage);

            Toast.makeText(MainActivity.this, "Success Save", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.reset)
    public void onClickReset(View v){
        first.setImageDrawable(null);
        imgEdit.destroyDrawingCache();
        imgEdit.removeAllViews();
    }

}
