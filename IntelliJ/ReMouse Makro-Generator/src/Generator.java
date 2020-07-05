import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Generate a ReMouse macro file for Wurzelimperium.
 */
public class Generator {

    private static final int PLANT_WIDTH = 2; // how many fields does the plant occupy for width
    private static final int PLANT_HEIGHT = 2; // how many fields does the plant occupy for height
    private static final int GARDEN_WIDTH = 17;
    private static final int GARDEN_HEIGHT = 12;
    private static final int WATERGARDEN_CENTAL_WIDTH = 13;
    private static final int WATERGARDEN_CENTRAL_HEIGHT = 8;
    private static final int WATERGARDEN_BORDER_UPPER_AND_LOWER_WIDTH = 17;
    private static final int WATERGARDEN_BORDER_MIDDLE_WIDTH = 2;
    private static final int WATERGARDEN_BORDER_LEFT_AND_RIGHT_HEIGHT = 12;
    private static final int WATERGARDEN_BORDER_MIDDLE_HEIGHT = 2;
    private static final int HERBGARDEN_WIDTH = 4;
    private static final int HERBGARDEN_HEIGHT = 6;
    private static final int GARDEN_X_START = 1082; // 760 für 1920x1080_normal, 642 für 1680x1050_normal, 1082 für 2560x1440_normal
    private static final int GARDEN_Y_START = 279; // 280 für 1920x1080_normal, 282 für 1680x1050_normal, 279 für 2560x1440_normal
    private static final int GARDEN_X_DIST = PLANT_WIDTH * 40; // number of fiels * pixel per field
    private static final int GARDEN_Y_DIST = PLANT_HEIGHT * 40; // number of fiels * pixel per field
    private static final int WATERGARDEN_CENTRAL_X_START = 1162; // 842 für 1920x1080_normal, 722 für 1680x1050_normal, 1162 für 2560x1440_normal
    private static final int WATERGARDEN_CENTRAL_Y_START = 348; // 349 für 1920x1080_normal, 349 für 1680x1050_normal, 348 für 2560x1440_normal
    private static final int WATERGARDEN_CENTRAL_X_DIST = PLANT_WIDTH * 40; // number of fiels * pixel per field
    private static final int WATERGARDEN_CENTRAL_Y_DIST = PLANT_HEIGHT * 40; // number of fiels * pixel per field
    private static final int WATERGARDEN_BORDER_X_START = 1083; // 766 für 1920x1080_normal, 643 für 1680x1050_normal, 1083 für 2560x1440_normal
    private static final int WATERGARDEN_BORDER_Y_START = 268; // 269 für 1920x1080_normal, 268 für 1680x1050_normal, 268 für 2560x1440_normal
    private static final int WATERGARDEN_BORDER_X_DIST = PLANT_WIDTH * 40; // number of fiels * pixel per field
    private static final int WATERGARDEN_BORDER_Y_DIST = PLANT_HEIGHT * 40; // number of fiels * pixel per field
    private static final int HERBGARDEN_X_START = 782; // 782 für 1920x1080_normal
    private static final int HERBGARDEN_Y_START = 296; // 296 für 1920x1080_normal
    private static final int HERBGARDEN_X_DIST = PLANT_WIDTH * 80; // number of fiels * pixel per field
    private static final int HERBGARDEN_Y_DIST = PLANT_HEIGHT * 80; // number of fiels * pixel per field
    private static final GardenMode MODE = GardenMode.WATER_BORDER; // NORMAL für normalen Garten, WATER_CENTRAL für Wassergarten zentral, WATER_BORDER für Wassergarten Rand, HERB für Kräutergarten

    private enum GardenMode {NORMAL, WATER_CENTRAL, WATER_BORDER, HERB/*todo expand enum by attributes*/}

