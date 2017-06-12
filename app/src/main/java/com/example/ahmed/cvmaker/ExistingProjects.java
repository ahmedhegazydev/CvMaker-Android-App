package com.example.ahmed.cvmaker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmed.db.DBController;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ahmed on 01/06/2017.
 */

public class ExistingProjects extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    public static final String KEY_PROJECT = "KEY_PROJECT";
    public static final int MODE = Context.MODE_PRIVATE;
    public static final String PROJECT_NAME = "PROJECT_NAME";
    String profileName = "";
    SharedPreferences sharedPreferences = null, prefProjectName = null;
    SharedPreferences.Editor editorProjectName = null;
    TextView textView = null;
    LinearLayout linearLayout = null;
    ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
    DBController dbController = null;
    ArrayList<String> organizationNames = new ArrayList<String>();
    Context context = null;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_projects);

        initVars();
        getProfileName();
        getProjcts();

    }

    private void initVars() {
        context = this;
        dbController = new DBController(getApplicationContext());

        ///////

        prefProjectName = getSharedPreferences(KEY_PROJECT, MODE);
        editorProjectName = prefProjectName.edit();


    }


    private void getProjcts() {

        String sql = "select * from " + DBController.tblProject + " where " +
                DBController.profileName + " =  '" + profileName + "' ";
        try {
            hashMaps = dbController.getProjectData(sql);
            for (int i = 0; i < hashMaps.size(); i++) {
                organizationNames.add(hashMaps.get(i).get(DBController.projectName));
            }

            if (organizationNames.isEmpty()) {
                //do nothing
            } else {
                linearLayout = (LinearLayout) findViewById(R.id.llExistProjects);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(5, 5, 5, 5);
                //////////////////////
                for (int i = 0; i < organizationNames.size(); i++) {
                    if (organizationNames.get(i) != null) {
                        textView = (TextView) getLayoutInflater().inflate(R.layout.simple_list_item_1, null);
                        textView.setText(organizationNames.get(i));
                        textView.setLayoutParams(layoutParams);
                        textView.setOnClickListener(this);
                        textView.setOnLongClickListener(this);

                        linearLayout.addView(textView);
                    }

                }

            }
        } catch (Exception e) {
            createToast(e.getMessage().toString());
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

    private void getProfileName() {
        sharedPreferences = getSharedPreferences(MainActivity.NAME, MainActivity.MODE);
        if (sharedPreferences.contains(MainActivity.PROFILE_NAME)) {
            profileName = sharedPreferences.getString(MainActivity.PROFILE_NAME, "");
        }
    }

    private void createToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
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


    @Override
    public void onClick(View view) {

        editorProjectName.putString(PROJECT_NAME, ((TextView)view).getText().toString());
        editorProjectName.commit();

        finish();
        startActivity(new Intent(context, ProjectDetailsActivity.class));


    }


    String projectName = "";
    @Override
    public boolean onLongClick(View view) {

        projectName = ((TextView)view).getText().toString();
        confirmDeletion();

        return false;
    }

    private void confirmDeletion() {

        new AlertDialog.Builder(context)
                .setMessage("Do you want to delete this project ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteProject();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }

    private void deleteProject() {
        String sql = "delete from "+DBController.tblProject+" where "+DBController.profileName+" = '"+profileName+"' and  "+
                DBController.projectName+" = '"+projectName+"' ";
        dbController.executeQuery(sql);
        createToast("Deleted");

        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }





    public void addNewProjectDetails(View view) {

        editorProjectName.putString(PROJECT_NAME, "");
        editorProjectName.commit();

        finish();
        startActivity(new Intent(getApplicationContext(), ProjectDetailsActivity.class));
    }
}
