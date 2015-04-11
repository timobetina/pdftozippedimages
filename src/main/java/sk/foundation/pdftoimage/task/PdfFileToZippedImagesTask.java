package sk.foundation.pdftoimage.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.qoppa.pdf.PDFException;
import com.qoppa.pdfImages.PDFImages;

import org.apache.commons.io.IOUtils;

public class PdfFileToZippedImagesTask extends AbstractConverterTask {

	private static final String PAGE_IMAGE_FILENAME = "output_";
	private static final String JPEG_SUFFIX = ".jpg";
	private static final String ARCHIVE_FILENAME = "archive.zip";
	private static final String PROCESSING_FINISHED_STATE = "processing finished";
	private static final String PROCESSING_PAGE_STATE = "processing page number ";

	private File parentDir;
	private String state;

	public PdfFileToZippedImagesTask(InputStream inputStream, File parentDir) {
		super(inputStream);
		this.parentDir = parentDir;
		state = "not started";
	}

	@Override
	public File convertInput() {
		ZipOutputStream zipOutputStream = null;
		File fileArchive = null;
		try {
			PDFImages pdfDoc = new PDFImages(getInputStream(), null);
			fileArchive = createFileInDir(ARCHIVE_FILENAME);
			zipOutputStream = new ZipOutputStream(new FileOutputStream(fileArchive));

			writePagesToZipArchive(pdfDoc, zipOutputStream);
			state = PROCESSING_FINISHED_STATE;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(zipOutputStream);
		}

		return fileArchive;
	}

	public String getState() {
		return state;
	}

	private void writePagesToZipArchive(PDFImages pdfDoc, ZipOutputStream zipOutputStream) throws IOException, PDFException {
		for (int page = 0; page < pdfDoc.getPageCount(); page++) {
			File imageFilename = writePageImageToFile(pdfDoc, page);
			copyFileToZipArchive(zipOutputStream, imageFilename);
			state = PROCESSING_PAGE_STATE + (page + 1);
		}
	}

	private File writePageImageToFile(PDFImages pdfDoc, int page) throws IOException, PDFException {
		File imageFile = createFileInDir(PAGE_IMAGE_FILENAME + page + JPEG_SUFFIX);
		pdfDoc.savePageAsJPEG(page, imageFile.getAbsolutePath(), 150, 0.8f);
		return imageFile;
	}

	private void copyFileToZipArchive(ZipOutputStream zipOutputStream, File fileToCopy) throws IOException {
		FileInputStream imageFileInputStream = null;
		try {
			imageFileInputStream = new FileInputStream(fileToCopy);
			zipOutputStream.putNextEntry(new ZipEntry(fileToCopy.getName()));

			IOUtils.copy(imageFileInputStream, zipOutputStream);

			zipOutputStream.closeEntry();
		} finally {
			IOUtils.closeQuietly(imageFileInputStream);
		}
	}

	private File createFileInDir(String filename) throws IOException {
		File file = new File(parentDir, filename);
		file.createNewFile();
		return file;
	}

}
