package pl.olszewski.Blog.storage;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.olszewski.Blog.domain.*;
import pl.olszewski.Blog.service.Menagers.*;

@Service
public class FileSystemStorageService implements StorageService {

	private final Path rootLocation;
	private AuthorManager am;
	private PostManager pm;
	private PostAuthorManager pam;
	private CommentManager cm;
	private AttachmentManager atm;

	@Autowired
	public FileSystemStorageService(StorageProperties properties, AttachmentManager atm, AuthorManager am, PostManager pm, CommentManager cm, PostAuthorManager pam) {
		this.rootLocation = Paths.get(properties.getLocation());
		this.am = am;
		this.pm = pm;
		this.cm = cm;
		this.pam = pam;
		this.atm = atm;
	}

	@Override
	public void store(MultipartFile[] files) {

		try {
			for (MultipartFile file: files) {
				if (!(file.isEmpty())) {
					Path destinationFile = this.rootLocation.resolve(
							Paths.get(file.getOriginalFilename()))
							.normalize().toAbsolutePath();
					if (destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
						try (InputStream inputStream = file.getInputStream()) {
							Files.copy(inputStream, destinationFile,
									StandardCopyOption.REPLACE_EXISTING);
						}
					}
				}
			}
		}
		catch (IOException ignored) {
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
				.filter(path -> !path.equals(this.rootLocation))
				.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
	@Override
	public void getCSV() {
	try {
		PrintWriter pw = new PrintWriter("upload-dir/Post.csv");
		for (Post post: pm.getAllPosts()) {
			pw.println(post.getId() + ";" + post.getPost_content()+ ";" + post.getTags());
		}
		pw.close();
	} catch (FileNotFoundException e) {
		System.err.println(e.getMessage());
	}
	try {
		PrintWriter pw = new PrintWriter("upload-dir/Attachment.csv");
		pw.println("id_post,filename");
		for (Attachment attachment: atm.getAllAttachments()) {
			pw.println(attachment.getIdPost() + ";" + attachment.getFilename());
		}
		pw.close();
	} catch (FileNotFoundException e) {
		System.err.println(e.getMessage());
	}
	try {
		PrintWriter pw = new PrintWriter("upload-dir/Author.csv");
		pw.println("id,first_name,last_name,username");
		for (Author author: am.getAllAuthors()) {
			pw.println(author.getId() + ";" +  author.getFirst_name() + ";" + author.getLast_name() + ";" + author.getUsername());
		}
		pw.close();
	} catch (FileNotFoundException e) {
		System.err.println(e.getMessage());
	}

	try {
		PrintWriter pw = new PrintWriter("upload-dir/Comment.csv");
		pw.println("id,username,id_post,comment_content");
		for (Comment comment: cm.getAllComment()) {
			pw.println(comment.getId() + ";" +  comment.getUsername() + ";" + comment.getIdPost() + ";" + comment.getComment_content());
		}
		pw.close();
	} catch (FileNotFoundException e) {
		System.err.println(e.getMessage());
	}

	try {
		PrintWriter pw = new PrintWriter("upload-dir/Post.csv");
		pw.println("id,post_content,tags");
		for (Post post: pm.getAllPosts()){
			pw.println(post.getId() + ";" + post.getPost_content() + ";" + post.getTags());
		}
		pw.close();
	} catch (FileNotFoundException e) {
		System.err.println(e.getMessage());
	}

	try {
		PrintWriter pw = new PrintWriter("upload-dir/Post_Author.csv");
		pw.println("id_post,id_author");
		for (PostAuthor postsAuthors: pam.getAllPostAuthor()) {
			pw.println(postsAuthors.getPost().getId() + ";" + postsAuthors.getAuthor().getId());
		}
		pw.close();
	} catch (FileNotFoundException e) {
		System.err.println(e.getMessage());
	}
}
}
