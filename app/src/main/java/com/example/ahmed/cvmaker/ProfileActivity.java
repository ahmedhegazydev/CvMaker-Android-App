package com.example.ahmed.cvmaker;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmed.db.DBController;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.datatype.Duration;

public class ProfileActivity extends AppCompatActivity {

    TextView textView = null;
    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor editor = null;
    Context context = null;
    //////////////////
    ArrayList<String> items = new ArrayList<String>();
    ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> hashMap = new HashMap<String, String>();
    DBController dbController = null;
    String profileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_profile);


        dbController = new DBController(getApplicationContext());
        textView = (TextView) findViewById(R.id.tvProfileName);
        context = this;
        //getPassedNameFromUser();
        getSharedPrefKeyVal();

    }

    private void getSharedPrefKeyVal() {
        sharedPreferences = getSharedPreferences(MainActivity.NAME, MainActivity.MODE);
        if (sharedPreferences.contains(MainActivity.PROFILE_NAME)) {
            profileName = sharedPreferences.getString(MainActivity.PROFILE_NAME, "");
            textView.setText(profileName);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        getSharedPrefKeyVal();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public class GeneratePdf extends AsyncTask<Void, Void, String> {

        /**
         * progress dialog to show user that the backup is processing.
         */
        Context context = null;
        private ProgressDialog dialog = null;
        ArrayList<HashMap<String, String>>
                hmsPersonalDetails = null,
                hmsEducationalDetails = null,
                hmsExperienceDetails = null,
                hmsProjectDetails = null,
                hmsReferenceDetails = null,
                hmsOtherDetails = null;
        String fileName = "";


        public GeneratePdf(Context context) {
            this.context = context;
            dialog = new ProgressDialog(this.context);
        }

        //Constructor
        public GeneratePdf(Context context,
                           String fileName,
                           ArrayList<HashMap<String, String>> hmsPersonalDetails,
                           ArrayList<HashMap<String, String>> hmsEducationalDetails,
                           ArrayList<HashMap<String, String>> hmsExperienceDetails,
                           ArrayList<HashMap<String, String>> hmsProjectDetails,
                           ArrayList<HashMap<String, String>> hmsReferenceDetails,
                           ArrayList<HashMap<String, String>> hmsOtherDetails
                           //The image
        ) {

            this.context = context;
            this.hmsPersonalDetails = hmsPersonalDetails;
            this.hmsEducationalDetails = hmsEducationalDetails;
            this.hmsExperienceDetails = hmsExperienceDetails;
            this.hmsProjectDetails = hmsProjectDetails;
            this.hmsReferenceDetails = hmsReferenceDetails;
            this.hmsOtherDetails = hmsOtherDetails;

            this.fileName = fileName;

            dialog = new ProgressDialog(this.context);


        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected String doInBackground(Void... voids) {
            //            creatPdf();
            createandDisplayPdf(this.fileName, hmsPersonalDetails, hmsEducationalDetails, hmsExperienceDetails,
                    hmsProjectDetails, hmsReferenceDetails, hmsOtherDetails);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Generating the Pdf");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            if (dialog.isShowing()) {
                dialog.dismiss();
//                dialog.cancel();
            }
            createToast("Pdf Created");
            //viewPdf(fileName + ".pdf", "Dir");
            finish();
            Intent intent = new Intent(context, GenerateResumeActivity.class);
            intent.putExtra("file_name", fileName + ".pdf");
            intent.putExtra("directory", "Dir");

            startActivity(intent);

        }
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

    public Document createandDisplayPdf(String fileName,
                                        ArrayList<HashMap<String, String>> hmsPersonal,
                                        ArrayList<HashMap<String, String>> hmsEdu,
                                        ArrayList<HashMap<String, String>> hmsExp,
                                        ArrayList<HashMap<String, String>> hmsProject,
                                        ArrayList<HashMap<String, String>> hmsRef,
                                        ArrayList<HashMap<String, String>> hmsOther) {
        Document doc = new Document();
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dir, fileName + ".pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter.getInstance(doc, fOut);
            //open the document
            doc.open();

            //personal details
            String fullName = hmsPersonal.get(0).get(DBController.fullName);
            String gender = hmsPersonal.get(0).get(DBController.gender);
            String dob = hmsPersonal.get(0).get(DBController.dateOfBirth);
            String address = hmsPersonal.get(0).get(DBController.address);
            String langs = hmsPersonal.get(0).get(DBController.langauges);
            String phone = hmsPersonal.get(0).get(DBController.phone);
            String email = hmsPersonal.get(0).get(DBController.email);


            // Create and add a Paragraph
            Paragraph p
                    = new Paragraph(fullName, new Font(Font.FontFamily.HELVETICA, 22));
            p.setAlignment(Element.ALIGN_CENTER);
            doc.add(p);
            /////------------------The personal Image
            String selectQuery = "select * from " + dbController.tblPersonal +
                    " where " +
                    DBController.profileName + " =  '" + profileName + "' ";
            ArrayList<HashMap<String, byte[]>> hmsImage = dbController.getBlobImageBySql(selectQuery);
            HashMap<String, byte[]> hmImage = hmsImage.get(0);
            if (!hashMap.isEmpty()) {
                byte[] bs = null;
                bs = hmImage.get(DBController.image);
                if (bs != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bs, 0, bs.length);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image image = Image.getInstance(stream.toByteArray());
                    doc.add(image);
                } else {

                }
            } else {

            }
            //-------------------------------------------------
            //Personal Details  ==>>> must
            // a table with two columns
            PdfPTable table = new PdfPTable(2);
            // the cell object
            PdfPCell cell;
            cell = new PdfPCell(new Phrase("Gender"));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(gender));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//center text in cell
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Contact Number"));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(phone));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//center text in cell
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Email"));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(email));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//center text in cell
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("DoB"));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(dob));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//center text in cell
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Languages"));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(langs));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//center text in cell
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Address"));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(address));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//center text in cell
            table.addCell(cell);
            table.setSpacingAfter(2f);
            doc.add(table);


            // add a couple of blank lines
