package com.rsdt.jotial.mapping.area348;

import android.view.View;
import android.widget.TextView;

import com.android.internal.util.Predicate;
import com.google.android.gms.maps.model.Marker;
import com.rsdt.jotiv2.R;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 2-11-2015
 * Description...
 */
public class TestInfoWindowBehaviour implements JotiInfoWindowAdapter.OnGetInfoWindowCallback, Predicate<Marker> {


    @Override
    public void onGetInfoWindow(View view, Marker marker) {


        TextView name = (TextView)view.findViewById(R.id.infoWindow_naam);
        name.setText("VosInfo");



    }


    @Override
    public boolean apply(Marker marker) {
        return (marker.getTitle().equals("LOL"));
    }

}
