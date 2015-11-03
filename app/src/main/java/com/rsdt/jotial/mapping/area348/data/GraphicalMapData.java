package com.rsdt.jotial.mapping.area348.data;

import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import com.android.internal.util.Predicate;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Object that holds the map items of the MapPartState.
 */
public class GraphicalMapData implements CapableActionPreformer {

    /**
     * Initializes a new instance of GraphicalMapData.
     * */
    public GraphicalMapData()
    {

    }

    /**
     * The markers of the GraphicalMapData.
     * */
    private ArrayList<GraphicalMapDataPair<Marker, MarkerOptions>> markers = new ArrayList<>();

    /**
     * The polylines of the GraphicalMapData.
     * */
    private ArrayList<GraphicalMapDataPair<Polyline, PolylineOptions>> polylines = new ArrayList<>();

    /**
     * The circles of the GraphicalMapData.
     * */
    private ArrayList<GraphicalMapDataPair<Circle, CircleOptions>> circles = new ArrayList<>();

    @Override
    /**
     * Preforms a action to each item.
     * */
    public void preform(ItemAction action) {

        if(action.getType() == Marker.class)
        {
            for(int i = 0; i < markers.size(); i++)
            {
                action.preform(markers.get(i));
            }
        }

        if(action.getType() == Polyline.class)
        {
            for(int i = 0; i < markers.size(); i++)
            {
                action.preform(markers.get(i));
            }
        }

        if(action.getType() == Circle.class)
        {
            for(int i = 0; i < markers.size(); i++)
            {
                action.preform(markers.get(i));
            }
        }
    }

    @Override
    /**
     * Preforms a action to each item when the condition is met.
     * */
    public void preform(ItemAction action, Predicate condition) {
        if(action.getType() == Marker.class)
        {
            for(int i = 0; i < markers.size(); i++)
            {
                if(condition.apply(markers.get(i)))
                {
                    action.preform(markers.get(i));
                }
            }
        }

        if(action.getType() == Polyline.class)
        {
            for(int i = 0; i < polylines.size(); i++)
            {
                if(condition.apply(polylines.get(i))) {
                    action.preform(polylines.get(i));
                }
            }
        }

        if(action.getType() == Circle.class)
        {
            for(int i = 0; i < circles.size(); i++)
            {
                if(condition.apply(circles.get(i))) {
                    action.preform(circles.get(i));
                }
            }
        }
    }


    /**
     * Finds a GraphicalMapDataPair on it's item id.
     *
     * @param id The id of the item.
     *
     * @return Returns the found pair, returns null if no pair was found.
     * */
    public <I, IO extends Parcelable> GraphicalMapDataPair<I, IO> findPair(String id)
    {
        GraphicalMapDataPair currentPair;
        for(int i = 0; i < markers.size(); i++)
        {
            currentPair = markers.get(i);
            if(currentPair.getId().equals(id))
                return currentPair;
        }
        return null;
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 3-11-2015
     * Object that holds the pairs of ItemOptions and MapDataPair.
     */
    public static class GraphicalMapDataPair<I, OI extends Parcelable> extends Pair<I, MapDataPair<OI>>
    {

        /**
         * Constructor for a Pair.
         *
         * @param first  the first object in the Pair
         * @param second the second object in the pair
         */
        public GraphicalMapDataPair(I first, MapDataPair<OI> second) {
            super(first, second);
        }

        /**
         * Gets the id of the Marker/Polyline/Circle.
         * */
        public String getId()
        {
            if(first instanceof Marker)
            {
                return ((Marker) first).getId();
            }
            if(first instanceof Polyline)
            {
                return ((Polyline) first).getId();
            }
            if(first instanceof Circle)
            {
                return ((Circle) first).getId();
            }
            Log.e("GraphicalMapData", "GraphicalMapDataPair.getId() - I is not a type of Marker, Polyline or Circle.");
            return null;
        }
    }

    /**
     * Gets the MapData from the GraphicalMapData.
     *
     * @return The MapData that this GraphicalMapData has.
     * */
    public MapData to()
    {
        MapData buffer = MapData.empty;

        ArrayList[] lists = new ArrayList[] { markers, polylines, circles };
        for(int l = 0; l < lists.length; l++)
        {
            ArrayList currentList = lists[l];
            for(int i = 0; i < currentList.size(); i++)
            {
                switch(l)
                {
                    case 0:
                        buffer.getMarkers().add(((GraphicalMapDataPair<Marker, MarkerOptions>)currentList.get(i)).second);
                        break;
                    case 1:
                        buffer.getPolylines().add(((GraphicalMapDataPair<Polyline, PolylineOptions>)currentList.get(i)).second);
                        break;
                    case 2:
                        buffer.getCircles().add(((GraphicalMapDataPair<Circle, CircleOptions>)currentList.get(i)).second);
                        break;
                }
            }
        }
        return buffer;
    }

    /**
     * Generates a GraphicalMapData from the MapData and by using the GoogleMap.
     *
     * @param mapData The MapData where the GraphicalMapData should be generated from.
     * @param googleMap The GoogleMap that is used to create the GraphicalMapData.
     *
     * @return The GraphicalMapData generated from the MapData using the GoogleMap.
     * */
    public static GraphicalMapData from(MapData mapData, GoogleMap googleMap)
    {
        GraphicalMapData buffer = new GraphicalMapData();

        MapDataPair<MarkerOptions> pair;
        for(int m = 0; m < mapData.getMarkers().size(); m++)
        {
            pair = mapData.getMarkers().get(m);
            buffer.markers.add(new GraphicalMapDataPair<>(googleMap.addMarker(pair.first), pair ));
        }
        return buffer;
    }
}
