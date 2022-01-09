package com.company.android.myapplication.Users;

public class Users {
    String nickname, image, uid, bio;

    public Users()
    {

    }

    public Users(String nickname, String name, String email, String search, String gender, String image, String surname, String uid) {
        this.nickname = nickname;
        this.image = image;
        this.uid = uid;
        this.bio = bio;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
