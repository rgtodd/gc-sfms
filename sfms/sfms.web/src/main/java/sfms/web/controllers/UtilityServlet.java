package sfms.web.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import sfms.storage.Storage;

@WebServlet(name = "utilityServlet", description = "Handles file upload requests", urlPatterns = "/utilityServlet/*")
public class UtilityServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final Logger logger = Logger.getLogger(UtilityServlet.class.getName());

	private static final int MAX_FILE_SIZE = 50000000;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			ServletFileUpload upload = new ServletFileUpload();
			upload.setSizeMax(MAX_FILE_SIZE);

			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();

				if (item.isFormField()) {
					// out.println("Got a form field: " + item.getFieldName());
				} else {
					// String fieldName = item.getFieldName();
					// InputStream in = item.openStream();

					ZonedDateTime utcNow = ZonedDateTime.now(ZoneOffset.UTC);
					String fileName = item.getName();
					String uploadedFileName = utcNow.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_"
							+ fileName;

					String bucketName = "rgt-ssms.appspot.com";
					String blobName = "uploads/" + uploadedFileName;
					String contentType = item.getContentType();

					logger.info("Saving to " + blobName);

					try (InputStream inputStream = item.openStream()) {
						try (WritableByteChannel writeChannel = Storage.getManager().getWritableByteChannel(bucketName,
								blobName,
								contentType)) {
							byte[] buffer = new byte[1024];
							int limit;
							while ((limit = inputStream.read(buffer)) >= 0) {
								writeChannel.write(ByteBuffer.wrap(buffer, 0, limit));
							}
						}
					}

					res.sendRedirect("/utility/uploadComplete?" + WebParameters.FILE_NAME + "=" + uploadedFileName);

					return;

					// out.println("--------------");
					// out.println("fileName = " + fileName);
					// out.println("field name = " + fieldName);
					// out.println("contentType = " + contentType);

					// String fileContents = null;
					// try {
					// fileContents = IOUtils.toString(in);
					// out.println("lenght: " + fileContents.length());
					// out.println(fileContents);
					// } finally {
					// IOUtils.closeQuietly(in);
					// }

				}
			}
		} catch (FileUploadException e) {
			throw new ServletException(e);
		}
	}

}
