package com.example.ahmed.cvmaker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ahmed.db.DBController;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class OtherDetailsActivity extends AppCompatActivity {

    EditText etDrivingLicence, etPassportNumber;
    Context context = null;
    String profileName = "", sql = "";
    SharedPreferences sharedPreferences = null;
    DBController dbController = null;
    AlertDialog.Builder builder = null;
    AlertDialog alertDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_other_details);


        initVars();
        getProfileName();
        getDataAdnSetToViews();


    }

    private void getDataAdnSetToViews() {

        //createToast(profileName);
        sql = "select * from " + DBController.tblOther + " where " + DBController.profileName + " =  '"+this.profileName+"' ";
        ArrayList<HashMap<String, String>> hashMaps = dbController.getOtherData(sql);
        if (!hashMaps.isEmpty()) {
            String driving = "";
            driving = hashMaps.get(0).get(DBController.otherDrivingLic);
            String passport = "";
            passport = hashMaps.get(0).get(DBController.otherPassportNumber);
            if (driving != "") {
                etDrivingLicence.setText(driving);
            }
            if (passport != "") {
                etPassportNumber.setText(passport);
            }
        }

    }

    public void createAlert(String message) {
        builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        if (alertDialog.isShowing()) {
            return;
        }
        //else
        alertDialog.show();
    }

    private void getProfileName() {
        if (sharedPreferences.contains(MainActivity.PROFILE_NAME)) {
            profileName = sharedPreferences.getString(MainActivity.PROFILE_NAME, "");
        }
    }


    private void initVars() {
        context = this;
        //////////////////////
        etDrivingLicence = (EditText) findViewById(R.id.etDrivingLic);
        etPassportNumber = (EditText) findViewById(R.id.etPassportNumber);
        ////////////////////////////////
        sharedPreferences = getSharedPreferences(MainActivity.NAME, MainActivity.MODE);
        dbController = new DBController(getApplicationContext());

    }

    public void saveConfirmation() {
        builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveInfoInDatabase();

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);

            }
        });
        builder.setTitle("Warning");
        builder.setMessage("Do you want to save before exit ?");
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        if (alertDialog.isShowing()) {
            return;
        }
        //else
        alertDialog.show();


    }

    private void saveInfoInDatabase() {

//        if (etDrivingLicence.getText().toString().trim().length() == 0) {
//            createAlert("Enter Driving Licence");
//            return;
//        }
//        if (etPassportNumber.getText().toString().trim().length() == 0) {
//            createAlert("Enter Passport Number");
//            return;
//        }


        sql = "select * from " + DBController.tblOther +
                " where " + DBController.profileName + " =  '" + profileName + "'  ";
        ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
        hashMaps = dbController.getOtherData(sql);
        if (hashMaps.isEmpty()) {
            //insert data
            sql = "insert into " + DBController.tblOther + " ( " + DBController.otherDrivingLic + ", " + DBController.otherPassportNumber + " ) " +
                    " values ( "+
                    " '" + etDrivingLicence.getText().toString() + "', '" + etPassportNumber.getText().toString() + "'  )" +
                    " where " + DBController.profileName + " =  '"+this.profileName+"' ";
            createAlert(sql);
            //dbController.executeQuery(sql);
            createToast("Inserted");
        } else {
            //the data already exist in db
            sql = "update " + DBController.tblOther + " set " +
                    DBController.otherDrivingLic + " = '" + etDrivingLicence.getText().toString() + "' ," +
                    DBController.otherPassportNumber + " = '" + etPassportNumber.getText().toString() + "' " +
                    " where " + DBController.profileName + " =  '"+this.profileName+"' ";
            //createAlert(sql);
            try {
                dbController.executeQuery(sql);
                createToast("Updated");
            }catch (Exception e){
                createAlert(e.getMessage().toString());
            }
        }

        finish();
        Intent intent = new Intent(context, ProfileActivity.class);
        startActivity(intent);


    }

    private void createToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }


    private void checkIfThereIsTexts() {
        //if all fields are empty
        if (etDrivingLicence.getText().toString().trim().length() == 0 &&
                etPassportNumber.getText().toString().trim().length() == 0) {
            //exit immediately
            finish();
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);

        } else {
            //exit confirmation
            saveConfirmation();
        }

    }

    @Override
    public void onBackPressed() {
        if (true) {
            checkIfThereIsTexts();
        } else
            super.onBackPressed();

    }
}
