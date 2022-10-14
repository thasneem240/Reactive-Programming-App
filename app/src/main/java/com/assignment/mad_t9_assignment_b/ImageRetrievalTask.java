package com.assignment.mad_t9_assignment_b;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ImageRetrievalTask implements Callable<Bitmap>
{
    private Activity uiActivity;
    private String data;
    private RemoteUtilities remoteUtilities;
    List<String> listOfImageUrl;

    ImageList imageList; // ImageList to Store All loaded Images


    public ImageRetrievalTask(Activity uiActivity)
    {
        remoteUtilities = RemoteUtilities.getInstance(uiActivity);
        this.uiActivity=uiActivity;
        this.data = null;

        listOfImageUrl = new ArrayList<>();
        imageList = ImageList.getInstance();
    }


    @Override
    public Bitmap call() throws Exception
    {
        Bitmap image = null;
        String endpoint = getEndpoint(this.data);

        if(endpoint==null)
        {
            uiActivity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(uiActivity,"No image found",Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {

           // image = getImageFromUrl(endpoint);

            /* Clear the Previous search images from image List */
            imageList.clear();

            /* Download the Image from given URL */

            for (String imageUrl:listOfImageUrl)
            {
                image = getImageFromUrl(imageUrl);
                imageList.addImage(image);
            }

            try
            {
                Thread.sleep(3000);
            }
            catch (Exception e)
            {

            }

        }
        return image;
    }


    /* Get End point And Add the Image Url to Image URL List */

    private String getEndpoint(String data)
    {
        String imageUrl = null;
        int countOfImageUrl = 0;

        try
        {
            JSONObject jBase = new JSONObject(data);
            JSONArray jHits = jBase.getJSONArray("hits");

            if(jHits.length()>0)
            {
                /* Load only 15 Images */

                if( jHits.length() > 15 )
                {
                    countOfImageUrl = 15;
                }
                else /* Load All Images */
                {
                    countOfImageUrl = jHits.length();
                }

                /* Add the Image Url to listOfImage URL */

                for (int i=0; i < countOfImageUrl; i++)
                {
                    JSONObject jHitsItem = jHits.getJSONObject(i);
                    imageUrl = jHitsItem.getString("previewURL");

                    listOfImageUrl.add(imageUrl);
                }

            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return imageUrl;
    }


    private Bitmap getImageFromUrl(String imageUrl)
    {
        Bitmap image = null;

        Uri.Builder url = Uri.parse(imageUrl).buildUpon();
        String urlString = url.build().toString();

        HttpURLConnection connection = remoteUtilities.openConnection(urlString);

        if(connection!=null)
        {
            if(remoteUtilities.isConnectionOkay(connection)==true)
            {
                image = getBitmapFromConnection(connection);

                connection.disconnect();
            }
        }

        return image;
    }

    public Bitmap getBitmapFromConnection(HttpURLConnection conn)
    {
        Bitmap data = null;

        try
        {
            InputStream inputStream = conn.getInputStream();
            byte[] byteData = getByteArrayFromInputStream(inputStream);

            data = BitmapFactory.decodeByteArray(byteData,0,byteData.length);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return data;
    }

    private byte[] getByteArrayFromInputStream(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1)
        {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }

    public void setData(String data)
    {
        this.data = data;
    }
}
