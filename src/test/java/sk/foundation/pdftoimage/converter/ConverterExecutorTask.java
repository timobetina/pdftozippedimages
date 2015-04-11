package sk.foundation.pdftoimage.converter;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sk.foundation.pdftoimage.Application;
import sk.foundation.pdftoimage.task.AbstractConverterTask;
import sk.foundation.pdftoimage.task.PdfFileToZippedImagesTask;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ConverterExecutorTask {

	@Autowired
	private ConverterExecutorService converterExecutorService;

	private SchedulingTaskExecutor taskExecutor;
	private AbstractConverterTask task;

	@Before
	public void mockTaskExecutor() {
		taskExecutor = Mockito.mock(SchedulingTaskExecutor.class);
		task = Mockito.mock(AbstractConverterTask.class);

		converterExecutorService.setTaskExecutor(taskExecutor);
	}

	@Test
	public void executeTaskWithCorrectId() {
		UUID[] generatedId = getMockedTaskId();

		String retrievedId = converterExecutorService.executeTask(task);

		Assert.assertEquals(generatedId[0].toString(), retrievedId);
		Mockito.verify(taskExecutor).submit(Mockito.eq(task));
	}

	@Test
	public void getCorrectTaskStateForExistingId() {
		UUID[] generatedId = getMockedTaskId();

		Mockito.when(task.getState()).thenReturn("OK State");

		converterExecutorService.executeTask(task);
		Assert.assertEquals("OK State", converterExecutorService.getStateOfTask(generatedId[0].toString()));
	}

	@Test
	public void getCorrectTaskStateForNotExistingId() {
		String retrievedMessage = converterExecutorService.getStateOfTask("som fake id");
		Assert.assertTrue(retrievedMessage.startsWith(converterExecutorService.NO_TASK_WITH_ID_MESSAGE));
	}

	@Test
	public void getAllRunningTasks() {
		AbstractConverterTask task1 = new PdfFileToZippedImagesTask(null, null);
		AbstractConverterTask task2 = new PdfFileToZippedImagesTask(null, null);

		converterExecutorService.executeTask(task1);
		converterExecutorService.executeTask(task2);

		List<AbstractConverterTask> taskList = converterExecutorService.getAllTasks();

		Assert.assertTrue(taskList.contains(task1));
		Assert.assertTrue(taskList.contains(task2));
	}

	private UUID[] getMockedTaskId() {
		final UUID[] generatedId = new UUID[1];

		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
				generatedId[0] = (UUID) invocationOnMock.getArguments()[0];
				return null;
			}
		}).when(task).setId(Mockito.any(UUID.class));

		return generatedId;
	}
}
