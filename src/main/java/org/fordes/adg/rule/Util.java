package org.fordes.adg.rule;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {

    private Util() {
    }

    public static Path resolvePath(String configuredPath) {
        Path path = Paths.get(configuredPath).normalize();
        if (!path.isAbsolute()) {
            path = Paths.get(Constant.ROOT_PATH).resolve(path).normalize();
        }
        return path;
    }
}