//            doc.add(Chunk.NEWLINE);
//            doc.add(Chunk.NEWLINE);
//            You can try a blank phrase:
//            document.add(new Phrase("\n"));

            //adding separator
//            DottedLineSeparator separator = new DottedLineSeparator();
//            separator.setPercentage(59500f / 523f);
//            Chunk linebreak = new Chunk(separator);
//
//            doc.add(linebreak);


            //Educational Details
            PdfPTable tblEdu = new PdfPTable(5);
            PdfPCell pdfPCell = null;
            for (int i = 1; i < hmsEdu.size(); i++) {
                HashMap<String, String> hashMap = hmsEdu.get(i);
                String degreeOrCert = hashMap.get(DBController.eduDegreeAndCerti);
                String schoolName = hashMap.get(DBController.eduSchoolName);
                String gpa = hashMap.get(DBController.eduGpa);
                String passingYear = hashMap.get(DBController.eduPassingYear);

                pdfPCell = new PdfPCell(new Phrase(i + ""));
                tblEdu.addCell(pdfPCell);
                pdfPCell = new PdfPCell(new Phrase(degreeOrCert));
                tblEdu.addCell(pdfPCell);
                pdfPCell = new PdfPCell(new Phrase(schoolName));
                tblEdu.addCell(pdfPCell);
                pdfPCell = new PdfPCell(new Phrase(gpa));
                tblEdu.addCell(pdfPCell);
                pdfPCell = new PdfPCell(new Phrase(passingYear));
                tblEdu.addCell(pdfPCell);

            }
            tblEdu.setSpacingAfter(2f);
            if (hmsEdu.size() >= 1) {
                // Create and add a Paragraph
                p = new Paragraph("Educational Details :", new Font(Font.FontFamily.HELVETICA, 22));
                p.setAlignment(Element.ALIGN_CENTER);
                p.setSpacingAfter(1f);
                doc.add(p);
                /////////////////////////////
                pdfPCell = new PdfPCell(new Phrase("NO."));
                tblEdu.addCell(pdfPCell);
                pdfPCell = new PdfPCell(new Phrase("DegreeAndCertificate"));
                tblEdu.addCell(pdfPCell);
                pdfPCell = new PdfPCell(new Phrase("School Name"));
                tblEdu.addCell(pdfPCell);
                pdfPCell = new PdfPCell(new Phrase("GPA"));
                tblEdu.addCell(pdfPCell);
                pdfPCell = new PdfPCell(new Phrase("Passing Year"));
                tblEdu.addCell(pdfPCell);
                ///////////////////
                doc.add(tblEdu);
            }


            //Experience Details

            PdfPTable tblExp = new PdfPTable(7);
            PdfPCell pdfPCellExp = new PdfPCell(new Phrase("No."));
            tblExp.addCell(pdfPCellExp);
            pdfPCellExp = new PdfPCell(new Phrase("Organ. Name"));
            tblExp.addCell(pdfPCellExp);
            pdfPCellExp = new PdfPCell(new Phrase("Position"));
            tblExp.addCell(pdfPCellExp);
            pdfPCellExp = new PdfPCell(new Phrase("Duration"));
            tblExp.addCell(pdfPCellExp);
            pdfPCellExp = new PdfPCell(new Phrase("Location"));
            tblExp.addCell(pdfPCellExp);
            pdfPCellExp = new PdfPCell(new Phrase("Salary"));
            tblExp.addCell(pdfPCellExp);
            pdfPCellExp = new PdfPCell(new Phrase("Job Respo."));
            tblExp.addCell(pdfPCellExp);
            for (int i = 1; i < hmsExp.size(); i++) {
                HashMap<String, String> hashMap = hmsExp.get(i);
                String organName = hashMap.get(DBController.expOrganizationName);
                String position = hashMap.get(DBController.expPosition);
                String duration = hashMap.get(DBController.expDuration);
                String location = hashMap.get(DBController.expOrganLocation);
                String salary = hashMap.get(DBController.expSalary);
                String jobResp = hashMap.get(DBController.expJobResp);

                pdfPCellExp = new PdfPCell(new Phrase(i + ""));
                tblExp.addCell(pdfPCellExp);
                pdfPCellExp = new PdfPCell(new Phrase(organName));
                tblExp.addCell(pdfPCellExp);
                pdfPCellExp = new PdfPCell(new Phrase(position));
                tblExp.addCell(pdfPCellExp);
                pdfPCellExp = new PdfPCell(new Phrase(duration));
                tblExp.addCell(pdfPCellExp);
                pdfPCellExp = new PdfPCell(new Phrase(location));
                tblExp.addCell(pdfPCellExp);
                pdfPCellExp = new PdfPCell(new Phrase(salary));
                tblExp.addCell(pdfPCellExp);
                pdfPCellExp = new PdfPCell(new Phrase(jobResp));
                tblExp.addCell(pdfPCellExp);

            }
            tblExp.setSpacingAfter(2f);
            if (hmsExp.size() != 0) {
                // Create and add a Paragraph
                p = new Paragraph("Experience Details :", new Font(Font.FontFamily.HELVETICA, 22));
                p.setAlignment(Element.ALIGN_CENTER);
                p.setSpacingAfter(1f);
                doc.add(p);
                /////////////////////
                doc.add(tblExp);
            }

            //Project Details
            PdfPTable tblProject = new PdfPTable(6);
            PdfPCell pdfPCellPro = new PdfPCell(new Phrase("NO."));
            tblProject.addCell(pdfPCellPro);
            pdfPCellPro = new PdfPCell(new Phrase("Project Name"));
            tblProject.addCell(pdfPCellPro);
            pdfPCellPro = new PdfPCell(new Phrase("Duration"));
            tblProject.addCell(pdfPCellPro);
            pdfPCellPro = new PdfPCell(new Phrase("Role"));
            tblProject.addCell(pdfPCellPro);
            pdfPCellPro = new PdfPCell(new Phrase("Team Size"));
            tblProject.addCell(pdfPCellPro);
            pdfPCellPro = new PdfPCell(new Phrase("Expertise"));
            tblProject.addCell(pdfPCellPro);
            for (int i = 1; i < hmsProject.size(); i++) {
                HashMap<String, String> hashMap = hmsProject.get(i);
                String proName = hashMap.get(DBController.projectName);
                String proDuration = hashMap.get(DBController.projectDuration);
                String proRole = hashMap.get(DBController.projectRole);
                String proTeamSize = hashMap.get(DBController.projectTeamSize);
                String proExpertise = hashMap.get(DBController.projectExpertise);

                pdfPCellPro = new PdfPCell(new Phrase(i + ""));
                tblProject.addCell(pdfPCellPro);
                pdfPCellPro = new PdfPCell(new Phrase(proName));
                tblProject.addCell(pdfPCellPro);
                pdfPCellPro = new PdfPCell(new Phrase(proDuration));
                tblProject.addCell(pdfPCellPro);
                pdfPCellPro = new PdfPCell(new Phrase(proRole));
                tblProject.addCell(pdfPCellPro);
                pdfPCellPro = new PdfPCell(new Phrase(proTeamSize));
                tblProject.addCell(pdfPCellPro);
                pdfPCellPro = new PdfPCell(new Phrase(proExpertise));
                tblProject.addCell(pdfPCellPro);

            }
            tblProject.setSpacingAfter(2f);
            if (hmsProject.size() != 0) {
                // Create and add a Paragraph
                p = new Paragraph("Project Details :", new Font(Font.FontFamily.HELVETICA, 22));
                p.setAlignment(Element.ALIGN_CENTER);
                p.setSpacingAfter(1f);
                doc.add(p);
                ///////////
                doc.add(tblProject);
            }


            //Ref Details
            PdfPTable tblRef = new PdfPTable(5);
            PdfPCell pdfPCellRef = new PdfPCell(new Phrase("NO."));
            tblRef.addCell(pdfPCellRef);
            pdfPCellRef = new PdfPCell(new Phrase("Ref Name"));
            tblRef.addCell(pdfPCellRef);
            pdfPCellRef = new PdfPCell(new Phrase("Details"));
            tblRef.addCell(pdfPCellRef);
            pdfPCellRef = new PdfPCell(new Phrase("Contact Number"));
            tblRef.addCell(pdfPCellRef);
            pdfPCellRef = new PdfPCell(new Phrase("Ref Email"));
            tblRef.addCell(pdfPCellRef);
            for (int i = 1; i < hmsRef.size(); i++) {
                HashMap<String, String> hashMap = hmsRef.get(i);
                String refName = hashMap.get(DBController.referName);
                String refDetails = hashMap.get(DBController.referDetails);
                String refContactNumber = hashMap.get(DBController.referContactNumber);
                String refEmail = hashMap.get(DBController.referEmail);

                pdfPCellRef = new PdfPCell(new Phrase(i + ""));
                tblRef.addCell(pdfPCellRef);
                pdfPCellRef = new PdfPCell(new Phrase(refName));
                tblRef.addCell(pdfPCellRef);
                pdfPCellRef = new PdfPCell(new Phrase(refDetails));
                tblRef.addCell(pdfPCellRef);
                pdfPCellRef = new PdfPCell(new Phrase(refContactNumber));
                tblRef.addCell(pdfPCellRef);
                pdfPCellRef = new PdfPCell(new Phrase(refEmail));
                tblRef.addCell(pdfPCellRef);

            }
            tblRef.setSpacingAfter(2f);
            if (hmsRef.size() != 0) {
                // Create and add a Paragraph
                p = new Paragraph("References Details :", new Font(Font.FontFamily.HELVETICA, 22));
                p.setAlignment(Element.ALIGN_CENTER);
                p.setSpacingAfter(1f);
                doc.add(p);
                ///////////////////////
                doc.add(tblRef);
            }


            //Other details
            String driviLic = hmsOther.get(0).get(DBController.otherDrivingLic);
            String passport = hmsOther.get(0).get(DBController.otherPassportNumber);

            if (driviLic != null || passport != null) {
                // Create and add a Paragraph
                p = new Paragraph("Other Details :", new Font(Font.FontFamily.HELVETICA, 22));
                p.setAlignment(Element.ALIGN_CENTER);
                p.setSpacingAfter(1f);
                doc.add(p);
                PdfPTable pdfPTable = new PdfPTable(2);
                PdfPCell pdfPCell1 = null;

                if (driviLic != null) {
                    pdfPCell1 = new PdfPCell(new Phrase("Driving Licence :"));
                    pdfPTable.addCell(pdfPCell1);
                    pdfPCell1 = new PdfPCell(new Phrase(driviLic));
                    pdfPTable.addCell(pdfPCell1);
                }
                if (passport != null) {
                    pdfPCell1 = new PdfPCell(new Phrase("Passport Number :"));
                    pdfPTable.addCell(pdfPCell1);
                    pdfPCell1 = new PdfPCell(new Phrase(passport));
                    pdfPTable.addCell(pdfPCell1);
                }
                doc.add(pdfPTable);//other table
            }


        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
        }

        return doc;

    }


    /**
     * Background task to generate pdf from users content
     *
     * @author androidsrc.net
     */
    private class PdfGenerationTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

