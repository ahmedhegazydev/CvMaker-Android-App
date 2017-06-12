package com.example.ahmed.cvmaker;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
//import com.itextpdf.text.Font;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

//import com.itextpdf.text.Font;


public class GenerateResumeActivity extends AppCompatActivity {

    Context context = null;
    String fileName = "";
    String dir = "Dir";



    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_resume);


        initVars();


    }

    // Method for opening a pdf file
    private void viewPdf(String file, String directory) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        Uri path = Uri.fromFile(pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    public void sharePdfFileViaEmail(String filename) {

        ///////////////
        //File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
        File filelocation = new File(Environment.getExternalStorageDirectory() + "/" + dir + "/" + filename);
        Uri path = Uri.fromFile(filelocation);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // set the type to 'email'
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"asd@gmail.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void initVars() {
        context = this;

        //get Extras
        Intent intent = getIntent();
        fileName = intent.getStringExtra("file_name");
        dir = intent.getStringExtra("directory");//= Dir

    }

    //onclick
    public void shareViaEmail(View view) {
        sharePdfFileViaEmail(this.fileName);
    }


    @Override
    public void onBackPressed() {
        if (true){
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }else {
            super.onBackPressed();
        }

    }

    /**
     * @param view
     */
    public void viewDownloadPrintResume(View view) {
        viewPdf(this.fileName, this.dir);
    }


    //onclick
    public void rateUs(View view) {
        rateUsFunction();
    }

    private void rateUsFunction() {

    }


    //onclick
    public void haveSuggestion(View view) {
        haveSuggestionFunction();
    }

    private void haveSuggestionFunction() {
    }
    //onclick
    public void helpUsWithTranslation(View view) {
        helpUsWithTranslationFunction();
    }

    private void helpUsWithTranslationFunction() {

    }
}
