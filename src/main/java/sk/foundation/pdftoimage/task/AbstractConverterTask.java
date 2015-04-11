package sk.foundation.pdftoimage.task;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.Callable;

public abstract class AbstractConverterTask implements Callable<File> {

	private InputStream inputStream;
	private UUID id;

	public AbstractConverterTask(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public File call() throws Exception {
		return convertInput();
	}

	public abstract File convertInput();

	public abstract String getState();

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

}