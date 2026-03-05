package com.example.sbd_modul2_kel03.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.sbd_modul2_kel03.model.Mahasiswa;
import com.example.sbd_modul2_kel03.model.IrsDetail;

@Controller
public class MahasiswaController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public String index(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        String sql;
        List<Mahasiswa> mahasiswa;
        
        // Filter is_deleted = 0 wajib ada di kedua kondisi agar data soft-delete tidak muncul
        if (keyword != null && !keyword.isEmpty()) {
            sql = "SELECT * FROM mahasiswa WHERE is_deleted = 0 AND (LOWER(nama) LIKE LOWER(?) OR nim LIKE ?)";
            mahasiswa = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Mahasiswa.class), "%"+keyword+"%", "%"+keyword+"%");
        } else {
            sql = "SELECT * FROM mahasiswa WHERE is_deleted = 0";
            mahasiswa = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Mahasiswa.class));
        }
        
        model.addAttribute("mahasiswa", mahasiswa);
        model.addAttribute("keyword", keyword);
        return "index";
    }

    @GetMapping("/detail/{nim}")
public String detail(@PathVariable("nim") String nim, Model model) {
    // 1. Ambil data profil mahasiswa
    String sqlMhs = "SELECT * FROM mahasiswa WHERE nim = ?";
    Mahasiswa mahasiswa = jdbcTemplate.queryForObject(sqlMhs, 
            BeanPropertyRowMapper.newInstance(Mahasiswa.class), nim);
    
    // 2. Query JOIN: Pastikan mk.matkul_nama dan mk.irs sesuai dengan tabel mata_kuliah
    // BeanPropertyRowMapper akan memetakan 'matkul_nama' ke 'setMatkulNama' di IrsDetail.java
    String sqlIrs = "SELECT mk.matkul_nama, mk.hari, mk.irs, i.status " +
                    "FROM irs i " +
                    "JOIN mata_kuliah mk ON i.matkul_id = mk.matkul_id " +
                    "WHERE i.nim = ?";
    
    List<IrsDetail> listIrs = jdbcTemplate.query(sqlIrs, 
            BeanPropertyRowMapper.newInstance(IrsDetail.class), nim);
    
    model.addAttribute("mahasiswa", mahasiswa);
    model.addAttribute("listIrs", listIrs);
    return "detail";
}

    @GetMapping("/trash")
    public String trash(Model model) {
        String sql = "SELECT * FROM mahasiswa WHERE is_deleted = 1";
        List<Mahasiswa> mahasiswa = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Mahasiswa.class));
        model.addAttribute("mahasiswa", mahasiswa);
        return "trash";
    }

    @GetMapping("/delete/{nim}")
    public String delete(@PathVariable("nim") String nim) {
        jdbcTemplate.update("UPDATE mahasiswa SET is_deleted = 1 WHERE nim = ?", nim);
        return "redirect:/";
    }
    
    @GetMapping("/restore/{nim}")
    public String restore(@PathVariable("nim") String nim) {
        jdbcTemplate.update("UPDATE mahasiswa SET is_deleted = 0 WHERE nim = ?", nim);
        return "redirect:/trash";
    }

    @GetMapping("/delete-permanent/{nim}")
    public String deletePermanent(@PathVariable("nim") String nim) {
        jdbcTemplate.update("DELETE FROM mahasiswa WHERE nim = ?", nim);
        return "redirect:/trash";
    }

    @GetMapping("/add")
    public String add(Model model) { return "add"; }

    @PostMapping("/add")
    public String add(Mahasiswa m) {
        jdbcTemplate.update("INSERT INTO mahasiswa (nim, nama, angkatan, gender, is_deleted) VALUES(?,?,?,?,0)", 
                m.getNim(), m.getNama(), m.getAngkatan(), m.getGender());
        return "redirect:/";
    }

    @GetMapping("/edit/{nim}")
    public String edit(@PathVariable("nim") String nim, Model model) {
        String sql = "SELECT * FROM mahasiswa WHERE nim = ?";
        Mahasiswa mahasiswa = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Mahasiswa.class), nim);
        model.addAttribute("mahasiswa", mahasiswa);
        return "edit";
    }

    @PostMapping("/edit")
    public String update(Mahasiswa m) {
        jdbcTemplate.update("UPDATE mahasiswa SET nama = ?, angkatan = ?, gender = ? WHERE nim = ?", 
                m.getNama(), m.getAngkatan(), m.getGender(), m.getNim());
        return "redirect:/";
    }
}