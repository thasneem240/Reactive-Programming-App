package com.assignment.mad_t9_assignment_b;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity
{

    private ImageList imageList;
    private Button searchImage;
    private ProgressBar progressBar;
    private EditText searchKey;
    private RecyclerView imageRecyclerView;
    private Button singleColumnView;
    private Button doubleColumnView;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Get the instance of imageList (Singleton)*/
        imageList = ImageList.getInstance();

        searchImage = findViewById(R.id.search_Image);
        progressBar = findViewById(R.id.progressBarId);
        searchKey = findViewById(R.id.inputSearch);
        imageRecyclerView = (RecyclerView) findViewById(R.id.imageRecyclerView);
        singleColumnView = findViewById(R.id.singleColumnView);
        doubleColumnView = findViewById(R.id.doubleColumnView);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        progressBar.setVisibility(View.INVISIBLE);
        imageRecyclerView.setVisibility(View.INVISIBLE);
        singleColumnView.setVisibility(View.INVISIBLE);
        doubleColumnView.setVisibility(View.INVISIBLE);

        searchImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(searchKey.getText().toString().isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Empty Field!! Please Input Search key", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    imageRecyclerView.setVisibility(View.INVISIBLE);
                    searchImage();
                }

            }
        });


        singleColumnView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                singleColumnView.setVisibility(View.INVISIBLE);
                doubleColumnView.setVisibility(View.INVISIBLE);

                setUserRecyclerView("single");
            }
        });

        doubleColumnView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                doubleColumnView.setVisibility(View.INVISIBLE);
                singleColumnView.setVisibility(View.INVISIBLE);

                setUserRecyclerView("double");
            }
        });


    }

    public void searchImage()
    {
        Toast.makeText(MainActivity.this, "Searching starts", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);

        SearchTask searchTask = new SearchTask(MainActivity.this);

        // Get the Search Key
        searchTask.setSearchkey(searchKey.getText().toString());

        Single<String> searchObservable = Single.fromCallable(searchTask);
        searchObservable = searchObservable.subscribeOn(Schedulers.io());
        searchObservable = searchObservable.observeOn(AndroidSchedulers.mainThread());

        
        searchObservable.subscribe(new SingleObserver<String>()
        {
            @Override
            public void onSubscribe(@NonNull Disposable d)
            {
            }

            @Override
            public void onSuccess(@NonNull String s)
            {
                Toast.makeText(MainActivity.this, "Searching Ends", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

                // Load images from the response
                loadImage(s);
            }

            @Override
            public void onError(@NonNull Throwable e)
            {
                Toast.makeText(MainActivity.this, "Searching Error", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    public void loadImage(String response)
    {
        ImageRetrievalTask imageRetrievalTask = new ImageRetrievalTask(MainActivity.this);
        imageRetrievalTask.setData(response);

        Toast.makeText(MainActivity.this, "Image loading starts", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);

        Single<Bitmap> searchObservable = Single.fromCallable(imageRetrievalTask);
        searchObservable = searchObservable.subscribeOn(Schedulers.io());
        searchObservable = searchObservable.observeOn(AndroidSchedulers.mainThread());

        searchObservable.subscribe(new SingleObserver<Bitmap>()
        {
            @Override
            public void onSubscribe(@NonNull Disposable d)
            {

            }

            @Override
            public void onSuccess(@NonNull Bitmap bitmap)
            {
                Toast.makeText(MainActivity.this, "Image loading Ends", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

                singleColumnView.setVisibility(View.VISIBLE);
                doubleColumnView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(@NonNull Throwable e)
            {
                Toast.makeText(MainActivity.this, "Image loading error, search again", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });


    }


    /* Set the Recycler view */

    private void setUserRecyclerView(String option)
    {
        if(!imageList.isEmpty())
        {
            imageRecyclerView.setVisibility(View.VISIBLE);

            /* Single Column view*/
            if(option.equals("single"))
            {
                imageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            }
            else /* double column view*/
            {
                imageRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
            }


            //Create Adapter for the recyclerview
            MyAdapter adapter = new MyAdapter();

            // Hook it up
            imageRecyclerView.setAdapter(adapter);
        }
        else
        {
            Toast.makeText(this, "Empty Image List", Toast.LENGTH_SHORT).show();
        }
    }



    /* Private inner Class for View holder */

    private class MyDataVHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        Button buttonUpload;

        public MyDataVHolder(@androidx.annotation.NonNull View itemView)
        {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            buttonUpload = itemView.findViewById(R.id.buttonUpload);
        }
    }




    /* Private inner Class for Adapter */

    private class MyAdapter extends RecyclerView.Adapter<MyDataVHolder>
    {

        @androidx.annotation.NonNull
        @Override
        public MyDataVHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.view_holder_image,parent,false);

            MyDataVHolder myDataVHolder = new MyDataVHolder(view);

            return  myDataVHolder;
        }

        @Override
        public void onBindViewHolder(@androidx.annotation.NonNull MyDataVHolder holder, int position)
        {
            ImageView imageView = holder.imageView;
            Button buttonUpload = holder.buttonUpload;

            // Single Data
            Bitmap singleBitmapImage = imageList.get(position);

            // Set the ImageView
            imageView.setImageBitmap(singleBitmapImage);

            buttonUpload.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    uploadToCloud(imageView);
                }
            });

        }

        @Override
        public int getItemCount()
        {
            int count = imageList.size();
            return count;
        }
    }


    /* Part of this code is Taken From https://firebase.google.com/docs/storage/android/upload-files*/

    /* Upload Image to Firebase Cloud Storage */

    private void uploadToCloud(ImageView imageView)
    {
        /* Set the Progress Bar */

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image...");
        progressDialog.show();

        /* Create a reference to image */

        /* First option with random key for image name */

//        final String randomKey = UUID.randomUUID().toString();
//        StorageReference imgRef = storageReference.child("images/" + randomKey);


        /* Second Option with Data as image name */

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date dateNow = new Date();
        String fileName = simpleDateFormat.format(dateNow);
        StorageReference imgRef = storageReference.child("images/" + fileName);


        /* Get the data from an ImageView as bytes */

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();



        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        UploadTask uploadTask = imgRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                // Handle unsuccessful uploads
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Failed to Upload", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Image Successfully Uploaded", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onProgress(@androidx.annotation.NonNull UploadTask.TaskSnapshot snapshot)
            {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Percentage: " + (int) progressPercent + "%");
            }
        });


    }


}