//            PdfDocument document = new PdfDocument();
//
//            // repaint the user's text into the page
//            View content = findViewById(R.id.pdf_content);
//
//            // crate a page description
//            int pageNumber = 1;
//            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(content.getWidth(),
//                    content.getHeight() - 20, pageNumber).create();
//
//            // create a new page from the PageInfo
//            PdfDocument.Page page = document.startPage(pageInfo);
//            //content.draw(page.getCanvas());
//
//            // do final processing of the page
//            document.finishPage(page);
//
//            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");
//            String pdfName = "pdfdemo"
//                    + sdf.format(Calendar.getInstance().getTime()) + ".pdf";
//
//            File outputFile = new File("/sdcard/PDFDemo_AndroidSRC/", pdfName);
//
//            try {
//                outputFile.createNewFile();
//                OutputStream out = new FileOutputStream(outputFile);
//                document.writeTo(out);
//                document.close();
//                out.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            //return outputFile.getPath();
            return "";
        }

        @Override
        protected void onPostExecute(String filePath) {
            if (filePath != null) {
//                generatePdf.setEnabled(true);
//                pdfContentView.setText("");
                Toast.makeText(getApplicationContext(),
                        "Pdf saved at " + filePath, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Error in Pdf creation" + filePath, Toast.LENGTH_SHORT)
                        .show();
            }

        }

    }

    private void createToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private void creatPdfFile() {

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "AhmedMohammed.pdf");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        byte[] data1 = {1, 1, 0, 0};
//        //write the bytes in file
//        if (file.exists()) {
//            OutputStream fo = new FileOutputStream(file);
//            fo.write(data1);
//            fo.close();
//            System.out.println("file created: " + file);
//            url = upload.upload(file);
//        }
        //deleting the file
