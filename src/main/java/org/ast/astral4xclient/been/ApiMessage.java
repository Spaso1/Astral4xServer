package org.ast.astral4xclient.been;

public class ApiMessage {
    private int code;
    private String message;
    private long time;
    public ApiMessage(int code, String message) {
        this.code = code;
        this.message = message;
        this.time = System.currentTimeMillis();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
