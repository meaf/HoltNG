package com.meaf.apeps.view.beans;

import com.meaf.apeps.model.entity.User;
import com.meaf.apeps.model.repository.UserRepository;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class UserBean {

  private UserRepository userRepository;

  public UserBean(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User findUser(String uname, String pass) {
    String b64Pass = encodePass(uname, pass);
    return userRepository.checkUser(uname, b64Pass);
  }


  public User createUser(User user) {
    String b64Pass = encodePass(user.getName(), user.getPassword());
    user.setPassword(b64Pass);
    return userRepository.save(user);
  }

  private String encodePass(String uname, String pass) {
    byte[] pwBytes = DigestUtils.sha512(Base64.encodeBase64String(uname.getBytes()) + pass);
    return Base64.encodeBase64String(pwBytes);
  }

  public boolean checkUsername(String username) {
    return userRepository.checkUsername(username) == null;
  }
}
