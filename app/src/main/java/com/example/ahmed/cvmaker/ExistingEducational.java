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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmed.db.DBController;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ahmed on 01/06/2017.
 */

public class ExistingEducational extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    String sql = "";
    Context context = null;
    LinearLayout linearLayout = null;//where the textview will be added
    TextView textView = null;
    DBController dbController = null;
    ArrayList<HashMap<String, String>> hashMaps = null;
    HashMap<String, String> hashMap = new HashMap<String, String>();
    ArrayList<String> items = new ArrayList<String>();
    String profileName = "";
    SharedPreferences prefProfileName = null, prefDegreeName = null;
    SharedPreferences.Editor editorDegName = null;
    final static String KEY_EDUCATIONAL = "KEY_EDUCATIONAL";
    final static int MODE = Context.MODE_PRIVATE;
    final static String DEGREE = "DEGREE";
    String degreeName = "";




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_educa);

        getDegreesFromDb();

    }


    private void getDegreesFromDb() {

        context = this;
        dbController = new DBController(context);
        /////////////////////////////////////////////
        prefDegreeName = getSharedPreferences(KEY_EDUCATIONAL, MODE);
        editorDegName = prefDegreeName.edit();
        ////////////////////////

        prefProfileName = getSharedPreferences(MainActivity.NAME ,MainActivity.MODE);
        if (prefProfileName.contains(MainActivity.PROFILE_NAME)) {
            profileName = prefProfileName.getString(MainActivity.PROFILE_NAME, "");
        }


        String sql = "select * from " + DBController.tblEdu + " where " +
                DBController.profileName + " =  '" + profileName +"'" ;
        //createAlert(sql);
        hashMaps = dbController.getEduData(sql);
        for (int i = 0; i < hashMaps.size(); i++){
            items.add(hashMaps.get(i).get(DBController.eduDegreeAndCerti));
        }

        if (items.isEmpty()) {
            //do nothing
        }else{
            linearLayout = (LinearLayout) findViewById(R.id.llExistEducational);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,5,5,5);
            //////////////////////
            for (int i = 0; i < items.size(); i++){
                if (items.get(i) != null) {
                    textView = (TextView) getLayoutInflater().inflate(R.layout.simple_list_item_1, null);
                    textView.setText(items.get(i));
                    textView.setLayoutParams(layoutParams);
                    textView.setOnClickListener(this);
                    textView.setOnLongClickListener(this);

                    linearLayout.addView(textView);
                }

            }

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
        if (alertDialog.isShowing()){
            return;
        }
        //else
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {
        if (true) {
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }else {
            super.onBackPressed();
        }


    }

    public void addNewEducationalDetails(View view) {

        prefDegreeName = getSharedPreferences(KEY_EDUCATIONAL, MODE);
        editorDegName = prefDegreeName.edit();

        editorDegName.putString(DEGREE, "");
        editorDegName.commit();
        //editorDegName.clear();//as it is new edu details


        finish();
        startActivity(new Intent(getApplicationContext(), EducationalActivity.class));


    }

    @Override
    public void onClick(View view) {


        editorDegName.putString(DEGREE, ((TextView)view).getText().toString());
        editorDegName.commit();

        finish();
        startActivity(new Intent(context, EducationalActivity.class));
    }

    @Override
    public boolean onLongClick(View view) {

        degreeName = ((TextView)view).getText().toString();
        confirmDeletion();

        return false;
    }

    private void confirmDeletion() {
       new AlertDialog.Builder(context)
               .setMessage("Do you want to delete this degree ?")
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       deleteDegree();
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

    private void deleteDegree() {
        try {
            sql = "delete from "+DBController.tblEdu+" where "+DBController.profileName+" =  '"+profileName+"' and  "+DBController.eduDegreeAndCerti+" = '"+degreeName+"' ";
            dbController.executeQuery(sql);
            createToast("Deleted Success");

            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }catch (Exception e){
            createAlert(e.getMessage().toString());
        }
    }

    private void createToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }


}
