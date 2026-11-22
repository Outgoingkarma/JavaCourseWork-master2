package com.example.coursework.hibernateControl;

import com.example.coursework.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Characterization testai: fiksuoja DABARTINĘ CustomHibernate.getUserByCredentials elgseną
 * (įskaitant keistas vietas), kad refaktoringas jos nepakeistų.
 */
@ExtendWith(MockitoExtension.class)
class CustomHibernateGetUserByCredentialsTest {

    private static final String LOGIN = "alice";
    private static final String RAW_PASSWORD = "secret123";

    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private EntityManager entityManager;
    @Mock
    private TypedQuery<User> query;
    @Mock
    private EntityTransaction transaction;

    private CustomHibernate customHibernate;

    @BeforeEach
    void setUp() {
        customHibernate = new CustomHibernate(entityManagerFactory);
    }

    // ---------- pagalbiniai metodai ----------

    private void givenEntityManagerOpen() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.isOpen()).thenReturn(true);
    }

    private void givenQueryReturns(User userOrNull) {
        givenEntityManagerOpen();
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultStream()).thenReturn(userOrNull == null ? Stream.empty() : Stream.of(userOrNull));
    }

    private User userWithPassword(String storedPassword) {
        User user = new User();
        user.setLogin(LOGIN);
        user.setPassword(storedPassword);
        return user;
    }

    private static String bcryptHash(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt(4)); // maža "cost" reikšmė, kad testai būtų greiti
    }

    // ---------- vartotojas / slaptažodis nenustatytas ----------

    @Test
    void returnsNull_whenUserNotFound() {
        givenQueryReturns(null);

        assertNull(customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD));

        verify(query).setParameter("login", LOGIN);
        verify(entityManager, never()).getTransaction();
    }

    @Test
    void returnsNull_whenStoredPasswordIsNull() {
        givenQueryReturns(userWithPassword(null));

        assertNull(customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD));
    }

    @Test
    void returnsNull_whenStoredPasswordIsEmpty() {
        givenQueryReturns(userWithPassword(""));

        assertNull(customHibernate.getUserByCredentials(LOGIN, ""));
    }

    @Test
    void returnsNull_whenStoredPasswordIsBlank() {
        givenQueryReturns(userWithPassword("   "));

        assertNull(customHibernate.getUserByCredentials(LOGIN, "   "));
    }

    // ---------- bcrypt šaka ----------

    @Test
    void returnsUser_whenBcryptPasswordMatches() {
        User stored = userWithPassword(bcryptHash(RAW_PASSWORD));
        givenQueryReturns(stored);

        assertSame(stored, customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD));

        // bcrypt slaptažodis neperhash'inamas ir nieko neįrašoma
        verify(entityManager, never()).getTransaction();
        verify(entityManager, never()).merge(any());
    }

    @Test
    void returnsNull_whenBcryptPasswordDoesNotMatch() {
        givenQueryReturns(userWithPassword(bcryptHash(RAW_PASSWORD)));

        assertNull(customHibernate.getUserByCredentials(LOGIN, "wrong-password"));

        verify(entityManager, never()).merge(any());
    }

    @Test
    void storedHashStartingWith2a_isCheckedAsBcrypt() {
        String hash = bcryptHash(RAW_PASSWORD);
        assertTrue(hash.startsWith("$2a$"));
        User stored = userWithPassword(hash);
        givenQueryReturns(stored);

        assertSame(stored, customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD));
    }

    /**
     * Dabartinė elgsena: kodas "$2b$" ir "$2y$" prefiksus laiko bcrypt, bet jBCrypt 0.4
     * palaiko tik "$2a$" -> išimtis, kuri nuryjama ir grąžinamas null (net su teisingu slaptažodžiu).
     */
    @Test
    void returnsNull_whenHashPrefixIs2b_becauseLibraryRejectsIt() {
        String hash2b = "$2b$" + bcryptHash(RAW_PASSWORD).substring(4);
        givenQueryReturns(userWithPassword(hash2b));

        assertNull(customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD));
        verify(entityManager, never()).merge(any());
    }

    @Test
    void returnsNull_whenHashPrefixIs2y_becauseLibraryRejectsIt() {
        String hash2y = "$2y$" + bcryptHash(RAW_PASSWORD).substring(4);
        givenQueryReturns(userWithPassword(hash2y));

        assertNull(customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD));
        verify(entityManager, never()).merge(any());
    }

    // ---------- plaintext (legacy) šaka ----------

    @Test
    void returnsUserAndUpgradesToBcrypt_whenPlaintextPasswordMatches() {
        User stored = userWithPassword(RAW_PASSWORD);
        givenQueryReturns(stored);
        when(entityManager.getTransaction()).thenReturn(transaction);

        User result = customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD);

        assertSame(stored, result);
        // slaptažodis pakeistas bcrypt hash'u, kuris tinka originaliam slaptažodžiui
        assertNotEquals(RAW_PASSWORD, stored.getPassword());
        assertTrue(stored.getPassword().startsWith("$2a$"));
        assertTrue(BCrypt.checkpw(RAW_PASSWORD, stored.getPassword()));

        verifyBeginMergeCommit(stored);
    }

    private void verifyBeginMergeCommit(User stored) {
        org.mockito.InOrder inOrder = inOrder(transaction, entityManager);
        inOrder.verify(transaction).begin();
        inOrder.verify(entityManager).merge(stored);
        inOrder.verify(transaction).commit();
    }

    @Test
    void mergesUserWithNewHash_whenPlaintextPasswordMatches() {
        User stored = userWithPassword(RAW_PASSWORD);
        givenQueryReturns(stored);
        when(entityManager.getTransaction()).thenReturn(transaction);

        customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD);

        ArgumentCaptor<User> merged = ArgumentCaptor.forClass(User.class);
        verify(entityManager).merge(merged.capture());
        assertTrue(BCrypt.checkpw(RAW_PASSWORD, merged.getValue().getPassword()));
    }

    @Test
    void returnsNull_andDoesNotUpdate_whenPlaintextPasswordDoesNotMatch() {
        User stored = userWithPassword(RAW_PASSWORD);
        givenQueryReturns(stored);

        assertNull(customHibernate.getUserByCredentials(LOGIN, "wrong-password"));

        assertEquals(RAW_PASSWORD, stored.getPassword()); // nepakeistas
        verify(entityManager, never()).getTransaction();
        verify(entityManager, never()).merge(any());
    }

    @Test
    void returnsNull_whenPlaintextPasswordUpgradeFails() {
        // Dabartinė elgsena: jei perhash'inimo transakcija nepavyksta, išimtis nuryjama -> null,
        // nors slaptažodis buvo teisingas.
        User stored = userWithPassword(RAW_PASSWORD);
        givenQueryReturns(stored);
        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException("commit failed")).when(transaction).commit();

        assertNull(customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD));
        verify(entityManager).close();
    }

    @Test
    void returnsNull_whenPasswordArgumentIsNull_andStoredIsPlaintext() {
        givenQueryReturns(userWithPassword(RAW_PASSWORD));

        assertNull(customHibernate.getUserByCredentials(LOGIN, null));
    }

    @Test
    void returnsNull_whenPasswordArgumentIsNull_andStoredIsBcrypt() {
        // BCrypt.checkpw(null, ...) meta išimtį, kuri nuryjama
        givenQueryReturns(userWithPassword(bcryptHash(RAW_PASSWORD)));

        assertNull(customHibernate.getUserByCredentials(LOGIN, null));
    }

    // ---------- išimtys ir resursų valdymas ----------

    @Test
    void returnsNull_whenQueryThrowsException() {
        givenEntityManagerOpen();
        when(entityManager.createQuery(anyString(), eq(User.class))).thenThrow(new RuntimeException("db down"));

        assertNull(customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD));
    }

    @Test
    void returnsNull_whenEntityManagerCannotBeCreated() {
        when(entityManagerFactory.createEntityManager()).thenThrow(new IllegalStateException("no connection"));

        assertNull(customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD));
    }

    @Test
    void closesEntityManager_whenUserFound() {
        givenQueryReturns(userWithPassword(bcryptHash(RAW_PASSWORD)));

        customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD);

        verify(entityManager).close();
    }

    @Test
    void closesEntityManager_whenUserNotFound() {
        givenQueryReturns(null);

        customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD);

        verify(entityManager).close();
    }

    @Test
    void closesEntityManager_whenExceptionThrown() {
        givenEntityManagerOpen();
        when(entityManager.createQuery(anyString(), eq(User.class))).thenThrow(new RuntimeException("boom"));

        customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD);

        verify(entityManager).close();
    }

    @Test
    void doesNotClose_whenEntityManagerAlreadyClosed() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.isOpen()).thenReturn(false);
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD);

        verify(entityManager, never()).close();
    }

    @Test
    void usesQueryRestrictedToUserAndRestaurantTypes() {
        givenQueryReturns(null);

        customHibernate.getUserByCredentials(LOGIN, RAW_PASSWORD);

        ArgumentCaptor<String> jpql = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpql.capture(), eq(User.class));
        assertEquals("SELECT u FROM User u WHERE u.login = :login AND TYPE(u) IN (User, Restaurant)", jpql.getValue());
    }
}
