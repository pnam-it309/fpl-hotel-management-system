package com.be.server.core.admin.phong.model.response;

import com.be.server.core.common.base.IsIdentify;

public interface TagResponse extends IsIdentify {
    String getMa();

    String getTen();

    String getMoTa();
}
