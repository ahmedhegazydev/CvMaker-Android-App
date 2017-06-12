package com.ahmed.pdf_creator;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ahmed on 26/05/2017.
 */

public class CreatePdf extends AsyncTask {

    Context context = null;
    ArrayList<HashMap<String, String>>
            hmsPersonalDetails = null,
            hmsEducationalDetails = null,
            hmsExperienceDetails = null,
            hmsProjectDetails = null,
            hmsReferenceDetails = null,
            hmsOtherDetails = null;


    //Constructor
    public CreatePdf(Context context,
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


    }

    @Override
    protected void onCancelled(Object o) {
        super.onCancelled(o);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
