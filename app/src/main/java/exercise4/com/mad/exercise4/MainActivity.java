package exercise4.com.mad.exercise4;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    private TextView jokeText;
    private Button oneJokeButton;
    private Button threeJokesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                new Download1JokeAsyncTask().execute();
            }
        });

        threeJokesButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Download multiple jokes and display them
             * @param v
             */
            @Override
            public void onClick(View v) {
                new DownloadNJokesAsyncTask(3).execute();
            }
        });


    }

    /**
     * A method to retrieve one joke from the internet
     */
    public String readOneJoke() throws IOException {
        URL url = new URL("http://www-staff.it.uts.edu.au/~rheise/sarcastic.cgi");
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
                result = readOneJoke();
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
     * A class that downloads and displays a given number of jokes
     * The class extends AsyncTask and runs on a background thread
     */
    private class DownloadNJokesAsyncTask extends AsyncTask<Void, Integer, String[]> {

        private ProgressDialog loadNJokesProgress;
        private int numJokes;

        protected DownloadNJokesAsyncTask(int numJokes) {
            this.numJokes = numJokes;
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
                    jokes[i] = readOneJoke();
                } catch (IOException readException) {
                    Log.d(INPUT_ERROR_TAG, "IOException thrown while trying" +
                            " to read one joke");
                    String[] errorStringArray = new String[1];
                    errorStringArray[0] = getString(R.string.load_error);
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
