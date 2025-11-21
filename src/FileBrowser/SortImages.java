package FileBrowser;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;


/**
 * reads the directory, saves all the fotos in an arrayList, saves all the paths in a seperate arrayList.
 * every foto which was taken within a given time will be pulled in a seperate folder.
 * <p>
 * Created by Chrissi on 11.05.2017.
 */
public class SortImages {
    //arraylist where the fotos are saved
    private static ArrayList<Foto> fotos;
    //arrayList where the paths of subdirectories are saved
    private static ArrayList<Path> subDirs = null;
    //the path to the directory of the fotos to sort
    private static Path sourcePath;
    //distance between Takes of the images
    private static int distance;

    private static final Logger LOGGER = Logger.getLogger(SortImages.class.getName());

    static void sort(int distanceBetweenTakes, Path dir) throws IOException {
        fotos = new ArrayList<>();
        subDirs = new ArrayList<>();
        sourcePath = dir;
        distance = distanceBetweenTakes;

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourcePath)) {
            for (Path file : directoryStream) {
                if (Files.isDirectory(file)) {
                    subDirs.add(file);
                } else {
                    for (int i = 0; i < UIConstants.SUPPORTED_IMAGE_FORMATS.length; i++) {
                        if (file.getFileName().toString().toLowerCase().endsWith(UIConstants.SUPPORTED_IMAGE_FORMATS[i])) {
                            fotos.add(new Foto(sourcePath.toString(), file.getFileName().toString()));
                        }
                    }
                }
            }
        } catch (IOException | DirectoryIteratorException e) {
            LOGGER.log(Level.SEVERE, "Failed to sort images", e);
        }

        fotos.sort((s1, s2) -> {
            try {
                return s1.getCreationDateTime().compareTo(s2.getCreationDateTime());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to compare images"+s1.getFilename()+" and "+s2.getFilename(), e);
                return 1;
            }
        });


        LOGGER.info("comparing... array size: " + fotos.size());
        for (int i = 0; i < fotos.size(); i++) {
            if (i > 0) {
                try {
                    compareFotos(fotos.get(i - 1), fotos.get(i), i);
                } catch (IOException e) {
                    LOGGER.severe("Failed to compare images"+fotos.get(i - 1).getFilename()+" and "+fotos.get(i).getFilename());
                }
            }
        }
    }



    private static void compareFotos(Foto pic1, Foto pic2, int index) throws IOException {
        /*
          Compares two fotos, checks if they are taken within given time.
          Checks if new directory exists. If not, calls createNewSubdir function
          moves file(s) to new directory

          @param pic1 foto 1 to compare
         * @param pic2 foto 2 to compare
         */
        //reads date and time when foto was created
        Date pic1Date = pic1.getCreationDateTime();
        Date pic2Date = pic2.getCreationDateTime();
        //output format of date and time
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm");

        Date timeBetween = new Date(pic2Date.getTime() - pic1Date.getTime());


        //creates new subdirectory if there's none yet. uses the dateFormatter to name the subdirectory by the date the foto was made
        if (subDirs.isEmpty()) {
            createNewSubDir(df.format(pic1Date));
        }

        //checks if fotos were created within given time. if it is so, files will be moved in new directory
        //if not, another subdirectory will be created

        if (timeBetween.getTime() < ((long) distance * 3600 * 1000)) { // 6 Std. getTime liefert Zeit in Millisekunden zurück
            fotos.set(index - 1, moveFile(subDirs.getLast().toAbsolutePath(), pic1));
            fotos.set(index, moveFile(subDirs.getLast().toAbsolutePath(), pic2));
         }
        else {
            //if i don't add this option, last foto will not be sorted!!!
            //call function to create new subDir & move 2nd pic
            createNewSubDir(df.format(pic2Date));
            fotos.set(index, moveFile(subDirs.getLast().toAbsolutePath(), pic2));
         }
    }


    private static Foto moveFile(Path path, Foto foto) {
        Path file = foto.getFile().toPath();
        File sv = new File(path.toString(), foto.getFilename());
        Path target = sv.toPath();

        //check if another file with same filename already exists. if not, just move to the new dir
        if (!(new File(path + "/" + file.getFileName()).exists())) {
            try {
                Files.move(file, target, ATOMIC_MOVE);
            } catch (Exception e) {
                LOGGER.severe("Failed to move file"+file.getFileName()+" to "+target.toString());
            }
        }


        return new Foto(target.toFile());
        //fotos.set(index, new FileBrowser.Foto(file));
    }

    /**
     * Checks if new directory exists. if not, creates a new directory
     * adds new directory to subDirs arrayList
     *
     * @param newSubDir Name of new subdirectory that needs to be created
     */
    private static void createNewSubDir(String newSubDir) {
        Path newDir = Paths.get(sourcePath.toString(), newSubDir);
        if (!Files.exists(newDir)) {
            try {
                Files.createDirectory(newDir);
            } catch (IOException e) {
                LOGGER.severe("Failed to create directory"+newDir.toString());
            }
        }
        subDirs.add(newDir);
    }

    /**
     * walks through the ArrayList of subDirs and moves every file back to sourceDir.
     * After content was moved, directory will be deleted
     */
    public static void undoChanges() {
        if (subDirs!= null) {
            int i = 0;
            for (Path subDir : subDirs) {
                //gets the content of the subdirectory
                try (DirectoryStream<Path> directoryStreamSub = Files.newDirectoryStream(subDir)) {
                    for (Path cont : directoryStreamSub) {
                        if (!Files.isDirectory(cont)) {
                            Foto file1 = new Foto(subDir.toString(), cont.getFileName().toString());
                            moveFile(sourcePath.toAbsolutePath(), file1);//.renameTo(sourcePath + file.getName());
                        }
                        i++;
                    }
                } catch (IOException ex) {
                    LOGGER.severe("Failed to move file"+sourcePath.getFileName()+" to "+ sourcePath);
                }
                try {
                    //deletes the directory
                    Files.delete(subDir);
                } catch (IOException e) {
                    LOGGER.severe("Failed to delete directory"+subDir);
                }
            }
        }
    }
}
