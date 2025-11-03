package com.be.server.core.admin.lichsu.service.impl;

import com.be.server.core.admin.lichsu.model.request.LsDatPhongRequest;
import com.be.server.core.admin.lichsu.model.response.LeTanResponse;
import com.be.server.core.admin.lichsu.repository.LsDatPhongRepository;
import com.be.server.core.admin.lichsu.repository.LsDichVuPhatSinhRepository;
import com.be.server.core.admin.lichsu.service.LsDatPhongService;
import com.be.server.core.common.base.PageableObject;
import com.be.server.core.common.base.ResponseObject;
import com.be.server.entity.DichVuPhatSinh;
import com.be.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LsDatPhongServiceImpl implements LsDatPhongService {
    private final LsDatPhongRepository lsDatPhongRepository;
    private final LsDichVuPhatSinhRepository dichVuPhatSinhRepository;

    @Override
    public ResponseObject<?> roomUsageHistory(LsDatPhongRequest request) {
        Pageable pageable = Helper.createPageable(request, "created_date");
        Page<LeTanResponse> page = lsDatPhongRepository.roomUsageHistory(
                request.getTenKhachHang(),
                request.getTuNgay(),
                request.getDenNgay(),
                pageable
        );
        page.forEach(item -> {
            List<String> list = dichVuPhatSinhRepository.findByDatPhongId(item.getDatPhongId())
                    .stream()
                    .map(DichVuPhatSinh::getTenDichVu)
                    .collect(Collectors.toList());
            item.setDichVuPhatSinh(list);
        });
        return new ResponseObject<>(
                PageableObject.of(page),
                HttpStatus.OK,
                "Lấy thành công lịch sử đặt phòng"
        );
    }
}
