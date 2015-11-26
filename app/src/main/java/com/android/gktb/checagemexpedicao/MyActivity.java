package com.android.gktb.checagemexpedicao;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.List;


public class MyActivity extends Activity {
    private String texto;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my);

        final EditText usuario = (EditText) findViewById(R.id.user);
        final EditText senha = (EditText) findViewById(R.id.pass);

        Button btn_sair = (Button) findViewById(R.id.btn_sair);
        btn_sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidaAcesso(usuario.getText().toString(), senha.getText().toString());

            }
        });



    }

    public void ValidaAcesso(final String user, final String pass){

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(MyActivity.this);
                dialog.setTitle("Autenticação");
                dialog.setMessage("Aguarde...");
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://10.55.1.242/nova_intranet/views/ti/WS_EXP.php");

                try{
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                    nameValuePairs.add(new BasicNameValuePair("acao", "login"));
                    nameValuePairs.add(new BasicNameValuePair("usuario", user));
                    nameValuePairs.add(new BasicNameValuePair("senha", pass));
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    BufferedReader res = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent())
                    );

                    if (res != null){

                        texto = res.readLine();
                        return texto;

                    }

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                dialog.dismiss();
                Integer resulted;
                resulted = Integer.parseInt(texto);

                //Toast.makeText(MyActivity.this, "O retorno foi: "+texto, Toast.LENGTH_LONG).show();

                if (resulted == 1){
                    //Toast.makeText(MyActivity.this, "Usuario e senha corretos", Toast.LENGTH_LONG).show();
                    Intent it = new Intent(MyActivity.this, TelaOs.class);
                    it.putExtra("user", user);
                    startActivity(it);
                    finish();
                }
                else{
                    Toast.makeText(MyActivity.this, "Usuario e senha incorretos", Toast.LENGTH_LONG).show();
                }
            }
        };
        task.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
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
