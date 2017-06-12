package com.example.ahmed.cvmaker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.ahmed.db.DBController;

import java.util.ArrayList;
import java.util.HashMap;

public class ReferenceDetailsActivity extends AppCompatActivity {

    Context context = null;
    EditText etRefeferenceName, etReferenceDetails, etContactNumber, etReferenceEmail;
    String profileName = "";
    DBController dbController = null;
    SharedPreferences sharedPreferences = null, prefRefName = null;
    String refName = "";
    String strRefName, strRefDetails, strRefEmail, strRefContactNumber = "";
    ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> hashMap = new HashMap<String, String>();
    String sql = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference_details);

        initVars();
        getProfileName();
        getRefDetails();


    }

    private void getRefDetails() {

        sql = "select * from " + DBController.tblRef + " where " + DBController.profileName +
                "  = '" + profileName + "' and  " + DBController.referName + " = '" + refName + "' ";
        hashMaps = dbController.getRefData(sql);
        //if stmt to be more secure
        if (hashMaps.isEmpty()) {
            //do nothing here as the hashmap is empty
        } else {
            strRefContactNumber = hashMaps.get(0).get(DBController.referContactNumber);
            strRefDetails = hashMaps.get(0).get(DBController.referDetails);
            strRefEmail = hashMaps.get(0).get(DBController.referEmail);
            strRefName = hashMaps.get(0).get(DBController.referName);

            if (strRefName != null)
                etRefeferenceName.setText(strRefName);
            if (strRefEmail != null)
                etReferenceEmail.setText(strRefEmail);
            if (strRefDetails != null)
                etReferenceDetails.setText(strRefDetails);
            if (strRefContactNumber != null)
                etContactNumber.setText(strRefContactNumber);


        }


    }

    private void getProfileName() {
        sharedPreferences = getSharedPreferences(MainActivity.NAME, MainActivity.MODE);
        if (sharedPreferences.contains(MainActivity.PROFILE_NAME)) {
            profileName = sharedPreferences.getString(MainActivity.PROFILE_NAME, "");
        }


        prefRefName = getSharedPreferences(ExistingReferences.KEY_REF, ExistingReferences.MODE);
        if (prefRefName.contains(ExistingReferences.REF_NAME)) {
            refName = prefRefName.getString(ExistingReferences.REF_NAME, "");
            createToast(refName);
        }


    }



    // Another option is the built in Patterns starting with API Level 8:
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    private void initVars() {

        dbController = new DBController(getApplicationContext());
        context = this;
        ///////////////////////
        etContactNumber = (EditText) findViewById(R.id.etContNumber);
        etRefeferenceName = (EditText) findViewById(R.id.etReferName);
        etReferenceDetails = (EditText) findViewById(R.id.etReferDetails);
        etReferenceEmail = (EditText) findViewById(R.id.etReferEmail);
        ////////////////////////////


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


        strRefContactNumber = etContactNumber.getText().toString();
        strRefDetails = etReferenceDetails.getText().toString();
        strRefEmail = etReferenceEmail.getText().toString();
        strRefName = etRefeferenceName.getText().toString();

        /////////////////Save indo in database


        if (strRefContactNumber.trim().length() == 0) {
            createAlert("Enter Contact Number");
            return;
        }
        if (strRefDetails.trim().length() == 0) {
            createAlert("Enter Reference Details");
            return;
        }
        if (!isValidEmail(strRefDetails.trim())) {
            createAlert("Enter Valid Email");
            return;
        }
        if (strRefEmail.trim().length() == 0) {
            createAlert("Enter Reference Email");
            return;
        }
        if (strRefName.trim().length() == 0) {
            createAlert("Enter Reference Name");
            return;
        }

        //else

//        String sql = "insert into "
//                + DBController.tblRef +
//                "( '" +
//                DBController.profileName + "', '" +
//                DBController.referName + "', '" +
//                DBController.referDetails + "', '" +
//                DBController.referContactNumber + "', '" +
//                DBController.referEmail + "' " +
//                " )" +
//                " values (  " +
//                "'" + profileName + "', " +
//                " '" + strRefName + "', " +
//                "'" + strRefDetails + "', " +
//                "'" + strContactNumber + "', " +
//                "'" + strRefEmail + "' " +
//                "  ) ";
//        dbController.executeQuery(sql);//insert
//        createToast("Saved Successfully");
//        finish();
//        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));


        try {
            String sql = "select * from " + DBController.tblRef +
                    " where " + DBController.profileName + " =   '" + profileName + "' and " + DBController.referName +
                    " = '" + refName + "' ";//this profilename value from shaerd preference
            if (dbController.getRefData(sql).isEmpty()) {//the data not exist
                sql = "insert into "
                        + DBController.tblRef +
                        "( '" +
                        DBController.profileName + "', '" +
                        DBController.referName + "', '" +
                        DBController.referEmail + "', '" +
                        DBController.referContactNumber + "', '" +
                        DBController.referDetails + "'  )" +
                        " values (  " +
                        "'" + profileName + "', " +
                        " '" + strRefName + "', " +
                        "'" + strRefEmail + "', " +
                        "'" + strRefContactNumber + "', " +
                        "'" + strRefDetails + "' " +
                        "  ) ";
                //createAlert(sql);
                dbController.executeQuery(sql);//insert
                createToast("Inserted");

            } else {//the data alredy exist so it will be updated

                sql = "update " + DBController.tblRef +
                        " set " +
                        DBController.referName + " =  '" + strRefName + "',  " +
                        DBController.referDetails + " =  '" + strRefDetails + "', " +
                        DBController.referContactNumber + " =  '" + strRefContactNumber + "',  " +
                        DBController.referEmail + " = '" + strRefEmail + "' " +
                        "where " +
                        DBController.profileName + " =  '" + profileName + "' and " + DBController.referName +
                        " = '" + refName + "' ";
                //createAlert(sql);
                dbController.executeQuery(sql);//update
                createToast("Updated");

            }

            //////////////exit
            finish();
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            // createToast("Saved Successfully");

        } catch (Exception e) {
            createAlert(e.getMessage().toString());
        }


    }

    private void createToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
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
        if (etRefeferenceName.getText().toString().trim().length() == 0 &&
                etReferenceDetails.getText().toString().trim().length() == 0 &&
                etReferenceEmail.getText().toString().trim().length() == 0 &&
                etContactNumber.getText().toString().trim().length() == 0


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


}
