package com.be.server.entity;

import com.be.server.entity.base.PrimaryEntity;
import com.be.server.infrastructure.constant.RoomStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamicUpdate
@Entity
@Table(name = "phong")
public class Phong extends PrimaryEntity implements Serializable {

    @Column(name = "ma_phong")
    private String maPhong;

    @Column(name = "ten_phong")
    private String tenPhong;

    @Column(name = "loai_phong")
    private String loaiPhong;

    @Column(name = "gia_hien_tai")
    private String giaHienTai;

    @Column(name = "suc_chua")
    private String sucChua;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "trang_thai_phong")
    private RoomStatus trangThaiPhong;

}