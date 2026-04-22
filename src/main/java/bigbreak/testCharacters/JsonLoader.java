package bigbreak.testCharacters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;

public class JsonLoader {
    public static List<SceneTestData> load(String path) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(
                new File(path),
                new TypeReference<List<SceneTestData>>() {}
        );
    }
}
