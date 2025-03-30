package org.example.firstlabis.model.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlUser {
    
    @XmlElement(name = "username")
    private String username;
    
    @XmlElement(name = "password")
    private String password;
    
    @XmlElement(name = "role")
    private Role role;
    
    @XmlElement(name = "enabled")
    private boolean enabled;
    
    public User toUser() {
        return User.builder()
                .username(username)
                .password(password)
                .role(role)
                .enabledStatus(enabled)
                .build();
    }
    
    public static XmlUser fromUser(User user) {
        return XmlUser.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
    }
} 