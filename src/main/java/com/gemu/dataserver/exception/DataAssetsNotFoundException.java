package com.gemu.dataserver.exception;

/**
 * 数据资源地址不存在
 *
 * Created by gemu on 30/05/2017.
 */
public class DataAssetsNotFoundException extends CustomException {

    public DataAssetsNotFoundException(String message) {
        super(message);
    }
}
