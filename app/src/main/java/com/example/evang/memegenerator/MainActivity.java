package com.example.evang.memegenerator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    TextView topText;
    TextView bottomText;
    EditText editTop;
    EditText editBottom;
    Button rotateButton;
    Button saveButton;
    ImageView imageView;
    private Uri imageUri;
    private InputStream imageStream;
    private Bitmap selectedImage;

    private static int RESULT_LOAD_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Variables to reference interface widgets
        topText = (TextView) findViewById(R.id.textviewTopText);
        bottomText = (TextView) findViewById(R.id.textviewBottomText);
        editTop = (EditText) findViewById(R.id.editTopText);
        editBottom = (EditText) findViewById(R.id.editBottomText);
        rotateButton = (Button) findViewById(R.id.buttonRotate);
        saveButton = (Button) findViewById(R.id.buttonSave);
        imageView = (ImageView) findViewById(R.id.imageAddImage);

        //Set font of meme text
        Typeface Impact = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Impact.TTF");
        topText.setTypeface(Impact);
        bottomText.setTypeface(Impact);

        //Make rotateButton invisible before addImage button is clicked
        rotateButton.setVisibility(View.INVISIBLE);

        //Set textViews when typing in the editTexts
        editTop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                topText.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        editBottom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bottomText.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Rotate image if it uploads incorrectly
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageView.setRotation(imageView.getRotation() + 90);
            }
        });

    }



    //Select image from gallery
    public void addImage(View view) {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
    }

    //Return selected image from gallery and set it to the imageView
    @Override
    protected void onActivityResult (int request, int result, Intent data){
        super.onActivityResult(request, result, data);

            if(request == RESULT_LOAD_IMAGE && result == RESULT_OK && null != data ) {
                try {
                    imageUri = data.getData();
                    imageStream = getContentResolver().openInputStream(imageUri);
                    selectedImage = BitmapFactory.decodeStream(imageStream);

                    imageView.setImageBitmap(selectedImage);
                    rotateButton.setVisibility(View.VISIBLE);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }else {
                Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
            }



    }

    //Initiate saving the meme to user's gallery
    public void saveMeme (View view) {
        View content = findViewById(R.id.relativeLayout);
        Bitmap bitmap = getScreenshot(content);
        String currentImage = "meme" + System.currentTimeMillis() + ".png";
        store(bitmap, currentImage);
    }

    //Create bitmap of imageView with meme text over it
    public static Bitmap getScreenshot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        Bitmap cbm = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return cbm;
    }

    //Store media file in user's gallery
    public void store(Bitmap bm, String filename) {
        String dirpath = getApplicationContext().getFilesDir().toString();
        File dir = new File(dirpath);
        if (!dir.exists()) {
            dir.mkdir();
        }

        MediaStore.Images.Media.insertImage(getContentResolver(), bm ,filename, null);

        Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
    }


}
