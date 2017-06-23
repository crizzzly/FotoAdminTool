import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.DosFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * reads the directory, saves all the fotos in an arrayList, saves all the paths in a seperate arrayList.
 * every foto which was taken within a given time will be pulled in a seperate folder.
 * <p>
 * Created by Chrissi on 11.05.2017.
 */
public class Test {

    public static void main(String[] args) {
        //arraylist where the fotos are saved
        ArrayList<Foto> fotos = new ArrayList<>();
        //arrayList where the paths are saved
        ArrayList<Path> subDirs = new ArrayList<>();
        //the path to the directory of the fotos to sort
        Path sourcePath = Paths.get("C:\\Users\\Chrissi\\Pictures\\imagesTest");
        Path documentDir = Paths.get("C:\\Users\\Chrissi\\Pictures\\imagesTest\\documents");

        //Manager manager = new Manager();

        // manager.initialize();
        //manager.createDbSchema();

        //reads the directory and saves every single file in the Foto-arrayList
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourcePath)) {
            for (Path file : directoryStream) {
                if (!Files.isDirectory(file)) {
                    fotos.add(new Foto(sourcePath.toString(), file.getFileName().toString()));
                }

                //System.out.println(file.getFileName());
            }
        } catch (IOException | DirectoryIteratorException e) {
            e.printStackTrace();
            System.err.println(e);
        }

        //always compares two fotos. calls the compareFoto method
        for (int i = 0; i < fotos.size(); i++) {
            if (i > 0) {
                compareFotos(fotos.get(i - 1), fotos.get(i), sourcePath, subDirs);
            }
        }
    }

    //compares two fotos, checks if they are taken within given time. if so and no folder is created yet,
    // creates new folder and moves foto into new folder

    /**
     * Checks if new directory exists if not, creates a new directory
     *
     * @param pic1       foto 1 to compare
     * @param pic2       foto 2 to compare
     * @param sourcePath path of the fotos which are sorted
     * @param subdirs    ArrayList where new subdirectories are saved
     */
    private static void compareFotos(Foto pic1, Foto pic2, Path sourcePath, ArrayList<Path> subdirs) {
        //reads date and time when foto was created
        Date pic1Date = pic1.getCreationDateTime();
        Date pic2Date = pic2.getCreationDateTime();
        //output format of date and time
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm");

        //System.out.println("Pic1: "+df.format(pic1Date)+ ", pic2: "+df.format(pic2Date));

        //creates new subdirectory if there's none yet. uses the dateFormatter
        // to name the subdirectory by the date the foto was made
        if (subdirs.size() < 1) {
            createNewSubDir(sourcePath, df.format(pic1Date), subdirs);
        }

        //checks if fotos were created within given time. if it is so, files will be moved in new directory
        //if not, another subdirectory will be created

        if ((pic2Date.getTime() - pic1Date.getTime()) < (6 * 60 * 60 * 1000)) { // 6 Std. getTime liefert Zeit in Millisekunden zurück
            //System.out.println("Subdirs.path = "+subdirs.get(subdirs.size()-1));

            pic1.moveFile(subdirs.get((subdirs.size() - 1)).toString());
            pic2.moveFile(subdirs.get((subdirs.size()) - 1).toString());

        } else {
            pic1.moveFile(subdirs.get((subdirs.size() - 1)).toString());

            createNewSubDir(sourcePath, df.format(pic2Date), subdirs);


            //System.out.println("createt new subDir" + df.format(pic2Date));
            //createNewSubDir(sourcePath, df.format(pic2Date), subdirs); */
        }
    }

    /**
     * Checks if new directory exists if not, creates a new directory
     * adds foto to subdirs arrayList
     *
     * @param sourcePath Path where the pictures to be sorted are saved
     * @param subdir     Name of new subdirectory that needs to be created
     * @param subdirs    ArrayList where new subdirectories are saved
     */
    private static void createNewSubDir(Path sourcePath, String subdir, ArrayList subdirs) {
        Path newDir = Paths.get(sourcePath.toString(), subdir);
        if (!Files.exists(newDir)) {
            try {

                Files.createDirectory(newDir);
                newDir = Files.setAttribute(newDir, "dos:readonly", false);

                DosFileAttributes attr = Files.readAttributes(newDir, DosFileAttributes.class);
                //System.out.println("is readOnly: "+attr.isReadOnly());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedOperationException x) {
                System.err.println("DOS file" +
                        " attributes not supported:" + x);
            }
        }

        subdirs.add(newDir);
    }
}