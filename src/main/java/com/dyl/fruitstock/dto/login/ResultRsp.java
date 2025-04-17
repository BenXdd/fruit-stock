package com.dyl.fruitstock.dto.login;

import java.io.Serializable;

public class ResultRsp<T> implements Serializable {
    private int code = 200;
    private String msg;
    private T data;

    public ResultRsp() {
    }

    public ResultRsp(T data) {
        this.data = data;
    }

    public static <T> ResultRsp<T> success() {
        return new ResultRsp();
    }

    public static <T> ResultRsp<T> success(T data) {
        return new ResultRsp(data);
    }

    public static <T> ResultRsp<T> failure(int code, String message) {
        return new ResultRsp(code, message, (Object)null);
    }

    public static <T> ResultRsp<T> failure(int code, String message, T data) {
        return new ResultRsp(code, message, data);
    }


    public static <T> ResultRsp<T> of(T data) {
        return new ResultRsp(data);
    }

    public boolean successful() {
        return this.code == 200;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public T getData() {
        return this.data;
    }

    public ResultRsp<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public ResultRsp<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public ResultRsp<T> setData(T data) {
        this.data = data;
        return this;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ResultRsp)) {
            return false;
        } else {
            ResultRsp<?> other = (ResultRsp)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getCode() != other.getCode()) {
                return false;
            } else {
                Object this$msg = this.getMsg();
                Object other$msg = other.getMsg();
                if (this$msg == null) {
                    if (other$msg != null) {
                        return false;
                    }
                } else if (!this$msg.equals(other$msg)) {
                    return false;
                }

                Object this$data = this.getData();
                Object other$data = other.getData();
                if (this$data == null) {
                    if (other$data != null) {
                        return false;
                    }
                } else if (!this$data.equals(other$data)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ResultRsp;
    }

    public int hashCode() {

        int result = 1;
        result = result * 59 + this.getCode();
        Object $msg = this.getMsg();
        result = result * 59 + ($msg == null ? 43 : $msg.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        return result;
    }

    public String toString() {
        return "ResultRsp(code=" + this.getCode() + ", msg=" + this.getMsg() + ", data=" + this.getData() + ")";
    }

    public ResultRsp(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}