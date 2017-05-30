package com.gemu.dataserver.exception;

/**
 * 实体来源不存在
 *
 * Created by gemu on 30/05/2017.
 */
public class EntityNotFoundException extends CustomException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
