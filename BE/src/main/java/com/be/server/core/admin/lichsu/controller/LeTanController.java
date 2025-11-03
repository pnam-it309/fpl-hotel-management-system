package com.be.server.core.admin.lichsu.controller;


import com.be.server.core.admin.lichsu.model.request.LsDatPhongRequest;
import com.be.server.core.admin.lichsu.service.LsDatPhongService;
import com.be.server.infrastructure.constant.MappingConstants;
import com.be.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_ROOM_USAGE_HISTORY)
@Slf4j
public class LeTanController {
    private final LsDatPhongService lsDatPhongService;

    @GetMapping("/history")
    public ResponseEntity<?> getAllRoomUsageHistory(LsDatPhongRequest request){
        return Helper.createResponseEntity(lsDatPhongService.roomUsageHistory(request));
    }
}
