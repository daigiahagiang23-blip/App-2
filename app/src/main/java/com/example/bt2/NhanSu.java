package com.example.bt2;

// Lớp lưu thông tin 1 nhân sự
public class NhanSu {
    public String ma;
    public String hoTen;
    public int namSinh;
    public String gioiTinh;
    public String queQuan;
    public String trinhDo;
    public byte[] anh;      // ảnh lưu dạng BLOB, có thể null
    public long luong;
    public String phongBan;

    public NhanSu() {
    }

    public NhanSu(String ma, String hoTen, int namSinh, String gioiTinh, String queQuan,
                  String trinhDo, byte[] anh, long luong, String phongBan) {
        this.ma = ma;
        this.hoTen = hoTen;
        this.namSinh = namSinh;
        this.gioiTinh = gioiTinh;
        this.queQuan = queQuan;
        this.trinhDo = trinhDo;
        this.anh = anh;
        this.luong = luong;
        this.phongBan = phongBan;
    }
}
