package pro.sky.tbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.tbot.entity.NotificationTask;
import pro.sky.tbot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private TelegramBot telegramBot;
    private final NotificationTaskRepository notificationTaskRepository;
    private final static String STR_CMD = "/start";
    private final static String GREET_MSG = "Добро пожаловать в чатбот-напоминалку!";
    private final static String WRNG_FORM_MSG = "Команда не распознана. " +
            "Пожалуйста, проверьте формат введенного сообщения";

    private final static Pattern PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");


    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);


    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskRepository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
    }


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
                sendMessage(getChatId(message), GREET_MSG);
            } else {
                Matcher matcher = PATTERN.matcher(message.text());

                if (matcher.matches()) {
                    logger.info("Task's been recieved.");
                    String date = matcher.group(1);
                    String item = matcher.group(3);
                    LocalDateTime dateTime = LocalDateTime.parse(date,
                            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

                    NotificationTask task = new NotificationTask();
                    task.setChatId(getChatId(message));
                    task.setNotificationSendDatetime(dateTime);
                    task.setMessageContent(item);
                    notificationTaskRepository.save(task);
                } else {
                    logger.info("Wrong format. Command wasn`t recognized.");
                    sendMessage(getChatId(message), WRNG_FORM_MSG);
                }
            }
        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }



    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> send = notificationTaskRepository.findAllByNotificationSendDatetime(now);
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

    private Long getChatId(Message message) {
        return message.chat().id();
    }
}
