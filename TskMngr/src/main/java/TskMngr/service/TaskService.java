package TskMngr.service;

import TskMngr.dto.ApiResponse;
import TskMngr.exception.ResourceNotFoundException;
import TskMngr.model.Task;
import TskMngr.model.User;
import TskMngr.repository.TaskRepository;
import TskMngr.repository.UserRepository;
import TskMngr.service.interfaces.TaskServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService implements TaskServiceInterface {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public ApiResponse createTask(Task task, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found, Id: " + userId));
        task.setUser(user);

        Task savedTask = taskRepository.save(task);
        return new ApiResponse("Task Saved", savedTask);
    }

    @Override
    public ApiResponse getTaskById(Integer taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(()-> new ResourceNotFoundException("Task not found, Id: " + taskId));
        return new ApiResponse("Found task", task);
    }

    @Override
    public List<Task> getAllTasks(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found, Id" + userId));
        List<Task> taskList = taskRepository.findAllByUserId(user.getId());
        return taskList;
    }

    @Override
    public ApiResponse updateTask(Task task, Integer id) {
        Task foundTask = taskRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Task not found, Id" + id));
        foundTask.setTask(task.getTask());
        foundTask.setCompleted(task.getCompleted());
        foundTask.setDetails(task.getDetails()); // Set details
        Task updatedTask = taskRepository.save(foundTask);
        return new ApiResponse("Task updated!",updatedTask);
    }

    @Override
    public void deleteTask(Integer id) {
        Task task = taskRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Task not found, Id" + id));
        taskRepository.delete(task);
    }

    @Override
    public ApiResponse doneTask(Integer id) {
        Task task = taskRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Task not found, Id" +id));
        task.setCompleted(true);
        return new ApiResponse("Task done", taskRepository.save(task));
    }

    @Override
    public ApiResponse pendingTask(Integer id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task Not Found, Id: " + id));
        task.setCompleted(false);
        return new ApiResponse("Task pending", taskRepository.save(task));
    }
}
