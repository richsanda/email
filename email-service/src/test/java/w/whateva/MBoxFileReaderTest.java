package w.whateva;

import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.extractor.EmbeddedDocumentUtil;
import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mbox.MboxParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MBoxFileReaderTest {

    @Test
    public void readMessages() {
        Path mbox = Paths.get("/Users/rich/life2/data/email/bill_shwah/Sent.mbox");
        MessageReader reader = new MBoxFileReader(mbox);
        Message[] messages = reader.readMessages();
        System.out.println(messages.length);
    }

    @Test
    public void readMessagesTika() throws FileNotFoundException {
        MboxParser2 mbox = new MboxParser2();
        InputStream s = new FileInputStream("/Users/rich/life2/data/email/bill_shwah/Sent.mbox/mbox");
        // AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(10000000);
        Metadata metadata = new Metadata();
        try {
            mbox.setTracking(true);
            mbox.parse(s,handler,metadata,new ParseContext());
            System.out.println(handler.toString());
        } catch (Exception e) {
            System.out.println("hm");
        }
    }

    interface MessageReader {
        Message[] readMessages();
    }

    static class MBoxFileReader implements MessageReader {
        private final Path path;  // Path to .mbox file

        public MBoxFileReader(Path path) {
            this.path = path;
        }

        @Override
        public Message[] readMessages() {
            Message[] messages = new Message[0];
            URLName server = new URLName("smtp:" + path.toString());
            Properties props = new Properties();
            props.setProperty("mail.mime.address.strict", "false");
            Session session = Session.getDefaultInstance(props);
            try {
                Folder folder = session.getFolder(server);
                folder.open(Folder.READ_ONLY);
                messages = folder.getMessages();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return messages;
        }
    }

    static class MboxParser2 extends MboxParser {

        @Override
        public void parse(InputStream stream, org.xml.sax.ContentHandler handler, Metadata metadata, ParseContext context)
                throws IOException, TikaException, SAXException {

            EmbeddedDocumentExtractor extractor = EmbeddedDocumentUtil.getEmbeddedDocumentExtractor(context);

            String charsetName = "utf-8";

            metadata.set(Metadata.CONTENT_TYPE, MBOX_MIME_TYPE);
            metadata.set(Metadata.CONTENT_ENCODING, charsetName);

            InputStreamReader isr = new InputStreamReader(stream, charsetName);
            try (BufferedReader reader = new BufferedReader(isr)) {
                String curLine = reader.readLine();
                int mailItem = 0;
                do {
                    if (curLine.startsWith(MBOX_RECORD_DIVIDER)) {
                        Metadata mailMetadata = new Metadata();
                        Queue<String> multiline = new LinkedList<>();

                        XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
                        xhtml.startDocument();

                        //mailMetadata.add(EMAIL_FROMLINE_METADATA, curLine.substring(MBOX_RECORD_DIVIDER.length()));
                        //mailMetadata.set(Metadata.CONTENT_TYPE, "message/rfc822");
                        //mailMetadata.set(TikaCoreProperties.CONTENT_TYPE_OVERRIDE, "message/rfc822");
                        curLine = reader.readLine();
                        if (curLine == null) {
                            break;
                        }
                        ByteArrayOutputStream message = new ByteArrayOutputStream(100000);
                        do {
                            if (curLine.startsWith(" ") || curLine.startsWith("\t")) {
                                String latestLine = multiline.poll();
                                latestLine += " " + curLine.trim();
                                multiline.add(latestLine);
                            } else {
                                multiline.add(curLine);
                            }

                            message.write(curLine.getBytes(charsetName));
                            message.write('\n');
                            curLine = reader.readLine();
                        }
                        while (curLine != null && !curLine.startsWith(MBOX_RECORD_DIVIDER) && message.size() < MAIL_MAX_SIZE);

                        // for (String item : multiline) {
                        //    saveHeaderInMetadata(mailMetadata, item);
                        //}

                        ByteArrayInputStream messageStream = new ByteArrayInputStream(message.toByteArray());
                        try {
                            doWithMessage(messageStream);
                        } catch (Exception e) {
                            System.out.println("OWELL");
                        }
                        message = null;

                        if (extractor.shouldParseEmbedded(mailMetadata)) {
                            extractor.parseEmbedded(messageStream, xhtml, mailMetadata, true);
                        }

                        xhtml.endDocument();

                        mailItem++;
                        //if (tracking) {
                        //    getTrackingMetadata().put(mailItem++, mailMetadata);
                        //}
                    } else {
                        curLine = reader.readLine();
                    }

                } while (curLine != null && !Thread.currentThread().isInterrupted());
            }
        }

        private void doWithMessage(ByteArrayInputStream s) throws Exception {
            String content = IOUtils.toString(s);
            Session session = Session.getInstance(new Properties());
            InputStream is = new ByteArrayInputStream(content.getBytes());
            MimeMessage message = new MimeMessage(session, is);
            MimeMessageParser parser = new MimeMessageParser(message).parse();
            Map<String, String> headers = new HashMap<>();
            message.getAllHeaderLines();
            for (Enumeration<Header> e = message.getAllHeaders(); e.hasMoreElements();) {
                Header h = e.nextElement();
                headers.put(h.getName(), h.getValue());
            }
            if (!headers.containsKey("Message-ID")) {
                System.out.println(String.format("%s has no message id", "THISONE"));
            } else {
                System.out.println(headers.get("Message-ID"));
            }
            System.out.println(parser);
        }
    }
}
