package com.ahmed.db;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DBController extends SQLiteOpenHelper {


    public static final String databasename = "BloodBankDb"; // Dtabasename


    public static final String tblPersonal = "tblPersonal";// tablename Users Details
    public static final String id = "id"; // auto generated ID column
    public static final String profileName = "profileName"; // profile name i id also can't be duplicated
    public static final String fullName = "FullName";
    public static final String email = "email";
    public static final String address = "address";
    public static final String dateOfBirth = "dob";
    public static final String gender = "gender";
    public static final String image = "image";
    public static final String phone = "phone";
    public static final String langauges = "langs";
    //Educational Details
    public static final String tblEdu = "tblEdu";
    public static final String eduDegreeAndCerti = "degreeAndCerti";
    public static final String eduGpa = "gpa";
    public static final String eduSchoolName = "schoolName";
    public static final String eduPassingYear = "passingYear";
    //Table name Project Details
    public static final String tblProject = "tblProject";
    public static final String projectName = "projectName";
    public static final String projectDuration = "projectDuration";
    public static final String projectRole = "projectRole";
    public static final String projectTeamSize = "projectTeamSize";
    public static final String projectExpertise = "projectExpertise";
    //Reference Details
    public static final String tblRef = "tblRef";
    public static final String referName = "referName";
    public static final String referDetails = "referDetails";
    public static final String referContactNumber = "referContactNumber";
    public static final String referEmail = "referEmail";
    //Experience Details
    public static final String tblExperience = "tblExperience";
    public static final String expOrganizationName = "expOrganizationName";
    public static final String expPosition = "expPosition";
    public static final String expDuration = "expDuration";
    public static final String expOrganLocation = "expOrganLocation";
    public static final String expSalary = "expSalary";
    public static final String expJobResp = "expJobResp";

    //Other Details
    public static final String tblOther = "tblOther";
    public static final String otherDrivingLic = "otherDrivingLic";
    public static final String otherPassportNumber = "otherPassportNumber";


    public static final int versioncode = 16; // versioncode of the database start from >= 1


    String[] statements = new String[]{
            //Personal  Details Table 
            "CREATE TABLE IF NOT EXISTS "
                    + tblPersonal + "(" +
                    this.id + " integer primary key, " +
                    this.profileName + " text, " +
                    this.fullName + " text, " +
                    this.gender + " text , " +
                    this.dateOfBirth + " text , " +
                    this.address + " text , " +
                    this.langauges + " text, " +
                    this.phone + " text, " +
                    this.email + " text, " +
                    this.image + " blob " +
                    ")"
            ,
            //Educational Details Table
            "CREATE TABLE IF NOT EXISTS "
                    + tblEdu + "(" +
                    this.profileName + " text, " +
                    this.eduDegreeAndCerti + " text, " +
                    this.eduSchoolName + " text, " +
                    this.eduGpa + " text, " +
                    this.eduPassingYear + " text " +
                    ")"
            ,
            //Other Details
            "CREATE TABLE IF NOT EXISTS "
                    + tblOther + "(" +
                    this.profileName + " text, " +
                    this.otherDrivingLic + " text, " +
                    this.otherPassportNumber + " text " +
                    ")"
            ,
            //experience Details
            "CREATE TABLE IF NOT EXISTS "
                    + tblExperience + "(" +
                    this.profileName + " text, " +
                    this.expOrganizationName + " text, " +
                    this.expPosition + " text, " +
                    this.expDuration + " text, " +
                    this.expOrganLocation + " text, " +
                    this.expSalary + " text, " +
                    this.expJobResp + " text " +
                    ")"
            ,
            //Reference Details
            "CREATE TABLE IF NOT EXISTS "
                    + tblRef + "(" +
                    this.profileName + " text, " +
                    this.referName + " text, " +
                    this.referDetails + " text, " +
                    this.referContactNumber + " text, " +
                    this.referEmail + " text " +
                    ")"
            ,
            //Project Details Table
            "CREATE TABLE IF NOT EXISTS "
                    + tblProject + "(" +
                    this.profileName + " text, " +
                    this.projectName + " text, " +
                    this.projectDuration + " text, " +
                    this.projectRole + " text, " +
                    this.projectTeamSize + " text, " +
                    this.projectExpertise + " text " +
                    ")"


    };
    String[] tableNames = new String[]{tblRef, tblEdu, tblExperience, tblOther, tblProject, tblPersonal};

    // constructor
    public DBController(Context context) {
        super(context, databasename, null, versioncode);


    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createTables(database);

    }

    private void createTables(SQLiteDatabase database) {

        for (String query : statements) {
            database.execSQL(query);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        tablesUpgrade(database);

        onCreate(database);
    }

    private void tablesUpgrade(SQLiteDatabase database) {

        for (String tablename : tableNames) {
            database.execSQL("DROP TABLE IF EXISTS " + tablename);
        }

    }

    /**
     * This method deletes all records of the table 1
     * the users table
     */
    public void deleteAllRecords() {
        // TODO Auto-generated method stub

//        SQLiteDatabase db = this.getWritableDatabase();
//        // db.execSQL("delete from "+ this.tblUsers);
//        db.delete(this.tblUsers, null, null);
//        db.close();

		/*
         * or, if you want the function to return the count of deleted rows,
		 * 
		 * db.delete(TABLE_NAME, "1", null); From the documentation of
		 * SQLiteDatabase delete method:
		 * 
		 * To remove all rows and get a count pass "1" as the whereClause.
		 */

    }


    public boolean deleteWithCondition(String query) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            database.execSQL(query);
            database.close();
            return true;
        } catch (SQLException e) {
            return false;
        }

    }


    public boolean insertImgInDb(byte[] bytes){
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBController.image, bytes);
            sqLiteDatabase.insert(DBController.tblPersonal, null, contentValues);

        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * didnot used
     *
     * @param bytes
     * @param id
     * @return
     */
    public boolean saveBytes(byte[] bytes, int id) {

        boolean ret = false;
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {

            String sql = "INSERT INTO IMAGES " + " ( IMAGE_ID" + ", IMAGE_BLOB" + " ) VALUES(?,?)";

            SQLiteStatement insertStmt = db.compileStatement(sql);
            insertStmt.clearBindings();
            insertStmt.bindLong(1, id);
            insertStmt.bindBlob(2, bytes);
            insertStmt.executeInsert();

            db.setTransactionSuccessful();
            db.endTransaction();

            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        }

        return ret;
    }

    /**
     * didnot used
     *
     * @param id
     * @return
     * @throws Exception
     */
    public byte[] getBytes(String profileName) throws Exception {

        byte[] ret = null;

        try {

            String selectQuery = "SELECT  I." + DBController.image +
                    "  FROM " + DBController.tblPersonal + " I WHERE I." + DBController.profileName + " = ?";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});

            if (!c.isClosed() && c.moveToFirst() && c.getCount() > 0) {

                if (c.getBlob(c.getColumnIndex(DBController.image)) != null) {
                    ret = c.getBlob(c.getColumnIndex(DBController.image));

                }
                c.close();
                if (db != null && db.isOpen())
                    db.close();
            }
            System.gc();
        } catch (Exception e) {
            System.gc();
            throw e;

        }
        return ret;
    }

    public void executeQuery(String sql) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(sql);
    }

