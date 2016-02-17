package com.rsdt.jotial.mapping.area348.filtering;

import com.android.internal.util.Predicate;
import com.rsdt.jotial.data.structures.area348.receivables.ScoutingGroepInfo;
import com.rsdt.jotial.mapping.area348.behaviour.MapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.MapBehaviourManager;
import com.rsdt.jotial.mapping.area348.clustering.ScoutingGroepClusterManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-2-2016
 * Filter for the map.
 */
public class MapFilter implements Filter {


    /**
     * List of filters that are/are going to be apply to the map.
     * */
    private ArrayList<Filter> filters = new ArrayList<>();

    @Override
    public void apply(ScoutingGroepClusterManager scoutingGroepClusterManager, MapBehaviourManager mapBehaviourManager) {

        Filter currentFilter;
        for(int i = 0; i < filters.size(); i++)
        {
            currentFilter = filters.get(i);
            if(currentFilter != null)
            {
                currentFilter.apply(scoutingGroepClusterManager, mapBehaviourManager);
            }
        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 14-2-2016
     * Class for building a MapFilter.
     */
    public static class Builder
    {
        /**
         * Holds the instance of the MapFilter, that is being build.
         * */
        private MapFilter buffer = new MapFilter();

        /**
         * Add a filter to MapFilter.
         * @param filter The filter to add.
         * @return The instance of the builder.
         * */
        public Builder addFilter(Filter filter)
        {
            buffer.filters.add(filter);
            return this;
        }

        /**
         * Create the MapFilter.
         * */
        public MapFilter create()
        {
            return buffer;
        }
    }


    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 14-2-2016
     * Filter for a MapBehaviour.
     */
    public static class MapBehaviourFilter implements Filter
    {
        /**
         * The condition indicating where the behaviour should apply to.
         * */
        private Predicate<MapBehaviour> condition;

        /**
         * The action of the filter.
         * */
        private CustomFilterAction action;

        /**
         * Initializes a new instance of MapBehaviourFilter.
         *
         * @param condition The condition indicating whenever the filter should be applied.
         * @param action The action indicating what should happen.
         * */
        public MapBehaviourFilter(Predicate<MapBehaviour> condition, int action)
        {
            this.condition = condition;
            switch (action)
            {
                case ACTION_SHOW:
                    this.action = new CustomFilterAction() {
                        @Override
                        public int getAction(MapBehaviour behaviour, boolean applies) {
                            return ACTION_SHOW;
                        }
                    };
                    break;
                case ACTION_HIDE:
                    this.action = new CustomFilterAction() {
                        @Override
                        public int getAction(MapBehaviour behaviour, boolean applies) {
                            return ACTION_HIDE;
                        }
                    };
                    break;
                case ACTION_NONE:
                    this.action = new CustomFilterAction() {
                        @Override
                        public int getAction(MapBehaviour behaviour, boolean applies) {
                            return ACTION_NONE;
                        }
                    };
                    break;
            }
        }

        /**
         * Initializes a new instance of MapBehaviourFilter.
         *
         * @param condition The condition indicating whenever the filter should be applied.
         * @param action The action indicating what should happen.
         * */
        public MapBehaviourFilter(Predicate<MapBehaviour> condition, CustomFilterAction action)
        {
            this.condition = condition;
            this.action = action;
        }

        @Override
        public void apply(ScoutingGroepClusterManager scoutingGroepClusterManager, MapBehaviourManager mapBehaviourManager) {

            for(HashMap.Entry<String, MapBehaviour> entry : mapBehaviourManager.entrySet())
            {
                switch (action.getAction(entry.getValue() ,condition.apply(entry.getValue())))
                {
                    case ACTION_SHOW:
                        entry.getValue().setVisible(true);
                        break;
                    case ACTION_HIDE:
                        entry.getValue().setVisible(false);
                        break;
                    case ACTION_NONE:

                        break;
                }
            }
        }

        public interface CustomFilterAction
        {
            int getAction(MapBehaviour behaviour, boolean applies);
        }

    }


    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 14-2-2016
     * Filter for a MapBehaviour.
     */
    public static class ScoutingGroepFilter implements Filter
    {

        /**
         * The condition indicating if the filter should be used on a certain item.
         * */
        private Predicate<ScoutingGroepInfo> condition;

        /**
         * The action of the filter.
         * */
        private int action;

        /**
         * Initializes a new instance of ScoutingGroepFilter.
         *
         * @param condition The condition indicating whenever the filter should be applied.
         * @param action The action indicating what should happen.
         * */
        public ScoutingGroepFilter(Predicate<ScoutingGroepInfo> condition, int action)
        {
            this.condition = condition;
            this.action = action;
        }

        @Override
        public void apply(ScoutingGroepClusterManager scoutingGroepClusterManager, MapBehaviourManager mapBehaviourManager) {
            scoutingGroepClusterManager.clearClusterItems();

            switch (action)
            {
                case ACTION_SHOW:
                    scoutingGroepClusterManager.addLocalItemsOnCondition(condition);
                    break;
                case ACTION_HIDE:
                    scoutingGroepClusterManager.addLocalItemsOnConditionInverted(condition);
                    break;
            }
            scoutingGroepClusterManager.cluster();
        }
    }


    public static final int ACTION_NONE = 0;

    /**
     * Defines the action SHOW.
     * */
    public static final int ACTION_SHOW = 1;

    /**
     * Defines the action HIDE.
     * */
    public static final int ACTION_HIDE = 2;
}
