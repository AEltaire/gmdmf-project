import request from '@/utils/request'

/* 
  LoginDTO

    private String rule;
    private String username;
    private String email;
    private String password;
    private String code;
    
*/

export const sendEmailCodeService = (loginDTO) => {
  return request.post("/login", loginDTO);
}

export const updatePasswordService = (loginDTO) => {
  return request.patch("/login", loginDTO);
}