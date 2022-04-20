package com.example.vizeproje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vizeproje.Models.Users;
import com.example.vizeproje.databinding.ActivitySettingsBinding;
import com.example.vizeproje.databinding.HeaderBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding=ActivitySettingsBinding.inflate(getLayoutInflater());
       setContentView(binding.getRoot());

       storage=FirebaseStorage.getInstance();
       auth=FirebaseAuth.getInstance();
       database= FirebaseDatabase.getInstance();


      //TextView txt=findViewById(R.id.GizlilikSozlesmesi);
      //txt.setMovementMethod(LinkMovementMethod.getInstance());
        binding.GizlilikSozlesmesi.setMovementMethod(LinkMovementMethod.getInstance());
        binding.Hakkimizda.setMovementMethod(LinkMovementMethod.getInstance());
        binding.YardM.setMovementMethod(LinkMovementMethod.getInstance());

        binding.DavetEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent=new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody="Uygulamayı İndir Ve Arkadaşlarınla Konuşmaya Başla: https://drive.google.com/drive/folders/19sXJwwbauREdIX8HdBycOd_OrCtw0u_T?usp=sharing";
                String shareSub="https://drive.google.com/drive/folders/19sXJwwbauREdIX8HdBycOd_OrCtw0u_T?usp=sharing";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                myIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(myIntent,"Kullanarak Paylaş"));
            }
        });

        binding.Bildirimler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "YAKINDA KULLANIMA SUNULACAK", Toast.LENGTH_SHORT).show();
            }
        });


       binding.backarrow.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent myInt=new Intent(getApplicationContext(),MainActivity.class);
               startActivity(myInt);
           }
       });


       // Kullanıcı adını ve durum bilgisini veritabanına kaydeder.
       binding.saveButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(!binding.txtAbout.getText().toString().equals("") && !binding.txtUserNameSettings.getText().toString().equals("")){
                   String status= binding.txtAbout.getText().toString();
                   String username= binding.txtUserNameSettings.getText().toString();

                   HashMap<String,Object> obj= new HashMap<>();
                   obj.put("userName",username);
                   obj.put("status",status);

                   database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                           .updateChildren(obj);

                   Toast.makeText(SettingsActivity.this, "PROFİL GÜNCELLENDİ", Toast.LENGTH_SHORT).show();
               }
               else{
                   Toast.makeText(SettingsActivity.this, "Lütfen Kullanıcı Adı ve Durum Girin", Toast.LENGTH_SHORT).show();
               }
           }
       });

       // profil resmini getirir
       database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
               .addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       Users users= snapshot.getValue(Users.class);
                       Picasso.get()
                               .load(users.getProfilePic())
                               .placeholder(R.drawable.user)
                               .into(binding.profileImageSettings);

                       binding.txtUserNameSettings.setText(users.getUserName());
                       binding.txtAbout.setText(users.getStatus());
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });

       binding.plus.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent=new Intent();
               intent.setAction(Intent.ACTION_GET_CONTENT);
               intent.setType("image/*");
               startActivityForResult(intent,25);
           }
       });
    }

    // profil resimi eklemek için
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==25 && data !=null && data.getData() != null){
            Uri sFile=data.getData();
            binding.profileImageSettings.setImageURI(sFile);

            final StorageReference reference= storage.getReference().child("profile_pic")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .child("profilePic").setValue(uri.toString());
                        }
                    });
                }
            });
        }
        else Toast.makeText(this, "Resim Yüklenemedi", Toast.LENGTH_SHORT).show();

    }
}