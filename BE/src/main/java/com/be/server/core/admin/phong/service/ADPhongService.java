package com.be.server.core.admin.phong.service;

import com.be.server.core.admin.phong.model.request.ADPhongSearchRequest;
import com.be.server.core.admin.phong.model.request.ADSavePhongRequest;
import com.be.server.core.common.base.ResponseObject;
import com.be.server.entity.Phong;

public interface ADPhongService {

    ResponseObject<?> getAllPhong(ADPhongSearchRequest request);

    ResponseObject<?> savePhong(ADSavePhongRequest phong);

    ResponseObject<?> getAllLoaiPhong();
//
//    ResponseObject<?> getPhongById(String id);
//
//    ResponseObject<?> roomUsageHistory(LsDatPhongRequest request);
//
//    ResponseObject<?> changeStatusPhong(String id);
}
