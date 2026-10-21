package com.example.bt2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;

// Màn hình dùng chung cho Thêm và Sửa
public class EditActivity extends AppCompatActivity {

    private final String[] dsTrinhDo = {"Trung cấp", "Cao đẳng", "Đại học", "Thạc sĩ", "Tiến sĩ"};
    private final String[] dsPhongBan = {"Hành chính", "Phần mềm", "Kỹ thuật", "Dự án"};

    private DatabaseHelper db;
    private TextView tvTieuDe;
    private ImageView imgAnh;
    private EditText edtMa, edtHoTen, edtNamSinh, edtQueQuan, edtLuong;
    private RadioButton rbNam, rbNu;
    private Spinner spnTrinhDo, spnPhongBan;

    private byte[] anhHienTai = null;   // ảnh đang chọn
    private String maCu = null;         // khác null nghĩa là đang ở chế độ Sửa

    // Mở thư viện ảnh để chọn
    private final ActivityResultLauncher<String> chonAnhLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    docAnh(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        db = new DatabaseHelper(this);

        tvTieuDe = findViewById(R.id.tvTieuDe);
        imgAnh = findViewById(R.id.imgAnh);
        edtMa = findViewById(R.id.edtMa);
        edtHoTen = findViewById(R.id.edtHoTen);
        edtNamSinh = findViewById(R.id.edtNamSinh);
        edtQueQuan = findViewById(R.id.edtQueQuan);
        edtLuong = findViewById(R.id.edtLuong);
        rbNam = findViewById(R.id.rbNam);
        rbNu = findViewById(R.id.rbNu);
        spnTrinhDo = findViewById(R.id.spnTrinhDo);
        spnPhongBan = findViewById(R.id.spnPhongBan);
        Button btnChonAnh = findViewById(R.id.btnChonAnh);
        Button btnLuu = findViewById(R.id.btnLuu);
        Button btnHuy = findViewById(R.id.btnHuy);

        spnTrinhDo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, dsTrinhDo));
        spnPhongBan.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, dsPhongBan));
        imgAnh.setImageResource(android.R.drawable.ic_menu_myplaces);

        // Có truyền mã sang -> chế độ Sửa, đổ dữ liệu cũ lên form
        maCu = getIntent().getStringExtra("ma");
        if (maCu == null) {
            tvTieuDe.setText("THÊM NHÂN SỰ");
        } else {
            tvTieuDe.setText("SỬA NHÂN SỰ");
            hienThiDuLieuCu();
        }

        btnChonAnh.setOnClickListener(v -> chonAnhLauncher.launch("image/*"));
        btnLuu.setOnClickListener(v -> luu());
        btnHuy.setOnClickListener(v -> finish());
    }

    private void hienThiDuLieuCu() {
        NhanSu ns = db.layTheoMa(maCu);
        if (ns == null) {
            finish();
            return;
        }
        edtMa.setText(ns.ma);
        edtMa.setEnabled(false);    // không cho sửa mã vì mã là khóa chính
        edtHoTen.setText(ns.hoTen);
        edtNamSinh.setText(String.valueOf(ns.namSinh));
        edtQueQuan.setText(ns.queQuan);
        edtLuong.setText(String.valueOf(ns.luong));
        if (ns.gioiTinh.equals("Nữ")) {
            rbNu.setChecked(true);
        } else {
            rbNam.setChecked(true);
        }
        spnTrinhDo.setSelection(viTri(dsTrinhDo, ns.trinhDo));
        spnPhongBan.setSelection(viTri(dsPhongBan, ns.phongBan));

        anhHienTai = ns.anh;
        if (anhHienTai != null) {
            imgAnh.setImageBitmap(BitmapFactory.decodeByteArray(anhHienTai, 0, anhHienTai.length));
        }
    }

    private int viTri(String[] mang, String giaTri) {
        for (int i = 0; i < mang.length; i++) {
            if (mang[i].equals(giaTri)) {
                return i;
            }
        }
        return 0;
    }

    // Đọc ảnh từ thư viện, thu nhỏ rồi chuyển thành byte[] để lưu vào SQLite
    private void docAnh(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            Bitmap goc = BitmapFactory.decodeStream(is);
            is.close();

            Bitmap nho = Bitmap.createScaledBitmap(goc, 200, 200, true);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            nho.compress(Bitmap.CompressFormat.JPEG, 85, os);
            anhHienTai = os.toByteArray();
            imgAnh.setImageBitmap(nho);
        } catch (Exception e) {
            Toast.makeText(this, "Không đọc được ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void luu() {
        String ma = edtMa.getText().toString().trim();
        String hoTen = edtHoTen.getText().toString().trim();
        String namSinhStr = edtNamSinh.getText().toString().trim();
        String queQuan = edtQueQuan.getText().toString().trim();
        String luongStr = edtLuong.getText().toString().trim();

        // Kiểm tra dữ liệu nhập
        if (ma.isEmpty() || hoTen.isEmpty() || namSinhStr.isEmpty()
                || queQuan.isEmpty() || luongStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int namSinh;
        long luong;
        try {
            namSinh = Integer.parseInt(namSinhStr);
            luong = Long.parseLong(luongStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Năm sinh và lương phải là số", Toast.LENGTH_SHORT).show();
            return;
        }

        int namNay = Calendar.getInstance().get(Calendar.YEAR);
        if (namSinh < 1950 || namSinh > namNay - 18) {
            Toast.makeText(this, "Năm sinh không hợp lệ (phải từ 1950 và đủ 18 tuổi)",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (luong <= 0) {
            Toast.makeText(this, "Lương phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }

        String gioiTinh = rbNu.isChecked() ? "Nữ" : "Nam";
        String trinhDo = spnTrinhDo.getSelectedItem().toString();
        String phongBan = spnPhongBan.getSelectedItem().toString();

        NhanSu ns = new NhanSu(ma, hoTen, namSinh, gioiTinh, queQuan, trinhDo,
                anhHienTai, luong, phongBan);

        if (maCu == null) {
            // Thêm mới
            if (db.them(ns)) {
                Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Mã nhân sự đã tồn tại", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Sửa
            if (db.sua(ns)) {
                Toast.makeText(this, "Sửa thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Sửa thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
