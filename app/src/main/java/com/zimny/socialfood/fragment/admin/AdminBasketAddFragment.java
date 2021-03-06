package com.zimny.socialfood.fragment.admin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zimny.socialfood.R;
import com.zimny.socialfood.adapter.FoodOrderAdminAddAdapter;
import com.zimny.socialfood.model.BaseOrder;
import com.zimny.socialfood.model.FoodOrder;
import com.zimny.socialfood.model.Group;
import com.zimny.socialfood.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AdminBasketAddFragment extends Fragment {
    @BindView(R.id.user)
    Spinner user;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.group)
    Spinner group;
    @BindView(R.id.save)
    Button save;
    ArrayList<User> users;
    ArrayList<Group> groups;
    ArrayList<FoodOrder> foodOrders;
    ArrayAdapter<User> usersAdapter;
    ArrayAdapter<Group> groupAdapter;
    FoodOrderAdminAddAdapter foodOrderAdminAddAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_basket_add, container, false);
        ButterKnife.bind(this, v);
        foodOrders = new ArrayList<>();
        users = new ArrayList<>();
        groups = new ArrayList<>();
        foodOrders.add(new FoodOrder());
        usersAdapter = new ArrayAdapter<User>(getContext(), android.R.layout.simple_spinner_item, users);
        groupAdapter = new ArrayAdapter<Group>(getContext(), android.R.layout.simple_spinner_item, groups);
        user.setAdapter(usersAdapter);
        group.setAdapter(groupAdapter);
        foodOrderAdminAddAdapter = new FoodOrderAdminAddAdapter(foodOrders);
        recyclerView.setAdapter(foodOrderAdminAddAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshots) {
                for (DataSnapshot dataSnapshot : dataSnapshots.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setUid(dataSnapshot.getKey());
                    users.add(user);
                    usersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshots) {
                for (DataSnapshot dataSnapshot : dataSnapshots.getChildren()) {
                    Group group = dataSnapshot.getValue(Group.class);
                    group.setUid(dataSnapshot.getKey());
                    groups.add(group);
                    groupAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseOrder baseOrder = new BaseOrder();
                baseOrder.setUidGroup(((Group) group.getSelectedItem()).getUid());
                databaseReference.child("baskets").child(((User) user.getSelectedItem()).getUid()).setValue(baseOrder);
                for (FoodOrder foodOrder : foodOrders) {
                    final Map<String, Object> foodOrders = new HashMap<>();
                    String uidfood = databaseReference.child("baskets").child(((User) user.getSelectedItem()).getUid()).child("foodOrders").push().getKey();
                    foodOrders.put("uidFood", foodOrder.getUid());
                    foodOrders.put("count", foodOrder.getCount());
                    databaseReference.child("baskets").child(((User) user.getSelectedItem()).getUid()).child("foodOrders").child(uidfood).setValue(foodOrders).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), foodOrders.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });


        return v;
    }


}
