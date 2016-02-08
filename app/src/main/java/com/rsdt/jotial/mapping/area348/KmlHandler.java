package com.rsdt.jotial.mapping.area348;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlPlacemark;
import com.google.maps.android.kml.KmlPolygon;
import com.rsdt.jotial.Constants;
import com.rsdt.jotial.JotiApp;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by mattijn on 2/8/16.
 */
public class KmlHandler {
    private final GoogleMap Gmap;
    private final int kmlfile;
    private HashMap<String,Polygon> deelgebieden = new HashMap<>();

    KmlHandler(GoogleMap gmap, int KmlFile){
        this.Gmap=gmap;
        this.kmlfile = KmlFile;
    }

    /**
     * voeg de deelgebieden van de kml onzichtbaar toe aan de kaart.
     * @return true bij geen error
     */
    public boolean readKML(){
        try {
            KmlLayer kmllayer = new KmlLayer(Gmap, kmlfile, JotiApp.getContext());
            for (KmlContainer temp : kmllayer.getContainers()){
                for (KmlContainer temp2: temp.getContainers()){
                    if (temp2.getProperty("name").equals("Deelgebieden")){
                        for (KmlPlacemark deelgebied : temp2.getPlacemarks()){
                            String teampart = String.valueOf(deelgebied.getProperty("name").toLowerCase().charAt(0));
                            KmlPolygon p = (KmlPolygon) deelgebied.getGeometry();
                            deelgebieden.put(teampart, Gmap.addPolygon(new PolygonOptions()
                                    .addAll(p.getOuterBoundaryCoordinates())
                                    .fillColor(Constants.getAssociatedAlphaColor(teampart, Constants.alfaDeelgebieden))
                                    .strokeWidth(Constants.lineThicknessDeelgebieden)
                                    .visible(false)));
                        }
                    }
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * laat de layer zien op de kaart
     * @param teampart deelgebied dat moet gezien moet worden.
     */
    public void enableDeelgebied(String teampart){
        deelgebieden.get(teampart).setVisible(true);
    }

    /**
     * laat verberg de layer op de kaart.
     * @param teampart deelgebied dat verborgen moet worden
     */
    public void disableDeelgebied(String teampart){
        deelgebieden.get(teampart).setVisible(false);
    }
}
