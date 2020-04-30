package br.com.security.func.test;

import br.com.security.func.utils.Geo;

/**
 * Created by mariomartins on 23/09/17.
 */

public class GeoTest {

    public static void main(String[] args) {

        double distancia = Geo.getDistancia(-18.733267, -47.509621, -18.733348, -47.509380);

        System.out.println(distancia);


    }
}
