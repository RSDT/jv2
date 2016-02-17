package com.rsdt.jotiv2;

import android.util.Log;

import com.android.internal.util.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 7-12-2015
 * Class for reporting a status.
 */
public final class Tracker {

    /**
     * The list that contains the
     * */
    private HashMap<TrackerSubscriberCallback, Predicate<String>> subscribers = new HashMap<>();

    /**
     * The list that contains the callbacks that should be unsubed after the report iteration has completed.
     * */
    private ArrayList<TrackerSubscriberCallback> postponedUnsubscribeCallbacks = new ArrayList<>();

    private ArrayList<TrackerMessage> postponedMessages = new ArrayList<>();

    /**
     * Subscribe a subscriber.
     *
     * @param callback The callback that should be registered.
     * @param filter The filter that should be applied, null for none.
     * */
    public void subscribe(TrackerSubscriberCallback callback, Predicate<String> filter)
    {
        subscribers.put(callback, filter);
    }

    /**
     * Unsubscribe a subscriber.
     *
     * @param callback The callback that should be unregistered.
     * */
    public void unsubscribe(TrackerSubscriberCallback callback)
    {
        subscribers.remove(callback);
    }

    /**
     * Postpone the unsubscribing of a subscriber to after the report iteration has completed.
     *
     * @param callback The callback that should be unregistered.
     * */
    public void postponeUnsubscribe(TrackerSubscriberCallback callback)
    {
        postponedUnsubscribeCallbacks.add(callback);
    }

    /**
     * Postpone the reporting of a message to after the report iteration has completed.
     * */
    public void postponeReport(TrackerMessage message)
    {
        postponedMessages.add(message);
    }

    /**
     * Report a message.
     *
     * @param message The message to report.
     * */
    public void report(TrackerMessage message)
    {
        /**
         * Iterate through subscriber entries.
         * */
        for(Map.Entry<TrackerSubscriberCallback, Predicate<String>> entry : subscribers.entrySet())
        {
            /**
             * Check if the callback is not null.
             * */
            if(entry.getKey() != null)
            {
                /**
                 * Check if the filter is null, if not apply the filter.
                 * */
                if(entry.getValue() != null)
                {
                    /**
                     * Check if the message should apply for the current subscriber.
                     * */
                    if(entry.getValue().apply(message.identifier))
                    {
                        entry.getKey().onConditionMet(message);
                    }
                }
                else
                {
                    /**
                     * No filter, so subscriber wants all the messages.
                     * Invoke callback.
                     * */
                    entry.getKey().onConditionMet(message);
                }
            }
        }

        if(!postponedUnsubscribeCallbacks.isEmpty())
        {
            /**
             * Loop through each postponed unsub callback.
             * */
            for(int i = 0; i < postponedUnsubscribeCallbacks.size(); i++)
            {
                /**
                 * Unsubscribe the current callback.
                 * */
                unsubscribe(postponedUnsubscribeCallbacks.get(i));
            }

            /**
             * Clear the list of postponed unsub callbacks.
             * */
            postponedUnsubscribeCallbacks.clear();
        }

        if(!postponedMessages.isEmpty())
        {
            for(int i = 0; i < postponedMessages.size(); i++)
            {
                report(postponedMessages.get(i));
            }

            postponedMessages.clear();
        }

        switch (message.level)
        {
            case TrackerMessage.LEVEL_INFO:
                /**
                 * Write the message to the console.
                 * */
                Log.i("Tracker", message.identifier + " - " + message.descripition);
                break;
            case TrackerMessage.LEVEL_WARNING:
                /**
                 * Write the message to the console.
                 * */
                Log.w("Tracker", message.identifier + " - " + message.descripition);
                break;
            case TrackerMessage.LEVEL_ERROR:
                /**
                 * Write the message to the console.
                 * */
                Log.e("Tracker", message.identifier + " - " + message.descripition);
                break;
        }
    }

    /**
     * Class that represents a message.
     * */
    public static class TrackerMessage
    {
        /**
         * The identifier of the Message.
         * */
        private String identifier;

        /**
         * The title of the Message.
         * */
        private String title;

        /**
         * The title of the Message.
         * */
        private String descripition;

        /**
         * The level of the Message.
         * */
        private int level = LEVEL_INFO;

        /**
         * Initializes a new instance of TrackerMessage.
         *
         * @param identifier The id of the Message.
         * @param title The title of the Message.
         * @param description  The description of the Message.
         * */
        public TrackerMessage(String identifier, String title, String description)
        {
            this.identifier = identifier;
            this.title = title;
            this.descripition = description;
        }

        /**
         * Initializes a new instance of TrackerMessage.
         *
         * @param identifier The id of the Message.
         * @param title The title of the Message.
         * @param description  The description of the Message.
         * */
        public TrackerMessage(String identifier, String title, String description, int level)
        {
            this.identifier = identifier;
            this.title = title;
            this.descripition = description;
            this.level = level;
        }

        /**
         * Gets the identifier of the TrackerMessage.
         * */
        public String getIdentifier() {
            return identifier;
        }

        /**
         * Gets the title of the TrackerMessage.
         * */
        public String getTitle() {
            return title;
        }

        /**
         * Gets the description of the TrackerMessage.
         * */
        public String getDescripition() {
            return descripition;
        }

        public static final int LEVEL_INFO = 1;

        public static final int LEVEL_WARNING = 2;

        public static final int LEVEL_ERROR = 3;
    }

    /**
     * Defines a callback for
     * */
    public interface TrackerSubscriberCallback
    {
        void onConditionMet(TrackerMessage message);
    }

}
