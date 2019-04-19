package com.unsettled.playfair;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    EditText mainTxt, keyTxt;
    RadioGroup rbGrp;
    RadioButton encRB, decRB;
    TextView resultTxt,bannerTxt;

    int r1, r2, c1, c2, size=10; //key matrix row column size
    String userTxt = "";
    char keyMatrix[][] = new char[size][size];
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainTxt = findViewById(R.id.mainTxt);
        keyTxt = findViewById(R.id.keyTxt);
        resultTxt = findViewById(R.id.resultTxt);
        bannerTxt = findViewById(R.id.bannerTxt);
        rbGrp = findViewById(R.id.rbGrp);
        encRB = findViewById(R.id.encRB);
        decRB = findViewById(R.id.decRB);
        encRB.setChecked(true);

        //call trigger function when onTextChanged of mainTxt fire
        mainTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                trigger();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //call trigger function when onTextChanged of keyTxt fire
        keyTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                trigger();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //call trigger function when onTextChanged of rbGrp fire
        rbGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                trigger();
            }
        });
    }


    //trigger function calls generateKey and codec function
    void trigger() {
        String str = keyTxt.getText().toString();

        //if key string is not null
        if(!str.matches("")) {
            userTxt = polishUserTxt(mainTxt.getText().toString()); //fetch user text using polishUserTxt function
            generateKey(); //call generateKey to generate key string

            //if encrypt radio button is checked
            if (encRB.isChecked()) {
                codec(1); //call codec to encrypt plain text
            }
            else if (decRB.isChecked()) { //if decrypt radio button is checked
                codec(9); //call codec to decrypt cipher text
            }
        } else {
            resultTxt.setText("");
            bannerTxt.setText("");
        }
    }


    //add "x" between two same characters and in the last if text length is in odd
    String polishUserTxt(String userTxt) {
        sb = new StringBuilder(userTxt);
        for (int k = 0; k < sb.length(); k = k + 2) {
            if (k + 1 == sb.length()) {
                sb.append('x');
            } else if (sb.charAt(k) == sb.charAt(k + 1)) {
                sb.insert(k+1,'x');
            }
        }
        return sb.toString();
    }

    //generate key string
    void generateKey() {

        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        sb = new StringBuilder(); //new StringBuilder sb
        sb.append(keyTxt.getText().toString()); // add user key string to sb
        sb.append(alpha); //add alphabets in uppercase to sb
        sb.append(" ~`!@#$₹€¥£¢%^&*()_-+=[]{}\\|:;\"'<>,.?/" ); //add special symbols to sb
        sb.append("0123456789"); //add digits to sb
        sb.append(alpha.toLowerCase()); //add alphabets in lowercase to sb

        String keyString = removeDuplicates(sb.toString()); //remove duplicate characters then store sb to string

        //store keyString into keyMatrix of 10x10
        int k = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                keyMatrix[i][j] = keyString.charAt(k);
                k++;
            }
        }

        //print keyMatrix into logcat for cross check
        System.out.println("deep arr: " + Arrays.deepToString(keyMatrix));
    }

    //remove duplicates character from keyString
    String removeDuplicates(String keyString) {
        sb = new StringBuilder();
        for (int i = 0; i < keyString.length(); i++) {
            if (!sb.toString().contains(String.valueOf(keyString.charAt(i)))) {
                sb.append(String.valueOf(keyString.charAt(i)));
            }
        }
        return sb.toString();
    }

    //encryption and decryption function
    void codec(int index) {

        sb = new StringBuilder();

        //if decryption
        if (decRB.isChecked()) {
            //add "x" in the end if text length is in odd
            if (userTxt.length() % 2 == 1) {
                userTxt = userTxt + 'x';
            }
            bannerTxt.setText(getString(R.string.banner_decryption));
        } else { //if encryption
            bannerTxt.setText(getString(R.string.banner_encryption));
        }

        //loop till mainTxt length
        for (int k = 0; k < userTxt.length(); k = k + 2) {
            //fetch array position of two characters
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (keyMatrix[i][j] == userTxt.charAt(k)) {
                        r1 = i;
                        c1 = j;
                    } if (keyMatrix[i][j] == userTxt.charAt(k + 1)) {
                        r2 = i;
                        c2 = j;
                    }
                }
            }

            //if row are same for both characters then  pick character to right of each character, wrap to left if needed
            if (r1 == r2) {
                sb.append(keyMatrix[r1][(c1+index)%size]);
                sb.append(keyMatrix[r2][(c2+index)%size]);
            } else if (c1 == c2) { //if columns are same for both characters then  pick character to below of each character, wrap to top if needed
                sb.append(keyMatrix[(r1+index)%size][c1]);
                sb.append(keyMatrix[(r2+index)%size][c2]);
            } else { //else pick same row opposite column
                sb.append(keyMatrix[r1][c2]);
                sb.append(keyMatrix[r2][c1]);
            }
        }

        //display output to resultTxt
        resultTxt.setText(sb.toString());
    }

}