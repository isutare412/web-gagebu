package me.redshore.web_gagebu.feature.user.validator;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    public void checkRequesterCanAccessUser(UUID requesterId, UUID userId) {
        if (requesterId.equals(userId)) {
            return;
        }

        throw new AppException(ErrorCode.FORBIDDEN,
                               String.format(
                                   "User with ID '%s' is not allowed to access user with ID '%s'",
                                   requesterId, userId));
    }
}
