package com.bahar.sqlitekullanimi;

import android.net.Uri;

public class Calisanlar
{
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String imgUri;

    public Calisanlar(int id, String firstName, String lastName, String email, String imgUri) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.imgUri = imgUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    @Override
    public String toString()
    {
        return id + " : " +
                firstName + " " +
                lastName + "\n" +
                "Email : " + email;
    }
}
