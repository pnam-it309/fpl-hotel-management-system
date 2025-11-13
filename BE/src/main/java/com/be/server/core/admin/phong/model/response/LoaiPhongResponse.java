package com.be.server.core.admin.phong.model.response;

import com.be.server.infrastructure.constant.EnLoaiPhong;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoaiPhongResponse {
    private String id;
    private EnLoaiPhong loaiPhong;
    private Integer soLuongNguoiToiDa;

    public LoaiPhongResponse(String id, EnLoaiPhong loaiPhong , Integer soLuongNguoiToiDa) {
        this.id = id;
        this.loaiPhong = loaiPhong;
        this.soLuongNguoiToiDa = soLuongNguoiToiDa;
    }
}
