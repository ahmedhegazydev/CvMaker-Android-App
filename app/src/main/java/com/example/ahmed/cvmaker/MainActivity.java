package com.example.ahmed.cvmaker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmed.db.DBController;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ahmed on 22/05/2017.
 */

public class MainActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {


    ArrayAdapter<String> adapter = null;
    LinearLayout linearLayout = null;
    DBController dbController = null;
    String selectQuery = null;
    ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> hashMap = new HashMap<String, String>();
    String profileName = "", id;
    EditText editText = null;
    AlertDialog adDecision = null;
    TextView tvEdit, tvDelete, tvCancel = null;
    Button buttonEdit = null;

    SharedPreferences sharedPreferences = null;
    Editor editor = null;
    public final static String NAME = "PREF_1";
    public final static Integer MODE = Context.MODE_PRIVATE;
    public final static String PROFILE_NAME = "PROFILE_NAME";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dbController = new DBController(getApplicationContext());
        getSharePref();
        getExistingProfileNamesFromDb();

    }

    private void getSharePref() {
        sharedPreferences = getSharedPreferences(NAME, MODE);
        editor = sharedPreferences.edit();

    }

    private void getExistingProfileNamesFromDb() {

        //Check if there are existinf profiles created by user before or not
        selectQuery = "select * from " + DBController.tblPersonal;
        if (dbController.getPersonalData(selectQuery).isEmpty()) {//let user to create anew one
            setContentView(R.layout.activity_main);
            editor.clear();//clear the profilename key
        } else {
            setContentView(R.layout.activity_existing_profiles);
            ArrayList<String> items = getItemsFromDb();//get all profile names from the database for list them in the listview
            //createToast(items.toString());
            addViews(items);//list the retrieved items into the list view to be selected by user
        }
    }

    public void createToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void addViews(final ArrayList<String> items) {
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        //////////////////////////////
        LayoutInflater layoutInflater = getLayoutInflater();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        //Rejecting duplicated
        ArrayList<String> strings = new ArrayList<String>();
        for (int i = 0; i < items.size(); i++) {
            if (!strings.contains(items.get(i))) {
                strings.add(items.get(i));
            }
        }
        //creting the views of profiles
        for (int i = 0; i < strings.size(); i++) {
            TextView textView = (TextView) layoutInflater.inflate(R.layout.simple_list_item_1, null);
            textView.setLayoutParams(layoutParams);
            //textView.setOnClickListener(this);
            textView.setOnLongClickListener(this);
            textView.setText(strings.get(i));
            //get id from database where profilename = this
            //String sql = "select * from " + DBController.tblUsers;
            //hashMaps = dbController.getFrofileNameFromTable(sql);
            //hashMap = hashMaps.get(i);
            //textView.setTag(hashMap.get(DBController.id));
            final int f = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    //intent.putExtra("profile_name", items.get(f));
                    startActivity(intent);
                    setSharedPref(items.get(f));
                }
            });
            ////////////////////////////////
            linearLayout.addView(textView);

        }

    }


    //That is the easier way , rather than passing data through the activities
    private void setSharedPref(String s) {

        this.sharedPreferences = getSharedPreferences(this.NAME, this.MODE);
        editor = this.sharedPreferences.edit();

        editor.clear();

        editor.putString(PROFILE_NAME, s);//changed every time automatically by key
        editor.commit();

    }

    private ArrayList<String> getItemsFromDb() {
        ArrayList<String> items = new ArrayList<String>();
        selectQuery = "select * from  " + DBController.tblPersonal;
        hashMaps = dbController.getPersonalData(selectQuery);
        for (int i = 0; i < hashMaps.size(); i++) {
            hashMap = hashMaps.get(i);
            String profileName = hashMap.get(DBController.profileName);
            items.add(profileName);
        }
        return items;
    }


    public void NextButton(View view) {

        EditText editText = (EditText) findViewById(R.id.etProfileName);
        String s = editText.getText().toString();
        if (s.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.enterName, Toast.LENGTH_SHORT).show();

        } else {

            //Check if name is already exist in dataabse
            selectQuery = "select * from " + DBController.tblPersonal + " where " + DBController.profileName + "  =   '" + s + "' ";

            //If the returned arraylist is empty, then that no results
            if (dbController.getPersonalData(selectQuery).isEmpty()) {

                try {
                    //Insert the profile name in db
                    String sql = "insert into " + DBController.tblPersonal + " (" + DBController.profileName + ") Values ( '" + s + "' )";
                    dbController.executeQuery(sql);
                    sql = "insert into " + DBController.tblEdu + "( " + DBController.profileName + ") values ( '" + s + "' )";
                    dbController.executeQuery(sql);
                    sql = "insert into " + DBController.tblProject + "( " + DBController.profileName + ") values ( '" + s + "' )";
                    dbController.executeQuery(sql);
                    sql = "insert into " + DBController.tblExperience + "( " + DBController.profileName + ") values ( '" + s + "' )";
                    dbController.executeQuery(sql);
                    sql = "insert into " + DBController.tblOther + "( " + DBController.profileName + ") values ( '" + s + "' )";
                    dbController.executeQuery(sql);
                    sql = "insert into " + DBController.tblRef + "( " + DBController.profileName + ") values ( '" + s + "' )";
                    dbController.executeQuery(sql);
                } catch (Exception e) {
                    createToast(e.getMessage().toString());
                }

                ///////////
                finish();
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
//                intent.putExtra("profile_name", s);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), R.string.profileAdded, Toast.LENGTH_SHORT).show();
                setSharedPref(s);
            } else {
                Toast.makeText(getApplicationContext(), "The Name Already Exist", Toast.LENGTH_SHORT).show();
            }
        }


    }

    public void addNewProfile(View view) {
        setContentView(R.layout.activity_main);

        //or

//        Intent intent = getIntent();
//        finish();
//        startActivity(intent);
//        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View view) {

        if (view.equals(tvDelete)) {
            deleteProfile(null);
        }
        if (view.equals(tvEdit)) {
            editProfile(null);
        }
        if (view.equals(tvCancel)) {
            cencelDecision(null);
        }
        if (view.equals(buttonEdit)) {
            editProfileName(null);
        }

    }


    @Override
    public boolean onLongClick(View view) {

        makeSelection();
        //get the tv text and set it to blobal variable
        profileName = ((TextView) view).getText().toString().trim();
        //get the id for updating the profle name
        id = ((TextView) view).getTag() + "";

        return true;
    }


    @SuppressLint("NewApi")
    private void makeSelection() {

//        View view = getLayoutInflater().inflate(R.layout.making_a_decision, null);
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setBackgroundResource(R.drawable.rounded_corners);
        linearLayout.setOrientation(LinearLayout.VERTICAL);


        LinearLayout.LayoutParams layoutParams = new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 8, 8, 8);

        tvDelete = new TextView(getApplicationContext());
        tvDelete.setText("Delete");
        tvDelete.setLayoutParams(layoutParams);
        tvDelete.setTypeface(Typeface.DEFAULT_BOLD);
        tvDelete.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        tvDelete.setTextColor(getResources().getColor(R.color.colorBlue));
        tvDelete.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tvDelete.setPadding(8, 8, 8, 8);
        tvDelete.setOnClickListener(this);

        tvEdit = new TextView(getApplicationContext());
        tvEdit.setText("Edit");
        tvEdit.setLayoutParams(layoutParams);
        tvEdit.setTypeface(Typeface.DEFAULT_BOLD);
        tvEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        tvEdit.setTextColor(getResources().getColor(R.color.colorBlue));
        tvEdit.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tvEdit.setPadding(8, 8, 8, 8);
        tvEdit.setOnClickListener(this);


        tvCancel = new TextView(getApplicationContext());
        tvCancel.setText("Cancel");
        tvCancel.setLayoutParams(layoutParams);
        tvCancel.setTypeface(Typeface.DEFAULT_BOLD);
        tvCancel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        tvCancel.setTextColor(getResources().getColor(R.color.colorBlue));
        tvCancel.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tvCancel.setPadding(8, 8, 8, 8);
        tvCancel.setOnClickListener(this);

        linearLayout.addView(tvDelete);
        linearLayout.addView(tvEdit);
        linearLayout.addView(tvCancel);


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Make A Decision");
        builder.setView(linearLayout);

        adDecision = builder.create();
        adDecision.setCanceledOnTouchOutside(false);
        if (adDecision.isShowing()) {
            return;
        }
        //esle
        adDecision.show();

    }

    public void deleteProfile(View view) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Do you want to delete")
                .setMessage("Do you really wnat to delete this profile ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteReoordFromDatabase();
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

    private void deleteReoordFromDatabase() {
        String query = "delete from " + DBController.tblPersonal + " where " +
                DBController.profileName + " =  '" + MainActivity.this.profileName + "' ";
        dbController.executeQuery(query);
        query = "delete from " + DBController.tblEdu + " where " +
                DBController.profileName + " =  '" + MainActivity.this.profileName + "' ";
        dbController.executeQuery(query);
        query = "delete from " + DBController.tblRef + " where " +
                DBController.profileName + " =  '" + MainActivity.this.profileName + "' ";
        dbController.executeQuery(query);
        query = "delete from " + DBController.tblOther + " where " +
                DBController.profileName + " =  '" + MainActivity.this.profileName + "' ";
        dbController.executeQuery(query);
        query = "delete from " + DBController.tblProject + " where " +
                DBController.profileName + " =  '" + MainActivity.this.profileName + "' ";
        dbController.executeQuery(query);
        query = "delete from " + DBController.tblExperience + " where " +
                DBController.profileName + " =  '" + MainActivity.this.profileName + "' ";
        dbController.executeQuery(query);


        createToast("Deleted Successfully");
        //restart activity
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void editProfile(View view) {
        //Showing the view to alow user to edit profilename

        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setBackgroundResource(R.drawable.rounded_corners);
        linearLayout.setOrientation(LinearLayout.VERTICAL);


        LinearLayout.LayoutParams layoutParams = new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 8, 8, 8);

        editText = new EditText(getApplicationContext());
        editText.setPadding(15, 15, 15, 15);
        editText.setTypeface(Typeface.DEFAULT.DEFAULT_BOLD);
        editText.setTextColor(getResources().getColor(R.color.colorBlue));
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setText(profileName);
        editText.setGravity(Gravity.CENTER);
        editText.setHint("Enter Name ...... ");
        editText.setHintTextColor(Color.DKGRAY);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        editText.setBackgroundResource(R.drawable.rounded_corners2);
        editText.setLayoutParams(layoutParams);
        //createToast(editText.getTag().toString());//for testing that the edittext have been found ok


        buttonEdit = new Button(getApplicationContext());
        buttonEdit.setText("Edit");
        buttonEdit.setTextColor(getResources().getColor(R.color.colorBlue));
        buttonEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        buttonEdit.setBackgroundColor(Color.parseColor("#FFFFFF"));
        buttonEdit.setLayoutParams(layoutParams);
        buttonEdit.setOnClickListener(this);

        linearLayout.addView(editText);
        linearLayout.addView(buttonEdit);

        new AlertDialog.Builder(MainActivity.this)
                .setView(linearLayout)
                .show();
        //set the default profile name to edittext as user see it before update
//        View view2 = getLayoutInflater().inflate(R.layout.edit_profile, null, false);
//        createToast(view2.getTag().toString());//ok
//        editText = (EditText) view2.findViewById(R.id.etEditProfileName);


    }

    public void cencelDecision(View view) {
        adDecision.dismiss();
    }

    public void editProfileName(View view) {
        String s = editText.getText().toString();
        if (s.trim().length() == 0) {//emtpty
            createAlert("Please, Enter Profile Name ");
        } else {
            updateProfileName(s);
        }

    }

    private void updateProfileName(String newName) {
        String sql = "";

        try {
            sql = "update " + DBController.tblPersonal + " set " + DBController.profileName + " = '" + newName + "' where " + DBController.profileName + " =  '" + profileName + "' ";
            dbController.executeQuery(sql);

            sql = "update " + DBController.tblEdu + " set " + DBController.profileName + " = '" + newName + "' where " + DBController.profileName + " =  '" + profileName + "' ";
            dbController.executeQuery(sql);

            sql = "update " + DBController.tblExperience + " set " + DBController.profileName + " = '" + newName + "' where " + DBController.profileName + " =  '" + profileName + "' ";
            dbController.executeQuery(sql);

            sql = "update " + DBController.tblProject + " set " + DBController.profileName + " = '" + newName + "' where " + DBController.profileName + " =  '" + profileName + "' ";
            dbController.executeQuery(sql);

            sql = "update " + DBController.tblRef + " set " + DBController.profileName + " = '" + newName + "' where " + DBController.profileName + " =  '" + profileName + "' ";
            dbController.executeQuery(sql);

            sql = "update " + DBController.tblOther + " set " + DBController.profileName + " = '" + newName + "' where " + DBController.profileName + " =  '" + profileName + "' ";
            dbController.executeQuery(sql);
        } catch (Exception e) {
            createAlert(e.getMessage().toString());
        } finally {

            createToast("Updated");

            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }


    }


    public void createAlert(String message) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }

}
