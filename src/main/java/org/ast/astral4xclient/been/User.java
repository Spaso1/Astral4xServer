package org.ast.astral4xclient.been;
import java.time.LocalDateTime;
import java.util.Set;

public class User {
    private Long id;

    private String username;
    private String password;

    private String email;

    //@Column(unique = true)
    private String token;
    private int updateStream; //b/s
    private int downStream; //b/s
    private long countStream; //b
    private int max_frp;

    public long getCountStream() {
        return countStream;
    }

    public int getMax_frp() {
        return max_frp;
    }

    public void setMax_frp(int max_frp) {
        this.max_frp = max_frp;
    }

    public void setCountStream(long countStream) {
        this.countStream = countStream;
    }

    public int getUpdateStream() {
        return updateStream;
    }

    public void setUpdateStream(int updateStream) {
        this.updateStream = updateStream;
    }

    public int getDownStream() {
        return downStream;
    }

    public void setDownStream(int downStream) {
        this.downStream = downStream;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
