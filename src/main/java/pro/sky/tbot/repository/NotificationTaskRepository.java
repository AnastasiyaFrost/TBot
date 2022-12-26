package pro.sky.tbot.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.tbot.entity.NotificationTask;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Id> {
    LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

    @Query(value = "SELECT * FROM DB_teleBot WHERE notificationSendDatetime = now", nativeQuery = true)
    List<NotificationTask> getTasksToSend();
}
