package com.example.ahmed.cvmaker;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
//import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmed.db.DBController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class EducationalActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, View.OnLongClickListener{

    EditText etDegreeOrCertificate, etUniversityName, etGpa, etPassingYear;
    //    TextView tvPassingYear;
    DBController dbController = null;
    String strDegreeOrCertificate, strSchoolName, strGpa, strPassingYear;
    int DATE_PICKER_ID = 123;
    int year, month, day;
    AlertDialog.Builder builder = null;
    /////////////////////
    SharedPreferences sharedPreferences = null, prefDegree = null;
    Editor editor = null;
    String profileName = null, degreeName = "", sql = "";
    /////////////////////
    ArrayList<HashMap<String, String>> hashMaps = null;
    HashMap<String, String> hashMap = new HashMap<String, String>();
    ArrayList<String> items = new ArrayList<String>();
    Context context = null;
    //////////////
    DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();
    Calendar dateAndTime = Calendar.getInstance();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
    ///////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educational);

        initMyView();
        getSharedPref();
        getDataFromDbAndSetToViews();

    }

    private void getDataFromDbAndSetToViews() {
        sql = "select * from "+DBController.tblEdu+ " where "+DBController.profileName +"  = '"+profileName+"' and  "+DBController.eduDegreeAndCerti+" = '"+degreeName+"' ";
        hashMaps = dbController.getEduData(sql);
        //if stmt to be more secure
        if (hashMaps.isEmpty()) {
            //do nothing here as the hashmap is empty
        }else{
            strPassingYear = hashMaps.get(0).get(DBController.eduPassingYear);
            strGpa = hashMaps.get(0).get(DBController.eduGpa);
            strSchoolName = hashMaps.get(0).get(DBController.eduSchoolName);
            strDegreeOrCertificate = hashMaps.get(0).get(DBController.eduDegreeAndCerti);

            if (strPassingYear != null)
                etPassingYear.setText(strPassingYear);
            if (strGpa != null)
                etGpa.setText(strGpa);
            if (strSchoolName != null)
                etUniversityName.setText(strSchoolName);
            if (strDegreeOrCertificate != null)
                etDegreeOrCertificate.setText(strDegreeOrCertificate);



        }


    }


    @Override
    protected void onResume() {
        super.onResume();

        getSharedPref();

    }


    private void getSharedPref() {

        sharedPreferences = getSharedPreferences(MainActivity.NAME, MainActivity.MODE);
        if (sharedPreferences.contains(MainActivity.PROFILE_NAME)) {//by key  and that true
            profileName = sharedPreferences.getString(MainActivity.PROFILE_NAME, "");//the default value here ""
            //createToast(profileName);
        }
        prefDegree = getSharedPreferences(ExistingEducational.KEY_EDUCATIONAL, ExistingEducational.MODE);
        if (prefDegree.contains(ExistingEducational.DEGREE)) {
            degreeName = prefDegree.getString(ExistingEducational.DEGREE, "");
            createToast(degreeName);
        }

    }


    private void initMyView() {

        context = EducationalActivity.this;
        //ActionBar actionBar = getActionBar();
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLUE));
        //actionBar.show();
        /////////////
        dbController = new DBController(getApplicationContext());
        //////////////////////////
        etDegreeOrCertificate = (EditText) findViewById(R.id.etDegreeOrCerti);
        etUniversityName = (EditText) findViewById(R.id.etSchool);
        etGpa = (EditText) findViewById(R.id.etGpa);
        etPassingYear = (EditText) findViewById(R.id.etCompletionDate);
        /////////////////////////
        //etPassingYear.setShowSoftInputOnFocus(false);//hide the softinput
        etPassingYear.setOnClickListener(this);
        etPassingYear.setOnFocusChangeListener(this);

    }

    //OnClick
//    public void getCompletionDate(View view) {
//
//
//        showMyCustomDatePicker();
//        //showDialog(DATE_PICKER_ID);
//
//    }

    private void showMyCustomDatePicker() {


        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(EducationalActivity.this);
        builder.setMessage("Select the option");
        builder.setPositiveButton("Still Studying", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                etPassingYear.setText("Still Studying");
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Completion Date", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                getCompletionPassingDate();
            }
        });
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        if (alertDialog.isShowing()) {
            return;
        }
        alertDialog.show();


    }

    private void getCompletionPassingDate() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
//                android.R.style.Theme_Holo_Light_Dialog_MinWidth,//This is main reason for different DatePickerDialog shapes, what will look like, with buttons ok and cancel
                //or with only button Done or ........
                onDateSetListener,
                year,
                month,
                day);
        //datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
