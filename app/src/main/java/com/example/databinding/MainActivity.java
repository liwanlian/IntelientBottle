package com.example.databinding;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.databinding.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private String url="https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=4138850978,2612460506&fm=200&gp=0.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        User user = new User("Micheal", "Jack",url);
        binding.setUser(user);

    }
    @BindingAdapter({"imageUrl"})
    public static void loadimage(ImageView imageView, String url){
        Log.e("TAG",url+"   -走到这里了11");
        Glide.with(imageView.getContext()).load(url)
               .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }


}

