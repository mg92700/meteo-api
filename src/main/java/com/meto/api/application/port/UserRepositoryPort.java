package com.meto.api.application.port;

import com.meto.api.domain.model.User;

public interface UserRepositoryPort {

    User findByEmail(String email);

}
