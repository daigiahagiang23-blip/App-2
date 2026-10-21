package com.example.bt2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NhanSuAdapter.OnItemClick {

    private DatabaseHelper db;
    private EditText edtTimKiem;
    private Spinner spnSapXep;
    private ListView lvNhanSu;
    private TextView tvSoLuong;
    private Button btnThem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        edtTimKiem = findViewById(R.id.edtTimKiem);
        spnSapXep = findViewById(R.id.spnSapXep);
        lvNhanSu = findViewById(R.id.lvNhanSu);
        tvSoLuong = findViewById(R.id.tvSoLuong);
        btnThem = findViewById(R.id.btnThem);

        // Dữ liệu cho Spinner sắp xếp (thứ tự trùng với hằng số SORT_ trong DatabaseHelper)
        String[] kieuSapXep = {"Mặc định (theo mã)", "Lương tăng dần", "Lương giảm dần"};
        spnSapXep.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, kieuSapXep));

        // Chọn kiểu sắp xếp khác thì load lại danh sách
        spnSapXep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadDanhSach();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Gõ chữ vào ô tìm kiếm thì load lại danh sách
        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadDanhSach();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Bấm nút Thêm: mở màn hình thêm (không truyền mã)
        btnThem.setOnClickListener(v -> startActivity(new Intent(this, EditActivity.class)));
    }

    // Quay lại từ màn hình thêm/sửa thì load lại danh sách
    @Override
    protected void onResume() {
        super.onResume();
        loadDanhSach();
    }

    private void loadDanhSach() {
        String tuKhoa = edtTimKiem.getText().toString();
        int kieu = spnSapXep.getSelectedItemPosition();

        List<NhanSu> ds = db.layDanhSach(tuKhoa, kieu);
        lvNhanSu.setAdapter(new NhanSuAdapter(this, ds, this));
        tvSoLuong.setText("Tổng số: " + ds.size() + " nhân sự");
    }

    @Override
    public void onSua(NhanSu ns) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("ma", ns.ma);
        startActivity(intent);
    }

    @Override
    public void onXoa(NhanSu ns) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.thong_tin_sv))
                .setMessage("Bạn có chắc muốn xóa nhân sự " + ns.hoTen + " không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    db.xoa(ns.ma);
                    Toast.makeText(this, "Đã xóa " + ns.hoTen, Toast.LENGTH_SHORT).show();
                    loadDanhSach();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
