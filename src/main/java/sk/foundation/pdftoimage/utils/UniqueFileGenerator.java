package sk.foundation.pdftoimage.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class UniqueFileGenerator {

	public synchronized File generateUniqueFileInDir() {
		return generateUniqueFileInDir(null);
	}

	public synchronized File generateUniqueFileInDir(File dir) {
		return generateUniqueFileInDir(dir, null);
	}

	public synchronized File generateUniqueFileInDir(File dir, String suffix) {
		File file = generateUniqueFile(dir, suffix);
		while (file.exists()) {
			file = generateUniqueFile(dir, suffix);
		}

		return file;
	}

	private File generateUniqueFile(File dir, String suffix) {
		return dir == null ? new File(generateUniqueFilename(suffix)) : new File(dir, generateUniqueFilename(suffix));
	}

	private String generateUniqueFilename(String suffix) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_SSS");
		String dateTime = dateFormat.format(new Date());
		String filename = dateTime + "_" + generateRandomSequence(10);

		if (suffix != null)
			filename += "." + suffix;

		return filename;
	}

	private String generateRandomSequence(int length) {
		final Random random = new Random();

		final String alphabet = "0123456789abcdefghijklmnopqrstuvwxyz";

		StringBuilder builder = new StringBuilder("");
		for (int i = 0; i < length; i++) {
			int rand = random.nextInt(alphabet.length());
			builder.append(alphabet.charAt(rand));
		}
		return builder.toString();
	}
}
