package levelGenerators.SelvaDarveauGenerator;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import engine.core.MarioLevelGenerator;
import engine.core.MarioLevelModel;
import engine.core.MarioTimer;


public class LevelGenerator implements MarioLevelGenerator {
    private final String LEVELS = "levels/original/";
    private final List<Slice> slices;
    private final List<Integer> starts;
    private final List<Integer> ends;

    // Reads all files in the level folder.
    public LevelGenerator() {
        slices = new ArrayList<>();
        starts = new ArrayList<>();
        ends = new ArrayList<>();

        // Get all level files in the specified directory.
        for (final File file: Objects.requireNonNull(new File(LEVELS).listFiles())) {
            if (!file.isDirectory()) {
                parse(file.getAbsolutePath());
            }
        }
    }

    // Reads a single file and splits it into slices that are added to the transition table.
    private void parse(String filename) {
        System.out.println("Reading " + filename);
        FileReader reader;
        List<char[]> columns = new ArrayList<>();
        try {
            int i;
            int column = 0;
            int row = 0;
            boolean firstColumn = true;
            reader = new FileReader(filename);

            // Separate each column into arrays of chars.
            while ((i = reader.read()) != -1) {
                if ((char) i != '\r' && (char) i != '\u0000') {
                    if ((char) i == '\n') {
                        if (firstColumn) {
                            firstColumn = false;
                        }
                        row++;
                        column = 0;
                    } else {
                        if (firstColumn) {
                            columns.add(new char[16]);
                        }


                        columns.get(column)[row] = (char) i;
                        column++;
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("Error with file " + filename);
            return;
        }

        // Convert arrays of char into slices.
        Slice lastSlice = null;
        for (char[] c : columns) {
            Slice tempSlice = null;

            for (Slice s : slices) {
                if (s.toString().equals(String.valueOf(c))) {
                    tempSlice = s;
                    break;
                }
            }

            if (tempSlice == null) {
                tempSlice = new Slice();
                for (int i = 0; i < 16; ++i) {

                    // Setting start/Mario slices.
                    if (c[i] == 'M') {
                        tempSlice.setMario(true);
                        starts.add(slices.size());
                    }

                    // Setting end/Flag slices.
                    if (c[i] == 'F') {
                        tempSlice.setFlag(true);
                        ends.add(slices.size());
                    }

                    // All other slices.
                    tempSlice.replaceChar(c[i], i);
                }
                slices.add(tempSlice);
            }

            if (lastSlice != null) {
                lastSlice.addNextSlice(tempSlice);
            }
            lastSlice = tempSlice;
        }
    }

    @Override
    public String getGeneratedLevel(MarioLevelModel model, MarioTimer timer) {
        Random rand = new Random();
        model.clearMap();
        int x = 1;
        boolean atEnd = false;

        // Adds the starting slice.
        Slice currentSlice = slices.get(starts.get(rand.nextInt(starts.size())));
        addSlice(model, 0, currentSlice);

        // Add slices until the end is reached.
        while (x < model.getWidth() - 1) {
            do {
                if (currentSlice.getNextSlices() >= 1) {
                    currentSlice = currentSlice.getMarkovSlice(rand);
                }
                else {
                    atEnd = true;
                    break;
                }
            } while (currentSlice.getNextSlices() < 1);

            addSlice(model, x, currentSlice);

            if (currentSlice.getFlag()) {
                atEnd = true;
                break;
            }
            x++;
        }

        // End the level if we didn't automatically generate an end.
        if (!atEnd) {
            currentSlice = slices.get(ends.get(rand.nextInt(ends.size())));
            addSlice(model, model.getWidth() - 1, currentSlice);
        }

        return model.getMap();
    }

    // Adds a specified slice to the level map.
    private void addSlice(MarioLevelModel model, int x, Slice currentSlice) {
        for (int i = 0; i < 16; ++i) {
            model.setBlock(x, i, currentSlice.getChar(i));
        }
    }

    @Override
    public String getGeneratorName() {
        return "MarkovChainGenerator";
    }
}