//    public void insertProfileName(String profileName) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(this.profileName, profileName);
//        db.insert(this.tblProfileNames, null, cv);
//        db.close();
//
//    }

    public ArrayList<HashMap<String, String>> getPersonalData(String selectQuery) {
        ArrayList<HashMap<String, String>> arrayList;
        arrayList = new ArrayList<HashMap<String, String>>();
        // String selectQuery = "SELECT * FROM " + this.tblUsers;
        //String selectQuery = "select * from " + this.tblUsers + " where username  = '" + username
        //      + "' and password =  '" + password + "'     ";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                // Respectively as inserted at first time
                //Personal Details
                map.put(this.id, cursor.getString(0));
                map.put(this.profileName, cursor.getString(1));
                map.put(this.fullName, cursor.getString(2));
                map.put(this.gender, cursor.getString(3));
                map.put(this.dateOfBirth, cursor.getString(4));
                map.put(this.address, cursor.getString(5));
                map.put(this.langauges, cursor.getString(6));
                map.put(this.phone, cursor.getString(7));
                map.put(this.email, cursor.getString(8));
                //image
                arrayList.add(map);

            } while (cursor.moveToNext());
        }
        // return contact list
        return arrayList;
    }


    public ArrayList<HashMap<String, String>> getEduData(String selectQuery) {
        ArrayList<HashMap<String, String>> arrayList;
        arrayList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                //Educational Details
                map.put(this.profileName, cursor.getString(0));
                map.put(this.eduDegreeAndCerti, cursor.getString(1));
                map.put(this.eduSchoolName, cursor.getString(2));
                map.put(this.eduGpa, cursor.getString(3));
                map.put(this.eduPassingYear, cursor.getString(4));
                arrayList.add(map);

            } while (cursor.moveToNext());
        }

        // return contact list
        return arrayList;
    }

    public ArrayList<HashMap<String, String>> getProjectData(String selectQuery) {
        ArrayList<HashMap<String, String>> arrayList;
        arrayList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                //Project Details
                map.put(this.profileName, cursor.getString(0));
                map.put(this.projectName, cursor.getString(1));
                map.put(this.projectDuration, cursor.getString(2));
                map.put(this.projectRole, cursor.getString(3));
                map.put(this.projectTeamSize, cursor.getString(4));
                map.put(this.projectExpertise, cursor.getString(5));

                arrayList.add(map);
            } while (cursor.moveToNext());
        }
        // return contact list
        return arrayList;
    }

    public ArrayList<HashMap<String, String>> getRefData(String selectQuery) {
        ArrayList<HashMap<String, String>> arrayList;
        arrayList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                //Reference Details
                map.put(this.profileName, cursor.getString(0));
                map.put(this.referName, cursor.getString(1));
                map.put(this.referDetails, cursor.getString(2));
                map.put(this.referContactNumber, cursor.getString(3));
                map.put(this.referEmail, cursor.getString(4));

                arrayList.add(map);
            } while (cursor.moveToNext());
        }
        // return contact list
        return arrayList;
    }


    public ArrayList<HashMap<String, String>> getExpData(String selectQuery) {
        ArrayList<HashMap<String, String>> arrayList;
        arrayList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                //Experience Details
                map.put(this.profileName, cursor.getString(0));
                map.put(this.expOrganizationName, cursor.getString(1));
                map.put(this.expPosition, cursor.getString(2));
                map.put(this.expDuration, cursor.getString(3));
                map.put(this.expOrganLocation, cursor.getString(4));
                map.put(this.expSalary, cursor.getString(5));
                map.put(this.expJobResp, cursor.getString(6));

                arrayList.add(map);
            } while (cursor.moveToNext());
        }
        // return contact list
        return arrayList;
    }

    public ArrayList<HashMap<String, String>> getOtherData(String selectQuery) {
        ArrayList<HashMap<String, String>> arrayList;
        arrayList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                //Other Details
                map.put(this.profileName, cursor.getString(0));
                map.put(this.otherDrivingLic, cursor.getString(1));
                map.put(this.otherPassportNumber, cursor.getString(2));

                arrayList.add(map);
            } while (cursor.moveToNext());
        }
        // return contact list
        return arrayList;
    }

    /**
     * This method will get the inserted image as blob for
     * the user to see it and update it
     *
     * @param selectQuery
     * @return
     */
    // For returning all users from database
    public ArrayList<HashMap<String, byte[]>> getBlobImageBySql(String selectQuery) {
        ArrayList<HashMap<String, byte[]>> arrayList = new ArrayList<HashMap<String, byte[]>>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, byte[]> map = new HashMap<String, byte[]>();
                map.put(this.image, cursor.getBlob(9));
                arrayList.add(map);
            } while (cursor.moveToNext());
        }
        // return contact list
        return arrayList;
    }

    /**
     * didnot used
     *
     * @return
     */
