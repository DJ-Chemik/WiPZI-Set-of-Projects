import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.james.mime4j.internal.ParserStreamContentHandler;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.tika.Tika;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOUtils;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageHandler;
import org.apache.tika.language.detect.LanguageResult;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class Exercise2
{

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private OptimaizeLangDetector langDetector;

    public static void main(String[] args)
    {
        Exercise2 exercise = new Exercise2();
        exercise.run();
    }

    private void run()
    {
        try
        {
            if (!new File("./outputDocuments").exists())
            {
                Files.createDirectory(Paths.get("./outputDocuments"));
            }

            initLangDetector();

            File directory = new File("./documents");
            File[] files = directory.listFiles();
            for (File file : files)
            {
                processFile(file);
            }
        } catch (IOException | SAXException | TikaException | ParseException e)
        {
            e.printStackTrace();
        }

    }

    private void initLangDetector() throws IOException
    {
        // TODO initialize language detector (langDetector)
        langDetector = new OptimaizeLangDetector();
    }

    private void processFile(File file) throws IOException, SAXException, TikaException, ParseException {
        Parser parser = new AutoDetectParser();
        MediaType mediaType;
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        FileInputStream content = new FileInputStream(file);

        parser.parse(content,handler,metadata,new ParseContext());
        LanguageIdentifier li = new LanguageIdentifier(handler.toString());
        String languageCode = li.toString().substring(0,2);

        String author = null;
        Date creationDate = null;
        Date lastModificationDate = null;

        ArrayList<String> authors = new ArrayList<>(Arrays.asList(metadata.getValues(Metadata.AUTHOR)));
        ArrayList<String> createDates = new ArrayList<>(Arrays.asList(metadata.getValues(Metadata.CREATION_DATE)));
        ArrayList<String> lastModificationDates = new ArrayList<>(Arrays.asList(metadata.getValues(Metadata.MODIFIED)));

        Tika tika = new Tika();
        tika.setMaxStringLength(-1);
        String mimeType = tika.detect(file);


        if (authors.size()>0){
            author=authors.get(0);
        }else{
            author="";
        }
        if (createDates.size()>0) {
            creationDate = new SimpleDateFormat(DATE_FORMAT).parse(createDates.get(0));
        }
        if (lastModificationDates.size()>0) {
            lastModificationDate = new SimpleDateFormat(DATE_FORMAT).parse(lastModificationDates.get(0));
        }


        // TODO: 09.03.2020 How to write all file to String???
        String finishContent = null;



        saveResult(file.getName(), languageCode, author, creationDate, lastModificationDate, mimeType, finishContent);
    }

    private String getFileContent( FileInputStream fis ) throws IOException {
        StringBuilder sb = new StringBuilder();
        Reader r = new InputStreamReader(fis, "UTF-8");  //or whatever encoding
        int ch = r.read();
        while(ch >= 0) {
            sb.append(ch);
            ch = r.read();
        }
        return sb.toString();
    }

    private void saveResult(String fileName, String language, String creatorName, Date creationDate,
                            Date lastModification, String mimeType, String content)
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        int index = fileName.lastIndexOf(".");
        String outName = fileName.substring(0, index) + ".txt";
        try
        {
            PrintWriter printWriter = new PrintWriter("./outputDocuments/" + outName);
            printWriter.write("Name: " + fileName + "\n");
            printWriter.write("Language: " + (language != null ? language : "") + "\n");
            printWriter.write("Creator: " + (creatorName != null ? creatorName : "") + "\n");
            String creationDateStr = creationDate == null ? "" : dateFormat.format(creationDate);
            printWriter.write("Creation date: " + creationDateStr + "\n");
            String lastModificationStr = lastModification == null ? "" : dateFormat.format(lastModification);
            printWriter.write("Last modification: " + lastModificationStr + "\n");
            printWriter.write("MIME type: " + (mimeType != null ? mimeType : "") + "\n");
            printWriter.write("\n");
            printWriter.write(content + "\n");
            printWriter.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

}
