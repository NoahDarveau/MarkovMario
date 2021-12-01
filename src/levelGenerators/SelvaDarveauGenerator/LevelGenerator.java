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
        Slice previousSlice;
        int prevHeight;
        int x = 1;

        // Adds the starting slice.
        Slice currentSlice = slices.get(starts.get(rand.nextInt(starts.size())));
        addSlice(model, 0, currentSlice);

        // Add slices until the end is reached.
        while (x < model.getWidth() - 1) {
            do {

                previousSlice = currentSlice;
                prevHeight = previousSlice.getGroundHeight();
                currentSlice = currentSlice.getMarkovSlice(rand);


               } while (currentSlice.getNextSlices() < 1 && currentSlice.getGroundHeight() < prevHeight + 4);

            // Duplicate previous slice if it attempts to place another mario slice or an early flag.
            if (currentSlice.getMario() || currentSlice.getFlag()) {
                currentSlice = previousSlice;
            }

            // Place slices.
            for (int i = 0; i < 16; ++i) {
                model.setBlock(x,  i, currentSlice.getChar(i));
                System.out.print(currentSlice.getChar(i));
            }
            System.out.println();
            x++;
        }

        // Place an ending slice.
        currentSlice = slices.get(ends.get(rand.nextInt(ends.size())));
        addSlice(model, model.getWidth() - 1, currentSlice);

        fixPipes(model);

        return model.getMap();
    }

    // Adds a specified slice to the level map.
    private void addSlice(MarioLevelModel model, int x, Slice currentSlice) {
        for (int i = 0; i < 16; ++i) {
            model.setBlock(x, i, currentSlice.getChar(i));
        }
    }

    private void fixPipes(MarioLevelModel model) {
        for (int x = 2; x < model.getWidth(); x++) {
            for (int y = 0; y < model.getHeight(); y++) {
                if (model.getBlock(x-2, y) != 't' && model.getBlock(x-1, y) == 't' && model.getBlock(x, y) != 't') {
                    model.setBlock(x, y, 't');
                } else if (model.getBlock(x-2, y) != 'T' && model.getBlock(x-1, y) == 'T' && model.getBlock(x, y) != 'T') {
                    model.setBlock(x, y, 'T');
                }
            }
        }
    }

    @Override
    public String getGeneratorName() {
        return "MarkovChainGenerator";
    }
}