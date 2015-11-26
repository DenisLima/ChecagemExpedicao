package com.android.gktb.checagemexpedicao;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

/**
 * Created by denis on 08/10/14.
 */
public class TelaOs extends Activity {
    private ProgressDialog dialog;
    private String texto;
    private String login_user;

    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tela_os);

        Intent in = getIntent();
        login_user = in.getStringExtra("user");

        final EditText num_ordem = (EditText) findViewById(R.id.num_ordem);

        Button btn_os = (Button) findViewById(R.id.btn_env_os);
        btn_os.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CarregaOs(num_ordem.getText().toString(), login_user);

            }
        });

        Button btn_voltar_os = (Button) findViewById(R.id.btn_voltar_os);
        btn_voltar_os.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(TelaOs.this, MyActivity.class);
                startActivity(it);
                finish();

            }
        });

    }

    public void CarregaOs(final String num_os, final String u){

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {

                dialog = new ProgressDialog(TelaOs.this);
                dialog.setTitle("Verificando");
                dialog.setMessage("Carregando OS...");
                dialog.show();

            }

            @Override
            protected String doInBackground(Void... params) {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://10.55.1.242/nova_intranet/views/ti/WS_EXP.php");

                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                    nameValuePairs.add(new BasicNameValuePair("acao", "carrega_os"));
                    nameValuePairs.add(new BasicNameValuePair("num_ordem", num_os));
                    nameValuePairs.add(new BasicNameValuePair("usuario", u));
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    BufferedReader res = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    if (res != null) {

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
            protected void onPostExecute(String s) {
                dialog.dismiss();

                //Integer resultado = Integer.parseInt(texto);
                //String resultado = texto.toString();
                //String[] cort = texto.split("-");
                Integer r = Integer.parseInt(texto);
                //String jsonInput = cort[1];
                //String jsonInput = "{ \"android\": [ { \"ver\": \"1.5\", \"name\": \"Cupcake\", \"api\": \"API level 3\" }, { \"ver\": \"1.6\", \"name\": \"Donut\", \"api\": \"API level 4\" }, { \"ver\": \"2.0-2.1\", \"name\": \"Eclair\", \"api\": \"API level 5-7\" }, { \"ver\": \"2.2\", \"name\": \"Froyo\", \"api\": \"API level 8\" }, { \"ver\": \"2.3\", \"name\": \"Gingerbread\", \"api\": \"API level 9-10\" }, { \"ver\": \"3.0-3.2\", \"name\": \"Honeycomb\", \"api\": \"API level 11-13\" }, { \"ver\": \"4.0\", \"name\": \"Ice Cream Sandwich\", \"api\": \"API level 14-15\" }, { \"ver\": \"4.1-4.3\", \"name\": \"JellyBean\", \"api\": \"API level 16-18\" }, { \"ver\": \"4.4\", \"name\": \"KitKat\", \"api\": \"API level 19\" } ] }";

                if (r == 1){

                    //Toast.makeText(TelaOs.this, "A OS existe!", Toast.LENGTH_LONG).show();
                    Intent it = new Intent(TelaOs.this, ListagemItem.class);
                    it.putExtra("usuario", u);
                    it.putExtra("num_ordem", num_os);
                    startActivity(it);
                    finish();

                }
                else if (r == 2){

                    Toast.makeText(TelaOs.this, "Essa OS já foi checada!", Toast.LENGTH_LONG).show();

                }
                else{

                    Toast.makeText(TelaOs.this, "A OS Nao existe!", Toast.LENGTH_LONG).show();

                }

            }
        };
        task.execute();
    }

}















