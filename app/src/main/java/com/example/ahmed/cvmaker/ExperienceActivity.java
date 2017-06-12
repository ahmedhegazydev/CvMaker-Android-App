package com.example.ahmed.cvmaker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.ahmed.db.DBController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ExperienceActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    EditText etOrganizationalName, etPosition, etDuration, etOrganizationLocation,
            etSalary, etJobRespon;
    Context context = null;
    Calendar calendar = Calendar.getInstance();
    DatePickerDialog datePickerDialog = null;
    DateFormat dateFormat = DateFormat.getDateInstance();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
    String dateStillWorking, dateEndDate;
    String date;
    String profileName = "";
    SharedPreferences sharedPreferences = null, prefOrgaName = null;
    DBController dbController = null;
    String organName = "";
    String sql = "";
    ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
    String strOrgName, strPosition,strDuration, strLocation, strSalary, strJobResp;
    HashMap<String, String> hashMap = new HashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience);

        initVars();//findViewsByIds
        getProfileName();
        getDataAndSetToViews();
    }

    private void getDataAndSetToViews() {

        sql = "select * from "+DBController.tblExperience+ " where "+DBController.profileName +
                "  = '"+profileName+"' and  "+DBController.expOrganizationName+" = '"+organName+"' ";
        hashMaps = dbController.getExpData(sql);
        //if stmt to be more secure
        if (hashMaps.isEmpty()) {
            //do nothing here as the hashmap is empty
        }else{
            strDuration = hashMaps.get(0).get(DBController.expDuration);
            strJobResp = hashMaps.get(0).get(DBController.expJobResp);
            strLocation = hashMaps.get(0).get(DBController.expOrganLocation);
            strPosition = hashMaps.get(0).get(DBController.expPosition);
            strSalary = hashMaps.get(0).get(DBController.expSalary);
            strOrgName = hashMaps.get(0).get(DBController.expOrganizationName);

            if (strDuration != null)
                etDuration.setText(strDuration);
            if (strOrgName != null)
                etOrganizationalName.setText(strOrgName);
            if (strSalary != null)
                etSalary.setText(strSalary);
            if (strJobResp != null)
                etJobRespon.setText(strJobResp);
            if (strLocation != null)
                etOrganizationLocation.setText(strLocation);
            if (strPosition != null)
                etPosition.setText(strPosition);




        }


    }

    private void getProfileName() {
        sharedPreferences = getSharedPreferences(MainActivity.NAME, MainActivity.MODE);
        if (sharedPreferences.contains(MainActivity.PROFILE_NAME)) {
            profileName = sharedPreferences.getString(MainActivity.PROFILE_NAME, "");
        }

        prefOrgaName = getSharedPreferences(ExistingExperience.KEY_EXP, ExistingExperience.MODE);
        if (prefOrgaName.contains(ExistingExperience.ORGAN_NAME)) {
            organName = prefOrgaName.getString(ExistingExperience.ORGAN_NAME, "");
            createToast(organName);
        }

    }

    private void initVars() {
        context = ExperienceActivity.this;
        //////////////////////
        dbController = new DBController(context);
        ////////////
        etOrganizationalName = (EditText) findViewById(R.id.etOrganizationName);
        etOrganizationLocation = (EditText) findViewById(R.id.etOrganizationLocation);
        etSalary = (EditText) findViewById(R.id.etSalary);
        etDuration = (EditText) findViewById(R.id.etDuration);
        etJobRespon = (EditText) findViewById(R.id.etjobResp);
        etPosition = (EditText) findViewById(R.id.etPosition);
        /////////////////////
        etDuration.setOnClickListener(this);//for all times the use clicks on it
        etDuration.setOnFocusChangeListener(this);//for the first time focused by user
        //etDuration.setShowSoftInputOnFocus(false);//hide the softinput
        ////////////////////////////////////////

    }

    public void createDatePickerDialog() {

        datePickerDialog = new DatePickerDialog(context,
                //android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DAY_OF_MONTH, i2);

                        date = dateStillWorking = simpleDateFormat.format(calendar.getTime());

                        selectDurationDialog();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        if (!datePickerDialog.isShowing()) {
            datePickerDialog.show();
        } else
            return;
    }

    public void endDatePickerDialog() {

        datePickerDialog = new DatePickerDialog(context,
                //android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DAY_OF_MONTH, i2);

                        dateEndDate = simpleDateFormat.format(calendar.getTime()) + " To " + date;
                        etDuration.setText(dateEndDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        if (!datePickerDialog.isShowing()) {
            datePickerDialog.show();
        } else
            return;
    }

    private void selectDurationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Please Select Duration");
        builder.setCancelable(false);
        builder.setPositiveButton("To Still Working", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dateStillWorking += " To Still Working";
                etDuration.setText(dateStillWorking);
            }
        });
        builder.setNegativeButton("End Date", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                endDatePickerDialog();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        } else return;
    }


    public void saveConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        if (alertDialog.isShowing()) {
            return;
        }
        //else
        alertDialog.show();


    }

    private void saveInfoInDatabase() {


        strDuration = etDuration.getText().toString();
        strJobResp = etJobRespon.getText().toString();
        strOrgName = etOrganizationalName.getText().toString();
        strLocation = etOrganizationLocation.getText().toString();
        strSalary = etSalary.getText().toString();
        strPosition = etPosition.getText().toString();


        if (etDuration.getText().toString().trim().length() == 0) {
            createAlert("Enter Duration");
            return;
        }
        if (etPosition.getText().toString().trim().length() == 0) {
            createAlert("Enter Position");
            return;
        }
        if (etJobRespon.getText().toString().trim().length() == 0) {
            createAlert("Enter Job Responsibility");
            return;
        }
        if (etOrganizationLocation.getText().toString().trim().length() == 0) {
            createAlert("Enter Organization Location");
            return;
        }
        if (etOrganizationalName.getText().toString().trim().length() == 0) {
            createAlert("Enter Organization Name");
            return;
        }
        if (etSalary.getText().toString().trim().length() == 0) {
            createAlert("Enter Salary");
            return;
        }
//        String sql = "insert into "
//                + DBController.tblExperience +
//                "( '" +
//                DBController.profileName + "', '" +
//                DBController.expJobResp + "', '" +
//                DBController.expSalary + "', '" +
//                DBController.expOrganLocation + "', '" +
//                DBController.expOrganizationName + "', '" +
//                DBController.expPosition + "', '" +
//                DBController.expDuration + "' " +
//                " )" +
//                " values (  " +
//                "'" + profileName + "', " +
//                " '" + strJobRespon + "', " +
//                "'" + strSalary + "', " +
//                "'" + strOrganizationLocation + "', " +
//                "'" + strOrganizationalName + "', " +
//                "'" + strPosition + "', " +
//                "'" + strDuration + "' " +
//                "  ) ";
//        dbController.executeQuery(sql);//insert
//        createToast("Saved Successfully");




        String sql = "select * from " + DBController.tblExperience +
                " where " + DBController.profileName + " =   '" + profileName + "' and "+DBController.expOrganizationName+
                " = '"+organName+"' ";//this profilename value from shaerd preference
        if (dbController.getExpData(sql).isEmpty()) {//the data not exist
            sql = "insert into "
                    + DBController.tblExperience +
                    "( '" +
                    DBController.profileName + "', '" +
                    DBController.expOrganizationName + "', '" +
                    DBController.expPosition + "', '" +
                    DBController.expDuration + "', '" +
                    DBController.expJobResp + "', '" +
                    DBController.expSalary + "', '" +
                    DBController.expOrganLocation + "'  )" +
                    " values (  " +
                    "'" + profileName + "', " +
                    " '" + strOrgName + "', " +
                    "'" + strPosition + "', " +
                    "'" + strDuration + "', " +
                    "'" + strJobResp + "', " +
                    "'" + strSalary + "', " +
                    "'" + strLocation + "' " +
                    "  ) ";
            //createAlert(sql);
            dbController.executeQuery(sql);//insert
            createToast("Inserted");

        } else {//the data alredy exist so it will be updated

            sql = "update " + DBController.tblExperience +
                    " set " +
                    DBController.expOrganLocation + " =  '" + strLocation + "',  " +
                    DBController.expPosition + " =  '" + strPosition + "', " +
                    DBController.expSalary + " =  '" + strSalary + "',  " +
                    DBController.expDuration + " =  '" + strDuration + "',  " +
                    DBController.expOrganizationName + " =  '" + strOrgName + "',  " +
                    DBController.expJobResp + " = '" + strJobResp + "' " +
                    "where " +
                    DBController.profileName + " =  '" + profileName + "' and "+DBController.expOrganizationName+
                    " = '"+organName+"' ";
            //createAlert(sql);
            dbController.executeQuery(sql);//update
            createToast("Updated");

        }

        //////////////exit
        finish();
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
        // createToast("Saved Successfully");


    }

    private void createAlert(String s) {
        new AlertDialog.Builder(context)
                .setMessage(s)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void checkIfThereIsTexts() {
        //if all fields are empty
        if (etPosition.getText().toString().trim().length() == 0
                && etJobRespon.getText().toString().trim().length() == 0
                && etDuration.getText().toString().trim().length() == 0
                && etSalary.getText().toString().trim().length() == 0
                && etOrganizationalName.getText().toString().trim().length() == 0
                && etOrganizationLocation.getText().toString().trim().length() == 0
                ) {
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
        } else {
            super.onBackPressed();
        }


    }

    @Override
    public void onClick(View view) {
        if (view.equals(etDuration)) {
            //createToast("Hello, from etDuration ");//ok
            createDatePickerDialog();
        }
    }

    private void createToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            //createToast("Hello, from etDuration ");//ok
            createDatePickerDialog();
        }
    }
}
