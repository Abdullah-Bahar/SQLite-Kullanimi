package com.bahar.sqlitekullanimi;

import android.net.Uri;

public class Calisanlar
{
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private byte[] imageBytes;

    public Calisanlar(int id, String firstName, String lastName, String email, byte[] imageBytes) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.imageBytes = imageBytes;
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

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
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
