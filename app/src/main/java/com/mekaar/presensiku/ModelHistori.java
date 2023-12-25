package com.mekaar.presensiku;

public class ModelHistori {

    String tanggalPresensi, waktuPresensi, imageModel, lokasi, uid;

    public ModelHistori() {
    }

    public ModelHistori(String tanggalPresensi, String waktuPresensi, String imageModel, String lokasi, String uid) {
        this.tanggalPresensi = tanggalPresensi;
        this.waktuPresensi = waktuPresensi;
        this.imageModel = imageModel;
        this.lokasi = lokasi;
        this.uid = uid;
    }

    public String getTanggalPresensi() {
        return tanggalPresensi;
    }

    public void setTanggalPresensi(String tanggalPresensi) {
        this.tanggalPresensi = tanggalPresensi;
    }

    public String getWaktuPresensi() {
        return waktuPresensi;
    }

    public void setWaktuPresensi(String waktuPresensi) {
        this.waktuPresensi = waktuPresensi;
    }

    public String getImageModel() {
        return imageModel;
    }

    public void setImageModel(String imageModel) {
        this.imageModel = imageModel;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
