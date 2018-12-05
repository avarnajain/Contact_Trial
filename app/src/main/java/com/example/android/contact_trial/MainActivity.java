package com.example.android.contact_trial;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.os.Bundle;
import android.os.Build;
import android.widget.Button;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;
import android.content.Intent;
import android.widget.ListView;
import android.util.Log;
import android.Manifest;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;
import android.net.Uri;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    ListView listView;
    ArrayList<String> contactsArray;
    ArrayAdapter<String> arrayAdapter;
    Button contactsButton;
    Cursor cursor;
    String name, contactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                Log.e("permission", "Permission already granted.");
            } else {
                requestPermission();
            }
        }

        listView = (ListView)findViewById(R.id.listview);
        contactsArray = new ArrayList<String>();
        contactsButton = (Button) findViewById(R.id.contacts);
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddContactstoArray();

//Initialize ArrayAdapter and declare that we’re converting Strings into Views//

                arrayAdapter = new ArrayAdapter<String>(
                        MainActivity.this,

//Specify the XML file where you’ve defined the layout for each item//

                        R.layout.contact_listview, R.id.textView,

//The array of data//

                        contactsArray
                );

//Set the Adapter to the ListView, using setAdapter()//

                listView.setAdapter(arrayAdapter);

            }

        });

    }

    public void AddContactstoArray(){

//Query the phone number table using the URI stored in CONTENT_URI//

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

        while (cursor.moveToNext()) {

//Get the display name for each contact//

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

//Get the phone number for each contact//

            contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

//Add each display name and phone number to the Array//

            contactsArray.add(name + " " + ":" + " " + contactNumber);
        }

        cursor.close();

    }

    public boolean checkPermission() {

        int ContactPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);

        return ContactPermissionResult == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        Manifest.permission.READ_CONTACTS,
                }, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE:
                contactsButton = (Button)findViewById(R.id.contacts);

                if (grantResults.length > 0) {

                    boolean ReadContactsPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (ReadContactsPermission) {

                        Toast.makeText(MainActivity.this,
                                "Permission accepted", Toast.LENGTH_LONG).show();

//If permission is denied...//

                    } else {
                        Toast.makeText(MainActivity.this,
                                "Permission denied", Toast.LENGTH_LONG).show();

//....disable the Call and Contacts buttons//

                        contactsButton.setEnabled(false);

                    }
                    break;
                }
        }
    }
}