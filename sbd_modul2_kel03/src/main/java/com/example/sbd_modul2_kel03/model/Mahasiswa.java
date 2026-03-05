package com.example.sbd_modul2_kel03.model;

public class Mahasiswa {
    private String nim;
    private String nama;
    private String angkatan;
    private String gender;
    private int isDeleted; // Tambahkan ini

    // Getter dan Setter
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAngkatan() { return angkatan; }
    public void setAngkatan(String angkatan) { this.angkatan = angkatan; }

    public int getIsDeleted() { return isDeleted; }
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }
}