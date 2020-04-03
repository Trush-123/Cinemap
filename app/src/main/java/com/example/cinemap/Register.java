package com.example.cinemap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class Register extends AppCompatActivity implements View.OnClickListener
{

    Button Register_Button;
    Button Back_Button;
    EditText Email_Input;
    EditText Password_Input;
    FirebaseAuth Auth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Register_Button=findViewById(R.id.Register_Start);
        Back_Button=findViewById(R.id.Register_Back);
        Email_Input=findViewById(R.id.Register_Email);
        Password_Input=findViewById(R.id.Register_Password);
        Register_Button.setOnClickListener(this);
        Back_Button.setOnClickListener(this);

        Auth = FirebaseAuth.getInstance();


    }

    //universal click handler
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.Register_Start: //if register button is pressed stats the register method
                Register_User();
                break;
            case R.id.Register_Back: //if back button is pressed finishes the activity and returns to the log in activity
                finish();
                break;
        }
    }

    private void Register_User()
    {
        String Email = Email_Input.getText().toString().trim();
        String Password = Password_Input.getText().toString();
        if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)) ////checks if the required fields are filled in
        {
            DisableAfterClick();//disables the button for 5 seconds to prevent spam
            Toast.makeText(Register.this, "Fill in All Of The Fields", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(task -> //gets the email and password form the insert fields and starts register
            {
                if (task.isSuccessful()) //if the register is successful starts the main screen activity
                {
                    Register_Button.setClickable(false);
                    Toast.makeText(Register.this, "Successfully Registered  " + Email , Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, MainScreen.class));
                }
                if (!task.isSuccessful()) // if the register fails checks reason
                {
                    if(Password.length()<6) //register failed because password is shorter than 6 characters(firebase auth requirement)
                    {
                        DisableAfterClick();
                        Toast.makeText(Register.this, "Password Must be at least 6 Characters", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        DisableAfterClick();
                        // Regular Expression For Email Validation
                        //https://regexr.com/2rhq7 For Documentation
                        String RegEx="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
                        if(Email.matches(RegEx)!=true) //register failed because the email is invalid
                        {
                            Toast.makeText(Register.this, "Invalid Email Address ", Toast.LENGTH_SHORT).show();
                        }
                        else //register failed because user already exists
                        {
                            Toast.makeText(Register.this, " User Already Registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private void DisableAfterClick() //starts the disable button AsyncTask
    {
        new Register.DisableTask().execute();
    }

    private class DisableTask extends AsyncTask<Void,Integer,Void>
    {
        //disables login button and waits for 5 seconds before re-enabling
        @Override
        protected void onPreExecute()
        {
            Register_Button.setClickable(false);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Register_Button.setClickable(true);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids)
        {

            for(int i=5;i>0;i--)
            {
                try
                {
                    Thread.sleep(1000);
                    publishProgress(i);
                }
                catch (InterruptedException e)
                {}
            }
            return null;
        }
    }
}
