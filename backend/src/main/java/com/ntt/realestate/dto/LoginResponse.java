package com.ntt.realestate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private boolean success;
    private String errorCode; // null, "MSG-LOGIN-INFOR-ERROR", "MSG-LOGIN-INFOR-NOT-ACTIVE"
    private String displayName;
}
