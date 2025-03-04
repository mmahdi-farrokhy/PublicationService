package mmf.publication.app.service;

import mmf.publication.app.dto.AppUserDTO;
import mmf.publication.app.entity.AppUser;

public interface IAppUserService {
    AppUser registerUser(AppUserDTO userDTO);

    AppUser findByUsername(String username);
}
