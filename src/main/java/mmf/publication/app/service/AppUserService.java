package mmf.publication.app.service;

import mmf.publication.app.dto.AppUserDTO;
import mmf.publication.app.entity.AppUser;
import mmf.publication.app.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserService implements IAppUserService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUser registerUser(AppUserDTO userDTO) {
        AppUser appUser = new AppUser();
        appUser.setUsername(userDTO.getUsername());
        appUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return appUserRepository.save(appUser);
    }

    @Override
    public AppUser findByUsername(String username) {
        Optional<AppUser> byUsername = appUserRepository.findByUsername(username);

        if (byUsername.isPresent()) {
            return byUsername.get();
        } else {
            throw new UsernameNotFoundException("User with username " + username + " does not exist!");
        }
    }
}
