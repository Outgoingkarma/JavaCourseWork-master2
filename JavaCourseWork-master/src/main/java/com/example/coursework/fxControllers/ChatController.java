package com.example.coursework.fxControllers;

import com.example.coursework.hibernateControl.GenericHibernate;
import com.example.coursework.model.*;
import com.example.coursework.utils.FxUtils;
import jakarta.persistence.EntityManagerFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    public Label orderLabel;
    @FXML
    public Label participantsLabel;
    @FXML
    public Label unreadLabel;
    @FXML
    public ListView<ChatMessage> messagesListView;
    @FXML
    public TextArea messageInput;
    @FXML
    public Button sendButton;
    @FXML
    public Button markReadButton;
    @FXML
    public Button refreshButton;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ObservableList<ChatMessage> messages = FXCollections.observableArrayList();
    public Button deleteChatMessageButton;
    public Button editChatMessageButton;
    private Timeline autoRefreshTimeline;
    private GenericHibernate genericHibernate;
    private FoodOrder order;
    private Chat chat;
    private User currentUser;
//a
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        messagesListView.setItems(messages);
        messagesListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(ChatMessage item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatMessage(item));
                }
            }
        });

        messagesListView.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnHidden(event -> stopAutoRefresh());
                    }
                });
            }
        });
    }

    public void setData(EntityManagerFactory emf, FoodOrder foodOrder, User user) {
        this.genericHibernate = new GenericHibernate(emf);
        this.currentUser = user;
        this.order = genericHibernate.getRecordById(FoodOrder.class, foodOrder.getId());
        if (this.order == null) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Chat", "Order not found", "Unable to load chat for the selected order.");
            return;
        }
        this.chat = ensureChatExists(this.order);
        configureMessagePermissions();
        updateHeader();
        refreshMessages();
        if (currentUser == null) {
            sendButton.setDisable(true);
            markReadButton.setDisable(true);
        }
        startAutoRefresh();
    }

    private void configureMessagePermissions() {
        boolean allowModifications = currentUser != null && !(currentUser instanceof Restaurant);
        deleteChatMessageButton.setVisible(allowModifications);
        deleteChatMessageButton.setManaged(allowModifications);
        deleteChatMessageButton.setDisable(!allowModifications);

        editChatMessageButton.setVisible(allowModifications);
        editChatMessageButton.setManaged(allowModifications);
        editChatMessageButton.setDisable(!allowModifications);
    }

    private Chat ensureChatExists(FoodOrder managedOrder) {
        if (managedOrder == null) {
            return null;
        }

        managedOrder.ensureChat();
        genericHibernate.update(managedOrder);
        FoodOrder refreshedOrder = genericHibernate.getRecordById(FoodOrder.class, managedOrder.getId());
        if (refreshedOrder != null) {
            this.order = refreshedOrder;
            return refreshedOrder.getChat();
        }


        return managedOrder.getChat();


    }

    private void updateHeader() {
        if (orderLabel != null && order != null) {
            orderLabel.setText("Order #" + order.getId());
        }
        if (participantsLabel != null && order != null) {
            participantsLabel.setText(buildParticipantsDescription(order));
        }
        updateUnreadLabel();
    }

    private String buildParticipantsDescription(FoodOrder order) {
        List<String> participants = new ArrayList<>();
        if (order.getCustomer() != null) {
            participants.add("Customer: " + order.getCustomer().getName() + " " + order.getCustomer().getSurname());
        }
        if (order.getDriver() != null) {
            participants.add("Driver: " + order.getDriver().getName() + " " + order.getDriver().getSurname());
        }
        if (order.getRestaurant() != null) {
            participants.add("Restaurant: " + order.getRestaurant().getRestaurantName());
        }
        return participants.isEmpty() ? "Participants not assigned" : String.join(" | ", participants);
    }

    private void refreshMessages() {
        if (chat == null) {
            messages.clear();
            return;
        }
        Chat managedChat = genericHibernate.getRecordById(Chat.class, chat.getId());
        if (managedChat != null) {
            this.chat = managedChat;
            List<ChatMessage> sorted = managedChat.getMessages() == null ? List.of() :
                    managedChat.getMessages().stream()
                            .sorted(Comparator.comparing(ChatMessage::getDateCreated))
                            .toList();
            messages.setAll(sorted);
            if (!messages.isEmpty()) {
                messagesListView.scrollTo(messages.size() - 1);
            }
        } else {
            messages.clear();
        }
        updateUnreadLabel();
    }

    private void updateUnreadLabel() {
        if (unreadLabel == null) {
            return;
        }
        if (chat == null || currentUser == null) {
            unreadLabel.setText("Unread: 0");
            return;
        }
        unreadLabel.setText("Unread: " + chat.getUnreadCount(currentUser));
    }

    @FXML
    public void sendMessage() {
        if (currentUser == null) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Chat", "No sender", "You must be logged in to send a message.");
            return;
        }
        if (chat == null) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Chat", "Chat missing", "Unable to find chat for this order.");
            return;
        }
        String text = messageInput.getText();
        if (text == null || text.trim().isEmpty()) {
            FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Chat", "Empty message", "Enter a message before sending.");
            return;
        }
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageText(text.trim());
        chatMessage.setMessageSender(currentUser);
        chatMessage.setDateCreated(java.time.LocalDateTime.now());
        chatMessage.setRead(false);
        chat.addMessage(chatMessage);
        genericHibernate.update(chat);
        messageInput.clear();
        refreshMessages();
    }

    @FXML
    public void markAllAsRead() {
        if (chat == null || currentUser == null) {
            return;
        }
        chat.markAllAsRead(currentUser);
        genericHibernate.update(chat);
        refreshMessages();
    }

    @FXML
    public void refreshChat() {
        refreshMessages();
    }


    private void startAutoRefresh() {
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop();
        }

        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> refreshMessages()));
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();
    }

    private void stopAutoRefresh() {
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop();
        }
    }




    private String formatMessage(ChatMessage item) {
        String senderName = "Unknown";
        if (item.getMessageSender() != null) {
            senderName = item.getMessageSender().getName() + " " + item.getMessageSender().getSurname();
        }
        String time = item.getDateCreated() == null ? "" : DATE_FORMATTER.format(item.getDateCreated());
        String readState = item.isRead() ? "" : " (unread)";
        return String.format("[%s] %s: %s%s", time, senderName, item.getMessageText(), readState);
    }

    public void deleteChatMessage(ActionEvent actionEvent) {

    }

    public void editChatMessage(ActionEvent actionEvent) {
    }
}