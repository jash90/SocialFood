package com.zimny.socialfood.model;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;


public class Group extends Info {
    private String uid;
    private ArrayList<UserRequest> users;
    private String admin;
    private ArrayList<Tag> tags;
    private String name;
    private String imageUpload;

    public Group() {
    }

    public Group(String uid, ArrayList<UserRequest> users, Address address, String uidAdmin, ArrayList<Tag> tags, String name, String imageUpload, int phone) {
        this.uid = uid;
        this.users = users;
        super.setAddress(address);
        super.setPhone(phone);
        this.admin = uidAdmin;
        this.tags = tags;
        this.name = name;
        this.imageUpload = imageUpload;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Exclude
    public ArrayList<UserRequest> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserRequest> users) {
        this.users = users;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getImageUpload() {
        return imageUpload;
    }

    public void setImageUpload(String imageUpload) {
        this.imageUpload = imageUpload;
    }

    @Exclude
    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }


    public String alltoString() {
        return "Group{" +
                "uid='" + uid + '\'' +
                ", users=" + users +
                ", admin='" + admin + '\'' +
                ", tags=" + tags +
                ", name='" + name + '\'' +
                ", imageUpload='" + imageUpload + '\'' +
                '}';
    }

    @Override
    public String toString() {
        return String.format("%s", name);
    }
}
