package com.example.cinemap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LogIn extends AppCompatActivity implements View.OnClickListener
{
    EditText Email_Input;
    EditText Password_Input;
    Button LogIn_Button;
    Button Register_Button;
    ImageView Logo;
    ProgressDialog mProgressDialog;

    FirebaseAuth Auth;
    FirebaseAuth.AuthStateListener StateListener;
    SharedPreferences FirstPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        Email_Input=findViewById(R.id.LogIn_Email);
        Password_Input=findViewById(R.id.LogIn_Password);
        LogIn_Button=findViewById(R.id.LogIn_Start);
        Register_Button=findViewById(R.id.Register_Open);
        Logo=findViewById(R.id.LogIn_Logo);
        Logo.setImageResource(R.drawable.logo);
        LogIn_Button.setOnClickListener(this);
        Register_Button.setOnClickListener(this);
        mProgressDialog = new ProgressDialog(LogIn.this);
        mProgressDialog.setMessage("Working");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        FirstPreferences=getSharedPreferences("FirstPreferences",MODE_PRIVATE);
        boolean FirstTime=FirstPreferences.getBoolean("FirstTime",true);
        //uses shared preferences to check if its the first time the app was launched and if it is preforms first time setup
        if(FirstTime)
        {
            FirstTimeSetup();
        }

        //checks if its the users first login(his details aren't in hte users firebase collection ) and if it is add his details to the firebase database


        Auth=FirebaseAuth.getInstance();


        StateListener= firebaseAuth -> //checks if user stayed logged in and if he did skips login screen and starts the main screen activity
        {
          if(firebaseAuth.getCurrentUser()!=null)
          {
              startActivity(new Intent(LogIn.this,MainScreen.class));
          }
        };
    }


    private void FirstTimeSetup()
    {

        // starts the database inserting service and shows its progress on a progress bar
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("First Time Setup");
        builder.setCancelable(false);
        builder.setMessage("Preforming First Time Setup Please Make Sure You Have a Stable Internet Connection ");
        builder.setPositiveButton("Start",(dialogInterface, i) ->
        {
            mProgressDialog.show();
            LogIn_Button.setClickable(false);
            Register_Button.setClickable(false);
            Intent intent=new Intent(this,Setup_Service.class);
            intent.putExtra("receiver",new RequestReceiver(new Handler()));
            startService(intent);
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
        SharedPreferences preferences=getSharedPreferences("FirstPreferences",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("FirstTime",false);
        editor.apply();
    }

    @Override
    protected void onStart()
    {
        Auth.addAuthStateListener(StateListener);
        super.onStart();
    }

    //universal click handler
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.LogIn_Start: //if login button is pressed start log in method
                Login();
                break;
            case R.id.Register_Open: //if register button is pressed open register activity
                startActivity(new Intent(LogIn.this,Register.class));
                break;
        }
    }

    //method that handles firebase login
    private void Login()
    {
        String Email=Email_Input.getText().toString();
        String Password=Password_Input.getText().toString();
        if(Email.isEmpty()||Password.isEmpty()) //checks if the required fields are filled in
        {
            DisableAfterClick();//disables the button for 5 seconds to prevent spam
            Toast.makeText(this, "Please fil the required fields", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(task -> //gets the email and password form the insert fields and starts log in
            {
                if(task.isSuccessful()) //if login is successful starts the main screen activity
                {
                    DisableAfterClick(); //disables the button in case of login delay
                    Toast.makeText(this, "Successfully Logged in with"+Email, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LogIn.this,MainScreen.class));
                }
                else //if login failed displays error message
                {
                    Toast.makeText(this, "Incorrect/Invalid email or password", Toast.LENGTH_SHORT).show();
                    DisableAfterClick();//disables the button for 5 seconds to prevent spam
                }
            });
        }
    }

    public void DisableAfterClick() //starts the disable button AsyncTask
    {
        new DisableTask().execute();
    }

    //gets the service progress
    private class RequestReceiver extends ResultReceiver
    {

        public RequestReceiver(Handler handler)
        {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData)
        {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode==Setup_Service.UPDATE_PROGRESS)
            {
                int progress = resultData.getInt("progress");
                mProgressDialog.setProgress(progress);
                if(progress==100)
                {
                    LogIn_Button.setClickable(true);
                    Register_Button.setClickable(true);
                    mProgressDialog.dismiss();
                    Toast.makeText(LogIn.this,"Setup Complete",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private class DisableTask extends AsyncTask<Void,Integer,Void>
    {
        //disables login button and waits for 5 seconds before re-enabling
        @Override
        protected void onPreExecute()
        {
            LogIn_Button.setClickable(false);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            LogIn_Button.setClickable(true);
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
