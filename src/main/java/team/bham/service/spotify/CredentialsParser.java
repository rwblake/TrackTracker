package team.bham.service.spotify;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CredentialsParser {

    /** Reads credentials.txt and returns the Client ID and Client Secret*/
    public static String[] parseCredentials() {
        String[] idAndSecret = { "", "" };
        idAndSecret[0] = System.getenv("SPOTIFY_CLIENT_ID");
        idAndSecret[1] = System.getenv("SPOTIFY_CLIENT_SECRET");
        //        try {
        //            File file = new File("credentials.txt");
        //            Scanner reader = new Scanner(file);
        //            idAndSecret[0] = reader.nextLine().trim();
        //            idAndSecret[1] = reader.nextLine().trim();
        //            reader.close();
        //        } catch (FileNotFoundException e) {
        //            System.out.println("ERROR: credentials.txt not found");
        //        }

        return idAndSecret;
    }
}
