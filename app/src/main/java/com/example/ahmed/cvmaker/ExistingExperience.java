package com.example.ahmed.cvmaker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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

public class ExistingExperience extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    String sql = "";
    String profileName = "";
    SharedPreferences sharedPreferences = null;
    TextView textView = null;
    LinearLayout linearLayout = null;
    ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
    DBController dbController = null;
    ArrayList<String> organizationNames = new ArrayList<String>();
    /////////////////////////
    //////////////
    SharedPreferences prefOrganiName = null;
    SharedPreferences.Editor editor = null;
    final static String KEY_EXP = "KEY_EXP";
    final static int MODE = Context.MODE_PRIVATE;
    Context context = null;
    String organName = "";
    final static String ORGAN_NAME = "ORGAN_NAME";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_experience);

        initVars();
        getProfileName();
        //createToast(profileName);
        getOrganizationNames();

    }

    private void initVars() {
        context = this;

        prefOrganiName = getSharedPreferences(KEY_EXP, MODE);
        editor = prefOrganiName.edit();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

    private void getOrganizationNames() {

        String sql = "select * from " + DBController.tblExperience + " where " +
                DBController.profileName + " =  '" + profileName + "' ";
        dbController = new DBController(getApplicationContext());

        try {
            hashMaps = dbController.getExpData(sql);
            for (int i = 0; i < hashMaps.size(); i++) {
                organizationNames.add(hashMaps.get(i).get(DBController.expOrganizationName));
            }

            if (organizationNames.isEmpty()) {
                //do nothing
            } else {
                linearLayout = (LinearLayout) findViewById(R.id.llExistExper);
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

    private void createToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


    private void getProfileName() {
        sharedPreferences = getSharedPreferences(MainActivity.NAME, MainActivity.MODE);
        if (sharedPreferences.contains(MainActivity.PROFILE_NAME)) {
            profileName = sharedPreferences.getString(MainActivity.PROFILE_NAME, "");
        }
    }


    public void addNewExp(View view) {


        editor.putString(ORGAN_NAME, "");
        editor.commit();

        finish();
        startActivity(new Intent(getApplicationContext(), ExperienceActivity.class));

    }

    @Override
    public void onClick(View view) {


        editor.putString(ORGAN_NAME, ((TextView)view).getText().toString());
        editor.commit();

        finish();
        startActivity(new Intent(context, ExperienceActivity.class));
    }

    @Override
    public boolean onLongClick(View view) {

        organName = ((TextView) view).getText().toString();
        ;
        confirmDeletion();

        return false;
    }

    private void confirmDeletion() {
        new AlertDialog.Builder(context)
                .setMessage("Do you want to delete this experience ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteOrganDetails();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }

    private void deleteOrganDetails() {
        try {
            sql = "delete from " + DBController.tblExperience + " where " +
                    DBController.profileName + " =  '" + profileName + "' and  " +
                    DBController.expOrganizationName + " = '" + organName + "' ";
            dbController.executeQuery(sql);
            createToast("Deleted Success");

            Intent intent = getIntent();
            finish();
            startActivity(intent);
        } catch (Exception e) {
            createAlert(e.getMessage().toString());
        }
    }

    private void createAlert(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(s);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