//        new AlertDialog.Builder(getApplicationContext())
//                .setTitle("Ahmed")
//                .setView(datePickerDialog)
//                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                })
//                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == DATE_PICKER_ID)
            return new DatePickerDialog(getApplicationContext(), onDateSetListener, year, month, day);
        return null;


    }


    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

            //Default values
//            EducationalActivity.this.year = year;
//            EducationalActivity.this.month = monthOfYear;
//            EducationalActivity.this.day = dayOfMonth;
            /////////////////
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            etPassingYear.setText(simpleDateFormat
                    .format(dateAndTime.getTime()));

        }
    };

    @Override
    public void onBackPressed() {

        if (true) {
            createAlertDialog();
        } else {
            super.onBackPressed();
        }


    }


    public void createAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EducationalActivity.this);
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

        if (etDegreeOrCertificate.getText().toString().trim().length() == 0) {
            createAlert("Please, Enter Degree Name");
            return;
        }
        if (etUniversityName.getText().toString().trim().length() == 0) {
            createAlert("Please, Enter School Name");
            return;
        }
        if (etGpa.getText().toString().trim().length() == 0) {
            createAlert("Please, Enter GPA Result");
            return;
        }
        if (etPassingYear.getText().toString().trim().length() == 0) {
            createAlert("Please, Enter Passing Year");
            return;
        }

        strDegreeOrCertificate = etDegreeOrCertificate.getText().toString();
        strGpa = etGpa.getText().toString();
        strPassingYear = etPassingYear.getText().toString();
        strSchoolName = etUniversityName.getText().toString();
        profileName = profileName;

        //else
        /////////////save info


//        String sql = "insert into "
//                + DBController.tblEdu +
//                "( '" +
//                DBController.profileName + "', '" +
//                DBController.eduDegreeAndCerti + "', '" +
//                DBController.eduSchoolName + "', '" +
//                DBController.eduGpa + "', '" +
//                DBController.eduPassingYear + "'  )" +
//                " values (  " +
//                "'" + profileName + "', " +
//                " '" + strDegreeOrCertificate + "', " +
//                "'" + strSchoolName + "', " +
//                "'" + strGpa + "', " +
//                "'" + strPassingYear + "' " +
//                "  ) ";
//        dbController.executeQuery(sql);//insert


        String sql = "select * from " + DBController.tblEdu +
                " where " + DBController.profileName + " =   '" + profileName + "' and "+DBController.eduDegreeAndCerti+
                " = '"+degreeName+"' ";//this profilename value from shaerd preference
        if (dbController.getEduData(sql).isEmpty()) {//the data not exist
            sql = "insert into "
                    + DBController.tblEdu +
                    "( '" +
                    DBController.profileName + "', '" +
                    DBController.eduDegreeAndCerti + "', '" +
                    DBController.eduSchoolName + "', '" +
                    DBController.eduGpa + "', '" +
                    DBController.eduPassingYear + "'  )" +
                    " values (  " +
                    "'" + profileName + "', " +
                    " '" + strDegreeOrCertificate + "', " +
                    "'" + strSchoolName + "', " +
                    "'" + strGpa + "', " +
                    "'" + strPassingYear + "' " +
                    "  ) ";
            //createAlert(sql);
            dbController.executeQuery(sql);//insert
            createToast("Inserted ");

        } else {//the data alredy exist so it will be updated

            sql = "update " + DBController.tblEdu +
                    " set " +
                    DBController.eduDegreeAndCerti + " =  '" + strDegreeOrCertificate + "',  " +
                    DBController.eduSchoolName + " =  '" + strSchoolName + "', "
                    + DBController.eduGpa + " =  '" + strGpa + "',  " +
                    DBController.eduPassingYear + " = '" + strPassingYear + "' " +
                    "where " + DBController.profileName + " =  '" + profileName + "' and "+DBController.eduDegreeAndCerti+
            " = '"+degreeName+"' ";
            //createAlert(sql);
            dbController.executeQuery(sql);//update
            createToast("updated ");

        }

        //////////////exit
        finish();
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
       // createToast("Saved Successfully");

    }

    private void createAlert(String s) {
        new AlertDialog.Builder(EducationalActivity.this)
                .setMessage(s)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();


    }

    public void createToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    //to show optionMenu,
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_menu, menu);
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.erase:
                resetAllViews();
                break;
            case R.id.questionAndAnswers:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void resetAllViews() {

        etGpa.setText("");
        etDegreeOrCertificate.setText("");
        etUniversityName.setText("");
        etPassingYear.setText("");


    }

    @Override
    public void onClick(View view) {
        if (view.equals(etPassingYear))
            showMyCustomDatePicker();

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        showMyCustomDatePicker();
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }


}
