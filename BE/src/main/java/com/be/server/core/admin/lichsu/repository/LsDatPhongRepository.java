package com.be.server.core.admin.lichsu.repository;

import com.be.server.core.admin.lichsu.model.response.LeTanResponse;
import com.be.server.repository.DatPhongRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LsDatPhongRepository extends DatPhongRepository {

    @Query("""
                SELECT new com.be.server.core.admin.lichsu.model.response.LeTanResponse(
                    u.id,
                    kh.hoTen,
                    u.maPhong,
                    u.thoiGianDat,
                    lt.tenLeTan,
                    u.giaHienTai,
                    u.thoiGianCheckIn,
                    u.thoiGianCheckOut,
                    u.trangThaiPhong
                )
                FROM DatPhong u
                LEFT JOIN LichSuDatPhong ls ON u.id = ls.datPhong.id
                LEFT JOIN LeTan lt ON ls.leTan.id = lt.id
                LEFT JOIN KhachHang kh ON u.khachHang.id = kh.id
                WHERE u.trangThaiPhong = 1
                AND (:tenKhachHang IS NULL OR kh.id = :tenKhachHang)
                          AND (:tuNgay IS NULL OR u.thoiGianCheckIn >= :tuNgay)
                          AND (:denNgay IS NULL OR u.thoiGianCheckOut <= :denNgay)
            """)
    Page<LeTanResponse> roomUsageHistory(
            @Param("tenKhachHang") String tenKhachHang,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            Pageable pageable);
}
