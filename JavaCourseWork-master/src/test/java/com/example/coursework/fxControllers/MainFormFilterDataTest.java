package com.example.coursework.fxControllers;

import com.example.coursework.fxTablesParameters.UserTableParameters;
import com.example.coursework.hibernateControl.CustomHibernate;
import com.example.coursework.model.BasicUser;
import com.example.coursework.model.Restaurant;
import com.example.coursework.model.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class MainFormFilterDataTest {
    private static final String NAME = "Jonas";
    private static final String SURNAME = "Jonaitis";
    private static final String CUSTOMER_ADDRESS = "Vilniaus g. 1";
    private static final String RESTAURANT_ADDRESS = "Kauno g. 2";

    private MainForm mainForm;
    private CustomHibernate customHibernate;
    private ObservableList<UserTableParameters> tableRows;
    private BasicUser customer;
    private Restaurant restaurant;

    @BeforeAll
    static void startJavaFxToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException alreadyStarted) {
            // Another JavaFX test has already started the toolkit.
        }
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() throws ReflectiveOperationException {
        mainForm = new MainForm();
        customHibernate = mock(CustomHibernate.class);
        Field hibernateField = MainForm.class.getDeclaredField("customHibernate");
        hibernateField.setAccessible(true);
        hibernateField.set(mainForm, customHibernate);

        mainForm.filterName = mock(TextField.class);
        mainForm.filterSurname = mock(TextField.class);
        mainForm.userTab = mock(Tab.class);
        mainForm.userTable = mock(TableView.class);
        tableRows = FXCollections.observableArrayList(new UserTableParameters());
        when(mainForm.userTable.getItems()).thenReturn(tableRows);

        customer = new BasicUser("customer", "customer-password", NAME, SURNAME,
                "+37060011111", "customer@example.lt", CUSTOMER_ADDRESS);
        customer.setId(17);
        restaurant = new Restaurant();
        restaurant.setId(24);
        restaurant.setLogin("restaurant");
        restaurant.setPassword("restaurant-password");
        restaurant.setName(NAME);
        restaurant.setSurname(SURNAME);
        restaurant.setEmail("restaurant@example.lt");
        restaurant.setPhone_number("+37060022222");
        restaurant.setAddress(RESTAURANT_ADDRESS);
    }

    @Test
    void filtersByNameWhenOnlyNameIsFilled() {
        setFilters(NAME, "");
        when(customHibernate.findByName(User.class, NAME)).thenReturn(List.of(customer, restaurant));

        mainForm.filterData();

        verify(customHibernate).findByName(User.class, NAME);
        verifyNoMoreInteractions(customHibernate);
        assertFilteredRows();
    }

    @Test
    void filtersBySurnameWhenOnlySurnameIsFilled() {
        setFilters("", SURNAME);
        when(customHibernate.findBySurname(User.class, SURNAME)).thenReturn(List.of(customer, restaurant));

        mainForm.filterData();

        verify(customHibernate).findBySurname(User.class, SURNAME);
        verifyNoMoreInteractions(customHibernate);
        assertFilteredRows();
    }

    @Test
    void filtersByBothNameAndSurnameWhenBothAreFilled() {
        setFilters(NAME, SURNAME);
        when(customHibernate.findByNameAndSurname(User.class, NAME, SURNAME))
                .thenReturn(List.of(customer, restaurant));

        mainForm.filterData();

        verify(customHibernate).findByNameAndSurname(User.class, NAME, SURNAME);
        verifyNoMoreInteractions(customHibernate);
        assertFilteredRows();
    }

    @Test
    void reloadsAllUsersWhenBothFiltersAreEmpty() {
        setFilters("", "");
        when(mainForm.userTab.isSelected()).thenReturn(true);
        when(customHibernate.getAllRecords(User.class)).thenReturn(List.of(customer, restaurant));

        mainForm.filterData();

        verify(customHibernate).getAllRecords(User.class);
        verifyNoMoreInteractions(customHibernate);
        assertEquals(2, tableRows.size());
        assertRow(customer, CUSTOMER_ADDRESS, "BasicUser", tableRows.get(0));
        assertRow(restaurant, RESTAURANT_ADDRESS, "Restaurant", tableRows.get(1));
    }

    @Test
    void treatsWhitespaceAsFilterTextAndClearsRowsWhenNoUsersMatch() {
        setFilters(" ", "");
        when(customHibernate.findByName(User.class, " ")).thenReturn(List.of());

        mainForm.filterData();

        verify(customHibernate).findByName(User.class, " ");
        verifyNoMoreInteractions(customHibernate);
        assertTrue(tableRows.isEmpty());
    }

    @Test
    void replacesPreviousResultsWhenTheFilterChanges() {
        setFilters(NAME, "");
        when(customHibernate.findByName(User.class, NAME)).thenReturn(List.of(customer));
        mainForm.filterData();
        assertEquals(1, tableRows.size());
        assertEquals(customer.getId(), tableRows.getFirst().getId());

        setFilters("", SURNAME);
        when(customHibernate.findBySurname(User.class, SURNAME)).thenReturn(List.of(restaurant));
        mainForm.filterData();

        assertEquals(1, tableRows.size());
        assertEquals(restaurant.getId(), tableRows.getFirst().getId());
    }

    private void setFilters(String name, String surname) {
        when(mainForm.filterName.getText()).thenReturn(name);
        when(mainForm.filterSurname.getText()).thenReturn(surname);
    }

    private void assertFilteredRows() {
        assertEquals(2, tableRows.size());
        assertRow(customer, CUSTOMER_ADDRESS, "BasicUser", tableRows.get(0));
        assertRow(restaurant, null, "Restaurant", tableRows.get(1));
    }

    private static void assertRow(User user, String address, String userType, UserTableParameters row) {
        assertAll(
                () -> assertEquals(user.getId(), row.getId(), "id"),
                () -> assertEquals(user.getLogin(), row.getLogin(), "login"),
                () -> assertEquals(user.getPassword(), row.getPassword(), "password"),
                () -> assertEquals(user.getName(), row.getName(), "name"),
                () -> assertEquals(user.getSurname(), row.getSurname(), "surname"),
                () -> assertEquals(user.getEmail(), row.getEmail(), "email"),
                () -> assertEquals(user.getPhone_number(), row.getPhoneNumber(), "phone number"),
                () -> assertEquals(address, row.getAddress(), "address"),
                () -> assertEquals(userType, row.getUserType(), "user type")
        );
    }
}
