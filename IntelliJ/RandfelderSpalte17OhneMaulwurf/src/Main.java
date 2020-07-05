import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    static final int X_END = 1282;
    static final int Y_START = 282;
    static final int Y_DELTA = 40;
    static final int ROW_COUNT = 12;
    static final int GARTENNR_MAX = 4;
    static final String tempscriptPath = "D:\\OneDrive - Humboldt-Universitaet zu Berlin, CMS\\Makros\\ReMouse Standard\\1680x1050_normal_smart\\gartenMakroSpalte17AnpfUndWaessernAvoidMoles.rms";
    static final String remousepath = "C:\\Program Files (x86)\\ReMouse Standard\\ReMouse.exe";
    public static void main(String[] args) throws IOException {
        Path macroPath = Paths.get(tempscriptPath);

        try (BufferedWriter writer = Files.newBufferedWriter(macroPath)) {
            writer.write("{WinI (\"Wurzelimperium - Google Chrome\")}");

            for (int gartenNr = 1; gartenNr <= GARTENNR_MAX; gartenNr++) {
                System.out.println("Maulwurfpositionen zwischen 1 und 12 einzeln eingeben:");
                Scanner reader = new Scanner(System.in);
                // höchstens 3 Maulwurfpositionen am Rand pro Garten einlesen
                List<Integer> molePositions = new ArrayList<>();
                for (int moleCounter = 0; moleCounter < 3; moleCounter++) {
                    int output = reader.nextInt();
                    if (output == 0) break;
                    molePositions.add(output - 1);
                }

                writer.newLine();
                writeEntries(molePositions, writer);

                // Wässern
                writer.write("{LMouseI down (" + 990 + "," + 197 + ")}");
                writer.newLine();
                writer.write("{LMouseI up (" + 990 + "," + 197 + ")}");
                writer.newLine();
                writer.write("{Delay 0.2}");
                writer.newLine();

                writeEntries(molePositions, writer);

                // jetzt in den nächsten Garten switchen
                switch (gartenNr) {
                    case 1:
                        writer.write("{LMouseI down (1241,776)}"); writer.newLine();
                        writer.write("{LMouseI up (1241,776)}"); writer.newLine();
                        writer.write("{Delay 3.2}"); writer.newLine();
                        writer.write("{LMouseI down (721,419)}"); writer.newLine();
                        writer.write("{LMouseI up (721,419)}"); writer.newLine();
                        writer.write("{Delay 5.2}"); writer.newLine(); break;
                    case 2:
                        writer.write("{LMouseI down (1241,776)}"); writer.newLine();
                        writer.write("{LMouseI up (1241,776)}"); writer.newLine();
                        writer.write("{Delay 3.2}"); writer.newLine();
                        writer.write("{LMouseI down (721,458)}"); writer.newLine();
                        writer.write("{LMouseI up (721,458)}"); writer.newLine();
                        writer.write("{Delay 5.2}"); writer.newLine(); break;
                    case 3:
                        writer.write("{LMouseI down (1241,776)}"); writer.newLine();
                        writer.write("{LMouseI up (1241,776)}"); writer.newLine();
                        writer.write("{Delay 3.2}"); writer.newLine();
                        writer.write("{LMouseI down (721,496)}"); writer.newLine();
                        writer.write("{LMouseI up (721,496)}"); writer.newLine();
                        writer.write("{Delay 5.2}"); writer.newLine(); break;
                    case 4:
                        writer.newLine(); break;
                }
            }
        }
        // Ausführen und Löschen
        Runtime.getRuntime().exec("\"" + remousepath + "\"" + " " + "\"" + tempscriptPath + "\""/*+" && del "+ tempscriptPath + " /q"*/);
    }

    private static void writeEntries(List<Integer> molePositions, BufferedWriter writer) throws IOException {
        // Anpflanzen
        for (int currentRow = 0; currentRow < ROW_COUNT; currentRow++) {
            if (!molePositions.contains(currentRow)) {
                writer.write("{LMouseI down (" + X_END + "," + (Y_START + currentRow * Y_DELTA) + ")}");
                writer.newLine();
                writer.write("{LMouseI up (" + X_END + "," + (Y_START + currentRow * Y_DELTA) + ")}");
                writer.newLine();
                writer.write("{Delay 0.2}");
                writer.newLine();
            }
        }
    }
}
