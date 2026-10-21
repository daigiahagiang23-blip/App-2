package com.example.bt2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

// Adapter đổ danh sách nhân sự vào ListView
public class NhanSuAdapter extends ArrayAdapter<NhanSu> {

    // Giao diện để MainActivity xử lý khi bấm Sửa / Xóa
    public interface OnItemClick {
        void onSua(NhanSu ns);

        void onXoa(NhanSu ns);
    }

    private final OnItemClick listener;

    public NhanSuAdapter(Context context, List<NhanSu> ds, OnItemClick listener) {
        super(context, 0, ds);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_nhan_su, parent, false);
        }

        final NhanSu ns = getItem(position);

        ImageView imgAnh = convertView.findViewById(R.id.imgAnh);
        TextView tvHoTen = convertView.findViewById(R.id.tvHoTen);
        TextView tvThongTin1 = convertView.findViewById(R.id.tvThongTin1);
        TextView tvThongTin2 = convertView.findViewById(R.id.tvThongTin2);
        TextView tvLuong = convertView.findViewById(R.id.tvLuong);
        Button btnSua = convertView.findViewById(R.id.btnSua);
        Button btnXoa = convertView.findViewById(R.id.btnXoa);

        tvHoTen.setText(ns.ma + " - " + ns.hoTen);
        tvThongTin1.setText(ns.gioiTinh + " | " + ns.namSinh + " | " + ns.queQuan);
        tvThongTin2.setText(ns.trinhDo + " | " + ns.phongBan);
        tvLuong.setText("Lương: " + new DecimalFormat("#,###").format(ns.luong) + " đ");

        // Có ảnh thì hiện, không có thì dùng ảnh mặc định
        if (ns.anh != null) {
            Bitmap bm = BitmapFactory.decodeByteArray(ns.anh, 0, ns.anh.length);
            imgAnh.setImageBitmap(bm);
        } else {
            imgAnh.setImageResource(android.R.drawable.ic_menu_myplaces);
        }

        btnSua.setOnClickListener(v -> listener.onSua(ns));
        btnXoa.setOnClickListener(v -> listener.onXoa(ns));

        return convertView;
    }
}
