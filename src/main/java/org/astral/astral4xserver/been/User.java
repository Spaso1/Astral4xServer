package org.astral.astral4xserver.been;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users" ,indexes = {
        @Index(name = "id", columnList = "id")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String password;
    @Column(unique = true)
    private String email;

    //@Column(unique = true)
    private String token;
    private LocalDateTime created_at;
    @Column(columnDefinition = "integer default 5242880")
    private int updateStream; //b/s
    @Column(columnDefinition = "integer default 5242880")
    private int downStream; //b/s
    @Column(columnDefinition = "bigint default 10000000000000")
    private long countStream; //b
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
    @Column(columnDefinition = "integer default 5")
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
    public void addRole(Role role) {
        this.roles.add(role);
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
