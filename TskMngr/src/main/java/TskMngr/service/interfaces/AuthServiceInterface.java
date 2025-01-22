package TskMngr.service.interfaces;

import TskMngr.dto.Login;
import TskMngr.dto.Register;
import TskMngr.model.User;
import TskMngr.security.AuthResponse;

import java.util.Optional;

public interface AuthServiceInterface {

    AuthResponse register(Register register);
    Optional<User> login(Login login);
}
