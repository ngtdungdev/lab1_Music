package com.instar.lab1;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.instar.lab1.DTO.Music;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class ListMusic {

    private Drive getGoogleDriveService(GoogleSignInAccount googleSignInAccount, Context context) {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context, Collections.singleton(DriveScopes.DRIVE));
        credential.setSelectedAccount(googleSignInAccount.getAccount());
        return new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName("Application Name")
                .build();
    }

    public List<Music> findMP3Files(Context context, String rootDirectoryPath) {
        List<Music> mp3Files = new ArrayList<>();
        mp3Files.add(new Music("https://drive.google.com/uc?export=download&id=1z8QIL9hmvnR5Q_JHrqMK-deo5gEXg9lg", "Sau Lời Khước Từ"));
        mp3Files.add(new Music("https://drive.google.com/uc?export=download&id=1Brh7CRjQ2XaXwDWliAoVSzWhDbEjLX71", "Bạn Đời"));
        mp3Files.add(new Music("https://drive.google.com/uc?export=download&id=1psWSOJB5lzo7zFj2jT6HZAnaISsjlAAK", "Thủy Triều"));
        mp3Files.add(new Music("https://drive.google.com/uc?export=download&id=1SJWgsnyAWwH5waXI2OeJI5BJ06AxUoSF", "Em Ổn Không"));
        mp3Files.add(new Music("https://drive.google.com/uc?export=download&id=1qokufk9scSm2oDn43dd8d24Yr1ADmPfk", "Chỉ Cần Được Ngồi Đây Với Em"));
        mp3Files.add(new Music("https://drive.google.com/uc?export=download&id=1Q6xEw87Njazy_nTkHn5AkTrlGh4iGTsU", "Anima Libera"));

        File directory = new File(rootDirectoryPath + "/Download/Album/");
        if (directory.exists() && directory.isDirectory()) {
            findMP3FilesRecursive(directory, mp3Files);
        }

//        Drive driveService = getGoogleDriveService(googleSignInAccount, context);
//        new Thread(() -> {
//            try {
//                Drive.Files.List request = driveService.files().list().setSpaces("drive")
//                        .setFields("nextPageToken, files(id, name)")
//                        .setPageSize(10);
//                FileList fileList = request.execute();
//                for (com.google.api.services.drive.model.File file : fileList.getFiles()) {
//                    String fileName = file.getName();
//                    String fileId = file.getId();
//                    Toast.makeText(context, fileName, Toast.LENGTH_SHORT).show();
//                }
//
//                // Lưu ý: Thực hiện cập nhật UI trên thread UI, nếu cần
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();


//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
//        if (account != null) {
//            DriveFolder rootFolder = Drive.DriveApi.getRootFolder(getGoogleApiClient());
//
//            Task<MetadataBuffer> queryTask = rootFolder.listChildren(getGoogleApiClient()).addOnSuccessListener(new OnSuccessListener<MetadataBuffer>() {
//                @Override
//                public void onSuccess(MetadataBuffer metadataBuffer) {
//                    List<FileItem> fileList = new ArrayList<>();
//                    for (Metadata metadata : metadataBuffer) {
//                        String name = metadata.getTitle();
//                        boolean isFolder = metadata.isFolder();
//                        fileList.add(new FileItem(name, isFolder));
//                    }
//                    metadataBuffer.release();
//
//                    // Update RecyclerView
//                    adapter.setFileList(fileList);
//                }
//            });
//        }


//        GoogleSignInOptions signInOptions =
//                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                        .requestEmail()
//                        .requestScopes(new Scope(DriveScopes.DRIVE))
//                        .build();
//        GoogleSignInClient client = GoogleSignIn.getClient(context, signInOptions);
//
//
//        Drive driveService = new Drive.Builder(
//                AndroidHttp.newCompatibleTransport(),
//                (JsonFactory) JacksonFactory.getDefaultInstance(),
//                (HttpRequestInitializer) GoogleSignIn.getLastSignedInAccount(context).getAccount())
//                .setApplicationName("Music")
//                .build();
//
//        String pageToken = null;
//        try {
//            do {
//                FileList result = driveService.files().list()
//                        .setQ("'" + "1YixgcVQ39TFhrZzjm6bI6AJYGFdxy0Ha" + "' in parents and name contains '.mp3'")
//                        .setSpaces("drive")
//                        .setFields("nextPageToken, files(id, name)")
//                        .setPageToken(pageToken)
//                        .execute();
//                for (File file : result.getFiles()) {
//                    System.out.printf("Found file: %s (%s)\n", file.getName(), file.getId());
//                }
//                pageToken = result.getNextPageToken();
//            } while (pageToken != null);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return mp3Files;
    }

    private void findMP3FilesRecursive(File directory, List<Music> mp3Files) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findMP3FilesRecursive(file, mp3Files);
                } else if (isMP3File(file)) {
                    String filePath = file.getAbsolutePath();
                    String fileName = file.getName();
                    for(Music music : mp3Files) {
                        if(fileName.equals(music.getFileName() + ".mp3")) music.setFilePath(filePath);
                    }
                }
            }
        }

    }

    private boolean isMP3File(File file) {
        String fileName = file.getName();
        return fileName.endsWith(".mp3") && fileName.endsWith("");
    }

}
