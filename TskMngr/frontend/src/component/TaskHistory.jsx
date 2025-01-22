import { useEffect, useState } from "react";
import {
  deleteTask,
  markDone,
  markPending,
  retrieveAllTasks,
} from "../service/TaskApiService";
import { Link, useNavigate } from "react-router-dom";
import { FaTrash, FaPen, FaEye, FaCheck, FaTimes } from "react-icons/fa";
import "../css/tasks.css";

const TaskHistory = ({ userId }) => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    allTasks(userId);
  }, [userId]);

  // Function to fetch all tasks
  function allTasks(userId) {
    setLoading(true);
    retrieveAllTasks(userId)
      .then((response) => {
        setTasks(response.data);
        setLoading(false);
      })
      .catch((error) => {
        setError("Failed to load tasks.");
        setLoading(false);
      });
  }

  // Function to mark task as done or pending
  function markTask(id, isChecked) {
    const updatedTasks = tasks.map((task) =>
      task.id === id ? { ...task, completed: isChecked } : task
    );
    setTasks(updatedTasks); // Optimistically update the task list

    if (isChecked) {
      markDone(id)
        .then(() => console.log("Marked as done"))
        .catch((error) => {
          console.error(error);
          setTasks(tasks); // Rollback to original tasks on error
        });
    } else {
      markPending(id)
        .then(() => console.log("Marked as pending"))
        .catch((error) => {
          console.error(error);
          setTasks(tasks); // Rollback to original tasks on error
        });
    }
  }

  // Function to delete a task
  function deleteTaskFun(id) {
    const updatedTasks = tasks.filter((task) => task.id !== id);
    setTasks(updatedTasks); // Optimistically remove the task from the state

    deleteTask(id)
      .then(() => console.log("Task deleted"))
      .catch((error) => {
        console.error(error);
        setTasks(tasks); // Rollback if deletion fails
      });
  }

  // Function to view task details
  const viewTaskDetails = (task) => {
    navigate(`/task-details/${task.id}`, { state: task });
  };

  // Function to navigate to update task page
  function updateTask(id) {
    navigate(`/update-task/${id}`);
  }

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-md-8">
          <div className="card shadow-lg">
            <div className="card-body">
              <div className="d-flex justify-content-between align-items-center mb-4">
                <h2 className="m-0">Task History</h2>
                <Link to="/add-task" className="btn btn-primary btn-sm">
                  <i className="fas fa-plus me-2"></i> Add Task
                </Link>
              </div>

              {loading ? (
                <p>Loading tasks...</p>
              ) : error ? (
                <p className="text-danger">{error}</p>
              ) : (
                tasks.length === 0 ? (
                  <p>No tasks available</p>
                ) : (
                  tasks.map(
                    (task) =>
                      task.completed && (
                        <div key={task.id} className="card mb-4">
                          <div className="card-body">
                            <div className="d-flex justify-content-end gap-2 mb-2">
                              <button
                                className="btn btn-sm btn-outline-primary"
                                onClick={() => viewTaskDetails(task)}
                              >
                                <FaEye />
                              </button>
                              <button
                                className="btn btn-sm btn-outline-secondary"
                                onClick={() => updateTask(task.id)}
                              >
                                <FaPen />
                              </button>
                              <button
                                className="btn btn-sm btn-outline-danger"
                                onClick={() => deleteTaskFun(task.id)}
                              >
                                <FaTrash />
                              </button>
                            </div>
                            <div className="d-flex align-items-center gap-3">
                              <div className="form-check">
                                <input
                                  className="form-check-input"
                                  checked={task.completed}
                                  onChange={(e) =>
                                    markTask(task.id, e.target.checked)
                                  }
                                  type="checkbox"
                                />
                              </div>
                              <div
                                className={`${
                                  task.completed
                                    ? "text-decoration-line-through text-muted"
                                    : ""
                                }`}
                              >
                                <strong>{task.task}</strong>
                              </div>
                            </div>
                            <div className="mt-2">
                              <small className="text-muted">
                                Created: {task.taskCreatedAt}
                              </small>
                              <div>
                                {task.completed ? (
                                  <FaCheck className="text-success" />
                                ) : (
                                  <FaTimes className="text-danger" />
                                )}
                              </div>
                            </div>
                          </div>
                        </div>
                      )
                  )
                )
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TaskHistory;
