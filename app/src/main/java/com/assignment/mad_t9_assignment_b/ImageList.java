package com.assignment.mad_t9_assignment_b;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class ImageList
{
    private static ImageList imageList = null;
    private ArrayList<Bitmap> listOfImage = null;

    private ImageList()
    {
        listOfImage = new ArrayList<>();
    }

    /* Singleton */
    public static ImageList getInstance()
    {
        if(imageList == null)
        {
            imageList = new ImageList();
        }

        return imageList;
    }

    public void addImage(Bitmap bitmap)
    {
        listOfImage.add(bitmap);
    }

    public boolean isEmpty()
    {
        return  listOfImage.isEmpty();
    }

    public int size()
    {
        return  listOfImage.size();
    }

    public Bitmap get(int position)
    {
        return listOfImage.get(position);
    }

    public void clear()
    {
        listOfImage.clear();
    }
}
