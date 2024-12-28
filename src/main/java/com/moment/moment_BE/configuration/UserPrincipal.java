package com.moment.moment_BE.configuration;

import javax.security.auth.Subject;
import java.security.Principal;

public class UserPrincipal implements Principal {

    private final  String userName;

    public UserPrincipal(String name) {
        this.userName = name;
    }

    @Override
    public String getName() {
        return userName;
    }

}
