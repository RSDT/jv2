package com.rsdt.jotial.mapping.area348.data;

import android.view.View;

import com.google.android.gms.maps.model.Marker;
import com.rsdt.jotial.communication.ApiResult;
import com.rsdt.jotial.mapping.area348.behaviour.InfoBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.ScoutingGroepBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.VosInfoBehaviour;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class for controlling the flow of handling and reading.
 * This class servers as a holder for the control values.
 * NOTE: MapPartState does not longer represent updating(internet) behaviour.
 */
public class MapPartState {


    /**
     * Initializes a new instance of MapPartState.
     * */
    public MapPartState()
    {

    }

    /**
     * The associated storage object.
     * */
    private MapData mapData;

    /**
     * The associated graphical map data.
     * */
    private GraphicalMapData graphicalMapData;

    /**
     * Gets the behaviours associated with this state.
     * */
    private InfoBehaviour behaviour;

    /**
     * Value indicating if the state is present on the map.
     */
    private boolean isOnMap = false;

    /**
     * Value indicating if the state should be shown.
     * NOTE: On the map and visible are different things, the state can be on the map but and still be not visible.
     */
    private boolean show = false;

    /**
     * Value indicating if the state should be updated.
     */
    private boolean update = false;

    /**
     * Value indicating if the state's GraphicalMapData is synced with it's MapData.
     * */
    private boolean synced = false;

    /**
     * Value indicating if the state is addable to the map.
     * */
    private boolean addable = true;


    public MapData getMapData() {
        return mapData;
    }

    public GraphicalMapData getGraphicalMapData() {
        return graphicalMapData;
    }

    public InfoBehaviour getBehaviour() {
        return behaviour;
    }

    public void setGraphicalMapData(GraphicalMapData graphicalMapData) {
        this.graphicalMapData = graphicalMapData;
    }

    public static MapPartState from(ApiResult apiResult)
    {
        MapPartState buffer = new MapPartState();
        /**
         * Split the url so we can determine the type of the request.
         * TODO: determine type in ApiResult?
         * */
        String[] args = apiResult.getRequest().getUrl().getPath().split("/");

        InfoBehaviour behaviour = null;

        /**
         * Switch on the first arg
         * */
        switch(args[1])
        {
            case "vos":
                behaviour = new VosInfoBehaviour(buffer);
                buffer.mapData = MapData.from(apiResult.getData(), behaviour);
                break;
            case "sc":
                behaviour = new ScoutingGroepBehaviour();
                break;
        }
        buffer.behaviour = behaviour;

        return buffer;
    }
}
