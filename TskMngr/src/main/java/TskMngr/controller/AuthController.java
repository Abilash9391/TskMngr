package TskMngr.controller;

import TskMngr.dto.Login;
import TskMngr.dto.Register;
import TskMngr.model.User;
import TskMngr.security.AuthResponse;
import TskMngr.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/auth")
public class AuthController {
        private static final Logger log = LoggerFactory.getLogger(AuthController.class);
        @Autowired
        private final AuthService authService;

        public AuthController(AuthService authService) {
                this.authService = authService;
        }

        @ResponseStatus(HttpStatus.CREATED)
        @PostMapping("/register")
        public ResponseEntity<AuthResponse> register(@Valid @RequestBody Register register){
                return ResponseEntity.ok(authService.register(register));
        }
        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(@Valid @RequestBody Login login) {
                log.info("Received login request for username: {}", login.getUsername());

                Optional<User> user = authService.login(login);

                if (user.isPresent()) {
                        log.info("Login successful for username: {}", login.getUsername());
                        User presentUser = user.get();
                        return ResponseEntity.ok(new AuthResponse(presentUser.getId(), presentUser.getUsername(), presentUser.getRole()));
              }
              else {
                        log.warn("Login failed for username: {}", login.getUsername());
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
        }

//        @PostMapping("/login")
//        public ResponseEntity<AuthResponse> login(@Valid @RequestBody Login login){
//                Optional<User> user = authService.login(login);
//                if (user.isPresent()){
//                        User presentUser = user.get();
//                        return ResponseEntity.ok(new AuthResponse(presentUser.getId(), presentUser.getUsername(), presentUser.getRole()));
//                }
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
}