package com.be.server.core.admin.phong.service.impl;

import com.be.server.core.admin.phong.model.request.ADPhongSearchRequest;
import com.be.server.core.admin.phong.model.request.ADSavePhongRequest;
import com.be.server.core.admin.phong.model.request.ADUpdatePhongRequest;
import com.be.server.core.admin.phong.model.response.ADPhongDetail;
import com.be.server.core.admin.phong.repository.ADLoaiPhongRepository;
import com.be.server.core.admin.phong.repository.ADPhongRepository;
import com.be.server.core.admin.phong.service.ADPhongService;
import com.be.server.core.common.base.PageableObject;
import com.be.server.core.common.base.ResponseObject;
import com.be.server.entity.LoaiPhong;
import com.be.server.entity.Phong;
import com.be.server.infrastructure.constant.*;
import com.be.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ADPhongServiceImpl implements ADPhongService {

    private final ADPhongRepository adPhongRepository;

    private final ADLoaiPhongRepository adLoaiPhongRepository;

    @Override
    public ResponseObject<?> getAllPhong(ADPhongSearchRequest request) {

        if (request.getPage() <= 0) {
            request.setPage(1);
        }

        return ResponseObject.successForward(
                PageableObject.of(adPhongRepository.getAllPhong(
                        request.getTuKhoa(),
                        request.getLoaiPhong(),
                        request.getTrangThaiHoatDong() != null ? request.getTrangThaiHoatDong() : null,
                        request.getGiaMin(),
                        request.getGiaMax(),
                        request.getSucChuaMin(),
                        request.getSucChuaMax(),
                        request.getTang(),
                        Helper.createPageable(request, "createDate")
                )),
                "Lấy danh sách thành công"
        );

    }

    //    @Override
//    public ResponseObject<?> changeStatusPhong(String id) {
//        Optional<Phong> existingPhong = adPhongRepository.findById(id);
//        if (existingPhong.isPresent()) {
//            Phong phong = existingPhong.get();
//            if (phong.getStatus().equals(EntityStatus.INACTIVE)) {
//                phong.setStatus(EntityStatus.ACTIVE);
//            } else {
//                if(phong.getTrangThaiPhong().equals(TrangThaiPhong.TRONG)) {
//                    phong.setStatus(EntityStatus.INACTIVE);
//                }
//                else if (phong.getTrangThaiPhong().equals(TrangThaiPhong.DANG_SU_DUNG)) {
//                    return new ResponseObject<>(null, HttpStatus.CONFLICT, "Không thể xóa phòng, phòng đang được sử dụng!");
//                }
//                else if (phong.getTrangThaiPhong().equals(TrangThaiPhong.DA_DAT)) {
//                    return new ResponseObject<>(null, HttpStatus.CONFLICT, "Không thể xóa phòng, phòng đã được đặt!");
//                }
//                else if (phong.getTrangThaiPhong().equals(TrangThaiPhong.DANG_DON)) {
//                    return new ResponseObject<>(null, HttpStatus.CONFLICT, "Không thể xóa phòng, phòng đang được dọn !");
//                }
//                else if (phong.getTrangThaiPhong().equals(TrangThaiPhong.BAO_TRI)) {
//                    return new ResponseObject<>(null, HttpStatus.CONFLICT, "Không thể xóa phòng, phòng đang được bảo trì !");
//                }
//                else if (phong.getTrangThaiPhong().equals(TrangThaiPhong.TAM_KHOA)) {
//                    return new ResponseObject<>(null, HttpStatus.CONFLICT, "Không thể xóa phòng, phòng đang được tạm khóa !");
//                }
//            }
//            adPhongRepository.save(phong);
//            return new ResponseObject<>(null, HttpStatus.OK, "Xóa phòng thành công!");
//        }
//        return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Phòng không tồn tại!");
//
//    }
//
    @Override
    public ResponseObject<?> savePhong(ADSavePhongRequest phong) {


        Optional<Phong> tenOptional = adPhongRepository.findByTen(phong.getTen());
        if (tenOptional.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.OK, "Tên phòng này đã tồn tại");
        }

        Phong addPhong = new Phong();
        addPhong.setMa(phong.getTen());
        addPhong.setTen(phong.getTen());

        Optional<LoaiPhong> optionalLoaiPhong = adLoaiPhongRepository.findById(phong.getIdLoaiPhong());
        if (optionalLoaiPhong.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy loại phòng");
        }

        if (phong.getTang() < 1 || phong.getTang() > 3 ) {
            return new ResponseObject<>(null, HttpStatus.OK, "Vị trí phòng không hợp lệ");
        }

        LoaiPhong loaiPhong = optionalLoaiPhong.get();

        addPhong.setTang(phong.getTang());
        addPhong.setTrangThaiHoatDong(TrangThaiHoatDong.DANG_HOAT_DONG);
        addPhong.setLoaiPhong(loaiPhong);

        adPhongRepository.save(addPhong);

        return new ResponseObject<>(null, HttpStatus.OK, "Thêm phòng thành công");
    }

    @Override
    public ResponseObject<?> getAllLoaiPhong(){
        return new ResponseObject<>(adLoaiPhongRepository.getAllLoaiPhong() , HttpStatus.OK , "lấy thành công loại phòng");
    }

    @Override
    public ResponseObject<?> updatePhong(String id, ADUpdatePhongRequest request) {
        Optional<Phong> existingPhong = adPhongRepository.findById(id);
        if (existingPhong.isEmpty()) {
            return ResponseObject.errorForward("Không tìm thấy phòng có id: " +id, HttpStatus.NOT_FOUND);
        }

        Phong existingPhong1 = existingPhong.get();

        Optional<Phong> tenOptional = adPhongRepository.findByTen(request.getTen());
        if (tenOptional.isPresent() && !tenOptional.get().getId().equals(id)) {
            return ResponseObject.errorForward("Tên phòng đã tồn tại", HttpStatus.CONFLICT);
        }

        if (request.getTang() < 1  || request.getTang() > 3){
            return ResponseObject.errorForward("Vị trí phòng không hợp lệ", HttpStatus.BAD_REQUEST);
        }

        Optional<LoaiPhong> optionalLoaiPhong = adLoaiPhongRepository.findById(request.getIdLoaiPhong());
        if (optionalLoaiPhong.isEmpty()) {
            return ResponseObject.errorForward("Không tìm thấy loại phòng", HttpStatus.NOT_FOUND);
        }

        existingPhong1.setMa(request.getTen());
        existingPhong1.setTen(request.getTen());
        existingPhong1.setTang(request.getTang());
        existingPhong1.setLoaiPhong(optionalLoaiPhong.get());
        existingPhong1.setTrangThaiHoatDong(request.getTrangThaiPhong());

        adPhongRepository.save(existingPhong1);

        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật phòng thành công");
    }


    @Override
    public ResponseObject<?> getPhongById(String id) {
        Optional<ADPhongDetail> phongDetail = adPhongRepository.getPhongDetailById(id);
        if(phongDetail.isEmpty()){
            return ResponseObject.errorForward("Không tìm thấy phòng có id: " + id, HttpStatus.NOT_FOUND);
        }


        return ResponseObject.successForward(phongDetail.get(), "Lấy thông tin phòng thành công");
    }
}