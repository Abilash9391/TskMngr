package TskMngr.service;

import TskMngr.dto.Login;
import TskMngr.dto.Register;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import TskMngr.exception.ApiException;
import TskMngr.model.User;
import TskMngr.repository.UserRepository;
import TskMngr.security.AuthResponse;
import TskMngr.service.interfaces.AuthServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements AuthServiceInterface {

    private final UserRepository usr;
    private static final Logger log = LoggerFactory.getLogger(ApiException.class);
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository usr, PasswordEncoder passwordEncoder) {
        this.usr = usr;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse register(Register register) {
        if (usr.existsByEmail(register.getEmail()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email has been already used.");
        if (usr.existsByUsername(register.getUsername()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username already taken.");

        User user = buildUser(register);
        User savedUser = null;
        try {
            savedUser = usr.save(user);
        } catch (StaleObjectStateException e) {
            // Handle the conflict (e.g., reload entity or inform user)
            User updatedUser = usr.findById(user.getId()).orElseThrow();
            // Retry the operation with the updated data
        }


        return new AuthResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getRole());
    }

    @Override
    public Optional<User> login(Login login) {


        return usr.findByUsernameOrEmail(login.getUsername(), login.getUsername())
                .filter(user -> {
                    log.info("Password provided: {}", login.getPassword());
                    log.info("Password from DB: {}", user.getPassword());
                    boolean passwordMatch = passwordEncoder.matches(login.getPassword(), user.getPassword());
                    log.info("Password match result: {}", passwordMatch);
                    return passwordEncoder.matches(login.getPassword(), user.getPassword());
                });
    }

    private User buildUser(Register register){
        User user = new User();
        user.setEmail(register.getEmail());
        user.setUsername(register.getUsername());
        user.setPassword(passwordEncoder.encode(register.getPassword()));
        user.setRole("USER");

        return user;
    }

}
