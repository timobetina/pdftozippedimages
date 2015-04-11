package sk.foundation.pdftoimage.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.stereotype.Service;

import sk.foundation.pdftoimage.task.AbstractConverterTask;

@Service
public class ConverterExecutorService<T extends AbstractConverterTask> {

	public static final String NO_TASK_WITH_ID_MESSAGE = "task does not exist with id ";

	@Autowired
	private SchedulingTaskExecutor taskExecutor;

	private Map<UUID, T> taskMap = Collections.synchronizedMap(new HashMap<UUID, T>());

	public String executeTask(T task) {
		UUID uuid = addTaskToMap(task);

		taskExecutor.submit(task);

		return uuid.toString();
	}

	public String getStateOfTask(String id) {
		try {
			T task = getTaskFromMap(id);

			if (task != null)
				return task.getState();
			else
				return NO_TASK_WITH_ID_MESSAGE + id;

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return NO_TASK_WITH_ID_MESSAGE + id;
		}
	}

	public List<AbstractConverterTask> getAllTasks() {
		List<AbstractConverterTask> taskList = new ArrayList<AbstractConverterTask>();

		for (AbstractConverterTask task : taskMap.values()) {
			taskList.add(task);
		}

		return taskList;
	}

	private T getTaskFromMap(String id) {
		UUID uuid = UUID.fromString(id);
		return taskMap.get(uuid);
	}

	private UUID addTaskToMap(T task) {
		UUID uuid = UUID.randomUUID();
		task.setId(uuid);
		taskMap.put(uuid, task);
		return uuid;
	}

	public void setTaskExecutor(SchedulingTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}
}
