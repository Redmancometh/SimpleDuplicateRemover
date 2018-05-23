package com.redmancometh.wipedupes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.codec.digest.DigestUtils;

public class DupeWipe {
	public static void main(String[] args) {
		File f = new File("cache");
		Path root = Paths.get(f.getAbsolutePath());
		try {
			Files.walk(root).parallel().filter((newPath) -> !Files.isDirectory(newPath)).forEach((path) -> {
				try (InputStream in = Files.newInputStream(path)) {
					System.out.println(path);
					checkDuplicatesFor(path, DigestUtils.md5Hex(in));
				} catch (IOException e) {
					if (e instanceof AccessDeniedException) {
						System.out.println("Access denied are you fucking kidding me?");
						System.out.println("Path: " + path);
					} else
						e.printStackTrace();
				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void checkDuplicatesFor(Path toCheck, String hash) throws IOException {
		File f = new File("cache");
		Path root = Paths.get(f.getAbsolutePath());
		Files.walk(root).parallel().filter((newPath) -> !Files.isDirectory(newPath)).forEach((path) -> {
			try {
				String otherHash = DigestUtils.md5Hex(Files.newInputStream(path));
				if ((!toCheck.toString().equals(path.toString())) && hash.equals(otherHash)) {
					System.out.println("found duplicate!");
					Files.delete(path);
				}

			} catch (IOException e) {
				if (e instanceof AccessDeniedException) {
					System.out.println("Access denied (INNER!) are you fucking kidding me?");
					System.out.println("Path: " + path);
				} else
					e.printStackTrace();
			}
		});
	}
}
