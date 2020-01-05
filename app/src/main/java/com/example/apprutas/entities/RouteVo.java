package com.example.apprutas.entities;

public class RouteVo {
    String id;
    String city;
    String lat;
    String lng;
    String idUser;

    public RouteVo(String id, String city, String lat, String lng, String idUser) {
        this.id = id;
        this.city = city;
        this.lat = lat;
        this.lng = lng;
        this.idUser = idUser;
    }

    public RouteVo(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
