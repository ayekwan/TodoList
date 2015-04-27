package com.example.student.todolist;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class todoLists extends ActionBarActivity
        implements AdapterView.OnItemClickListener,
                    AdapterView.OnItemLongClickListener{

    //TodoDBHelper helper;
    SimpleAdapter adapter;
    ArrayList<Map<String, String>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_lists);

        /*TodoDBHelper helper = new TodoDBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM todo;", null);*/

        data = new ArrayList<Map<String, String>>();

        adapter = new SimpleAdapter(this,
                data,
                android.R.layout.simple_list_item_2, // A textview
                new String[] {"title","detail"}, // column to be displayed
                new int[] {android.R.id.text1,android.R.id.text2} // ID of textview to display
                );

        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);

        LoadTodoTask task = new LoadTodoTask();
        task.execute();
    }

    class LoadTodoTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            BufferedReader reader;
            StringBuilder buffer = new StringBuilder();
            String line;
            String title;
            String detail;
            String ID;

            try {
                Log.e("LoadTodoTask", "");
                URL u = new URL("http://ict.siit.tu.ac.th/~u5522793272/fetch.php?");
                HttpURLConnection h = (HttpURLConnection)u.openConnection();
                h.setRequestMethod("GET");
                h.setDoInput(true);
                h.connect();

                int response = h.getResponseCode();
                if (response == 200) {
                    reader = new BufferedReader(new InputStreamReader(h.getInputStream()));
                    while((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    Log.e("LoadTodoTask", buffer.toString());
                    //Parsing JSON and displaying messages
                    JSONObject json = new JSONObject(buffer.toString());
                    int length = json.getJSONArray("msg").length();
                    //Toast t = Toast.makeText(getApplicationContext(), Integer.toString(length), Toast.LENGTH_SHORT);
                    //t.show();
                    //Log.d("len",Integer.toString(length));


                    for(int i = 0; i < length; i++) {
                        title = json.getJSONArray("msg").getJSONObject(i).getString("title");
                        detail = json.getJSONArray("msg").getJSONObject(i).getString("detail");
                        ID = json.getJSONArray("msg").getJSONObject(i).getString("_id");




                        //To append a new message:
                        Map<String, String> item = new HashMap<String, String>();
                        item.put("_id",ID);
                        item.put("title", title);
                        item.put("detail", detail);
                        data.add(0, item);
                    }


                    return true;
                }
            } catch (MalformedURLException e) {
                Log.e("LoadMessageTask", "Invalid URL");
            } catch (IOException e) {
                Log.e("LoadMessageTask", "I/O Exception");
            } catch (JSONException e) {
                Log.e("LoadMessageTask", "Invalid JSON");
        }
            catch (Exception e){
                Log.e("LoadMessageTask", "Exception");
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                adapter.notifyDataSetChanged();

            }
        }


    }

    public void buttonClicked(View v) {
        int id = v.getId();
        Intent i;

        switch(id) {
            case R.id.main:
                i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;

            case R.id.addtodo:
                i = new Intent(this, addTodo.class);
                startActivityForResult(i, 88);
                break;


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int _id = Integer.parseInt(data.get(position).get("_id"));
        Log.d("todo", _id + " is clicked");
    }



    class DeleteTodoTask extends AsyncTask<String, Void, Boolean> {
        String line;
        StringBuilder buffer = new StringBuilder();

        @Override
        protected Boolean doInBackground(String... params) {
            String _id = params[0];

            Log.d("params0", _id);


            HttpClient h = new DefaultHttpClient();
            HttpPost p = new HttpPost("http://ict.siit.tu.ac.th/~u5522793272/delete.php");

            List<NameValuePair> values = new ArrayList<NameValuePair>();
            values.add(new BasicNameValuePair("_id", _id));

            try {
                p.setEntity(new UrlEncodedFormEntity(values));
                HttpResponse response = h.execute(p);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                Log.d("return_text", buffer.toString());
            } catch (UnsupportedEncodingException e) {
                Log.e("Error", "Invalid encoding");
            } catch (ClientProtocolException e) {
                Log.e("Error", "Error in posting a message");
            } catch (IOException e) {
                Log.e("Error", "I/O Exception");
            }


            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast t = Toast.makeText(todoLists.this.getApplicationContext(),
                        "Successfully delete your task",
                        Toast.LENGTH_SHORT);
                t.show();
                //LoadMessageTask task = new LoadMessageTask();
                //task.execute();
                /**
                adapter = new SimpleAdapter(this,
                        data,
                        android.R.layout.simple_list_item_2, // A textview
                        new String[] {"title","detail"}, // column to be displayed
                        new int[] {android.R.id.text1,android.R.id.text2} // ID of textview to display
                );

                ListView lv = (ListView)findViewById(R.id.listView);
                lv.setAdapter(adapter);
                return true;
                 */

            } else {
                Toast t = Toast.makeText(todoLists.this.getApplicationContext(),
                        "Unable to delete your task",
                        Toast.LENGTH_SHORT);
                t.show();
            }
        }

    }


        @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            int _id = Integer.parseInt(data.get(position).get("_id"));

        //DELETE ASYNC TASK EXEC

            DeleteTodoTask p = new DeleteTodoTask();
            p.execute(_id+"");




            data = new ArrayList<Map<String, String>>();

            adapter = new SimpleAdapter(this,
                    data,
                    android.R.layout.simple_list_item_2, // A textview
                    new String[] {"title","detail"}, // column to be displayed
                    new int[] {android.R.id.text1,android.R.id.text2} // ID of textview to display
            );

            ListView lv = (ListView)findViewById(R.id.listView);
            lv.setAdapter(adapter);
            LoadTodoTask task = new LoadTodoTask();
            task.execute();
            return true;

        /*
        TodoDBHelper helper = new TodoDBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        int n = db.delete("todo",
                "_id = ?",
                new String[]{Long.toString(id)});

        if (n == 1) {
            Toast t = Toast.makeText(this.getApplicationContext(),
                    "Successfully deleted the selected item.",
                    Toast.LENGTH_SHORT);
            t.show();

            // retrieve a new collection of records
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM todo;",
                    null);

            // update the adapter
            adapter = new SimpleAdapter(this,
                    data,
                    android.R.layout.simple_list_item_2, // A textview
                     // cursor to a data collection
                    new String[] {"title","detail"}, // column to be displayed
                    new int[] {android.R.id.text1,android.R.id.text2} // ID of textview to display
                    );

            ListView lv = (ListView)findViewById(R.id.listView);
            lv.setAdapter(adapter);
            //adapter.changeCursor(cursor);
        }
        db.close();
        return true;
        */



    }

}
