package com.be.server.core.admin.lichsu.model.response;

import com.be.server.infrastructure.constant.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeTanResponse {
    private String datPhongId;
    private String hoTen;
    private String maPhong;
    private String tenPhong;
    private BigDecimal giaHienTai;
    private LocalDateTime thoiGianDat;
    private LocalDateTime thoiGianCheckIn;
    private LocalDateTime thoiGianCheckOut;
    private RoomStatus trangThaiPhong;
    private List<String> dichVuPhatSinh;

    public LeTanResponse(String datPhongId, String hoTen, String maPhong, LocalDateTime thoiGianDat, String tenPhong, BigDecimal giaHienTai, LocalDateTime thoiGianCheckIn, LocalDateTime thoiGianCheckOut, RoomStatus trangThaiPhong) {
        this.datPhongId = datPhongId;
        this.hoTen = hoTen;
        this.maPhong = maPhong;
        this.thoiGianDat = thoiGianDat;
        this.tenPhong = tenPhong;
        this.giaHienTai = giaHienTai;
        this.thoiGianCheckIn = thoiGianCheckIn;
        this.thoiGianCheckOut = thoiGianCheckOut;
        this.trangThaiPhong = trangThaiPhong;
    }
}
