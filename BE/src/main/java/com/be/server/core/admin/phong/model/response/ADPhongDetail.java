package com.be.server.core.admin.phong.model.response;

import com.be.server.core.common.base.IsIdentify;
import com.be.server.infrastructure.constant.TrangThaiHoatDong;

public interface ADPhongDetail extends IsIdentify {
    String getMa();
    String getTen();
    Integer getTang();
    String getIdLoaiPhong();
    String getTenLoaiPhong();
    Integer getSoNguoiToiDa();
    Integer getGiaCaNgay();
    TrangThaiHoatDong getTrangThaiHoatDong();
}

