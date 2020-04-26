package com.example.demoaccident;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    ArrayList<String>  contacts = new ArrayList<>() ;
    ArrayAdapter<String> adapter;
    EditText newcon;
    Button add, del;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        listView = (ListView)findViewById(R.id.ls);
        newcon = (EditText)findViewById(R.id.editText);
        add = (Button) findViewById(R.id.newadd);
        del = (Button)findViewById(R.id.delete);
        try {
            FileInputStream inputStream = openFileInput("contacts");
            ObjectInputStream in = new ObjectInputStream(inputStream);
            contacts = (ArrayList<String>) in.readObject();
            in.close();
            inputStream.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        adapter= new ArrayAdapter<>(Main2Activity.this, android.R.layout.simple_list_item_multiple_choice,contacts);


        View.OnClickListener addlistner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(newcon.getText().toString())){
                contacts.add(newcon.getText().toString().trim());
                newcon.setText("");
                adapter.notifyDataSetChanged();

                dataupdate();}


            }


        };

        add.setOnClickListener(addlistner);
        listView.setAdapter(adapter);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int j =1;
                SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
                if (sparseBooleanArray.size()!=0){

                    for (int i = listView.getCount()-1;i >=0; i--){
                        if(sparseBooleanArray.get(i)){

                            adapter.remove(contacts.get(i));

                        }


                    }
                    Toast.makeText(Main2Activity.this,"contact deleted successfuly", Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(Main2Activity.this,"No contact selected",Toast.LENGTH_SHORT).show();
                }
                sparseBooleanArray.clear();
                adapter.notifyDataSetChanged();
                dataupdate();

            }
        });



    }


        private void dataupdate() {
            try {
                FileOutputStream fileOutputStream = openFileOutput("contacts", Context.MODE_PRIVATE);
                ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
                out.writeObject(contacts);
                out.close();
                fileOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

}
