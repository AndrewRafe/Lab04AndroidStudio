/*
 *  Copyright (C) 2016 Andrew Rafe
 *  Lab 04 Mobile Applications Development
 *  Author Andrew Rafe 98134152
 */
package exercise4.com.mad.exercise4;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * An activity that reads jokes from a website and displays them to the screen
 */
public class MainActivity extends AppCompatActivity {

    public static final String INPUT_ERROR_TAG = "IO ERROR";
    public static final String URL_STRING = "http://www-staff.it.uts.edu.au/~rheise/sarcastic.cgi";
    public static final String URL_SHORT_FILTER_OPTION = "?len=Short";
    public static final String URL_MEDIUM_FILTER_OPTION = "?len=Medium";
    public static final String URL_LONG_FILTER_OPTION = "?len=Long";
    public static final int NUM_JOKES = 3;

    private TextView jokeText;
    private Button oneJokeButton;
    private Button threeJokesButton;
    private Spinner jokeLengthSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpJokeLengthSpinner();
        jokeText = (TextView) findViewById(R.id.main_joke_tv);
        oneJokeButton = (Button) findViewById(R.id.main_one_joke_btn);
        threeJokesButton = (Button) findViewById(R.id.main_three_jokes_btn);

        oneJokeButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Download one joke and display it
             * @param v
             */
            @Override
            public void onClick(View v) {
                new Download1JokeAsyncTask(jokeLengthSpinner.getSelectedItem().toString()).execute();
            }
        });

        threeJokesButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Download multiple jokes and display them
             * @param v
             */
            @Override
            public void onClick(View v) {
                new DownloadNJokesAsyncTask(NUM_JOKES, jokeLengthSpinner.getSelectedItem().toString()).execute();
            }
        });

    }

    /**
     * Helper method to populate and initiate the joke length spinner
     */
    public void setUpJokeLengthSpinner() {
        jokeLengthSpinner = (Spinner) findViewById(R.id.main_joke_length_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.joke_length, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        jokeLengthSpinner.setAdapter(adapter);
        jokeLengthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            /**
             * Will display a toast notification to alert the user to the length of joke
             * selected from the spinner.
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, (jokeLengthSpinner.getSelectedItem().toString()
                        + " " + getString(R.string.joke_length_toast_alert)), Toast.LENGTH_SHORT).show();
            }

            /**
             * When nothing is selected in the spinner no toast notification needs to be generated
             * as no options have been change
             * @param parent
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //INTENTIONALLY BLANK
            }

        });
    }

    /**
     * A method to retrieve one joke from the internet given a specific joke length
     */
    public String readOneJoke(String jokeLength) throws IOException {
        String filterOption;
        if (jokeLength.equals(getString(R.string.short_joke_length_option))) {
            filterOption = URL_SHORT_FILTER_OPTION;
        } else if (jokeLength.equals(getString(R.string.medium_joke_length_option))) {
            filterOption = URL_MEDIUM_FILTER_OPTION;
        } else {
            filterOption = URL_LONG_FILTER_OPTION;
        }

        URL url = new URL(URL_STRING + filterOption);
        URLConnection conn = url.openConnection();
        //Obtain the Input Stream
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        //The joke is a one liner so just read one line
        String joke = in.readLine();
        in.close();
        return joke;
    }

    /**
     * A class to download and display a joke onto the UI
     * This class runs on a background thread by extending AsyncTask
     */
    private class Download1JokeAsyncTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog loadOneJokeProgress;
        private String lengthFilterOption;

        protected Download1JokeAsyncTask(String lengthFilterOption) {
            this.lengthFilterOption = lengthFilterOption;
        }

        /**
         * Initiates and shows the progress dialog for loading one joke
         */
        @Override
        protected void onPreExecute() {
            loadOneJokeProgress = ProgressDialog.show(MainActivity.this, "", getString(R.string.load_one_joke_message));
        }

        /**
         * Loads a joke and returns it as the result
         * @param params
         * @return jokeString
         */
        @Override
        protected String doInBackground(Void... params) {
            String result;
            try {
                result = readOneJoke(lengthFilterOption);
                return result;
            } catch(IOException inException) {
                Log.d(INPUT_ERROR_TAG, "IOException thrown while trying" +
                        " to read one joke");
                return getString(R.string.load_error);
            }

        }

        /**
         * Change the joke text field to contain the downloaded joke and close the
         * progress dialog
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            jokeText.setText(result);
            loadOneJokeProgress.dismiss();
        }

    }

    /**
     * A class that downloads and displays a given number of jokes and a joke length
     * The class extends AsyncTask and runs on a background thread
     */
    private class DownloadNJokesAsyncTask extends AsyncTask<Void, Integer, String[]> {

        private ProgressDialog loadNJokesProgress;
        private int numJokes;
        private String lengthFilterOption;

        /**
         * Constructor class that stores the number of jokes to be retrieved and the filter
         * option for the joke length
         * @param numJokes
         * @param lengthFilterOption
         */
        protected DownloadNJokesAsyncTask(int numJokes, String lengthFilterOption) {
            this.numJokes = numJokes;
            this.lengthFilterOption = lengthFilterOption;
        }

        /**
         * Initialise the progress dialog and change the settings of the progress dialog
         */
        @Override
        protected void onPreExecute() {
            loadNJokesProgress = new ProgressDialog(MainActivity.this);
            loadNJokesProgress.setMax(numJokes);
            loadNJokesProgress.setIndeterminate(false);
            loadNJokesProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            loadNJokesProgress.setMessage(getString(R.string.load_multiple_jokes_message));
            loadNJokesProgress.show();
        }

        /**
         * Load a given number of jokes and return them as an array of strings
         * @param params
         * @return arrayOfStringJokes
         */
        @Override
        protected String[] doInBackground(Void... params) {
            String[] jokes = new String[numJokes];
            for (int i = 0; i < numJokes; i++) {
                publishProgress(i);
                try {
                    jokes[i] = readOneJoke(lengthFilterOption);
                } catch (IOException readException) {
                    Log.d(INPUT_ERROR_TAG, "IOException thrown while trying" +
                            " to read one joke");
                    String[] errorStringArray = new String[1];
                    errorStringArray[0] = getString(R.string.load_error_plural);
                    return errorStringArray;
                }
            }
            return jokes;
        }

        /**
         * Will update the progress bar and change the message to display which joke is being loaded
         * @param update
         */
        @Override
        protected void onProgressUpdate(Integer... update) {
            loadNJokesProgress.setMessage(getString(R.string.load_multiple_jokes_message) + " " + (update[0] + 1) + "...");
            loadNJokesProgress.setProgress(update[0]);
        }

        /**
         * Display all jokes loaded to the screen and dismiss the progress dialog
         * @param results
         */
        @Override
        protected void onPostExecute(String[] results) {
            String allJokesString = "";
            for (String joke:results) {
                allJokesString += joke + "\n\n";
            }
            jokeText.setText(allJokesString);
            loadNJokesProgress.dismiss();
        }
    }
}
