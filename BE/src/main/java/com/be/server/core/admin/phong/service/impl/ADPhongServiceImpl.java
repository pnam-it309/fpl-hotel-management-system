package com.be.server.core.admin.phong.service.impl;

import com.be.server.core.admin.phong.model.request.ADPhongSearchRequest;
import com.be.server.core.admin.phong.model.request.ADSavePhongRequest;
import com.be.server.core.admin.phong.repository.ADLoaiPhongRepository;
import com.be.server.core.admin.phong.repository.ADPhongRepository;
import com.be.server.core.admin.phong.repository.LsDatPhongRepository;
import com.be.server.core.admin.phong.repository.LsDichVuPhatSinhRepository;
import com.be.server.core.admin.phong.service.ADPhongService;
import com.be.server.core.common.base.PageableObject;
import com.be.server.core.common.base.ResponseObject;
import com.be.server.entity.LoaiPhong;
import com.be.server.entity.Phong;
import com.be.server.infrastructure.constant.EnLoaiPhong;
import com.be.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ADPhongServiceImpl implements ADPhongService {

    private final ADPhongRepository adPhongRepository;

    private final LsDatPhongRepository lsDatPhongRepository;

    private final LsDichVuPhatSinhRepository dichVuPhatSinhRepository;

    private final ADLoaiPhongRepository adLoaiPhongRepository;

    @Override
    public ResponseObject<?> getAllPhong(ADPhongSearchRequest request) {

        System.out.println(adPhongRepository.getAllPhong(Helper.createPageable(request), request));

        return ResponseObject.successForward(
                PageableObject.of(adPhongRepository.getAllPhong(
                        Helper.createPageable(request), request
                )), "lấy danh sách thành công"
        );

    }

//    private boolean hasFilters(ADPhongSearchRequest request) {
//        return (request.getQ() != null && !request.getQ().trim().isEmpty()) ||
//                request.getLoaiPhong() != null ||
//                request.getTrangThaiPhong() != null ||
//                request.getGiaMin() != null ||
//                request.getGiaMax() != null ||
//                request.getSucChuaMin() != null ||
//                request.getSucChuaMax() != null;
//    }
//
//    @Override
//    public ResponseObject<?> getPhongById(String id) {
//        try {
//            return adPhongRepository.findByIdProjection(id)
//                    .map(phong -> new ResponseObject<>(
//                            phong,
//                            HttpStatus.OK,
//                            "Lấy thông tin phòng thành công"
//                    ))
//                    .orElseGet(() -> new ResponseObject<>(
//                            null,
//                            HttpStatus.NOT_FOUND,
//                            "Không tìm thấy phòng"
//                    ));
//
//        } catch (Exception e) {
//            log.error("Lỗi khi lấy thông tin phòng: ", e);
//            return new ResponseObject<>(
//                    null,
//                    HttpStatus.INTERNAL_SERVER_ERROR,
//                    "Không thể tải thông tin phòng"
//            );
//        }
//    }
//
//    @Override
//    public ResponseObject<?> roomUsageHistory(LsDatPhongRequest request) {
//        Pageable pageable = Helper.createPageable(request, "created_date");
//        Page<LeTanResponse> page = lsDatPhongRepository.roomUsageHistory(
//                request.getTenKhachHang(),
//                request.getTuNgay(),
//                request.getDenNgay(),
//                pageable
//        );
//        page.forEach(item -> {
//            List<DichVuPhatSinh> dichVus = dichVuPhatSinhRepository.findByDatPhongId(item.getDatPhongId());
//            List<String> tenDichVus = dichVus.stream()
//                    .map(DichVuPhatSinh::getTenDichVu)
//                    .collect(Collectors.toList());
//            item.setDichVuPhatSinh(tenDichVus);
//            BigDecimal tongThanhTien = dichVus.stream()
//                    .map(DichVuPhatSinh::getThanhTien)
//                    .filter(Objects::nonNull)
//                    .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//            item.setTongTienPhatSinh(tongThanhTien);
//        });
//
//        return new ResponseObject<>(
//                PageableObject.of(page),
//                HttpStatus.OK,
//                "Lấy thành công lịch sử đặt phòng"
//        );
//    }
//
//    @Override
//    public ResponseObject<?> changeStatusPhong(String id) {
//        Optional<Phong> existingPhong = adPhongRepository.findById(id);
//        if (existingPhong.isPresent()) {
//            Phong phong = existingPhong.get();
//            if (phong.getStatus().equals(EntityStatus.INACTIVE)) {
//                phong.setStatus(EntityStatus.ACTIVE);
//            } else {
//                if(phong.getTrangThaiPhong().equals(RoomStatus.TRONG)) {
//                    phong.setStatus(EntityStatus.INACTIVE);
//                }
//                else if (phong.getTrangThaiPhong().equals(RoomStatus.DANG_SU_DUNG)) {
//                    return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Phòng đang được sử dụng!");
//                }
//                else if (phong.getTrangThaiPhong().equals(RoomStatus.DA_DAT)) {
//                    return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Phòng đã được đặt!");
//                }
//            }
//            adPhongRepository.save(phong);
//            return new ResponseObject<>(null, HttpStatus.OK, "Thay đổi trạng thái phòng thành công!");
//        }
//        return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Phòng không tồn tại!");
//
//    }

    @Override
    public ResponseObject<?> savePhong(ADSavePhongRequest phong){
        Optional<Phong> maOptional = adPhongRepository.findByMa(phong.getMa());
        if(maOptional.isPresent()){
            return new ResponseObject<>(null , HttpStatus.OK , "Mã phòng này dã tồn tai");
        }

        Optional<Phong> tenOptional = adPhongRepository.findByMa(phong.getTen());
        if(tenOptional.isPresent()){
            return new ResponseObject<>(null , HttpStatus.OK , "Tên phòng này dã tồn tai");
        }
        Phong addPhong = new Phong();
        addPhong.setMa(phong.getMa());
        addPhong.setTen(phong.getTen());

        Optional<LoaiPhong> optionalLoaiPhong = adLoaiPhongRepository.findById(phong.getIdLoaiPhong());
        if(optionalLoaiPhong.isEmpty()){
            return new ResponseObject<>(null , HttpStatus.NOT_FOUND , "không tìm thấy phòng");
        }

        LoaiPhong loaiPhong = optionalLoaiPhong.get();
        if(loaiPhong.getLoaiPhong() == EnLoaiPhong.DON ){
            addPhong.setGiaHienTai(new BigDecimal(1000000));
        }else if(loaiPhong.getLoaiPhong() == EnLoaiPhong.DOI) {
            addPhong.setGiaHienTai(new BigDecimal(2000000));
        }else {
            addPhong.setGiaHienTai(new BigDecimal(3000000));
        }
        if(phong.getSucChua() <= 0){
            return new ResponseObject<>(null , HttpStatus.OK , "giá phòng không hợp lệ ");
        }
        addPhong.setLoaiPhong(loaiPhong);
        addPhong.setSucChua(phong.getSucChua());

        adPhongRepository.save(addPhong);

        return new ResponseObject<>(null , HttpStatus.OK , "thêm thành công");
    }

    @Override
    public ResponseObject<?> getAllLoaiPhong(){
        return new ResponseObject<>(adLoaiPhongRepository.getAllLoaiPhong() , HttpStatus.OK , "lấy thành công loại phòng");
    }
}