//        file.delete();
//        System.out.println("file deleted");


        // Create a document and set it's properties


    }

    private void getPassedNameFromUser() {

        //Set The text that user has set it at the beginning  to textview from first activity
        //get passed data
        Intent intent = getIntent();
        textView = (TextView) findViewById(R.id.tvProfileName);
        if (sharedPreferences.contains(MainActivity.PROFILE_NAME)) {
            textView.setText(sharedPreferences.getString(MainActivity.PROFILE_NAME, ""));
        }
        //textView.setText(intent.getStringExtra("profile_name"));
        ///////////////////////////////////


    }


    private void getExistingDegrees() {

        //Getting all degree items from the database
        String sql = "select * from " + DBController.tblEdu + " where " + DBController.profileName + " =  '" + this.profileName + "' ";
        try {
            ArrayList<String> items = new ArrayList<String>();
            ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMaps = dbController.getEduData(sql);
            for (int i = 0; i < hashMaps.size(); i++) {
                hashMap = hashMaps.get(i);
                String degreeItem = hashMap.get(DBController.eduDegreeAndCerti);
                if (degreeItem != null) {
                    items.add(degreeItem);
                }
            }
            //Starting the proper activity
            if (!items.isEmpty()) {
                finish();
                Intent intent = new Intent(getApplicationContext(), ExistingEducational.class);
                startActivity(intent);
            } else {
                finish();
                Intent intent = new Intent(getApplicationContext(), EducationalActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            //createAlert(e.getMessage().toString());
            createToast(e.getMessage().toString());
        }

    }


    private void getExistingProjects() {

        //Getting all degree items from the database
        String sql = "select * from " + DBController.tblProject + " where " + DBController.profileName + " =  '" + this.profileName + "' ";
        try {
            ArrayList<String> items = new ArrayList<String>();
            ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMaps = dbController.getProjectData(sql);
            //createAlert(hashMaps.toString());
            for (int i = 0; i < hashMaps.size(); i++) {
                hashMap = hashMaps.get(i);
                String projectItem = hashMap.get(DBController.projectName);//title
                if (projectItem != null) {
                    items.add(projectItem);
                }
            }

            //Starting the proper activity
            if (!items.isEmpty()) {
                finish();
                Intent intent = new Intent(getApplicationContext(), ExistingProjects.class);
                startActivity(intent);
            } else {
                finish();
                Intent intent = new Intent(getApplicationContext(), ProjectDetailsActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            //createAlert(e.getMessage().toString());
            createToast(e.getMessage().toString());
        }

    }

    private void getExistingExperience() {

        //Getting all degree items from the database
        String sql = "select * from " + DBController.tblExperience + " where " + DBController.profileName + " =  '" + this.profileName + "' ";
        try {
            ArrayList<String> items = new ArrayList<String>();
            ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMaps = dbController.getExpData(sql);
            for (int i = 0; i < hashMaps.size(); i++) {
                hashMap = hashMaps.get(i);
                String organizationName = hashMap.get(DBController.expOrganizationName);//Organization Name
                if (organizationName != null) {
                    items.add(organizationName);
                }
            }
            //Starting the proper activity
            if (!items.isEmpty()) {
                finish();
                Intent intent = new Intent(getApplicationContext(), ExistingExperience.class);
                startActivity(intent);
            } else {
                finish();
                Intent intent = new Intent(getApplicationContext(), ExperienceActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            //createAlert(e.getMessage().toString());
            createToast(e.getMessage().toString());
        }

    }


    private void getExistingRef() {

        //Getting all degree items from the database
        String sql = "select * from " + DBController.tblRef + " where " + DBController.profileName + " =  '" + this.profileName + "' ";
        try {
            ArrayList<String> items = new ArrayList<String>();
            ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMaps = dbController.getRefData(sql);
            //createAlert(hashMaps.toString());
            for (int i = 0; i < hashMaps.size(); i++) {
                hashMap = hashMaps.get(i);
                String refName = hashMap.get(DBController.referName);//ref Name
                if (refName != null) {
                    items.add(refName);
                }
            }
            //Starting the proper activity
            if (!items.isEmpty()) {
                finish();
                Intent intent = new Intent(getApplicationContext(), ExistingReferences.class);
                startActivity(intent);
            } else {
                finish();
                Intent intent = new Intent(getApplicationContext(), ReferenceDetailsActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            //createAlert(e.getMessage().toString());
            createToast(e.getMessage().toString());
        }

    }

    private void createAlert(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(s);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
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


    public void personalDetails(View view) {
        finish();
        Intent intent = new Intent(getApplicationContext(), PersonalDataActivity.class);
        //intent.putExtra("profile_name", textView.getText().toString());
        startActivity(intent);
        //createToast("personal");

    }

    public void educationDetails(View view) {
        getExistingDegrees();
    }


    public void projectDetails(View view) {
        getExistingProjects();
    }

    public void experienceDetails(View view) {
        getExistingExperience();
    }

    public void uploadPhoto(View view) {
        finish();
        Intent intent = new Intent(getApplicationContext(), UploadPhotoActivity.class);
        startActivity(intent);
    }

    public void referenceDetails(View view) {
        getExistingRef();

    }

    public void generateResume(View view) {

        try {
            //execute asyncTask
            // new GeneratePdf(context).execute();
            //check if the all fields of personal details are completed or not
            String sql = "select * from " + DBController.tblPersonal + " where " + DBController.profileName + " = '" + profileName + "'  ";
            hashMaps = dbController.getPersonalData(sql);
            //createAlert(hashMap.toString());
            if (hashMaps.isEmpty()) {
                // do nothing
            } else {
                //check if all fields is completed
                String fullName = hashMaps.get(0).get(DBController.fullName);
                String gender = hashMaps.get(0).get(DBController.gender);
                String dateOfBirth = hashMaps.get(0).get(DBController.dateOfBirth);
                String address = hashMaps.get(0).get(DBController.address);
                String langauges = hashMaps.get(0).get(DBController.langauges);
                String phone = hashMaps.get(0).get(DBController.phone);
                String email = hashMaps.get(0).get(DBController.email);
                //createAlert(fullName+" "+gender+" "+dateOfBirth+" "+address+" "+langauges+" "+phone+" "+email);
                if (fullName == null || gender == null || dateOfBirth == null || address == null || langauges == null || phone == null
                        || email == null) {
                    createAlert("Can't Generate without Completing personal Details");

                } else {
                    new GeneratePdf(context,
                            fullName,
                            dbController.getPersonalData("select * from " + DBController.tblPersonal + " where " + DBController.profileName + " = '" + profileName + "' "),
                            dbController.getEduData("select * from " + DBController.tblEdu + " where " + DBController.profileName + " = '" + profileName + "' "),
                            dbController.getExpData("select * from " + DBController.tblExperience + " where " + DBController.profileName + " = '" + profileName + "' "),
                            dbController.getProjectData("select * from " + DBController.tblProject + " where " + DBController.profileName + " = '" + profileName + "' "),
                            dbController.getRefData("select * from " + DBController.tblRef + " where " + DBController.profileName + " = '" + profileName + "' "),
                            dbController.getOtherData("select * from " + DBController.tblOther + " where " + DBController.profileName + " = '" + profileName + "' ")
                    ).execute();
                }

            }
        } catch (Exception e) {
            createAlert(e.getMessage().toString());
        }

//        finish();
//        Intent intent = new Intent(getApplicationContext(), GenerateResumeActivity.class);
//        startActivity(intent);


    }


    public void otherDetails(View view) {
        finish();
        Intent intent = new Intent(getApplicationContext(), OtherDetailsActivity.class);
        startActivity(intent);
    }
}
