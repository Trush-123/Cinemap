package com.example.cinemap;

public class User
{
    String Username;
    String Uid;
    String ProfileImage;

    public User(String username, String uid, String profileImage) {
        Username = username;
        Uid = uid;
        ProfileImage = profileImage;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }
}
