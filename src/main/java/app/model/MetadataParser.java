package app.model;


import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MetadataParser {

    public static Map<String, String> parse(Path path) throws IOException, SAXException, TikaException {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try (InputStream stream = Files.newInputStream(path)) {
            parser.parse(stream, handler, metadata);
            Map<String, String> map = new HashMap<>();
            map.put("genre", metadata.get("xmpDM:genre"));
            map.put("album", metadata.get("xmpDM:album"));
            map.put("trackNumber", metadata.get("xmpDM:trackNumber"));
            map.put("year", metadata.get("xmpDM:releaseDate"));
            map.put("artist", metadata.get("xmpDM:artist"));
            map.put("title", metadata.get("dc:title"));
            map.put("albumArtist", metadata.get("xmpDM:albumArtist"));
            map.put("duration", metadata.get("xmpDM:duration"));
            map.put("rate", metadata.get("xmpDM:audioSampleRate"));
            return map;
        }
    }
}
