package com.re.lkspmi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import ru.spmi.lk.authorization.LkSpmi;

public class DownloadService extends Service {
    public DownloadService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int fileId = intent.getIntExtra("file_id", 0);
        String filename = intent.getStringExtra("filename");
        String path = intent.getStringExtra("path");

        new DownloadTask(fileId, filename, path, intent).execute();
        return super.onStartCommand(intent, flags, startId);
    }

    class DownloadTask extends AsyncTask<Void, Void, String>{
        int fileId;
        String filename;
        String path;
        NotificationManager notificationManager;
        NotificationCompat.Builder notificationBuilder;
        Intent intent;

        public DownloadTask(int fileId, String filename, String path, Intent intent) {
            this.fileId = fileId;
            this.filename = filename;
            this.path = path;
            this.intent = intent;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(DownloadService.this, "Загрузка начата", Toast.LENGTH_SHORT).show();
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel
                        (NOTIFICATION_CHANNEL_ID, "Downloading", NotificationManager.IMPORTANCE_HIGH);

                // Configure the notification channel.
                notificationChannel.setDescription("downloading");
                notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);

                notificationManager.createNotificationChannel(notificationChannel);
            }


            notificationBuilder = new NotificationCompat.Builder(DownloadService.this, NOTIFICATION_CHANNEL_ID);

            notificationBuilder
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentTitle("Загрузка")
                    .setOngoing(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setVibrate(new long[0])
                    .setSmallIcon(R.drawable.ic_menu_share)
                    .setContentText("Файл " + filename + " загружается");

            notificationManager.notify(1, notificationBuilder.build());
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                LkSingleton.getInstance().getLkSpmi().downloadFile(fileId, filename, path);
                return "";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            if (s != null){

                notificationManager.cancel(1);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                File file = new File(path + "/" + filename);
                if (Build.VERSION.SDK_INT >= 24) {
                    Uri uri = FileProvider.getUriForFile(DownloadService.this,
                            "com.re.lkspmi" + ".fileprovider", file);
                    intent.setDataAndType(uri, "*/*");
                }
                else intent.setDataAndType(Uri.fromFile(file), "*/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                PendingIntent pendingIntent = PendingIntent.getActivity(DownloadService.this,
                        0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                notificationBuilder
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pendingIntent)
                        .setOngoing(false)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentTitle("Загрузка")
                        .setVibrate(new long[0])
                        .setSmallIcon(R.drawable.ic_menu_share)
                        .setContentText("Файл " + filename + " успешно загружен");
                Toast.makeText(DownloadService.this, "Файл "
                        + filename + " успешно загружен", Toast.LENGTH_SHORT).show();

                notificationManager.notify(1, notificationBuilder.build());
            }
            else {
                notificationManager.cancel(1);
                notificationBuilder
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle("Загрузка")
                        .setOngoing(false)
                        .setVibrate(new long[0])
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setSmallIcon(R.drawable.ic_menu_share)
                        .setContentText("Ошибка загрузки файла " + filename);
                notificationManager.notify(1, notificationBuilder.build());
                Toast.makeText(DownloadService.this, "Ошибка загрузки файла " + filename,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
