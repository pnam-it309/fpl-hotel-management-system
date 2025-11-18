package com.be.server.core.admin.loaiphong.service;

import com.be.server.core.admin.loaiphong.model.request.ADSaveLoaiPhongRequest;
import com.be.server.core.common.base.ResponseObject;
import com.be.server.infrastructure.constant.EntityStatus;

public interface ADLoaiPhongService {

    ResponseObject<?> getAllLoaiPhong(String tuKhoa, EntityStatus trangThai);

    ResponseObject<?> saveLoaiPhong(ADSaveLoaiPhongRequest request);

    ResponseObject<?> updateLoaiPhong(String id, ADSaveLoaiPhongRequest request);

    ResponseObject<?> deleteLoaiPhong(String id);
}
