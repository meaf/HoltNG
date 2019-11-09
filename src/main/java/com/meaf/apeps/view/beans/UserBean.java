package com.meaf.apeps.view.beans;

import com.meaf.apeps.model.entity.User;
import com.meaf.apeps.model.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.binary.Base64;

@Component
@SessionScope
public class UserBean {

  private UserRepository userRepository;

  public UserBean(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User findUser(String uname, String pass){
    byte[] pwBytes = DigestUtils.sha512(Base64.encodeBase64String(uname.getBytes()) + pass);
    String b64Pass = Base64.encodeBase64String(pwBytes);

    return userRepository.checkUser(uname, b64Pass);
  }

  public User createUser(String uname, String pass){
    String saltedPass = Base64.encodeBase64String(uname.getBytes()) + pass;
    byte[] pwBytes = DigestUtils.sha512(saltedPass);
    String b64Pass = Base64.encodeBase64String(pwBytes);

    User user = new User();
    user.setName(uname);
    user.setPassword(b64Pass);
    return userRepository.save(user);
  }


}
