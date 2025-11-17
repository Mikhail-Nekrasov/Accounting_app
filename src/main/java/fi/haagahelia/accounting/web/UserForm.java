package fi.haagahelia.accounting.web;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForm {
    private String username;
    private String password;
    private String passwordConfirm;

}

