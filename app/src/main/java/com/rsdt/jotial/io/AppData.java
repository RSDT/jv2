package com.rsdt.jotial.io;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.rsdt.jotial.JotiApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-1-2016
 * Class that handles all caching operations.
 */
public class AppData {


    /**
     * Checks if the save exists.
     *
     * @param filename The name of the file where the save should be.
     * */
    public static boolean hasSave(String filename)
    {
        return new File(JotiApp.getContext().getFilesDir(), filename).exists();
    }

    /**
     * Saves a object in Json format.
     *
     * @param object The object that should be saved.
     * @param filename The name of the file where the object should be saved on.
     * */
    public static void saveObjectAsJson(Object object, String filename)
    {
        new SaveTask(object, filename).run();
    }

    /**
     * Saves a object in Json format in the background.
     *
     * @param object The object that should be saved.
     * @param filename The name of the file where the object should be saved on.
     * */
    public static void saveObjectAsJsonInBackground(Object object, String filename)
    {
        new Thread(new SaveTask(object, filename)).run();
    }

    /**
     * Saves a Drawable in a file.
     *
     * @param drawable The Drawable that should be saved.
     * @param filename The name of the file where the Drawable should be saved on.
     * */
    public static void saveDrawable(Drawable drawable, String filename)
    {
        new SaveDrawableTask(drawable, filename).run();
    }

    /**
     * Saves a Drawable in a file in the background.
     *
     * @param drawable The Drawable that should be saved.
     * @param filename The name of the file where the Drawable should be saved on.
     * */
    public static void saveDrawableInBackground(Drawable drawable, String filename)
    {
        new Thread(new SaveDrawableTask(drawable, filename)).run();
    }

    /**
     * Gets a object out of the save.
     *
     * @param filename The name of the file where the object is stored.
     * @param type The type of the object.
     * */
    public static <T> T getObject(String filename, Type type)
    {
        try
        {
            File file = new File(JotiApp.getContext().getFilesDir(), filename);
            JsonReader jsonReader = new JsonReader(new FileReader(file));
            jsonReader.setLenient(true);
            return (T)new Gson().fromJson(jsonReader, type);
        }
        catch(Exception e)
        {
            Log.e("AppData", e.getCause().toString(), e);
        }
        return null;
    }

    /**
     * Gets the Drawable out of the save.
     *
     * @param filename The name of the file where the Drawable is stored.
     * */
    public static Drawable getDrawable(String filename)
    {

        try
        {
            File file = new File(JotiApp.getContext().getFilesDir(), filename);
            return new BitmapDrawable(BitmapFactory.decodeStream(new FileInputStream(file)));
        }
        catch (Exception e)
        {
            Log.e("AppData", e.getStackTrace().toString(), e);
        }
        return null;
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 29-1-2016
     * Class for saving a object.
     */
    public static class SaveTask implements Runnable {

        /**
         * The object that is going to be saved.
         * */
        private Object object;

        /**
         * The name of the file where the object is going to be saved.
         * */
        private String filename;

        /**
         * Initializes a new instance of SaveTask.
         * */
        public SaveTask(Object object, String filename)
        {
            this.object = object;
            this.filename = filename;
        }

        @Override
        public void run() {
            try
            {
                File file = new File(JotiApp.getContext().getFilesDir(), filename);
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(new Gson().toJson(object));
                fileWriter.flush();
                fileWriter.close();
            }
            catch(Exception e)
            {
                Log.e("AppData", "Error occured", e);
            }
        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 12-2-2016
     * Class for saving a drawable.
     */
    public static class SaveDrawableTask implements Runnable
    {

        private Drawable drawable;

        private String filename;

        public SaveDrawableTask(Drawable drawable, String filename)
        {
            this.drawable = drawable;
            this.filename = filename;
        }

        @Override
        public void run() {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(new File(JotiApp.getContext().getFilesDir(), filename));
                ((BitmapDrawable)drawable).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

}
