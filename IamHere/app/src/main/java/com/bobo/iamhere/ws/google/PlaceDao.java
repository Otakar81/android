package com.bobo.iamhere.ws.google;

import androidx.annotation.NonNull;

public class PlaceDao implements Comparable {

    private String id;

    private double latitudine;
    private double longitudine;

    private String icon;
    private String name;
    private String formattedAddress;

    private String urlDettaglio;

    private float rating;
    private int priceLevel; //TODO
    private int isOpenNow; //1 = Si, 0 = no, -1 = Non ho info

    private float distanzaDaMe;


    public PlaceDao(String id, double latitudine, double longitudine, String icon, String name, String formattedAddress, String urlDettaglio, float rating, int isOpenNow)
    {
        this.id = id;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.icon = icon;
        this.name = name;
        this.formattedAddress = formattedAddress;
        this.urlDettaglio = urlDettaglio;
        this.rating = rating;
        this.isOpenNow = isOpenNow;
    }

    public String getId() {
        return id;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public String getUrlDettaglio() {
        return urlDettaglio;
    }

    public float getRating() {
        return rating;
    }

    public int isOpenNow() {
        return isOpenNow;
    }

    /*
    public String getIsOpenString()
    {
        //Formatto isOpen
        String isOpen = "(Aperto)";

        if(isOpenNow == 0)
            isOpen = "(Chiuso)";
        else if(isOpenNow == -1)
            isOpen = "";

        return isOpen;
    }
    */

    public String getFormattedDistanzaDaMe()
    {
        float distanza = distanzaDaMe;
        String unitaDiMisura = "m";

        if(distanza > 1000) {
            distanza = distanza / 1000;
            unitaDiMisura = "km";

            //Arrotondo ai due decimali
            distanza = (float) (Math.round( distanza * Math.pow( 10, 2 ) )/Math.pow( 10, 2 ));
        }else{
            distanza = (float) (Math.round( distanza * Math.pow( 10, 1 ) )/Math.pow( 10, 1 ));
        }

        return distanza + unitaDiMisura;
    }

    public float getDistanzaDaMe() {
        return distanzaDaMe;
    }

    public void setDistanzaDaMe(float distanzaDaMe) {
        this.distanzaDaMe = distanzaDaMe;
    }

    @Override
    public String toString() {

        //Formatto isOpen
        String isOpen = "(Aperto)";

        if(isOpenNow == 0)
            isOpen = "(Chiuso)";
        else if(isOpenNow == -1)
            isOpen = "";

        //Formatto il rating (potrei non avere info)
        String ratingString = "";

        if (rating >= 0)
            ratingString = " (Rating: " + rating + ")";



        String luogoToString = name + ratingString + "\n";
        luogoToString += formattedAddress + "\n";
        luogoToString += "Distanza: " + (int)distanzaDaMe + "m  " + isOpen;

        return luogoToString;
    }

    @Override
    public int compareTo(@NonNull Object o) {

        PlaceDao luogo = (PlaceDao) o;

        if(this.distanzaDaMe > luogo.getDistanzaDaMe())
            return 1;
        else if(this.distanzaDaMe < luogo.getDistanzaDaMe())
            return -1;
        else
            return 0;
    }
}
