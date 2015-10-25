package com.rsdt.jotial.mapping.area348.data;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class for controlling the flow of updating, handling and reading.
 * This class servers as a holder for the control values.
 */
public class MapPartState {


    public MapPartState()
    {

    }

    /**
     * Value indicating if there is a pending interaction for this state.
     */
    private boolean pending = false;

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
     * Value indicating if the state has local data.
     */
    private boolean hasLocalData = false;

    /**
     * Value indicating if the state's MapBindObject is synced with it's StorageObject.
     * */
    private boolean synced = false;

    /**
     * Value indicating if the state is addable to the map.
     * */
    private boolean addable = true;



}
