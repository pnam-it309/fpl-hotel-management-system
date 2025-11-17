package com.be.server.core.admin.phong.model.response;

import com.be.server.infrastructure.constant.TrangThaiHoatDong;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhongResponse {
    private String id;
    private String ma;
    private String ten;
    private BigDecimal price;
    private Integer tang;
    private String loaiPhong;
    private Integer sucChua;
    private TrangThaiHoatDong trangThaiHoatDong;
    private List<TagInfo> tags;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagInfo {
        private String id;
        private String ma;
        private String ten;
    }
}