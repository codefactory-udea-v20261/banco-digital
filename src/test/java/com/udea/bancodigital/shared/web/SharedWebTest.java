package com.udea.bancodigital.shared.web;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

class SharedWebTest {

    @Test
    void testApiResponseStaticMethods() {
        ApiResponse<String> ok = ApiResponse.ok("data");
        assertTrue(ok.isSuccess());
        assertEquals("data", ok.getData());
        assertNotNull(ok.getTimestamp());

        ApiResponse<String> okMsg = ApiResponse.ok("msg", "data");
        assertEquals("msg", okMsg.getMessage());

        ApiResponse<String> created = ApiResponse.created("data");
        assertEquals("Recurso creado exitosamente", created.getMessage());

        ApiError errorObj = ApiError.builder()
                .errorCode("ERR")
                .message("Error")
                .httpStatus(400)
                .details(Collections.singletonList("detail"))
                .traceId("trace-123")
                .build();
        
        ApiResponse<Object> error = ApiResponse.error(errorObj);
        assertFalse(error.isSuccess());
        assertEquals(errorObj, error.getError());
    }

    @Test
    void testApiErrorGetters() {
        ApiError error = ApiError.builder()
                .errorCode("CODE")
                .message("Message")
                .httpStatus(500)
                .traceId("ID")
                .build();
        
        assertEquals("CODE", error.getErrorCode());
        assertEquals("Message", error.getMessage());
        assertEquals(500, error.getHttpStatus());
        assertEquals("ID", error.getTraceId());
        assertNull(error.getDetails());
    }
}
