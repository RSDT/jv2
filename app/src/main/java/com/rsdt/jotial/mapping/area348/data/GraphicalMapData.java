package com.rsdt.jotial.mapping.area348.data;

import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Object that holds the map items of the.
 */
public class GraphicalMapData {

    /**
     * Empty constructor for the GraphicalMapData.
     * */
    private GraphicalMapData() {}

    /**
     * Initializes a new instance of GraphicalMapData.
     * */
    protected GraphicalMapData(MapData mapData, GoogleMap googleMap)
    {
        from(this, mapData, googleMap);
    }

    /**
     * The markers of the GraphicalMapData.
     * */
    protected ArrayList<GraphicalMapDataPair<Marker, MarkerOptions>> markers = new ArrayList<>();

    /**
     * The polylines of the GraphicalMapData.
     * */
    protected ArrayList<GraphicalMapDataPair<Polyline, PolylineOptions>> polylines = new ArrayList<>();

    /**
     * The circles of the GraphicalMapData.
     * */
    protected ArrayList<GraphicalMapDataPair<Circle, CircleOptions>> circles = new ArrayList<>();

    /**
     * Gets the markers of the GraphicalMapData.
     * */
    public ArrayList<GraphicalMapDataPair<Marker, MarkerOptions>> getMarkers() {
        return markers;
    }

    /**
     * Gets the polylines of the GraphicalMapData.
     * */
    public ArrayList<GraphicalMapDataPair<Polyline, PolylineOptions>> getPolylines() {
        return polylines;
    }

    /**
     * Gets the circles of the GraphicalMapData.
     * */
    public ArrayList<GraphicalMapDataPair<Circle, CircleOptions>> getCircles() {
        return circles;
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
     * Sets the visible property of all the markers, polylines and circles.
     *
     * @param value The value indicating the visibility. True means visible.
     * */
    public void setVisible(boolean value)
    {
        for(int i = 0; i < markers.size(); i++)
        {
            markers.get(i).setVisible(value);
        }

        for(int i = 0; i < polylines.size(); i++)
        {
            polylines.get(i).setVisible(value);
        }

        for(int i = 0; i < circles.size(); i++)
        {
            circles.get(i).setVisible(value);
        }
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

        /**
         * Sets the visible property of the Marker/Polyline/Circle.
         * */
        public void setVisible(boolean value)
        {
            if(first instanceof Marker)
            {
                ((Marker) first).setVisible(value);
            }
            else
            {
                if(first instanceof Polyline)
                {
                    ((Polyline) first).setVisible(value);
                }
                else
                {
                    if(first instanceof Circle)
                    {
                        ((Circle) first).setVisible(value);
                    }
                    else
                    {
                        Log.e("GraphicalMapData", "GraphicalMapDataPair.getId() - I is not a type of Marker, Polyline or Circle.");
                    }
                }
            }

        }

        /**
         * Removes the Marker/Polyline/Circle.
         * */
        public void remove()
        {
            if(first instanceof Marker)
            {
                ((Marker) first).remove();
            }
            else
            {
                if(first instanceof Polyline)
                {
                    ((Polyline) first).remove();
                }
                else
                {
                    if(first instanceof Circle)
                    {
                        ((Circle) first).remove();
                    }
                    else
                    {
                        Log.e("GraphicalMapData", "GraphicalMapDataPair.getId() - I is not a type of Marker, Polyline or Circle.");
                    }
                }
            }
        }
    }

    /**
     * Gets the MapData from the GraphicalMapData.
     *
     * @return The MapData that this GraphicalMapData has.
     * */
    public MapData to()
    {
        MapData buffer = MapData.empty();

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
     * Disposes memory.
     * */
    public void destroy()
    {
        if(markers != null)
        {
            markers.clear();
            markers = null;
        }

        if(polylines != null)
        {
            polylines.clear();
            polylines = null;
        }

        if(circles != null)
        {
            circles.clear();
            circles = null;
        }
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

        ArrayList[] lists = new ArrayList[] { mapData.getMarkers(), mapData.getPolylines(), mapData.getCircles()};
        ArrayList currentList;
        for(int l = 0; l < lists.length; l++) {
            currentList = lists[l];
            for(int i = 0; i < currentList.size(); i++)
                switch (l) {
                    case 0:
                        MapDataPair<MarkerOptions> pairMarker = (MapDataPair<MarkerOptions>) currentList.get(i);
                        buffer.markers.add(new GraphicalMapDataPair<>(googleMap.addMarker(pairMarker.first), pairMarker));
                        break;
                    case 1:
                        MapDataPair<PolylineOptions> pairPolyline = (MapDataPair<PolylineOptions>) currentList.get(i);
                        buffer.polylines.add(new GraphicalMapDataPair<>(googleMap.addPolyline(pairPolyline.first), pairPolyline));
                        break;
                    case 2:
                        MapDataPair<CircleOptions> pairCircle = (MapDataPair<CircleOptions>) currentList.get(i);
                        buffer.circles.add(new GraphicalMapDataPair<>(googleMap.addCircle(pairCircle.first), pairCircle));
                        break;
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
    public static GraphicalMapData from(GraphicalMapData in, MapData mapData, GoogleMap googleMap)
    {
        ArrayList[] lists = new ArrayList[] { mapData.getMarkers(), mapData.getPolylines(), mapData.getCircles()};
        ArrayList currentList;
        for(int l = 0; l < lists.length; l++) {
            currentList = lists[l];
            for(int i = 0; i < currentList.size(); i++)
                switch (l) {
                    case 0:
                        MapDataPair<MarkerOptions> pairMarker = (MapDataPair<MarkerOptions>) currentList.get(i);
                        in.markers.add(new GraphicalMapDataPair<>(googleMap.addMarker(pairMarker.first), pairMarker));
                        break;
                    case 1:
                        MapDataPair<PolylineOptions> pairPolyline = (MapDataPair<PolylineOptions>) currentList.get(i);
                        in.polylines.add(new GraphicalMapDataPair<>(googleMap.addPolyline(pairPolyline.first), pairPolyline));
                        break;
                    case 2:
                        MapDataPair<CircleOptions> pairCircle = (MapDataPair<CircleOptions>) currentList.get(i);
                        in.circles.add(new GraphicalMapDataPair<>(googleMap.addCircle(pairCircle.first), pairCircle));
                        break;
                }
        }
        return in;
    }

}
