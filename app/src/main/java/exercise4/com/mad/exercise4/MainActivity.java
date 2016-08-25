package exercise4.com.mad.exercise4;

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

    public TextView jokeText;
    private Button oneJokeButton;
    private Button threeJokesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jokeText = (TextView) findViewById(R.id.main_joke_tv);
        oneJokeButton = (Button) findViewById(R.id.main_one_joke_btn);
        threeJokesButton = (Button) findViewById(R.id.main_three_jokes_btn);


        assert oneJokeButton != null;
        oneJokeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                class Download1JokeAsyncTask extends AsyncTask<Void, Void, String> {

                    private TextView jokeText;

                    @Override
                    protected void onPreExecute() {
                        jokeText = (TextView) findViewById(R.id.main_joke_tv);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            String result = readOneJoke();
                            return result;
                        } catch(IOException inException) {
                            Log.d(INPUT_ERROR_TAG, "IOException thrown while trying" +
                                    " to read one joke");
                        }
                        return null;

                    }

                    @Override
                    protected void onPostExecute(String result) {
                        jokeText.setText(result);
                    }

                }

                new Download1JokeAsyncTask().execute();

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
}
