package pro.sky.tbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    private Long chatId;
    private String messageContent;
    private LocalDateTime notificationSendDatetime;

    public NotificationTask(long id, Long chatId, String messageContent,
                            LocalDateTime notificationSendDatetime) {
        this.id = id;
        this.chatId = chatId;
        this.messageContent = messageContent;
        this.notificationSendDatetime = notificationSendDatetime;
    }

    public NotificationTask() {

    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public LocalDateTime getNotificationSendDatetime() {
        return notificationSendDatetime;
    }

    public void setNotificationSendDatetime(LocalDateTime notificationSendDatetime) {
        this.notificationSendDatetime = notificationSendDatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return id == that.id && chatId == that.chatId && Objects.equals(messageContent, that.messageContent) && Objects.equals(notificationSendDatetime, that.notificationSendDatetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, messageContent, notificationSendDatetime);
    }

    @Override
    public String toString() {
        return "TelegramBotNotificationTask{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", notificationText='" + messageContent + '\'' +
                ", notificationSendDatetime=" + notificationSendDatetime +
                '}';
    }
}
