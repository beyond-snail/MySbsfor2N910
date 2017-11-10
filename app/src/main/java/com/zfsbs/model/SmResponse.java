package com.zfsbs.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/9 0009.
 */

public class SmResponse implements Serializable{

    private String mername; //商户名称
    private String merid; //商户号
    private String termid; //终端号
    private String acqno; //收单行
    private String iison; //发卡行
    private String expdate; //有效期
    private String authcode; //授权码
    private String priaccount;//卡号
    private String refernumber; //参考号
    private String batchno; //批次号
    private String systraceno; //凭证号
    private String orderid_scan; //订单号
    private String translocaltime; //交易时间
    private String translocaldate; //交易日期
    private String transamount; //交易金额
    private String pay_tp; //交易类型

    public String getMername() {
        return mername;
    }

    public void setMername(String mername) {
        this.mername = mername;
    }

    public String getMerid() {
        return merid;
    }

    public void setMerid(String merid) {
        this.merid = merid;
    }

    public String getTermid() {
        return termid;
    }

    public void setTermid(String termid) {
        this.termid = termid;
    }

    public String getAcqno() {
        return acqno;
    }

    public void setAcqno(String acqno) {
        this.acqno = acqno;
    }

    public String getIison() {
        return iison;
    }

    public void setIison(String iison) {
        this.iison = iison;
    }

    public String getExpdate() {
        return expdate;
    }

    public void setExpdate(String expdate) {
        this.expdate = expdate;
    }

    public String getAuthcode() {
        return authcode;
    }

    public void setAuthcode(String authcode) {
        this.authcode = authcode;
    }

    public String getPriaccount() {
        return priaccount;
    }

    public void setPriaccount(String priaccount) {
        this.priaccount = priaccount;
    }

    public String getRefernumber() {
        return refernumber;
    }

    public void setRefernumber(String refernumber) {
        this.refernumber = refernumber;
    }

    public String getBatchno() {
        return batchno;
    }

    public void setBatchno(String batchno) {
        this.batchno = batchno;
    }

    public String getSystraceno() {
        return systraceno;
    }

    public void setSystraceno(String systraceno) {
        this.systraceno = systraceno;
    }

    public String getOrderid_scan() {
        return orderid_scan;
    }

    public void setOrderid_scan(String orderid_scan) {
        this.orderid_scan = orderid_scan;
    }

    public String getTranslocaltime() {
        return translocaltime;
    }

    public void setTranslocaltime(String translocaltime) {
        this.translocaltime = translocaltime;
    }

    public String getTranslocaldate() {
        return translocaldate;
    }

    public void setTranslocaldate(String translocaldate) {
        this.translocaldate = translocaldate;
    }

    public String getTransamount() {
        return transamount;
    }

    public void setTransamount(String transamount) {
        this.transamount = transamount;
    }

    public String getPay_tp() {
        return pay_tp;
    }

    public void setPay_tp(String pay_tp) {
        this.pay_tp = pay_tp;
    }
}
