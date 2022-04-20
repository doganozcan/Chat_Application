package com.example.vizeproje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vizeproje.databinding.ActivityPhoneSignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneSignInActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ActivityPhoneSignInBinding binding;

    private PhoneAuthProvider.ForceResendingToken forceResendingToken; // Kod gönderimi başarısız olursa kodu yeniden göndermek için kullanılır.
    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String  mVericationId; // doğrulama kodunu tutacak

    private static final String TAG="MAIN_TAG";

    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding=ActivityPhoneSignInBinding.inflate(getLayoutInflater());
       setContentView(binding.getRoot());

       firebaseAuth=FirebaseAuth.getInstance();

       binding.txtDogrulamaKod.setVisibility(View.INVISIBLE);
       binding.btnKodDogrula.setVisibility(View.INVISIBLE);

       pd=new ProgressDialog(this);
       pd.setTitle("Lütfen Bekleyin");
       pd.setCanceledOnTouchOutside(false);


       binding.lblHesapOlusturPhone.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent myInt=new Intent(getApplicationContext(),SignUpActivity.class);
           }
       });

       binding.MailGirisPhone.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent myIntent=new Intent(getApplicationContext(),SignInActivity.class);
               startActivity(myIntent);
           }
       });



       mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
           @Override
           public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
               signInWithPhoneAuthCredential(phoneAuthCredential);
           }

           @Override
           public void onVerificationFailed(@NonNull FirebaseException e) {
               pd.dismiss();
               Toast.makeText(PhoneSignInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
           }

           @Override
           public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
               super.onCodeSent(verificationId, forceResendingToken);

               Log.d(TAG,"onCodeSent: "+verificationId);
               mVericationId=verificationId;
               forceResendingToken=token;
               pd.dismiss();

               binding.txtTelefon.setEnabled(false);
               binding.btnKodGonder.setVisibility(View.INVISIBLE);

               binding.txtDogrulamaKod.setVisibility(View.VISIBLE);
               binding.btnKodDogrula.setVisibility(View.VISIBLE);

               Toast.makeText(PhoneSignInActivity.this, "Doğrulama Kodu Gönderildi", Toast.LENGTH_SHORT).show();
           }
       };

       binding.btnKodGonder.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String phone=binding.txtTelefon.getText().toString().trim();
               if(TextUtils.isEmpty(phone)){
                   Toast.makeText(PhoneSignInActivity.this, "Lütfen Telefon Numaranızı Girin", Toast.LENGTH_SHORT).show();
               }
               else
                   startPhoneNumberVerification(phone);
           }
       });

       binding.lblKodTekrarGonder.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String phone=binding.txtTelefon.getText().toString().trim();
               if(TextUtils.isEmpty(phone)){
                   Toast.makeText(PhoneSignInActivity.this, "Lütfen Telefon Numaranızı Girin", Toast.LENGTH_SHORT).show();
               }
               else
                   resendVerificationCode(phone,forceResendingToken);
           }
       });

       binding.btnKodDogrula.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String code=binding.txtDogrulamaKod.getText().toString().trim();
               if(TextUtils.isEmpty(code)){
                   Toast.makeText(PhoneSignInActivity.this, "Lütfen Doğrulama Kodunuzu Doğru Girin", Toast.LENGTH_SHORT).show();
               }
               else
                   verifyPhoneNumberWithCode(mVericationId,code);
           }
       });

    }

    private void startPhoneNumberVerification(String phone) {
        pd.setMessage("Telefon Numarası Doğrulanıyor");
        pd.show();

        PhoneAuthOptions options=
                PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber("+90"+phone)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void resendVerificationCode(String phone, PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Kod Tekrar Gönderiliyor");
        pd.show();

        PhoneAuthOptions options=
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L,TimeUnit.SECONDS)
                        .setActivity(this)
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        pd.setMessage("Kod Doğrulanıyor");
        pd.show();
        PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationId,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("Giriş Yapılıyor");
        pd.show();
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        pd.dismiss();
                        String phone =firebaseAuth.getCurrentUser().getPhoneNumber();
                        Toast.makeText(PhoneSignInActivity.this, "Giriş Yapıldı", Toast.LENGTH_SHORT).show();

                        Intent myInt=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(myInt);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(PhoneSignInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}