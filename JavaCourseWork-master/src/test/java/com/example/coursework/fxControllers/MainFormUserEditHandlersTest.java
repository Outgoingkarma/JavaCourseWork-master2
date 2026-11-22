package com.example.coursework.fxControllers;

import com.example.coursework.fxTablesParameters.UserTableParameters;
import com.example.coursework.hibernateControl.CustomHibernate;
import com.example.coursework.model.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellEditEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Characterization testai MainForm.initialize() vartotojų lentelės redagavimo apdorojimui.
 * Jie fiksuoja DABARTINĘ elgseną, įskaitant akivaizdžias klaidas (surname/email/phone/address
 * redagavimas DB įraše nustato name, login redagavimas nustato password).
 * Klaidos taisomos vėliau atskiru "fix:" commit'u.
 */
class MainFormUserEditHandlersTest {

    private static final String NEW_VALUE = "NEW";

    private MainForm mainForm;
    private CustomHibernate customHibernate;
    private UserTableParameters row;
    private User dbUser;

    /** JavaFX valdikliai (Control klasės) reikalauja inicializuoto toolkit'o, net kai jie tik mock'inami. */
    @BeforeAll
    static void startJavaFxToolkit() {
        try {
            Platform.startup(() -> { });
        } catch (IllegalStateException alreadyStarted) {
            // toolkit jau paleistas
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        mainForm = new MainForm();
        populateAllFxFieldsWithMocks(mainForm);

        customHibernate = mock(CustomHibernate.class);
        setField(mainForm, "customHibernate", customHibernate);

        row = new UserTableParameters();
        row.setId(7);
        row.setLogin("rowLogin");
        row.setPassword("rowPassword");
        row.setName("rowName");
        row.setSurname("rowSurname");
        row.setEmail("rowEmail");
        row.setPhoneNumber("rowPhone");
        row.setAddress("rowAddress");

        dbUser = new User();
        dbUser.setId(7);
        dbUser.setLogin("dbLogin");
        dbUser.setPassword("dbPassword");
        dbUser.setName("dbName");
        dbUser.setSurname("dbSurname");
        dbUser.setEmail("dbEmail");
        dbUser.setPhone_number("dbPhone");
        when(customHibernate.getRecordById(User.class, 7)).thenReturn(dbUser);

        mainForm.initialize(null, null);
    }

    // ---------- kiekvieno stulpelio dabartinė elgsena ----------

    @Test
    void loginColumn_updatesRowLogin_butSetsPasswordOnDbUser() {
        fire(mainForm.userLoginColumn);

        assertEquals(NEW_VALUE, row.getLogin());
        assertEquals(NEW_VALUE, dbUser.getPassword());      // klaida: turėtų būti login
        assertEquals("dbLogin", dbUser.getLogin());         // login DB nepakeistas
        verify(customHibernate).update(dbUser);
    }

    @Test
    void passwordColumn_updatesRowPassword_andSetsPasswordOnDbUser() {
        fire(mainForm.userPasswordColumn);

        assertEquals(NEW_VALUE, row.getPassword());
        assertEquals(NEW_VALUE, dbUser.getPassword());
        verify(customHibernate).update(dbUser);
    }

    @Test
    void nameColumn_updatesRowName_andSetsNameOnDbUser() {
        fire(mainForm.userNameColumn);

        assertEquals(NEW_VALUE, row.getName());
        assertEquals(NEW_VALUE, dbUser.getName());
        verify(customHibernate).update(dbUser);
    }

    @Test
    void surnameColumn_updatesRowSurname_butSetsNameOnDbUser() {
        fire(mainForm.userSurnameColumn);

        assertEquals(NEW_VALUE, row.getSurname());
        assertEquals(NEW_VALUE, dbUser.getName());          // klaida: turėtų būti surname
        assertEquals("dbSurname", dbUser.getSurname());
        verify(customHibernate).update(dbUser);
    }

    @Test
    void emailColumn_updatesRowEmail_butSetsNameOnDbUser() {
        fire(mainForm.userEmailColumn);

        assertEquals(NEW_VALUE, row.getEmail());
        assertEquals(NEW_VALUE, dbUser.getName());          // klaida: turėtų būti email
        assertEquals("dbEmail", dbUser.getEmail());
        verify(customHibernate).update(dbUser);
    }

    @Test
    void phoneColumn_updatesRowPhone_butSetsNameOnDbUser() {
        fire(mainForm.userPhoneColumn);

        assertEquals(NEW_VALUE, row.getPhoneNumber());
        assertEquals(NEW_VALUE, dbUser.getName());          // klaida: turėtų būti phone_number
        assertEquals("dbPhone", dbUser.getPhone_number());
        verify(customHibernate).update(dbUser);
    }

    @Test
    void addressColumn_updatesRowAddress_butSetsNameOnDbUser() {
        fire(mainForm.userAddressColumn);

        assertEquals(NEW_VALUE, row.getAddress());
        assertEquals(NEW_VALUE, dbUser.getName());          // klaida: User neturi address lauko
        verify(customHibernate).update(dbUser);
    }

    @Test
    void editingOneColumn_doesNotChangeOtherRowFields() {
        fire(mainForm.userSurnameColumn);

        assertEquals("rowLogin", row.getLogin());
        assertEquals("rowPassword", row.getPassword());
        assertEquals("rowName", row.getName());
        assertEquals("rowEmail", row.getEmail());
        assertEquals("rowPhone", row.getPhoneNumber());
        assertEquals("rowAddress", row.getAddress());
    }

    @Test
    void everyUserColumn_loadsRecordById_andSavesExactlyOnce() {
        TableColumn<UserTableParameters, String>[] columns = userColumns();
        for (TableColumn<UserTableParameters, String> column : columns) {
            fire(column);
        }

        verify(customHibernate, times(columns.length)).getRecordById(User.class, 7);
        verify(customHibernate, times(columns.length)).update(dbUser);
    }

    // ---------- pagalbiniai metodai ----------

    @SuppressWarnings("unchecked")
    private TableColumn<UserTableParameters, String>[] userColumns() {
        return new TableColumn[]{
                mainForm.userLoginColumn, mainForm.userPasswordColumn, mainForm.userNameColumn,
                mainForm.userSurnameColumn, mainForm.userEmailColumn, mainForm.userPhoneColumn,
                mainForm.userAddressColumn};
    }

    /** Pagauna stulpeliui užregistruotą setOnEditCommit handler'į ir iškviečia jį su NEW_VALUE. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void fire(TableColumn<UserTableParameters, String> column) {
        ArgumentCaptor<EventHandler> handler = ArgumentCaptor.forClass(EventHandler.class);
        verify(column).setOnEditCommit(handler.capture());

        TableView<UserTableParameters> tableView = mock(TableView.class);
        when(tableView.getItems()).thenReturn(FXCollections.observableArrayList(row));
        TablePosition<UserTableParameters, String> position = mock(TablePosition.class);
        when(position.getRow()).thenReturn(0);
        CellEditEvent<UserTableParameters, String> event = mock(CellEditEvent.class);
        when(event.getTableView()).thenReturn(tableView);
        when(event.getTablePosition()).thenReturn(position);
        when(event.getNewValue()).thenReturn(NEW_VALUE);

        handler.getValue().handle(event);
    }

    /** initialize() naudoja daug @FXML laukų – kiekvieną pakeičiame Mockito mock'u (deep stubs). */
    private static void populateAllFxFieldsWithMocks(MainForm form) throws Exception {
        for (Field field : MainForm.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) continue;
            if (!Modifier.isPublic(field.getModifiers())) continue;
            if (field.getType().isPrimitive()) continue;
            field.set(form, mock(field.getType(), RETURNS_DEEP_STUBS));
        }
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
