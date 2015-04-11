package sk.foundation.pdftoimage.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import sk.foundation.pdftoimage.converter.ConverterExecutorService;
import sk.foundation.pdftoimage.task.PdfFileToZippedImagesTask;
import sk.foundation.pdftoimage.utils.UniqueFileGenerator;

@Controller
public class FileConverterController {

	@Autowired
	private ConverterExecutorService converterExecutorService;
	@Autowired
	private UniqueFileGenerator uniqueFileGenerator;

	@RequestMapping(value = "/uploadFile", method = RequestMethod.GET)
	public String greetingForm() {
		return "uploadFile";
	}

	@RequestMapping(value = "/stateUpdated", method = RequestMethod.GET)
	public String stateOfAllTasks(Model model) {
		model.addAttribute("taskList", converterExecutorService.getAllTasks());

		return "stateUpdated";
	}

	@RequestMapping(value = "/stateUpdated", method = RequestMethod.POST)
	public String stateOfAllTasksRefresh(Model model) {
		model.addAttribute("taskList", converterExecutorService.getAllTasks());

		return "stateUpdated";
	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("repeat") String repeat, Model model) {
		if (!file.isEmpty()) {
			try {
				int repeatCount = repeat == null ? 1 : Integer.valueOf(repeat);
				for (int i = 0; i < repeatCount; i++) {
					File dirOfProcessedFile = generateAndCreateDir();
					PdfFileToZippedImagesTask task = new PdfFileToZippedImagesTask(file.getInputStream(), dirOfProcessedFile);

					converterExecutorService.executeTask(task);

					model.addAttribute("file", file);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		} else {
			model.addAttribute("error", "file is empty");
		}
		return "uploadFile";
	}

	@RequestMapping(value = "/state", method = RequestMethod.GET)
	public String stateOfExecutedTask(Model model, @RequestParam("id") String id) {
		model.addAttribute("stateOfTask", converterExecutorService.getStateOfTask(id));
		return "state";
	}

	private File generateAndCreateDir() {
		File dirOfProcessedFile = uniqueFileGenerator.generateUniqueFileInDir();
		dirOfProcessedFile.mkdir();
		return dirOfProcessedFile;
	}
}
