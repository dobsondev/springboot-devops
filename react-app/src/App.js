import { useState, useEffect } from 'react'

import Header from "./components/Header"
import Tasks from "./components/Tasks"
import AddTask from './components/AddTask'

const App = () => {
  const api_url = process.env.NODE_ENV === 'production' ? 'http://springboot.local/api' : process.env.REACT_APP_LOCAL_SPRINGBOOT_URL;

  const [showAddTask, setShowAddTask] = useState(false)
  const [tasks, setTasks] = useState([])

  useEffect(() => {
    const getTasks = async () => {
      const tasksFromServer = await fetchTasks()
      setTasks(tasksFromServer)
    }

    getTasks()
  }, [])

  // Fetch Tasks
  const fetchTasks = async () => {
    const res = await fetch(api_url + '/tasks')
    const data = await res.json()

    return data
  }

  // Fetch Task
  const fetchTask = async (id) => {
    const res = await fetch(`${api_url}/tasks/${id}`)
    const data = await res.json()

    return data
  }

  // Add Task
  const addTask = async (task) => {
    const res = await fetch(api_url + '/tasks', {
      method: 'POST',
      headers: {
        'Content-type': 'application/json',
      },
      body: JSON.stringify(task),
    })

    const data = await res.json()

    setTasks([...tasks, data])
  }

  // Delete Task
  const deleteTask = async (id) => {
    const res = await fetch(`${api_url}/tasks/${id}`, {
      method: 'DELETE',
    })
    // We should control the response status to decide if we will change 
    // the state or not.
    res.status === 204
      ? setTasks(tasks.filter((task) => task.id !== id))
      : alert('Error Deleting This Task')
  }

  // Toggle Completed
  const toggleCompleted = async (id) => {
    const taskToToggle = await fetchTask(id)
    const updTask = { ...taskToToggle, completed: !taskToToggle.completed }

    const res = await fetch(`${api_url}/tasks/${id}`, {
      method: 'PUT',
      headers: {
        'Content-type': 'application/json',
      },
      body: JSON.stringify(updTask),
    })

    const data = await res.json()

    setTasks(
      tasks.map((task) =>
        task.id === id ? { ...task, completed: data.completed } : task
      )
    )
  }

  return (
    <div className="container">
      <Header
        onAdd={() => setShowAddTask(!showAddTask)}
        showAdd={showAddTask}
      />
      {showAddTask && <AddTask onAdd={addTask} />}
      {tasks.length > 0 ? (
        <Tasks 
          tasks={tasks} 
          onDelete={deleteTask} 
          onToggle={toggleCompleted}
        />
      ) : (
        'No tasks to display.'
      )}
    </div>
  );
}

export default App;
