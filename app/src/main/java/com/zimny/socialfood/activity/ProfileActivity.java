package com.zimny.socialfood.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.zimny.socialfood.R;
import com.zimny.socialfood.model.Address;
import com.zimny.socialfood.model.User;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_PICKER = 2000;
    @BindView(R.id.firstname)
    MaterialEditText firstname;
    @BindView(R.id.lastname)
    MaterialEditText lastname;
    @BindView(R.id.street)
    MaterialEditText street;
    @BindView(R.id.numberHouse)
    MaterialEditText numberHome;
    @BindView(R.id.numberBuilding)
    MaterialEditText numberBuilding;
    @BindView(R.id.city)
    MaterialEditText city;
    @BindView(R.id.postalCode)
    MaterialEditText postalCode;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.image)
    CircleImageView image;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    SharedPreferences sharedPreferences;

    SharedPreferences.Editor sharedPreferencesEditor;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    Bitmap userProfile = null;
    private User user;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("My Account");
        toolbar.setTitleTextColor(Color.WHITE);
        sharedPreferences = getSharedPreferences("Name", Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();
        final StorageReference imageRef = storageRef.child(String.format("%s.png", firebaseUser.getUid()));
        //  XLog.d(String.format("%s.png", firebaseUser.getUid()));
        storageRef.child(String.format("%s.png", firebaseUser.getUid())).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext())
                        .load(uri)
                        .into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //  Toast.makeText(getContext(),exception.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                //  XLog.d(exception.getLocalizedMessage());
            }
        });
        databaseReference.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {

                    if (user.getFirstname() != null) {
                        firstname.setText(user.getFirstname());
                    }

                    if (user.getLastname() != null) {
                        lastname.setText(user.getLastname());
                    }
                    if (user.getAddress() != null) {
                        if (user.getAddress().getNameStreet() != null) {
                            street.setText(user.getAddress().getNameStreet());
                        } else {
                            user.getAddress().setNameStreet("");
                        }
                        if (user.getAddress().getNumberBuilding() != null) {
                            numberBuilding.setText(user.getAddress().getNumberBuilding());
                        } else {
                            user.getAddress().setNumberBuilding("");
                        }
                        if (user.getAddress().getNumberHouse() != null) {
                            numberHome.setText(user.getAddress().getNumberHouse());
                        } else {
                            user.getAddress().setNumberHouse("");
                        }
                        if (user.getAddress().getCity() != null) {
                            city.setText(user.getAddress().getCity());
                        } else {
                            user.getAddress().setCity("");
                        }
                        if (user.getAddress().getPostalCode() != null) {
                            postalCode.setText(user.getAddress().getPostalCode());
                        } else {
                            user.getAddress().setPostalCode("");
                        }

                    } else {
                        Address address = new Address("", "", "", "", "");
                        user.setAddress(address);
                    }

                } else {
                    user = new User();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_PICKER);
            }
        });

//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                firebaseAuth.signOut();
//                Intent intent = new Intent(getContext(), LoginActivity.class);
//                startActivity(intent);
//            }
//        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!firstname.getText().toString().isEmpty()) {
                    user.setFirstname(firstname.getText().toString());
                }
                if (!lastname.getText().toString().isEmpty()) {
                    user.setLastname(lastname.getText().toString());
                }
                if (!street.getText().toString().isEmpty()) {
                    user.getAddress().setNameStreet(street.getText().toString());
                }
                if (!city.getText().toString().isEmpty()) {
                    user.getAddress().setCity(city.getText().toString());
                }
                if (!postalCode.getText().toString().isEmpty()) {
                    user.getAddress().setPostalCode(postalCode.getText().toString());
                }
                if (!numberBuilding.getText().toString().isEmpty()) {
                    user.getAddress().setNumberBuilding(numberBuilding.getText().toString());
                }
                if (!numberHome.getText().toString().isEmpty()) {
                    user.getAddress().setNumberHouse(numberHome.getText().toString());
                }
                databaseReference.child("friends").child(firebaseUser.getUid()).setValue(user);
                if (userProfile != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference mountainsRef = storageRef.child(String.format("%s.png", firebaseUser.getUid()));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    userProfile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setAction("changeImageProfile");
                            sendBroadcast(intent);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
                }
                Toast.makeText(getApplicationContext(), "Dane zmienione", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("save", false);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKER && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            try {
                Picasso.with(getApplicationContext())
                        .load(data.getData())
                        .into(image);

                userProfile = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                sharedPreferencesEditor.putString("image", data.getData().toString());
                sharedPreferencesEditor.commit();
            } catch (java.io.IOException e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


}
