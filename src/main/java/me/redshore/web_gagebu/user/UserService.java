package me.redshore.web_gagebu.user;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.user.dto.UserOidcUpsertCommand;
import me.redshore.web_gagebu.user.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    @Transactional
    public User upsertUser(UserOidcUpsertCommand command) {
        var optionalUser =
            this.userRepository.findByIdpTypeAndIdpIdentifier(command.idpType(),
                                                              command.idpIdentifier());

        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            mergeUser(user, command);
        } else {
            user = this.userMapper.toEntity(command);
        }

        return this.userRepository.save(user);
    }

    private void mergeUser(User user, UserOidcUpsertCommand command) {
        user.setPictureUrl(command.pictureUrl());
        user.setEmail(command.email());
    }

}
