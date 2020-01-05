package com.example.apprutas.entities;

public class UserVo {

    String id;
    String name;
    String lastName;
    byte[] avatar;

    public UserVo(String id, String name, String lastName, byte[] avatar) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.avatar = avatar;
    }

    public UserVo(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }
}
