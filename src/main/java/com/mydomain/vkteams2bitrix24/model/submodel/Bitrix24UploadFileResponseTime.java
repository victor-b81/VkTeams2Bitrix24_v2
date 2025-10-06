package com.mydomain.vkteams2bitrix24.model.submodel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Bitrix24UploadFileResponseTime {
    public double start;
    public double finish;
    public double duration;
    public double processing;
    public Date date_start;
    public Date date_finish;
    public int operating_reset_at;
    public double operating;
}
