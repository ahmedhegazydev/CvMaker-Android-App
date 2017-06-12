package com.example.ahmed.cvmaker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
//import android.app.Dialog;
//import android.app.DialogFragment;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.DialogPreference;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ahmed.db.DBController;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;

public class PersonalDataActivity extends Activity {

    EditText etEmail, etAddress, etPhoneNumber, etDateOfBirth, etPersonName, etKnownLanguages;
    Spinner spinnerGender = null;
    Boolean spinnerChanged = false;//For idicating wether user change the spiner value or not before leaving actvity
    DBController dbController = null;
    ArrayList<HashMap<String, String>> hashMaps = null;
    String selectQuery = null;
    String profileName = "";
    SharedPreferences sharedPreferences = null;
    String gender = "";


    class AppDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder theDialog = new AlertDialog.Builder(getActivity());
            theDialog.setMessage("Do you want to save information before exit?");
            theDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveInformationInDatabase();
                }
            });
            theDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


            return theDialog.create();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_personal_data);


        initializeMyViews();
        getProfileName();
        //get the data from database and set it in fields, As user sea and edit it
        getDataForAlteration();


    }

    private void getProfileName() {
        if (sharedPreferences.contains(MainActivity.PROFILE_NAME)) {
            profileName = sharedPreferences.getString(MainActivity.PROFILE_NAME, "");
        }
        //createToast(profileName);
    }

    private void getDataForAlteration() {
        selectQuery = "select * from " + DBController.tblPersonal +
                " where " + DBController.profileName + " =  '" + profileName + "'  ";
        //createAlert(selectQuery);
        hashMaps = dbController.getPersonalData(selectQuery);
        if (!hashMaps.isEmpty()) {
            //We will get only one row from dataset
            HashMap<String, String> hashMap = hashMaps.get(0);
            String fullName = "";
            fullName = hashMap.get(DBController.fullName);
            //String gender = "";
            gender = hashMap.get(DBController.gender);
            String dob = hashMap.get(DBController.dateOfBirth);
            String address = hashMap.get(DBController.address);
            String languages = hashMap.get(DBController.langauges);
            String phoneNumber = hashMap.get(DBController.phone);
            String emailId = hashMap.get(DBController.email);
            ////////////////////////////////////

            if (fullName == null) {
                etPersonName.setText(profileName);
            } else {
                etPersonName.setText(fullName);
            }
            //etPersonName.setText(fullName);
            //createToast(gender);
//            if (gender == "Male") {
//                spinnerGender.setSelection(0);
//            } else {
//                if (gender  == "Female") {
//                    spinnerGender.setSelection(1);
//                } else {
//                    spinnerGender.setSelection(2);
//                }
//            }
            etDateOfBirth.setText(dob);
            etAddress.setText(address);
            etKnownLanguages.setText(languages);
            etPhoneNumber.setText(phoneNumber);
            etEmail.setText(emailId);
            //createAlert(fullName+gender+dob+address+languages+phoneNumber+emailId);

        } else {

        }


    }

    private void getPassedData() {
//        Intent intent = getIntent();
//        etPersonName.setText(intent.getStringExtra("profile_name"));//will be null as reason getDataForAlteration()
//        etPersonName.setTag(intent.getStringExtra("profile_name"));//as it is hidden from user and ca't change it

        createToast(profileName);
        etPersonName.setText(profileName);//The default is empty

    }

    private void initializeMyViews() {

        dbController = new DBController(getApplicationContext());
        sharedPreferences = getSharedPreferences(MainActivity.NAME, MainActivity.MODE);


        //Full name
        etPersonName = (EditText) findViewById(R.id.etName);
        //Email
        etEmail = (EditText) findViewById(R.id.etEmailId);
        //The Address
        etAddress = (EditText) findViewById(R.id.setAddress);
        //Phone Number
        etPhoneNumber = (EditText) findViewById(R.id.etContactNumber);
        //The Known Languages
        etKnownLanguages = (EditText) findViewById(R.id.etLanguages);
        //The Date Of Birth
        etDateOfBirth = (EditText) findViewById(R.id.etDateOfBirth);
        //The Gender
        spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
        //For idicating wether user change the spiner value or not before leaving actvity

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerChanged = true;
                gender = spinnerGender.getSelectedItem().toString();
                //createToast(gender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                gender = "Other";//by default
            }
        });



    }


    public boolean checkIfUserTypedText() {

        if (etDateOfBirth.getText().toString().trim().length() != 0
                || etKnownLanguages.getText().toString().trim().length() != 0
                || etAddress.getText().toString().trim().length() != 0
                || etEmail.getText().toString().trim().length() != 0
                || etPersonName.getText().toString().trim().length() != 0
                || etPhoneNumber.getText().toString().trim().length() != 0
                ) {
            return true;

        }
        //For idicating wether user change the spiner value or not before leaving actvity
        if (spinnerChanged) {
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {


        //or make it true forever
        if (checkIfUserTypedText()) {
            AlertDialog.Builder theDialog = new AlertDialog.Builder(PersonalDataActivity.this);
            theDialog.setMessage("Do you want to save information before exit?");
            theDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveInformationInDatabase();
                }
            });
            theDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //finish();
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    //intent.putExtra("profile_name", etPersonName.getTag().toString());
                    startActivity(intent);
                }
            });
            theDialog.show();
            return;

        } else {
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();


    }


    public void createToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }


    public void createAlert(String msg) {
        AlertDialog.Builder theDialog = new AlertDialog.Builder(PersonalDataActivity.this);
        theDialog.setMessage(msg);
        theDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        theDialog.show();
    }

    private void saveInformationInDatabase() {

        if (etPersonName.getText().toString().trim().length() == 0) {
            createAlert("Please, Enter Name");
            etPersonName.requestFocus();
            return;
        }
        if (spinnerChanged == false) {
            createAlert("Please, Enter Gender");
            return;
        }
        if (etAddress.getText().toString().trim().length() == 0) {
            createAlert("Please, Enter Address");
            etAddress.requestFocus();
            return;
        }
        if (etKnownLanguages.getText().toString().trim().length() == 0) {
            createAlert("Please, Enter Languages");
            etKnownLanguages.requestFocus();
            return;
        }
        if (etPhoneNumber.getText().toString().trim().length() == 0) {
            createAlert("Please, Enter Phone Number");
            etPhoneNumber.requestFocus();
            return;
        }
        if (etEmail.getText().toString().trim().length() == 0) {
            createAlert("Please, Enter Email Id");
            etPersonName.requestFocus();
            return;
        }
        if (!isValidEmail(etEmail.getText().toString())) {
            createAlert("Enter Valid Email");
            etEmail.requestFocus();
            return;
        }
        //Else


        //check if it exist or not
        //if exist then will be updated
        //if not , then will be inserted
        //but that will be not shown for user as i work by onbackPressed button

        String query = "select * from " + DBController.tblPersonal +
                " where " + DBController.profileName + " =  '" + profileName + "'  ";
        hashMaps = dbController.getPersonalData(query);
        String sql = "";
        if (hashMaps.isEmpty()) {//this user is new so will be inserted in db
            //Saving the information in database
//            dbController.insertPersonalData(
//                    getIntent().getStringExtra("profile_name").toString(),
//                    etPersonName.getText().toString(),
//                    spinnerGender.getSelectedItem().toString(),
//                    etDateOfBirth.getText().toString(),
//                    etAddress.getText().toString(),
//                    etKnownLanguages.getText().toString(),
//                    etPhoneNumber.getText().toString(),
//                    etEmail.getText().toString()
//            );
//            createAlert("Inserted Successfully");
            sql = "insert into " + DBController.tblPersonal +
                    "(" +
                    DBController.fullName + "," +
                    DBController.gender + "," +
                    DBController.dateOfBirth + "," +
                    DBController.address + "," +
                    DBController.langauges + ", " +
                    DBController.phone + ", " +
                    DBController.email +
                    ") values ( '" +
                    etPersonName.getText().toString() + "', " +
                    //" '" + spinnerGender.getSelectedItem().toString() + "', " +
                    " '" + gender + "', " +
                    " '" + etDateOfBirth.getText().toString() + "', " +
                    " '" + etAddress.getText().toString() + "', " +
                    " '" + etKnownLanguages.getText().toString() + "', " +
                    "'" + etPhoneNumber.getText().toString() + "', " +
                    "  '" + etEmail.getText().toString() + "' )" +
                    " where  " + DBController.profileName + " =   '" + profileName + "'  ";
            dbController.executeQuery(sql);
            createAlert("inserted Successfully");

        } else {
            sql = "update " + DBController.tblPersonal + " set " +
                    DBController.fullName + " =  '" + etPersonName.getText().toString() + "', " +
                    DBController.gender + " =  '" + gender + "', " +
                    DBController.dateOfBirth + " =  '" + etDateOfBirth.getText().toString() + "', " +
                    DBController.address + " =  '" + etAddress.getText().toString() + "', " +
                    DBController.langauges + " =  '" + etKnownLanguages.getText().toString() + "', " +
                    DBController.phone + " =  '" + etPhoneNumber.getText().toString() + "', " +
                    DBController.email + " =  '" + etEmail.getText().toString() + "' " +
                    " where " +
                    DBController.profileName + " =   '" + profileName + "'  ";
            // createAlert(sql);
            dbController.executeQuery(sql);
            createToast("Updated Success");

        }
        finish();
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        //i.putExtra("profile_name", etPersonName.getTag().toString());
        startActivity(i);


    }


    // Another option is the built in Patterns starting with API Level 8:
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


}
