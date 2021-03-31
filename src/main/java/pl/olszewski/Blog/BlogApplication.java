package pl.olszewski.Blog;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import pl.olszewski.Blog.domain.Author;
import pl.olszewski.Blog.storage.StorageProperties;
import pl.olszewski.Blog.storage.StorageService;

import java.io.*;

@SpringBootApplication
@ImportResource("classpath:beans.xml")
@EnableConfigurationProperties(StorageProperties.class)
public class BlogApplication {

	public static void main(String[] args) {
//		createXML();
		SpringApplication.run(BlogApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}

	private static void createXML() {
		try {
			PrintWriter pw = new PrintWriter("src/main/resources/beans.xml");
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			pw.println("<beans xmlns=\"http://www.springframework.org/schema/beans\"\n" +
					"       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
					"       xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\">\n");
			writeToXML(pw, new BufferedReader(new FileReader("files/Author.csv")), "Author");
			writeToXML(pw, new BufferedReader(new FileReader("files/Post_Author.csv")), "PostAuthorId");
			writeToXML(pw, new BufferedReader(new FileReader("files/Post.csv")), "Post");
			writeCommentsToXML(pw, new BufferedReader(new FileReader("files/Comment.csv")), "Comment");
			writeCommentsToXML(pw, new BufferedReader(new FileReader("files/Attachment.csv")), "Attachment");
			pw.println("</beans>");
			pw.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void writeToXML(PrintWriter pw, BufferedReader br, String fileName) throws IOException {
		String line = "";
		int x = 0;
		if ((line = br.readLine()) != null) {
			String[] head = line.split(",");
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
				pw.println("\t<bean id=\""+fileName+(++x)+"\" class=\"pl.olszewski.Blog.domain."+fileName+"\" >");
				for (int i = 0; i < head.length; i++) {
					pw.println("\t\t<property name=\""+head[i]+"\" value=\""+values[i].replaceAll("\"", "")+"\" />");
				}
				pw.println("\t</bean>\n");
			}
		}
	}
	private static void writeCommentsToXML(PrintWriter pw, BufferedReader br, String fileName) throws IOException {
		String line = "";
		int x = 0;
		if ((line = br.readLine()) != null) {
			String[] head = line.split(",");
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
				pw.println("\t<bean id=\""+fileName+(++x)+"\" class=\"pl.olszewski.Blog.domain."+fileName+"\" >");
				for (int i = 0; i < head.length; i++) {
					if(head[i].equals("id_post"))
					{
						pw.println("\t\t<property name=\""+"idPost"+"\" value=\""+values[i].replaceAll("\"", "")+"\" />");
					}
					else pw.println("\t\t<property name=\""+head[i]+"\" value=\""+values[i].replaceAll("\"", "")+"\" />");
				}
				pw.println("\t</bean>\n");
			}
		}
	}
}
