package ufrpe.carolina.adoteumpet.other;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ufrpe.carolina.adoteumpet.entity.Pet;
import ufrpe.carolina.adoteumpet.entity.Shelter;

/**
 * Created by AnaCarolina on 13/03/2017.
 */

public class ApiHttp {
    private static String BASE_URL = "http://adoteumpetapi.azurewebsites.net/api";
    private Context mContext;

    public ApiHttp(Context ctx){
        mContext = ctx;
    }

    private HttpURLConnection abrirConexao(String url, String metodo,boolean doOutput) throws Exception{
        URL urlCon = new URL(url);
        HttpURLConnection conexao = (HttpURLConnection)urlCon.openConnection();
        conexao.setReadTimeout(15000);
        conexao.setConnectTimeout(15000);
        conexao.setRequestMethod(metodo);
        conexao.setDoInput(true);
        conexao.setDoOutput(doOutput);
        if(doOutput){
            conexao.addRequestProperty("Content-Type","application/json");
        }
        conexao.connect();
        return conexao;
    }

    public boolean acessarComFacebook(Integer Id,String Nome, String Email,String Sexo,String IdFacebook) throws Exception{
        String url = BASE_URL + "/LoginFacebook";
        HttpURLConnection conexao = abrirConexao(url,"POST",true);

        OutputStream os = conexao.getOutputStream();
        os.write(userToJsonBytes(Nome,Email,Sexo,IdFacebook));
        os.flush();
        os.close();

        int responseCode = conexao.getResponseCode();
        Log.d("Response API",String.valueOf(responseCode));

        if(responseCode == HttpURLConnection.HTTP_OK){
            InputStream is = conexao.getInputStream();
            String s = streamToString(is);
            is.close();

            JSONObject json = new JSONObject(s);
            String Code = json.getString("Code");
            String IdUserApp = json.getString("Id");
            String Msg = json.getString("Msg");
            //TODO salvar Id em sharedPreferences
            return true;
        }else{
            throw new RuntimeException("Erro");
        }


    }

    private byte[] userToJsonBytes(String Nome, String Email,String Sexo,String IdFacebook){
        try{
            JSONObject jsonLoginFacebookViewModel = new JSONObject();
            jsonLoginFacebookViewModel.put("Nome",Nome);
            jsonLoginFacebookViewModel.put("Email",Email);
            jsonLoginFacebookViewModel.put("Sexo",Sexo);
            jsonLoginFacebookViewModel.put("IdFacebook",IdFacebook);

            String json = jsonLoginFacebookViewModel.toString();
            Log.d("UserToJsonBytes",json);
            return json.getBytes();

        }catch (JSONException e){
            e.printStackTrace();
        }

        return null;
    }

    private void registrarUserApp(String Nome, String Email, String sexo, String Telefone, String DataNascimento, String Senha){

    }

    private void login(String email, String Password){

    }

    private List<Pet> getPets() throws Exception{
        String url = BASE_URL + "/Pets";
        HttpURLConnection conexao = abrirConexao(url,"GET",false);
        List<Pet> pets = new ArrayList<Pet>();

        if(conexao.getResponseCode() == HttpURLConnection.HTTP_OK){
            String jsonString = streamToString(conexao.getInputStream());
            JSONArray json = new JSONArray(jsonString);

            /*for (int i = 0; i < json.length(); i++){
                JSONObject petJSON = json.getJSONObject(i);
                Pet s = new Pet(petJSON.getInt("Id"),
                        petJSON.getString("Name"),
                        petJSON.getString("Phone"),
                        petJSON.getString("Email"),
                        petJSON.getString("Address"),
                        petJSON.getString("PhotoUrl"),
                        petJSON.getInt("IdUserApp"),
                        petJSON.getString("RegisterData"));
                pets.add(s);
            }*/
        }

        return pets;
    }

    //private Pet getPet(int Id){ }

    private List<Shelter> getShelters() throws Exception{
        String url = BASE_URL + "/Shelters";
        HttpURLConnection conexao = abrirConexao(url,"GET",false);
        List<Shelter> shelters = new ArrayList<Shelter>();

        if(conexao.getResponseCode() == HttpURLConnection.HTTP_OK){
            String jsonString = streamToString(conexao.getInputStream());
            JSONArray json = new JSONArray(jsonString);

            for (int i = 0; i < json.length(); i++){
                JSONObject shelterJSON = json.getJSONObject(i);
                Shelter s = new Shelter(shelterJSON.getInt("Id"),
                        shelterJSON.getString("Name"),
                        shelterJSON.getString("Phone"),
                        shelterJSON.getString("Email"),
                        shelterJSON.getString("Address"),
                        shelterJSON.getString("PhotoUrl"),
                        shelterJSON.getInt("IdUserApp"),
                        shelterJSON.getString("RegisterData"));
                shelters.add(s);
            }
        }

        return shelters;
    }

    //private Shelter getShelter(int Id){

    //}

    private String streamToString(InputStream is) throws IOException{
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int lidos;
        while((lidos = is.read(bytes))>0){
            baos.write(bytes,0,lidos);;
        }
        return new String(baos.toByteArray());
    }
}