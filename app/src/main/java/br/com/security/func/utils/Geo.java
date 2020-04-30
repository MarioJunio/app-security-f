package br.com.security.func.utils;

/**
 * Created by mariomartins on 23/09/17.
 */

public class Geo {

    public static int getDistancia(double latitude, double longitude, double latitudePto, double longitudePto) {

        latitude = Math.toRadians(latitude);
        longitude = Math.toRadians(longitude);

        latitudePto = Math.toRadians(latitudePto);
        longitudePto = Math.toRadians(longitudePto);

        double dlon, dlat, a, distancia;

        dlon = longitudePto - longitude;
        dlat = latitudePto - latitude;

        a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(latitude) * Math.cos(latitudePto) * Math.pow(Math.sin(dlon / 2), 2);

        distancia = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int) Math.round(6378140 * distancia); /* 6378140 is the radius of the Earth in meters*/
    }
}
