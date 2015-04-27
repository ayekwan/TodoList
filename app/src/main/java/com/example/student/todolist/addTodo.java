package com.example.student.todolist;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class addTodo extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
    }

    class PostTodoTask extends AsyncTask<String, Void, Boolean> {
        String line;
        StringBuilder buffer = new StringBuilder();

        @Override
        protected Boolean doInBackground(String... params) {
            String title = params[0];
            String detail = params[1];
            Log.d("params0",title);
            Log.d("params1",detail);

            HttpClient h = new DefaultHttpClient();
            HttpPost p = new HttpPost("http://ict.siit.tu.ac.th/~u5522793272/post.php");

            List<NameValuePair> values = new ArrayList<NameValuePair>();
            values.add(new BasicNameValuePair("title", title));
            values.add(new BasicNameValuePair("detail", detail));
            try {
                p.setEntity(new UrlEncodedFormEntity(values));
                HttpResponse response = h.execute(p);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                Log.d("return_text",buffer.toString());
            } catch (UnsupportedEncodingException e) {
                Log.e("Error", "Invalid encoding");
            } catch (ClientProtocolException e) {
                Log.e("Error", "Error in posting a message");
            } catch (IOException e) {
                Log.e("Error", "I/O Exception");
            }
            /**
            catch (Exception e){
                Log.e("Error", "aaaaaaException");
            }
*/

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast t = Toast.makeText(addTodo.this.getApplicationContext(),
                        "Successfully add your task",
                        Toast.LENGTH_SHORT);
                t.show();
                //LoadMessageTask task = new LoadMessageTask();
                //task.execute();
            }
            else {
                Toast t = Toast.makeText(addTodo.this.getApplicationContext(),
                        "Unable to add your task",
                        Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }


    public void addClicked(View v) {
        EditText etTitle = (EditText)findViewById(R.id.editText);
        EditText etMessage = (EditText)findViewById(R.id.editText2);
        //EditText detail = (EditText)findViewById(R.id.editText2);



        String txetTitle = etTitle.getText().toString().trim();
        String txetMessage = etMessage.getText().toString().trim();
        //String sDetail = detail.getText().toString();


        if (txetTitle.length() > 0 && txetMessage.length() > 0) {
            PostTodoTask p = new PostTodoTask();
            p.execute(txetTitle,txetMessage);
        }

        Intent i;


        i = new Intent(this, todoLists.class);
        startActivity(i);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_todo, menu);
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
}
