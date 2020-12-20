package com.example.project1;

public class User {
    private String phone;
    private String publicKey;

    public User(String phone, String publicKey) {
        this.phone = phone;
        this.publicKey = publicKey;
    }

    public String getPhone() {
        return phone;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

}
