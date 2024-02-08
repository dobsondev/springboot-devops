import { FaTimes } from 'react-icons/fa'

const Task = ({ task, onDelete, onToggle }) => {
    return (
        <div className={`task ${task.completed ? 'completed' : ''}`} onDoubleClick={() => onToggle(task.id)}>
            <h3>
                {task.title}{' '}
                <FaTimes 
                onClick={() => onDelete(task.id)}
                style={{ color: 'red', cursor: 'pointer' }} 
                />
            </h3>
            <p>{task.description}</p>
        </div>
    )
}

export default Task