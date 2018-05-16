package com.redmancometh.wipedupes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DupeWipe {
	private static List<Path> deleted = new CopyOnWriteArrayList();
	public static void main(String[] args) {
		File f = new File("cache");
		Path root = Paths.get(f.getAbsolutePath());
		try {
			Files.walk(root).filter((newPath) -> !Files.isDirectory(newPath)).forEach((path) -> {
				try {
					if (!deleted.contains(path)) {
						System.out.println(path);
						byte[] bytes = MessageDigest.getInstance("MD5").digest(Files.readAllBytes(path));
						checkDuplicatesFor(path, bytes);
					}
				} catch (NoSuchAlgorithmException | IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void checkDuplicatesFor(Path toCheck, byte[] bytes) throws IOException {
		File f = new File("cache");
		Path root = Paths.get(f.getAbsolutePath());
		Files.walk(root).filter((newPath) -> !Files.isDirectory(newPath)).forEach((path) -> {
			byte[] otherBytes;
			try {
				otherBytes = MessageDigest.getInstance("MD5").digest(Files.readAllBytes(path));
				if ((!deleted.contains(path)) && path.toAbsolutePath().equals(toCheck) && Arrays.equals(bytes, otherBytes)) {
					System.out.println("found duplicate!");
					try {
						deleted.add(path);
						Files.delete(path);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (NoSuchAlgorithmException | IOException e1) {
				e1.printStackTrace();
			}

		});
	}
}
