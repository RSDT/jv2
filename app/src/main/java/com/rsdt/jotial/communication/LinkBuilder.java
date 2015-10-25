package com.rsdt.jotial.communication;

import android.support.annotation.Nullable;

import java.net.URL;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 19-10-2015
 * Class that servers as a help tool for building up URLs.
 */
public class LinkBuilder {

    /**
     * The root of the URL the LinkBuilder is going to build.
     * */
    private static String root;

    /**
     * Sets the root of the URl that the LinkBuilder is going to build.
     * @param root
     */
    public static void setRoot(String root) {
        LinkBuilder.root = root;
    }

    @Nullable
    /**
     * Builds up a URL with the given args from the LinkBuilder's root.
     * @param args The arguments the URl should have.
     * @return A URL with the given args and LinkBuilder's root. Returns null if building failed.
     */
    public static URL build(String[] args)
    {
        String pasted = root;
        for(int i = 0; i < args.length; i++)
        {
            pasted += "/" + args[i];
        }
        try { return new URL(pasted); } catch (Exception e) { return null; }
    }
}
