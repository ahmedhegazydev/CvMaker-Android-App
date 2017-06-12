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

public class ExistingReferences extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {


    String profileName = "";
    SharedPreferences sharedPreferences = null, prefRefName = null;
    SharedPreferences.Editor editorRefName = null;
    TextView textView = null;
    LinearLayout linearLayout = null;
    ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
    DBController dbController = null;
    ArrayList<String> organizationNames = new ArrayList<String>();
    Context context = null;
    final static String REF_NAME = "REF_NAME";
    final static String KEY_REF = "KEY_REF";
    final static int MODE = Context.MODE_PRIVATE;
    String sql = "";
    String refName = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_references);


        initVars();
        getProfileName();
        getRefNames();


    }

    private void initVars() {
        context = this;
        dbController = new DBController(context);
        /////////////////
        prefRefName = getSharedPreferences(KEY_REF, MODE);
        editorRefName = prefRefName.edit();


    }

    private void getProfileName() {
        sharedPreferences = getSharedPreferences(MainActivity.NAME, MainActivity.MODE);
        if (sharedPreferences.contains(MainActivity.PROFILE_NAME)) {
            profileName = sharedPreferences.getString(MainActivity.PROFILE_NAME, "");
        }
    }

    private void getRefNames() {

        String sql = "select * from " + DBController.tblRef + " where " +
                DBController.profileName + " =  '" + profileName + "' ";
        try {
            hashMaps = dbController.getRefData(sql);
            for (int i = 0; i < hashMaps.size(); i++) {
                organizationNames.add(hashMaps.get(i).get(DBController.referName));
            }

            if (organizationNames.isEmpty()) {
                //do nothing
            } else {
                linearLayout = (LinearLayout) findViewById(R.id.llExistRef);
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
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View view) {


        editorRefName.putString(REF_NAME, ((TextView)view).getText().toString());
        editorRefName.commit();

        finish();
        startActivity(new Intent(context, ReferenceDetailsActivity.class));

    }

    @Override
    public boolean onLongClick(View view) {

        refName = ((TextView) view).getText().toString();
        confirmDeletion();

        return false;
    }

    private void confirmDeletion() {
        new AlertDialog.Builder(context)
                .setMessage("Do you want to delete this reference ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteRefDetails();
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

    private void deleteRefDetails() {
        try {
            sql = "delete from " + DBController.tblRef + " where " +
                    DBController.profileName + " =  '" + profileName + "' and  " +
                    DBController.referName + " = '" + refName + "' ";
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

    public void addNewRefDetails(View view) {


        editorRefName.putString(REF_NAME, "");
        editorRefName.commit();

        finish();
        startActivity(new Intent(getApplicationContext(), ReferenceDetailsActivity.class));
    }
}
