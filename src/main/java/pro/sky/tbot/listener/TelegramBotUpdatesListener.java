package pro.sky.tbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.tbot.entity.NotificationTask;
import pro.sky.tbot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final NotificationTaskRepository notificationTaskRepository;
    private static String STR_CMD = "/start";
    private static String GREET_MSG = "Добро пожаловать в чатбот-напоминалку!";
    private static String WRNG_FORM_MSG = "Команда не распознана. " +
            "Пожалуйста, проверьте формат введенного сообщения";

    private static Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");


    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);


    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            Message message = update.message();


            if (message.text().equals(STR_CMD)) {
                logger.info("Command's been recieved: " + STR_CMD);
                sendMessage(showChatId(message), GREET_MSG);
            } else {
                Matcher matcher = pattern.matcher(message.text());

                if (matcher.matches()) {
                    logger.info("Task's been recieved.");
                    String date = matcher.group(1);
                    String item = matcher.group(3);
                    LocalDateTime dateTime = LocalDateTime.parse(date,
                            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

                    NotificationTask task = new NotificationTask();
                    task.setChatId(showChatId(message));
                    task.setNotificationSendDatetime(dateTime);
                    task.setMessageContent(item);
                    notificationTaskRepository.save(task);
                } else {
                    logger.info("Wrong format. Command wasn`t recognized.");
                    sendMessage(showChatId(message), WRNG_FORM_MSG);
                }
            }
        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }



    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        List<NotificationTask> send = notificationTaskRepository.getTasksToSend();
        send.forEach(task -> {
            logger.info("Sending a task: {}", task);
            sendMessage(task);
        });
    }

    private void sendMessage(Long chatId, String messageContent) {
        SendMessage sendMessage = new SendMessage(chatId, messageContent);
        telegramBot.execute(sendMessage);
    }

    private void sendMessage(NotificationTask task) {
        sendMessage(task.getChatId(), task.getMessageContent());
    }

    private Long showChatId(Message message) {
        return message.chat().id();
    }
}
