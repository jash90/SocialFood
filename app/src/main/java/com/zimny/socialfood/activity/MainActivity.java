package com.zimny.socialfood.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.zimny.socialfood.R;
import com.zimny.socialfood.fragment.MainFragment;
import com.zimny.socialfood.fragment.MyAccountFragment;
import com.zimny.socialfood.model.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ideo7 on 18.08.2017.
 */

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    User user;
    DatabaseReference databaseReference;
    ProfileDrawerItem profileDrawerItem;
    AccountHeader accountHeader;
    Bitmap userProfile = null;
    Drawer drawer;
    Boolean admin = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private IntentFilter intentFilter = new IntentFilter("changeImageProfile");
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageRef = firebaseStorage.getReference();
            StorageReference imageRef = storageRef.child(String.format("%s.png", firebaseUser.getUid()));
            final long ONE_MEGABYTE = 1024 * 1024;
            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    userProfile = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    profileDrawerItem.getIcon().setBitmap(userProfile);
                    accountHeader.updateProfile(profileDrawerItem);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    profileDrawerItem.getIcon().setBitmap(userProfile);
                    accountHeader.updateProfile(profileDrawerItem);
                }
            });
            //XLog.d("broadcast");

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("Name", Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        Intent intent = getIntent();
        admin = intent.getBooleanExtra("admin", false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        registerReceiver(broadcastReceiver, intentFilter);
        profileDrawerItem = new ProfileDrawerItem();
        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.primary)
                //        .addProfiles(new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(R.drawable.ic_app))
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }
        });
        Drawer.OnDrawerItemClickListener onDrawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                //XLog.d(position);
                if (admin) {
                    switch (position) {
                        case 1:
                            Intent intent1 = new Intent(MainActivity.this, AdminActivity.class);
                            intent1.putExtra("logout", false);
                            startActivity(intent1);
                            //finish();
                            drawer.closeDrawer();
                            return true;
                        case 2:
                            MainFragment mainFragment = new MainFragment();
                            ft.replace(R.id.content, mainFragment);
                            ft.commit();
                            drawer.closeDrawer();
                            return true;
                        case 14:
                            firebaseAuth.signOut();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.putExtra("logout", false);
                            startActivity(intent);
                            return true;
                        case 13:
                            MyAccountFragment myAccountFragment = new MyAccountFragment();
                            ft.replace(R.id.content, myAccountFragment);
                            ft.commit();
                            drawer.closeDrawer();
                            return true;
                        default:
                            return false;

                    }

                } else {
                    switch (position) {
                        case 1:
                            MainFragment mainFragment = new MainFragment();
                            ft.replace(R.id.content, mainFragment);
                            ft.commit();
                            drawer.closeDrawer();
                            return true;
                        case 13:
                            firebaseAuth.signOut();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.putExtra("logout", false);
                            startActivity(intent);
                            return true;
                        case 12:
                            MyAccountFragment myAccountFragment = new MyAccountFragment();
                            ft.replace(R.id.content, myAccountFragment);
                            ft.commit();
                            drawer.closeDrawer();
                            return true;
                        default:
                            return false;

                    }
                }
            }


        };
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(accountHeader)
                .withSavedInstance(savedInstanceState)
                .withDisplayBelowStatusBar(false)
                .withTranslucentStatusBar(false)
                .withToolbar(toolbar)
                .withDrawerItems(admin ? builderAdmin() : builderUser())
