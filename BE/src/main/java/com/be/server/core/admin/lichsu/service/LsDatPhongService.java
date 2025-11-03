package com.be.server.core.admin.lichsu.service;

import com.be.server.core.admin.lichsu.model.request.LsDatPhongRequest;
import com.be.server.core.common.base.ResponseObject;

public interface LsDatPhongService {
    ResponseObject<?> roomUsageHistory(LsDatPhongRequest request);
}
