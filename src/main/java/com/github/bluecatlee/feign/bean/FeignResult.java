package com.github.bluecatlee.feign.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeignResult {

    private int code;

    private String message;

    private Object data;

}
