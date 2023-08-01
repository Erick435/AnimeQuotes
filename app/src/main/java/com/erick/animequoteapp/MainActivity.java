package com.erick.animequoteapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;
import android.content.Intent;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;



public class MainActivity extends AppCompatActivity {

    private TextView titleTextView, resultTextView;
    private Spinner quoteSpinner;
    private EditText inputEditText;
    private Button startButton;
    private SharedPreferences sharedPreferences;
    // At the top of your MainActivity, define a variable for your RecyclerView and Adapter:
    private RecyclerView quoteRecyclerView;
    private QuoteAdapter quoteAdapter;
    private List<Quote> quoteList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing variables
        titleTextView = findViewById(R.id.textView);
        resultTextView = findViewById(R.id.resultTextView);
        quoteSpinner = findViewById(R.id.spinner);
        inputEditText = findViewById(R.id.editText);
        startButton = findViewById(R.id.button);
        quoteList = new ArrayList<>();
        sharedPreferences = getSharedPreferences("AnimeQuoteApp", Context.MODE_PRIVATE);




        //Deals with the spinner when using the drop-down (Listener)
        quoteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                //if Random quote is selected, hide the text box
                if (selectedItem.equals("Random Quote")) {
                    inputEditText.setVisibility(View.GONE);
                } else {
                    //if Random Quote is NOT selected, show input text box
                    inputEditText.setVisibility(View.VISIBLE);
                    if (selectedItem.equals("Quote By Anime Title")) {
                        inputEditText.setHint("One Piece. . .");
                    } else if (selectedItem.equals("Quote By Anime Character")) {
                        inputEditText.setHint("Saitama. . .");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button historyButton = findViewById(R.id.historyButton);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        //Create the OnClickListener so when we are calling the API to get our data
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedItem = quoteSpinner.getSelectedItem().toString();
                String input = inputEditText.getText().toString();
                final String[] apiUrl = {""};




                if (selectedItem.equals("Random Quote")) {
                    apiUrl[0] = "https://animechan.xyz/api/random";
                } else if (selectedItem.equals("Quote By Anime Title")) {
                    apiUrl[0] = "https://animechan.xyz/api/random/anime?title=" + input;
                } else if (selectedItem.equals("Quote By Anime Character")) {
                    apiUrl[0] = "https://animechan.xyz/api/random/character?name=" + input;
                }



                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        makeApiCall(apiUrl[0]);
                    }
                }).start();

            }
        });

        quoteRecyclerView = findViewById(R.id.quoteRecyclerView);
        quoteRecyclerView.setLayoutManager(new LinearLayoutManager(this));  // Add this line


        List<Quote> quoteList = new ArrayList<>();
        // Initialize the adapter with the list and set the adapter to the RecyclerView
        quoteAdapter = new QuoteAdapter(quoteList);
        quoteRecyclerView.setAdapter(quoteAdapter);
        quoteRecyclerView.setHasFixedSize(true);

    }

    //make the API call here
    private void makeApiCall(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            BufferedReader reader;

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else { // error
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                parseResponse(response.toString());
            } else { // error
                // Handle the error. You might want to log it or show it to the user.
                System.out.println("Error response from server: " + response.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //parse our responses
    private void parseResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String anime = jsonObject.getString("anime");
            String character = jsonObject.getString("character");
            String quote = jsonObject.getString("quote");

            // In your parseResponse method, after you get the anime, character, and quote:
            Quote newQuote = new Quote(anime, character, quote);

            //Construct a string to represent the new quote
            String result = "Anime: " + anime + "\nCharacter: " + character + "\nQuote: " + quote;


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resultTextView.setText(result);
                    quoteList.add(newQuote);
                    quoteAdapter.notifyDataSetChanged();
                }
            });


            Set<String> history = sharedPreferences.getStringSet("quoteHistory", new HashSet<>());
            history.add(result);
            sharedPreferences.edit().putStringSet("quoteHistory", history).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





}