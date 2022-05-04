package com.xinlvyao.commons;

public class SeckillResult {
    // 页面200  成功，400、500 失败
    private int status;
    // 服务端向客户端发送的数据
    private Object data;
    // 服务端想客户端发送的消息
    private String msg;

    public static SeckillResult ok(){
        SeckillResult er = new SeckillResult();
        er.setStatus(200);
        er.setMsg("ok");
        System.out.println("Ego:");
        return er;
    }
    public static SeckillResult ok(Object data){
        SeckillResult er = new SeckillResult();
        er.setStatus(200);
        er.setMsg("ok");
        er.setData(data);
        return er;
    }

    public static SeckillResult ok(String msg){
        SeckillResult er = new SeckillResult();
        er.setStatus(200);
        er.setMsg(msg);
        return er;
    }

    public static SeckillResult error(String msg){
        SeckillResult er = new SeckillResult();
        er.setStatus(400);
        er.setMsg(msg);
        return er;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
