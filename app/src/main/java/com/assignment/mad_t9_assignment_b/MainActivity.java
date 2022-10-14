package com.assignment.mad_t9_assignment_b;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity
{

    private Button searchImage;
    private ProgressBar progressBar;
    private EditText searchKey;
    private RecyclerView imageRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        searchImage = findViewById(R.id.search_Image);
        progressBar = findViewById(R.id.progressBarId);
        searchKey = findViewById(R.id.inputSearch);
        imageRecyclerView = findViewById(R.id.imageRecyclerView);




        progressBar.setVisibility(View.INVISIBLE);

        searchImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                imageRecyclerView.setVisibility(View.INVISIBLE);
                searchImage();
            }
        });


    }

    public void searchImage()
    {
        Toast.makeText(MainActivity.this, "Searching starts", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);

        SearchTask searchTask = new SearchTask(MainActivity.this);
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
//                loadImage(s);
            }

            @Override
            public void onError(@NonNull Throwable e)
            {
                Toast.makeText(MainActivity.this, "Searching Error", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

//    public void loadImage(String response)
//    {
//        ImageRetrievalTask imageRetrievalTask = new ImageRetrievalTask(MainActivity.this);
//        imageRetrievalTask.setData(response);
//
//        Toast.makeText(MainActivity.this, "Image loading starts", Toast.LENGTH_SHORT).show();
//        progressBar.setVisibility(View.VISIBLE);
//        Single<Bitmap> searchObservable = Single.fromCallable(imageRetrievalTask);
//        searchObservable = searchObservable.subscribeOn(Schedulers.io());
//        searchObservable = searchObservable.observeOn(AndroidSchedulers.mainThread());
//        searchObservable.subscribe(new SingleObserver<Bitmap>()
//        {
//            @Override
//            public void onSubscribe(@NonNull Disposable d)
//            {
//
//            }
//
//            @Override
//            public void onSuccess(@NonNull Bitmap bitmap)
//            {
//                Toast.makeText(MainActivity.this, "Image loading Ends", Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.INVISIBLE);
//                picture.setVisibility(View.VISIBLE);
//                picture.setImageBitmap(bitmap);
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e)
//            {
//                Toast.makeText(MainActivity.this, "Image loading error, search again", Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.INVISIBLE);
//            }
//        });
//
//
//    }






}