    public static void main(String[] args) throws IOException {
        if (args.length == 0) return;

        // z.B. "C:\\Users\\Daniel\\OneDrive - Humboldt-Universitaet zu Berlin, CMS\\Makros\\ReMouse Standard\\1920x1080_normal_smart\\garten_2_markieren.rms"
        Path macroPath = Paths.get(args[0]);

        // todo automatically deduct X_START, Y_START from MODE? Ask for user input for resolution to give res-specific values?

        if (MODE == GardenMode.NORMAL) {
            writeMacroFileGarden(macroPath);
        } else if (MODE == GardenMode.WATER_CENTRAL) {
            writeMacroFileWaterCentral(macroPath);
        } else if (MODE == GardenMode.WATER_BORDER) {
            writeMacroFileWaterBorder(macroPath);
        } else if (MODE == GardenMode.HERB) {
            writeMacroFileHerb(macroPath);
        } else {
            throw new UnsupportedOperationException("Unknown mode, no idea what macro should be for");
        }
    }

//    private static void writeMacroFile(GardenMode whichGarden, Path macroPath) throws IOException {}

    private static void writeMacroFileHerb(Path macroPath) throws IOException {
        writeMacroFile(macroPath, HERBGARDEN_X_START, HERBGARDEN_WIDTH, HERBGARDEN_X_DIST, HERBGARDEN_Y_START, HERBGARDEN_HEIGHT, HERBGARDEN_Y_DIST);
    }

    private static void writeMacroFileGarden(Path macroPath) throws IOException {
        writeMacroFile(macroPath, GARDEN_X_START, GARDEN_WIDTH, GARDEN_X_DIST, GARDEN_Y_START, GARDEN_HEIGHT, GARDEN_Y_DIST);
    }

    private static void writeMacroFileWaterCentral(Path macroPath) throws IOException { // todo merge methods into one, add enum as parameter
        writeMacroFile(macroPath, WATERGARDEN_CENTRAL_X_START, WATERGARDEN_CENTAL_WIDTH, WATERGARDEN_CENTRAL_X_DIST, WATERGARDEN_CENTRAL_Y_START, WATERGARDEN_CENTRAL_HEIGHT, WATERGARDEN_CENTRAL_Y_DIST);
    }

    private static void writeMacroFileWaterBorder(Path macroPath) throws IOException {
        writeMacroFile(macroPath, WATERGARDEN_BORDER_X_START, WATERGARDEN_BORDER_UPPER_AND_LOWER_WIDTH, WATERGARDEN_BORDER_X_DIST, WATERGARDEN_BORDER_Y_START, WATERGARDEN_BORDER_LEFT_AND_RIGHT_HEIGHT, WATERGARDEN_BORDER_Y_DIST);
    }

    private static void writeMacroFile(Path macroPath, int gardenXStart, int gardenWidth, int gardenXDist, int gardenYStart, int gardenHeight, int gardenYDist) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(macroPath)) {
            writer.write("{WinI (\"Wurzelimperium - Google Chrome\")}");
            writer.newLine();
            for (int currentX = gardenXStart; currentX <= gardenXStart + (gardenWidth /PLANT_WIDTH - 1) * gardenXDist; currentX += gardenXDist) {
                for (int currentY = gardenYStart; currentY <= gardenYStart + (gardenHeight /PLANT_HEIGHT - 1) * gardenYDist; currentY += gardenYDist) {
                    if (MODE == GardenMode.WATER_BORDER) {
                        if (watergardenFieldIsNotABorderField(gardenXStart, gardenYStart, currentX, currentY))
                            continue; // skip if field does not belong to border anymore
                    }
                    writer.write("{LMouseI down ("+currentX+","+currentY+")}");
                    writer.newLine();
                    writer.write("{LMouseI up ("+currentX+","+currentY+")}");
                    writer.newLine();
                }
            }
        }
    }

    private static boolean watergardenFieldIsNotABorderField(int gardenXStart, int gardenYStart, int currentX, int currentY) {
        return currentX >= gardenXStart + WATERGARDEN_BORDER_MIDDLE_WIDTH  * WATERGARDEN_BORDER_X_DIST && currentX < gardenXStart + (WATERGARDEN_BORDER_UPPER_AND_LOWER_WIDTH - WATERGARDEN_BORDER_MIDDLE_WIDTH)  * WATERGARDEN_BORDER_X_DIST
            && currentY >= gardenYStart + WATERGARDEN_BORDER_MIDDLE_HEIGHT * WATERGARDEN_BORDER_Y_DIST && currentY < gardenYStart + (WATERGARDEN_BORDER_LEFT_AND_RIGHT_HEIGHT - WATERGARDEN_BORDER_MIDDLE_HEIGHT) * WATERGARDEN_BORDER_Y_DIST;
    }
}