//                .addDrawerItems(
//                        new PrimaryDrawerItem().withName("Main").withIcon(GoogleMaterial.Icon.gmd_home),
//                        new DividerDrawerItem(),
//                        new PrimaryDrawerItem().withName("Favorites Restaurant").withIcon(GoogleMaterial.Icon.gmd_store),
//                        new PrimaryDrawerItem().withName("Favorites Food").withIcon(GoogleMaterial.Icon.gmd_restaurant_menu),
//                        new DividerDrawerItem(),
//                        new PrimaryDrawerItem().withName("Shopping cart").withIcon(GoogleMaterial.Icon.gmd_shopping_basket),
//                        new PrimaryDrawerItem().withName("Orders").withIcon(FontAwesome.Icon.faw_list_alt),
//                        new DividerDrawerItem(),
//                        new PrimaryDrawerItem().withName("Groups").withIcon(FontAwesome.Icon.faw_users),
//                        new PrimaryDrawerItem().withName("Friends").withIcon(FontAwesome.Icon.faw_user_circle_o).withBadge("20"),
//                        new DividerDrawerItem(),
//                        new PrimaryDrawerItem().withName("Settings").withIcon(GoogleMaterial.Icon.gmd_settings),
//                        new PrimaryDrawerItem().withName("Logout").withIcon(GoogleMaterial.Icon.gmd_exit_to_app)
//                )
                .withOnDrawerItemClickListener(onDrawerItemClickListener)
                .build();
        if (firebaseUser != null) {
            databaseReference.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getFirstname() != null && user.getLastname() != null) {
                            profileDrawerItem.withName(String.format("%s %s", user.getFirstname(), user.getLastname()));
                        }
                        if (user.getUsername() != null) {
                            profileDrawerItem.withEmail(user.getUsername());
                        }
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        StorageReference storageRef = firebaseStorage.getReference();
                        StorageReference imageRef = storageRef.child(String.format("%s.png", firebaseUser.getUid()));
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                profileDrawerItem.withIcon(uri);
                                accountHeader.addProfiles(profileDrawerItem);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                profileDrawerItem.withIcon(R.drawable.ic_app);
                                accountHeader.addProfiles(profileDrawerItem);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getBaseContext(), databaseError.getDetails(), Toast.LENGTH_SHORT).show();
                }
            });


        }
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
        toolbar.setSubtitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        toolbar.setNavigationIcon(new

                IconicsDrawable(this).
                icon(GoogleMaterial.Icon.gmd_menu).
                color(Color.WHITE).
                sizeDp(16));

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MainFragment mainFragment = new MainFragment();
        ft.replace(R.id.content, mainFragment);
        ft.commit();


    }

    @Override
    public void onResume() {
        registerReceiver(broadcastReceiver, intentFilter);
        super.onResume();
    }


    @Override
    public void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    public ArrayList<IDrawerItem> builderUser() {
        ArrayList<IDrawerItem> list = new ArrayList<>();
        list.add(new PrimaryDrawerItem().withName("Main").withIcon(GoogleMaterial.Icon.gmd_home));
        list.add(new DividerDrawerItem());
        list.add(new PrimaryDrawerItem().withName("Favorites Restaurant").withIcon(GoogleMaterial.Icon.gmd_store));
        list.add(new PrimaryDrawerItem().withName("Favorites Food").withIcon(GoogleMaterial.Icon.gmd_restaurant_menu));
        list.add(new DividerDrawerItem());
        list.add(new PrimaryDrawerItem().withName("Shopping cart").withIcon(GoogleMaterial.Icon.gmd_shopping_basket));
        list.add(new PrimaryDrawerItem().withName("Orders").withIcon(FontAwesome.Icon.faw_list_alt));
        list.add(new DividerDrawerItem());
        list.add(new PrimaryDrawerItem().withName("Groups").withIcon(FontAwesome.Icon.faw_users));
        list.add(new PrimaryDrawerItem().withName("Friends").withIcon(FontAwesome.Icon.faw_user_circle_o).withBadge("20"));
        list.add(new DividerDrawerItem());
        list.add(new PrimaryDrawerItem().withName("Settings").withIcon(GoogleMaterial.Icon.gmd_settings));
        list.add(new PrimaryDrawerItem().withName("Logout").withIcon(GoogleMaterial.Icon.gmd_exit_to_app));
        return list;
    }

    public ArrayList<IDrawerItem> builderAdmin() {
        ArrayList<IDrawerItem> list = new ArrayList<>();
        list.add(new PrimaryDrawerItem().withName("Admin").withIcon(FontAwesome.Icon.faw_user_secret));
        list.add(new PrimaryDrawerItem().withName("Main").withIcon(GoogleMaterial.Icon.gmd_home));
        list.add(new DividerDrawerItem());
        list.add(new PrimaryDrawerItem().withName("Favorites Restaurant").withIcon(GoogleMaterial.Icon.gmd_store));
        list.add(new PrimaryDrawerItem().withName("Favorites Food").withIcon(GoogleMaterial.Icon.gmd_restaurant_menu));
        list.add(new DividerDrawerItem());
        list.add(new PrimaryDrawerItem().withName("Shopping cart").withIcon(GoogleMaterial.Icon.gmd_shopping_basket));
        list.add(new PrimaryDrawerItem().withName("Orders").withIcon(FontAwesome.Icon.faw_list_alt));
        list.add(new DividerDrawerItem());
        list.add(new PrimaryDrawerItem().withName("Groups").withIcon(FontAwesome.Icon.faw_users));
        list.add(new PrimaryDrawerItem().withName("Friends").withIcon(FontAwesome.Icon.faw_user_circle_o).withBadge("20"));
        list.add(new DividerDrawerItem());
        list.add(new PrimaryDrawerItem().withName("Settings").withIcon(GoogleMaterial.Icon.gmd_settings));
        list.add(new PrimaryDrawerItem().withName("Logout").withIcon(GoogleMaterial.Icon.gmd_exit_to_app));
        return list;
    }

}
