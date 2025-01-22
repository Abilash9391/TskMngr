package TskMngr.service.interfaces;

import TskMngr.dto.ApiResponse;
import TskMngr.model.Task;


import java.util.List;

public interface TaskServiceInterface {
    ApiResponse createTask(Task task, Long userId);

    ApiResponse getTaskById(Integer taskId);

    List<Task> getAllTasks(Long userId);

    ApiResponse updateTask(Task task, Integer id);

    public void deleteTask(Integer id);

    ApiResponse doneTask(Integer id);

    ApiResponse pendingTask(Integer id);
}
