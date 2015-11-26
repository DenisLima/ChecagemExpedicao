package com.android.gktb.checagemexpedicao;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gktb.checagemexpedicao.library.AdapterListView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListagemItem extends Activity {

    ListView list;
    TextView ver;
    TextView name;
    TextView qt;
    ImageView api;
    private String num_ordem;
    private String num_nf;
    private String of_qtd;
    private String texto;
    ProgressDialog dialog;
    String ordem;
    String url;
    String user;


    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    //URL to get JSON Array
    //private static String url; // = "http://api.learn2crack.com/android/jsonos/";
    // = "http://10.55.1.242/nova_intranet/views/ti/ws_exp.php?acao=carrega_json&&num_ordem=323508&&usuario=exp";
    //JSON Node Names
    private static final String TAG_OS = "android";
    private static final String TAG_VER = "ver";
    private static final String TAG_NAME = "name";
    private static final String TAG_API = "api";
    private static final String TAG_QTD = "qt";
    JSONArray android = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lista_item);
        oslist = new ArrayList<HashMap<String, String>>();

//        new JSONParse().execute();

        Button BtnCarrega = (Button) findViewById(R.id.btn_carrega);
        BtnCarrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JSONParse().execute();
            }
        });

        TextView TxtOs = (TextView) findViewById(R.id.txt_os);

        Button BtnVoltar = (Button) findViewById(R.id.getdata);
        BtnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ListagemItem.this, TelaOs.class);
                startActivity(it);
                //new JSONParse().execute();
            }
        });

        Button BtnChecar = (Button) findViewById(R.id.checar);
        BtnChecar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                startActivityForResult(intent, 0);
               // new JSONParse().execute();
            }
        });


       /* Button Btngetdata = (Button)findViewById(R.id.getdata);
        Btngetdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JSONParse().execute();
            }
        });*/

        Intent it = getIntent();
        user  = it.getStringExtra("usuario");
        ordem = it.getStringExtra("num_ordem");

        TxtOs.setText(ordem);

        //Gravando a OS na sessao
        //ses

        url = "http://10.55.1.242/nova_intranet/views/ti/ws_exp.php?acao=carrega_json&&num_ordem="+ordem+"&&usuario="+user;
        //url = "http://10.55.1.242/nova_intranet/views/ti/ws_exp_homol.php?acao=carrega_json&&num_ordem=324490&&usuario=exp";

        Button btnSair = (Button) findViewById(R.id.btnSair);
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                num_ordem = intent.getStringExtra("SCAN_RESULT");

                Intent it = new Intent("com.google.zxing.client.android.SCAN");
                startActivityForResult(it, 1);

            }
        } else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                num_nf = intent.getStringExtra("SCAN_RESULT");

			/*	Toast.makeText(MenuActivity.this,
						"O numero da NF é : " + num_nf, Toast.LENGTH_LONG)
						.show(); */

                Intent it = new Intent("com.google.zxing.client.android.SCAN");
                startActivityForResult(it, 2);

            }
        }
        else if (requestCode == 2) {
        if (resultCode == RESULT_OK) {
            of_qtd = intent.getStringExtra("SCAN_RESULT");

			/*	Toast.makeText(MenuActivity.this,
						"O numero da NF é : " + num_nf, Toast.LENGTH_LONG)
						.show(); */

            EnviaAoServer(num_ordem, num_nf, ordem, of_qtd);

        }
    }

    }

    public void EnviaAoServer(final String num_ordem, final String num_nf, final String ord, final String ofqtd) {

        AsyncTask<Void, Void, String> myAsync = new AsyncTask<Void, Void, String>() {


            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                dialog = new ProgressDialog(ListagemItem.this);
                dialog.setMessage("Getting Data ...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                // TODO Auto-generated method stub

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://10.55.1.242/nova_intranet/views/ti/WS_EXP.php");

                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                    nameValuePairs.add(new BasicNameValuePair("acao","carrega_dados"));
                    nameValuePairs.add(new BasicNameValuePair("num_ordem",num_ordem));
                    nameValuePairs.add(new BasicNameValuePair("num_nf", num_nf));
                    nameValuePairs.add(new BasicNameValuePair("os", ordem));
                    nameValuePairs.add(new BasicNameValuePair("ofqtd", ofqtd));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httppost);

                    // HttpEntity entity = response.getEntity();

                    BufferedReader res = new BufferedReader(
                            new InputStreamReader(response.getEntity()
                                    .getContent()));

                    if (res != null) {

                        texto = res.readLine();
                        return texto;

                    }
                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {

                // TODO Auto-generated method stub
                dialog.dismiss();
                String[] res = texto.split("!");
                Integer par = Integer.parseInt(res[1]);

              /*Intent it = new Intent(ListagemItem.this, TelaPrincipal.class);
                it.putExtra("id", id[1]);
                it.putExtra("img", id[0]);
                it.putExtra("material", id[2]);
                startActivity(it);
                finish();*/
                Toast.makeText(ListagemItem.this, res[0], Toast.LENGTH_LONG).show();

                if (par == 10){

                    AlertDialog alertDialog = new AlertDialog.Builder(ListagemItem.this).create();
                    alertDialog.setTitle("Aviso");
                    alertDialog.setMessage("DANFE LIBERADA");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // here you can add functions

                        }
                    });
                    alertDialog.setIcon(R.drawable.nuvem);
                    alertDialog.show();

                }

                new JSONParse().execute();

            }

        };
        myAsync.execute();

    }


    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ver = (TextView)findViewById(R.id.vers);
            name = (TextView)findViewById(R.id.name);
            api = (ImageView)findViewById(R.id.api);
            qt = (TextView)findViewById(R.id.txt_qtd_json);
            pDialog = new ProgressDialog(ListagemItem.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();


        }
        @Override
        protected JSONObject doInBackground(String... args) {
            oslist.clear();
            AdapterListView jParser = new AdapterListView();
            // Getting JSON from URL
            //String ord = "http://10.55.1.242/nova_intranet/views/ti/ws_exp.php?acao=carrega_json&&num_ordem="+ordem+"&&usuario=exp";
            JSONObject json = jParser.getJSONFromUrl(url);

            return json;

        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                android = json.getJSONArray(TAG_OS);
                for(int i = 0; i < android.length(); i++){

                    JSONObject c = android.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String ver = c.getString(TAG_VER);
                    String name = c.getString(TAG_NAME);
                    String json_qt = c.getString(TAG_QTD);
                    final Integer api = Integer.parseInt(c.getString(TAG_API));
                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_VER, ver);
                    map.put(TAG_NAME, name);
                    map.put(TAG_QTD, json_qt);

                    if( api == 0 ) {
                        map.put(TAG_API, String.valueOf(R.drawable.att_android));
                    }
                    else if (api == 1) {
                        map.put(TAG_API, String.valueOf(R.drawable.ok_android));
                    }
                    else {
                        map.put(TAG_API, String.valueOf(R.drawable.nok_android));
                    }

                    oslist.add(map);
                    list=(ListView)findViewById(R.id.list);

                    final BaseAdapter adapter = new SimpleAdapter(ListagemItem.this, oslist,
                            R.layout.item_list,
                            new String[] { TAG_VER,TAG_NAME, TAG_API, TAG_QTD }, new int[] {
                            R.id.vers,R.id.name, R.id.api, R.id.txt_qtd_json});
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            //Toast.makeText(ListagemItem.this, "You Clicked at "+oslist.get(+position).get("name")+api, Toast.LENGTH_SHORT).show();

                         }

                        });

                }

            } catch (JSONException e) {
                e.printStackTrace();

            }

        }


    }


}