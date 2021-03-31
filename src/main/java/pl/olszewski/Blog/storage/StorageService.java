package pl.olszewski.Blog.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import pl.olszewski.Blog.domain.Post;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface StorageService {

	void init();

	void store(MultipartFile[] files);

	Stream<Path> loadAll();

	Path load(String filename);

	Resource loadAsResource(String filename);

	void deleteAll();

	void getCSV();
}
