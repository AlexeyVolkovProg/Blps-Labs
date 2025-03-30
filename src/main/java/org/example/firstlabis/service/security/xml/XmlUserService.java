package org.example.firstlabis.service.security.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.example.firstlabis.model.security.User;
import org.example.firstlabis.model.security.XmlUser;
import org.example.firstlabis.model.security.XmlUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Spring-сервис для обработки пользователей в XML-файлах.
 */
@Slf4j
@Service
public class XmlUserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private static final String XML_FILE_PATH = "users.xml";
    private static final File XML_FILE = new File(XML_FILE_PATH);

    @Autowired
    public XmlUserService(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Загрузить всех пользователей из XML-файла.
     */
    public List<User> getAllUsers() {
        XmlUsers xmlUsers = loadUsersFromXml();
        return xmlUsers.getUsers().stream()
                .map(XmlUser::toUser)
                .collect(Collectors.toList());
    }

    /**
     * Сохранить нового пользователя в XML-файл.
     */
    public User saveUser(User user) {
        // Get existing user if present
        Optional<User> existingUser = findByUsername(user.getUsername());
        
        // Only encode password for new users
        if (existingUser.isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existingUser.get().getPassword());
        }
        
        // Get all existing users
        XmlUsers xmlUsers = loadUsersFromXml();
        
        // Remove user if already exists
        if (existingUser.isPresent()) {
            xmlUsers.getUsers().removeIf(u -> 
                u.getUsername().equals(user.getUsername()));  
        }
        
        
        // Add new user
        xmlUsers.getUsers().add(XmlUser.fromUser(user));
        
        // Save to XML
        saveUsersToXml(xmlUsers);
        
        return user;
    }

    /**
     * Удалить пользователя из XML-файла по имени пользователя.
     * @param username Имя пользователя для удаления
     * @return true, если пользователь был удален, false если пользователь не найден
     */
    public boolean deleteUserByUsername(String username) {
        XmlUsers xmlUsers = loadUsersFromXml();
        int initialSize = xmlUsers.getUsers().size();
        
        // Remove user if exists
        xmlUsers.getUsers().removeIf(existingUser -> 
                existingUser.getUsername().equals(username));
        
        boolean removed = xmlUsers.getUsers().size() < initialSize;
        
        if (removed) {
            // Save updated XML
            saveUsersToXml(xmlUsers);
            log.info("User deleted: {}", username);
        } else {
            log.info("User not found for deletion: {}", username);
        }
        
        return removed;
    }

    public Optional<User> findByUsername(String username) {
        XmlUsers xmlUsers = loadUsersFromXml();
        return xmlUsers.getUsers().stream()
                .filter(user -> user.getUsername().equals(username))
                .map(XmlUser::toUser)
                .findFirst();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    private XmlUsers loadUsersFromXml() {
        try {
            // Create initial file if it doesn't exist
            if (!XML_FILE.exists()) {
                XML_FILE.createNewFile();
                saveUsersToXml(new XmlUsers());
            }
            
            JAXBContext context = JAXBContext.newInstance(XmlUsers.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            
            try (FileReader reader = new FileReader(XML_FILE)) {
                return (XmlUsers) unmarshaller.unmarshal(reader);
            } catch (JAXBException | IOException e) {
                log.error("Error loading users from XML", e);
                return new XmlUsers();
            }
        } catch (Exception e) {
            log.error("Error initializing XML file", e);
            return new XmlUsers();
        }
    }

    private void saveUsersToXml(XmlUsers xmlUsers) {
        try {
            JAXBContext context = JAXBContext.newInstance(XmlUsers.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            try (FileWriter writer = new FileWriter(XML_FILE)) {
                marshaller.marshal(xmlUsers, writer);
            }
        } catch (JAXBException | IOException e) {
            log.error("Error saving users to XML", e);
        }
    }
} 