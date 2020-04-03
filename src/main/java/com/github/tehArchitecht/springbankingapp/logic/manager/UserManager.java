package com.github.tehArchitecht.springbankingapp.logic.manager;

import com.github.tehArchitecht.springbankingapp.data.model.User;
import com.github.tehArchitecht.springbankingapp.logic.Result;
import com.github.tehArchitecht.springbankingapp.logic.Status;
import com.github.tehArchitecht.springbankingapp.logic.dto.request.SignInWithNameRequest;
import com.github.tehArchitecht.springbankingapp.logic.dto.request.SignInWithPhoneNumberRequest;
import com.github.tehArchitecht.springbankingapp.logic.dto.request.SignUpRequest;
import com.github.tehArchitecht.springbankingapp.logic.service.EntityMapperService;
import com.github.tehArchitecht.springbankingapp.security.service.JwtTokenService;
import com.github.tehArchitecht.springbankingapp.service.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserManager extends SecuredValidatingManager {
    private final EntityMapperService entityMapperService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserManager(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService,
                       EntityMapperService entityMapperService, UserService userService,
                       PasswordEncoder passwordEncoder) {
        super(authenticationManager, jwtTokenService);
        this.entityMapperService = entityMapperService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public Status signUp (SignUpRequest request) {
        if (request == null || hasConstraintViolations(request))
            return Status.FAILURE_VALIDATION_ERROR;

        String userName = request.getUserName();
        String phoneNumber = request.getPhoneNumber();

        try {
            if (userService.isNameInUse(userName) || userService.isPhoneNumberInUse(phoneNumber))
                return Status.SING_UP_FAILURE_NAME_OR_PHONE_NUMBER_TAKEN;

            User user = entityMapperService.extractUser(request);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.add(user);
            return Status.SIGN_UP_SUCCESS;
        } catch (DataAccessException e) {
            return Status.FAILURE_INTERNAL_ERROR;
        }
    }

    public Result<String> signInWithName(SignInWithNameRequest request) {
        if (request == null || hasConstraintViolations(request))
            return Result.ofFailure(Status.FAILURE_VALIDATION_ERROR);

        String userName = request.getUserName();
        String password = request.getPassword();

        try {
            return Result.ofSuccess(Status.SIGN_IN_WITH_NAME_SUCCESS, generateToken(userName, password));
        } catch (DisabledException | BadCredentialsException e) {
            return Result.ofFailure(Status.SIGN_IN_WITH_NAME_FAILURE_WRONG_DATA);
        }
    }

    public Result<String> signWithPhoneNumber(SignInWithPhoneNumberRequest request) {
        if (request == null || hasConstraintViolations(request))
            return Result.ofFailure(Status.FAILURE_VALIDATION_ERROR);

        String phoneNumber = request.getPhoneNumber();
        String password = request.getPassword();

        try {
            Optional<User> optional = userService.getByPhoneNumber(phoneNumber);
            if (!optional.isPresent())
                return Result.ofFailure(Status.SIGN_IN_WITH_PHONE_NUMBER_FAILURE_WRONG_DATA);

            String userName = optional.get().getName();
            return Result.ofSuccess(Status.SIGN_IN_WITH_PHONE_NUMBER_SUCCESS, generateToken(userName, password));
        } catch (DisabledException | BadCredentialsException e) {
            return Result.ofFailure(Status.SIGN_IN_WITH_PHONE_NUMBER_FAILURE_WRONG_DATA);
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }
}
