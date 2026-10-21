package com.example.bt2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// Lớp làm việc với SQLite
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "nhansu.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "NhanSu";

    // Kiểu sắp xếp theo lương
    public static final int SORT_MAC_DINH = 0;
    public static final int SORT_TANG = 1;
    public static final int SORT_GIAM = 2;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + "ma TEXT PRIMARY KEY, "
                + "hoTen TEXT, "
                + "namSinh INTEGER, "
                + "gioiTinh TEXT, "
                + "queQuan TEXT, "
                + "trinhDo TEXT, "
                + "anh BLOB, "
                + "luong INTEGER, "
                + "phongBan TEXT)");

        // Thêm sẵn một ít dữ liệu mẫu
        themMau(db, "NV001", "Nguyễn Văn Hùng", 1990, "Nam", "Hà Nội", "Đại học", 15000000, "Phần mềm");
        themMau(db, "NV002", "Trần Thị Mai Anh", 1994, "Nữ", "Hải Phòng", "Đại học", 12000000, "Hành chính");
        themMau(db, "NV003", "Lê Quang Minh", 1988, "Nam", "Đà Nẵng", "Thạc sĩ", 22000000, "Dự án");
        themMau(db, "NV004", "Phạm Thị Thu Hà", 1996, "Nữ", "Nghệ An", "Cao đẳng", 9500000, "Hành chính");
        themMau(db, "NV005", "Võ Hoàng Long", 1992, "Nam", "TP. Hồ Chí Minh", "Đại học", 18000000, "Phần mềm");
        themMau(db, "NV006", "Đặng Thị Ngọc Lan", 1991, "Nữ", "Huế", "Thạc sĩ", 20000000, "Dự án");
        themMau(db, "NV007", "Bùi Đức Thắng", 1985, "Nam", "Thanh Hóa", "Đại học", 16500000, "Kỹ thuật");
        themMau(db, "NV008", "Hoàng Thị Kim Ngân", 1998, "Nữ", "Cần Thơ", "Cao đẳng", 10000000, "Kỹ thuật");
        themMau(db, "NV009", "Ngô Thanh Tùng", 1993, "Nam", "Bình Định", "Đại học", 14000000, "Phần mềm");
        themMau(db, "NV010", "Đỗ Thị Hương Giang", 1989, "Nữ", "Nam Định", "Tiến sĩ", 30000000, "Dự án");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    private void themMau(SQLiteDatabase db, String ma, String hoTen, int namSinh, String gioiTinh,
                         String queQuan, String trinhDo, long luong, String phongBan) {
        db.insert(TABLE, null, taoValues(new NhanSu(ma, hoTen, namSinh, gioiTinh, queQuan,
                trinhDo, null, luong, phongBan)));
    }

    private ContentValues taoValues(NhanSu ns) {
        ContentValues v = new ContentValues();
        v.put("ma", ns.ma);
        v.put("hoTen", ns.hoTen);
        v.put("namSinh", ns.namSinh);
        v.put("gioiTinh", ns.gioiTinh);
        v.put("queQuan", ns.queQuan);
        v.put("trinhDo", ns.trinhDo);
        v.put("anh", ns.anh);
        v.put("luong", ns.luong);
        v.put("phongBan", ns.phongBan);
        return v;
    }

    // Thêm: trả về false nếu bị trùng mã
    public boolean them(NhanSu ns) {
        long kq = getWritableDatabase().insert(TABLE, null, taoValues(ns));
        return kq != -1;
    }

    // Sửa theo mã
    public boolean sua(NhanSu ns) {
        int kq = getWritableDatabase().update(TABLE, taoValues(ns), "ma = ?", new String[]{ns.ma});
        return kq > 0;
    }

    // Xóa theo mã
    public boolean xoa(String ma) {
        int kq = getWritableDatabase().delete(TABLE, "ma = ?", new String[]{ma});
        return kq > 0;
    }

    // Lấy 1 nhân sự theo mã
    public NhanSu layTheoMa(String ma) {
        Cursor c = getReadableDatabase().query(TABLE, null, "ma = ?", new String[]{ma},
                null, null, null);
        NhanSu ns = null;
        if (c.moveToFirst()) {
            ns = docDong(c);
        }
        c.close();
        return ns;
    }

    // Lấy danh sách: tìm kiếm theo từ khóa (mã, họ tên, quê quán, phòng ban) và sắp xếp theo lương
    public List<NhanSu> layDanhSach(String tuKhoa, int kieuSapXep) {
        String where = null;
        String[] args = null;
        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            where = "ma LIKE ? OR hoTen LIKE ? OR queQuan LIKE ? OR phongBan LIKE ?";
            String k = "%" + tuKhoa.trim() + "%";
            args = new String[]{k, k, k, k};
        }

        String orderBy = "ma ASC";
        if (kieuSapXep == SORT_TANG) {
            orderBy = "luong ASC";
        } else if (kieuSapXep == SORT_GIAM) {
            orderBy = "luong DESC";
        }

        List<NhanSu> ds = new ArrayList<>();
        Cursor c = getReadableDatabase().query(TABLE, null, where, args, null, null, orderBy);
        while (c.moveToNext()) {
            ds.add(docDong(c));
        }
        c.close();
        return ds;
    }

    private NhanSu docDong(Cursor c) {
        NhanSu ns = new NhanSu();
        ns.ma = c.getString(c.getColumnIndexOrThrow("ma"));
        ns.hoTen = c.getString(c.getColumnIndexOrThrow("hoTen"));
        ns.namSinh = c.getInt(c.getColumnIndexOrThrow("namSinh"));
        ns.gioiTinh = c.getString(c.getColumnIndexOrThrow("gioiTinh"));
        ns.queQuan = c.getString(c.getColumnIndexOrThrow("queQuan"));
        ns.trinhDo = c.getString(c.getColumnIndexOrThrow("trinhDo"));
        ns.anh = c.getBlob(c.getColumnIndexOrThrow("anh"));
        ns.luong = c.getLong(c.getColumnIndexOrThrow("luong"));
        ns.phongBan = c.getString(c.getColumnIndexOrThrow("phongBan"));
        return ns;
    }
}
