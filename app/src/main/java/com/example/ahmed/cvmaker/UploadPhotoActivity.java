package com.example.ahmed.cvmaker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
//import android.provider.MediaStore;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ahmed.db.DBController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class UploadPhotoActivity extends AppCompatActivity implements View.OnClickListener {


    AlertDialog.Builder builder = null;
    AlertDialog alertDialog = null;
    private static Uri selectedImage;
    private static InputStream imageStream;
    private static final int SELECT_PHOTO = 2;
    /////////////////////////////////////////////////
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static String mCurrentPhotoPath;
    Bitmap mImageBitmap = null;
    ///////////////////////////
    ImageView imageView = null;
    String profileName = "";
    SharedPreferences sharedPreferences = null;
    //////////////////
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] photo = null;
    DBController dbController = null;
    Context context = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        imageView = (ImageView) findViewById(R.id.ivUploadImage);
        imageView.setOnClickListener(this);
        dbController = new DBController(getApplicationContext());
        context = this;

        getProfileName();

        getTheImageFromDataBase();

    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);


    }

    private void getTheImageFromDataBase() {
        // get the image from database
        try {
            String selectQuery = "select * from " + dbController.tblPersonal +
                    " where " +
                    DBController.profileName + " =  '" + profileName + "' ";
            createAlert(selectQuery);
            ArrayList<HashMap<String, byte[]>> hashMaps = dbController.getBlobImageBySql(selectQuery);
            HashMap<String, byte[]> hashMap = hashMaps.get(0);
            createToast(hashMaps.toString());
            if (!hashMap.isEmpty()) {
                byte[] bs = null;
                bs = hashMap.get(DBController.image);
                createToast(bs.toString());
                if (bs != null) {
                    createToast("not null");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bs, 0, bs.length);
                    //imageView.setBackgroundResource(0);
                    imageView.setImageBitmap(bitmap);
                    imageView.invalidate();

                } else {

                }
            } else {

            }
        } catch (Exception e) {
//        createToast(e.getMessage().toString());
            //createAlert(e.getMessage().toString());
            //e.printStackTrace();
            //Log.e("ahmed", e.getMessage().toString());
        } finally {

        }

    }

    private void getProfileName() {
        sharedPreferences = getSharedPreferences(MainActivity.NAME, MainActivity.MODE);
        if (sharedPreferences.contains(MainActivity.PROFILE_NAME)) {
            profileName = sharedPreferences.getString(MainActivity.PROFILE_NAME, "");
            // createToast(profileName);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    selectedImage = data.getData();
                    try {
                        imageStream = getApplicationContext().getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mImageBitmap = BitmapFactory.decodeStream(imageStream);
                    imageView.setBackgroundResource(0);
                    imageView.setImageBitmap(mImageBitmap);
                    //ivPersonalIamge.setVisibility(View.VISIBLE);
                    //ivPersonalIamge.invalidate();
                    // new FragmentRegister().setmImageBitmap(mImageBitmap);
                    // path1 = selectedImage.getPath();
                }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)

        {
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),
                        Uri.parse(mCurrentPhotoPath));
                imageView.setBackgroundResource(0);
                imageView.setImageBitmap(mImageBitmap);
                //ivPersonalIamge.invalidate();
                // new FragmentRegister().setmImageBitmap(mImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //ivPersonalIamge.setVisibility(View.VISIBLE);
        }


    }


    /**
     * Showing alertdialog with two buttons one of them to take photo from your
     * phone built in Gallery and the other for taking camera pic then set the
     * taken photo as imagebitmap to image view
     *
     * @param context
     */
    public void selectOption(Context context) {
        builder = new AlertDialog.Builder(context)
                .setTitle("Upload Picture Option")
                .setMessage("Select option to upload picture for resume !")
                .setPositiveButton("Upload from gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                    }
                }).setNegativeButton("Take picture from camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cameraIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                            }
                            if (photoFile != null) {
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

                            } else {

                            }
                        }
                    }
                });
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        if (alertDialog.isShowing()) {
            return;
        }
        alertDialog.show();

    }

    private void createToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


    /**
     * Create formatted image file in phone storage for the future use
     *
     * @return Camera Image file
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, // prefix
                ".jpg", // suffix
                storageDir // directory
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    @Override
    public void onClick(View view) {

        if (view.equals(imageView)) {
            selectOption(UploadPhotoActivity.this);
        }

    }

    @Override
    public void onBackPressed() {
        if (true) {
            new saveImage(context).execute();

        } else {
            super.onBackPressed();
        }


    }


    class saveImage extends AsyncTask<Void, Void, String> {


        ProgressDialog dialog = null;
        Context context = null;

        public saveImage(Context context) {
            this.context = context;
            dialog = new ProgressDialog(this.context);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage("Saving The Image ....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String sql = "";
            try {
                Bitmap bitmap = mImageBitmap;
                if (bitmap == null) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    photo = baos.toByteArray();
//                    dbController.insertImgInDb(photo);
                    sql = "update " + DBController.tblPersonal + " set " + DBController.image + " = '" + photo + "' " +
                            " where " + DBController.profileName + " = '" + profileName + "' ";

                    dbController.executeQuery(sql);

                }

            } catch (Exception e) {
                createAlert(e.getMessage().toString());
            }

            return sql;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (dialog.isShowing()) {
                dialog.dismiss();

                createAlert(s);

//                finish();
//                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//                createToast("Saved");
            }

        }
    }

    private void createAlert(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(s);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        if (alertDialog.isShowing()) {
            return;
        }
        //else
        alertDialog.show();
    }
}
