package com.czm.wormforwb.service;

public interface EmailSendService {

    Boolean sendEmail(String title, String content, String getterEmail);

}
