package me.redshore.web_gagebu.feature.accountbook.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import me.redshore.web_gagebu.feature.accountbook.domain.AccountBook;
import me.redshore.web_gagebu.feature.accountbook.domain.Invitation;

@DataJpaTest
class InvitationRepositoryTest {

    @Autowired
    private AccountBookRepository accountBookRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Test
    @DisplayName("InvitationRepository should be loaded")
    void contextLoads() {
        assertNotNull(accountBookRepository);
        assertNotNull(invitationRepository);
    }

    @Nested
    @DisplayName("method deleteByExpirationBefore")
    class Describe_deleteByExpirationBefore {

        @Nested
        @DisplayName("when expiration is null")
        class Context_with_null_expiration {

            @BeforeEach
            @Transactional
            void setUp() {
                var accountBook = AccountBook.builder().name("tester").build();
                accountBookRepository.save(accountBook);

                var invitation = Invitation.builder().accountBook(accountBook).build();
                invitationRepository.save(invitation);
            }

            @Test
            @DisplayName("it should not deleted")
            void it_should_not_delete() {
                var deleteCount =
                    invitationRepository.deleteByExpirationBefore(ZonedDateTime.now());
                assertEquals(0, deleteCount);
            }

        }

        @Nested
        @DisplayName("when expiration is valid")
        class Context_with_valid_expiration {

            @BeforeEach
            @Transactional
            void setUp() {
                var accountBook = AccountBook
                    .builder()
                    .name("tester")
                    .build();
                accountBookRepository.save(accountBook);

                var invitation = Invitation
                    .builder()
                    .accountBook(accountBook)
                    .expiration(ZonedDateTime.now().minusDays(1))
                    .build();
                invitationRepository.save(invitation);
            }

            @Test
            @DisplayName("it should be deleted")
            void it_should_delete() {
                var deleteCount =
                    invitationRepository.deleteByExpirationBefore(ZonedDateTime.now());
                assertEquals(1, deleteCount);
            }

        }

    }

}
