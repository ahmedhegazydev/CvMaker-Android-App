package com.example.ahmed.cvmaker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.ahmed.db.DBController;

import java.util.ArrayList;
import java.util.HashMap;

public class ProjectDetailsActivity extends AppCompatActivity {

    EditText etProjectName, etProjectDuration, etRole, etTeamSize, etExpertize;
    Context context = null;
    String profileName = "";
    SharedPreferences sharedPreferences = null, prefProjectName = null;
    DBController dbController = null;
    String projectName = "";
    String strExpertise, strRole, strTeamSize, strProjectDuration, strProjectName;
    String sql = "";
    ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> hashMap = new HashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        initViews();
        getProfileName();
        getDataFromDb();
    }

    private void getDataFromDb() {


        sql = "select * from " + DBController.tblProject + " where " + DBController.profileName +
                "  = '" + profileName + "' and  " + DBController.projectName + " = '" + projectName + "' ";
        hashMaps = dbController.getProjectData(sql);
        //if stmt to be more secure
        if (hashMaps.isEmpty()) {
            //do nothing here as the hashmap is empty
        } else {
            strExpertise = hashMaps.get(0).get(DBController.projectExpertise);
            strProjectDuration = hashMaps.get(0).get(DBController.projectDuration);
            strProjectName = hashMaps.get(0).get(DBController.projectName);
            strRole = hashMaps.get(0).get(DBController.projectRole);
            strTeamSize = hashMaps.get(0).get(DBController.projectTeamSize);



            if (strTeamSize != null)
                etTeamSize.setText(strTeamSize);
            if (strRole != null)
                etRole.setText(strRole);
            if (strProjectName != null)
                etProjectName.setText(strProjectName);
            if (strExpertise != null)
                etExpertize.setText(strExpertise);
            if (strProjectDuration != null)
                etProjectDuration.setText(strProjectDuration);


        }


}

    private void initViews() {
        context = this;
        dbController = new DBController(context);
        ///////////////////////////
        etProjectName = (EditText) findViewById(R.id.etProjectName);
        etProjectDuration = (EditText) findViewById(R.id.etProjectDuration);
        etRole = (EditText) findViewById(R.id.etRole);
        etTeamSize = (EditText) findViewById(R.id.etTeamSize);
        etExpertize = (EditText) findViewById(R.id.etExpertise);
        ///////////////////////////////


    }


    private void getProfileName() {
        sharedPreferences = getSharedPreferences(MainActivity.NAME, MainActivity.MODE);
        if (sharedPreferences.contains(MainActivity.PROFILE_NAME)) {
            profileName = sharedPreferences.getString(MainActivity.PROFILE_NAME, "");
        }

        prefProjectName = getSharedPreferences(ExistingProjects.KEY_PROJECT, ExistingProjects.MODE);
        if (prefProjectName.contains(ExistingProjects.PROJECT_NAME)) {
            projectName = prefProjectName.getString(ExistingProjects.PROJECT_NAME, "");
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

    private void checkIfThereIsTexts() {
        //if all fields are empty
        if (etExpertize.getText().toString().trim().length() == 0
                && etRole.getText().toString().trim().length() == 0
                && etTeamSize.getText().toString().trim().length() == 0
                && etProjectDuration.getText().toString().trim().length() == 0
                && etProjectName.getText().toString().trim().length() == 0
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

    private void saveInfoInDatabase() {

        strExpertise = etExpertize.getText().toString();
        strProjectDuration = etProjectDuration.getText().toString();
        strProjectName = etProjectName.getText().toString();
        strRole = etRole.getText().toString();
        strTeamSize = etTeamSize.getText().toString();


        if (strExpertise.trim().length() == 0) {
            createAlert("Enter Expertise");
            return;
        }
        if (strProjectDuration.trim().length() == 0) {
            createAlert("Enter Project Duration");
            return;
        }
        if (strProjectName.trim().length() == 0) {
            createAlert("Enter Project Name");
            return;
        }
        if (strRole.trim().length() == 0) {
            createAlert("Enter Role");
            return;
        }
        if (strTeamSize.trim().length() == 0) {
            createAlert("Enter Team Size");
            return;
        }


//        String sql = "insert into "
//                + DBController.tblProject +
//                "( '" +
//                DBController.profileName + "', '" +
//                DBController.projectName + "', '" +
//                DBController.projectRole + "', '" +
//                DBController.projectExpertise + "', '" +
//                DBController.projectTeamSize + "', '" +
//                DBController.projectDuration + "'  " +
//                ")" +
//                " values (  " +
//                "'" + profileName + "', " +
//                " '" + strProjectName + "', " +
//                "'" + strRole + "', " +
//                "'" + strExpertise + "', " +
//                "'" + strTeamSize + "', " +
//                "'" + strProjectDuration + "' " +
//                "  ) ";
//        //createAlert(sql);
//        dbController.executeQuery(sql);//insert
//        createToast("Inserted ");

        String sql = "select * from " + DBController.tblProject +
                " where " + DBController.profileName + " =   '" + profileName + "' and " +
                DBController.projectName + " =  '" + projectName + "' ";//this profilename value from shaerd preference
        if (dbController.getProjectData(sql).isEmpty()) {//the data not exist
            sql = "insert into "
                    + DBController.tblProject +
                    "( '" +
                    DBController.profileName + "', '" +
                    DBController.projectName + "', '" +
                    DBController.projectRole + "', '" +
                    DBController.projectExpertise + "', '" +
                    DBController.projectTeamSize + "', '" +
                    DBController.projectDuration + "'  " +
                    ")" +
                    " values (  " +
                    "'" + profileName + "', " +
                    " '" + strProjectName + "', " +
                    "'" + strRole + "', " +
                    "'" + strExpertise + "', " +
                    "'" + strTeamSize + "', " +
                    "'" + strProjectDuration + "' " +
                    "  ) ";
            //createAlert(sql);
            dbController.executeQuery(sql);//insert
            createToast("Inserted ");

        } else {//the data alredy exist so it will be updated

            sql = "update " + DBController.tblProject +
                    " set " +
                    DBController.projectExpertise + " =  '" + strExpertise + "',  " +
                    DBController.projectDuration + " =  '" + strProjectDuration + "', "
                    + DBController.projectRole + " =  '" + strRole + "',  " +
                    DBController.projectTeamSize + " = '" + strTeamSize + "' ," +
                    DBController.projectName + " = '" + strProjectName + "' " +
                    "where " + DBController.profileName + " =  '" + profileName + "' and " +
                    DBController.projectName + " =  '" + projectName + "' ";
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


}
