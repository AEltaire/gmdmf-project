package net.zjitc.service;

import net.zjitc.dto.LoginDTO;

public interface LoginService {

    void checkUser(LoginDTO loginDTO);

    void updateLoginPassword(LoginDTO loginDTO);
}
