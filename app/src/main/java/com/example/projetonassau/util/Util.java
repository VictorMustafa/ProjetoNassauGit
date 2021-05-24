package com.example.projetonassau.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

public class Util {


    public static boolean statusInternet(Context context) {

        ConnectivityManager conexao = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conexao != null) {

            // PARA DISPOSTIVOS NOVOS
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                NetworkCapabilities recursosRede = conexao.getNetworkCapabilities(conexao.getActiveNetwork());

                if (recursosRede != null) {//VERIFICAMOS SE RECUPERAMOS ALGO

                    if (recursosRede.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {

                        //VERIFICAMOS SE DISPOSITIVO TEM 3G
                        return true;

                    } else if (recursosRede.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

                        //VERIFICAMOS SE DISPOSITIVO TEM WIFFI
                        return true;

                    }

                    //NÃO POSSUI UMA CONEXAO DE REDE VÁLIDA

                    return false;

                }

            } else {//COMECO DO ELSE

                // PARA DISPOSTIVOS ANTIGOS  (PRECAUÇÃO)
                NetworkInfo informacao = conexao.getActiveNetworkInfo();


                if (informacao != null && informacao.isConnected()) {
                    return true;
                } else
                    return false;

            }//FIM DO ELSE
        }

        return false;
    }


    public static boolean verificarCampos(Context context, String texto_1, String texto_2) {


        if (!texto_1.isEmpty() && !texto_2.isEmpty()) {

            if (statusInternet(context)) {

                return true;

            } else {

                Toast.makeText(context, "Sem conexão com a Internet", Toast.LENGTH_LONG).show();

                return false;
            }

        } else {

            Toast.makeText(context, "Preencha os campos", Toast.LENGTH_LONG).show();
            return false;
        }


    }


}