//    public int numberOfRows() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        int numRows = (int) DatabaseUtils.queryNumEntries(db, this.tblUsers);
//        return numRows;
//    }

    /**
     * didnot used
     *
     * @param id
     * @return
     */
//    public Cursor getData(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res = db.rawQuery("select * from '" + this.tblUsers + "' where id=" + id + "", null);
//        return res;
//    }


    /**
     * This method will update password when the username matches with this
     * record from users table
     *
     * @param username
     * @param password
     * @return
     */
//    public boolean updatePassword(String username, String password) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        // contentValues.put("username", username);
//        contentValues.put("password", password);
//        // contentValues.put("firstname", firstName);
//        // contentValues.put("lastname", lastName);
//        db.update(this.tblUsers, contentValues, "username = ?", new String[]{username});
//        return true;
//    }

    /**
     * @param username
     * @param password
     * @param firstName
     * @param lastName
     * @return
     */
//    public boolean updateData(String username, String password, String firstName, String lastName) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("username", username);
//        contentValues.put("password", password);
//        contentValues.put("firstname", firstName);
//        contentValues.put("lastname", lastName);
//        db.update(this.tblUsers, contentValues, "firstname = ? and lastname = ?",
//                new String[]{firstName, lastName});
//        return true;
//    }

    /**
     * This method will update all user data that has been logged in using
     * the username and password
     *
     * @param username
     * @param password
     * @param firstName
     * @param lastName
     * @param phone
     * @param gender
     * @param bloodGroup
     * @param email
     * @param city
     * @param country
     * @param donorOrNot
     * @param bytes
     * @return
     */
//    public boolean updateData(String username, String password, String firstName, String lastName, String phone,
//                              String gender, String bloodGroup, String email, String city, String country, String donorOrNot,
//                              byte[] bytes) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("username", username);
//        contentValues.put("password", password);
//        contentValues.put("firstname", firstName);
//        contentValues.put("lastname", lastName);
//        contentValues.put("city", city);
//        contentValues.put("country", country);
//        contentValues.put("email", email);
//        contentValues.put("phone", phone);
//        contentValues.put("donorornot", donorOrNot);
//        contentValues.put("bloodgroup", bloodGroup);
//        contentValues.put("image", bytes);
//        contentValues.put("gender", gender);
//
//        db.update(this.tblUsers, contentValues, "username = ? and password = ?", new String[]{username, password});
//        return true;
//    }

}