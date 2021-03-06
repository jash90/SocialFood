package com.zimny.socialfood.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.zimny.socialfood.R;
import com.zimny.socialfood.activity.details.UserDetailsActivity;
import com.zimny.socialfood.model.User;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by ideo7 on 10.10.2017.
 */

public class RequestFriendsAdapter extends RecyclerView.Adapter<RequestFriendsAdapter.ViewHolder> {
    ArrayList<User> users = new ArrayList<>();

    public RequestFriendsAdapter(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public RequestFriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_request_friend, parent, false);
        return new RequestFriendsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RequestFriendsAdapter.ViewHolder holder, int position) {
        final User user = users.get(position);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        holder.firstname.setText(user.getFirstname());
        holder.lastname.setText(user.getLastname());
        holder.city.setText(user.getAddress().getCity());
        holder.userImageCircle.setImageDrawable(new IconicsDrawable(holder.itemView.getContext()).icon(FontAwesome.Icon.faw_user_circle).sizeDp(40));
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference imageRef = storageReference.child(String.format("%s.png", user.getUid()));
        Glide.with(holder.itemView.getContext())
                .using(new FirebaseImageLoader())
                .load(imageRef)
                .asBitmap()
                .placeholder(new IconicsDrawable(holder.itemView.getContext()).icon(FontAwesome.Icon.faw_user_circle).sizeDp(40))
                .signature(new StringSignature(user.getImageUpload()))
                .into(holder.userImageCircle);
        if (user.getBirthday() != null) {
            LocalDate birthday2 = LocalDate.fromDateFields(user.getBirthday());
            Period period = new Period(birthday2, LocalDate.now(), PeriodType.years());
            holder.age.setVisibility(View.VISIBLE);
            holder.age.setText(String.format("%d y.o.", period.getYears()));
        } else {
            holder.age.setVisibility(View.GONE);
        }
        holder.userImageCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UserDetailsActivity.class);
                intent.putExtra("user", new Gson().toJson(user));
                view.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.userImageCircle)
        CircleImageView userImageCircle;
        @BindView(R.id.firstname)
        TextView firstname;
        @BindView(R.id.lastname)
        TextView lastname;
        @BindView(R.id.age)
        TextView age;
        @BindView(R.id.city)
        TextView city;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
