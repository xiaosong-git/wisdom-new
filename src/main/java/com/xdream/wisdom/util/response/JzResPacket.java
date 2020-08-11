package com.xdream.wisdom.util.response;

import java.util.List;

public class JzResPacket {

    List<ErrorRes> ErrorRes;
    private String name;
    private String pid;
    private String msg;
    private String photo;



    public List<ErrorRes> getErrorRes() {
        return ErrorRes;
    }

    public void setErrorRes(List<ErrorRes> errorRes) {
        ErrorRes = errorRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
