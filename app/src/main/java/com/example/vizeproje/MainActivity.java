package com.example.vizeproje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.vizeproje.Adapter.FragmentsAdapter;
import com.example.vizeproje.Fragments.CallsFragment;
import com.example.vizeproje.Models.Users;
import com.example.vizeproje.databinding.ActivityMainBinding;
import com.example.vizeproje.databinding.HeaderBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity{
    ActivityMainBinding binding;
    HeaderBinding headerBinding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth= FirebaseAuth.getInstance();

        binding.viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        ((MenuInflater) inflater).inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent=new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.lokasyon:
                Intent intent2=new Intent(getApplicationContext(),LocationActivity.class);
                startActivity(intent2);
                break;

            case R.id.menuDavet:
                Intent myIntent=new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody="Uygulamayı İndir Ve Arkadaşlarınla Konuşmaya Başla: https://drive.google.com/drive/folders/19sXJwwbauREdIX8HdBycOd_OrCtw0u_T?usp=sharing";
                String shareSub="https://drive.google.com/drive/folders/19sXJwwbauREdIX8HdBycOd_OrCtw0u_T?usp=sharing";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                myIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(myIntent,"Kullanarak Paylaş"));
                break;

            case R.id.logout:
                mAuth.signOut();
                Intent intent3=new Intent(getApplicationContext(),SignInActivity.class);
                startActivity(intent3);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}