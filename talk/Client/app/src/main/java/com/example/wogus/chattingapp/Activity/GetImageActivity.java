package com.example.wogus.chattingapp.Activity;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wogus.chattingapp.Class.ImageResizeUtils;
import com.example.wogus.chattingapp.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GetImageActivity extends AppCompatActivity {

	private static final String TAG = "blackjin";
	private Boolean isCamera = false;
	private Boolean isPermission = true;

	private static final int PICK_FROM_ALBUM = 1;
	private static final int PICK_FROM_CAMERA = 2;

	private File tempFile;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activiy_get_image);

		tedPermission();

		findViewById(R.id.btnGallery).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// 권한 허용에 동의하지 않았을 경우 토스트를 띄웁니다.
				if(isPermission) goToAlbum();
				else Toast.makeText(view.getContext(),"갤러리권한x", Toast.LENGTH_LONG).show();
			}
		});

		findViewById(R.id.btnCamera).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// 권한 허용에 동의하지 않았을 경우 토스트를 띄웁니다.
				if(isPermission)  takePhoto();
				else Toast.makeText(view.getContext(),"카메라찍는권한x", Toast.LENGTH_LONG).show();
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

			if(tempFile != null) {
				if (tempFile.exists()) {
					if (tempFile.delete()) {
						Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
						tempFile = null;
					}
				}
			}

			return;
		}
		switch (requestCode) {
			case PICK_FROM_ALBUM: {

				Uri photoUri = data.getData();

				cropImage(photoUri);

				break;
			}
			case PICK_FROM_CAMERA: {

				Uri photoUri = Uri.fromFile(tempFile);
				Log.d("두번째URI:",":"+photoUri);
				ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, isCamera);
				cropImage(photoUri);

				break;
			}
			case Crop.REQUEST_CROP: {

				setImage();
			}
		}
	}
	private void cropImage(Uri photoUri) {

		Log.d(TAG, "tempFile : " + tempFile);

		/**
		 *  갤러리에서 선택한 경우에는 tempFile 이 없으므로 새로 생성해줍니다.
		 */
		if(tempFile == null) {
			try {
				tempFile = createImageFile();
			} catch (IOException e) {
				Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
				finish();
				e.printStackTrace();
			}
		}
		//크롭 후 저장할 Uri
		Uri savingUri = Uri.fromFile(tempFile);

		Crop.of(photoUri, savingUri).asSquare().start(this);
	}
	private void goToAlbum() {
		tempFile=null;
		isCamera = false;
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, PICK_FROM_ALBUM);
	}
	private void takePhoto() {
		tempFile=null;
		isCamera = true;
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		try {
			tempFile = createImageFile();
		} catch (IOException e) {
			Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
			finish();
			e.printStackTrace();
		}
		if (tempFile != null) {
			Uri photoUri = FileProvider.getUriForFile(this, "com.example.wogus.chattingapp.fileprovider", tempFile);
			Log.d("처음URI:",":"+photoUri);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(intent, PICK_FROM_CAMERA);
		}
	}
	/**
	 *  tempFile 을 bitmap 으로 변환 후 ImageView 에 설정한다.
	 */
	private void setImage() {

		ImageView imageView = findViewById(R.id.imageView);
		ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, true);
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
		imageView.setImageBitmap(originalBm);

		/**
		 *  tempFile 사용 후 null 처리를 해줘야 합니다.
		 *  (resultCode != RESULT_OK) 일 때 tempFile 을 삭제하기 때문에
		 *  기존에 데이터가 남아 있게 되면 원치 않은 삭제가 이뤄집니다.
		 */
		tempFile = null;

	}
	/**
	 *  폴더 및 파일 만들기
	 */
	private File createImageFile() throws IOException {

		// 이미지 파일 이름 ( blackJin_{시간}_ )
		String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
		String imageFileName = "blackJin_" + timeStamp + "_";

		// 이미지가 저장될 폴더 이름 ( blackJin )
		File storageDir = new File(Environment.getExternalStorageDirectory() + "/blackJin/");
		if (!storageDir.exists()) storageDir.mkdirs();

		// 파일 생성
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);
		Log.d(TAG, "createImageFile : " + image.getAbsolutePath());

		return image;
	}

	/**
	 *  권한 설정
	 */
	private void tedPermission() {

		PermissionListener permissionListener = new PermissionListener() {
			@Override
			public void onPermissionGranted() {
				// 권한 요청 성공
				isPermission = true;

			}

			@Override
			public void onPermissionDenied(ArrayList<String> deniedPermissions) {
				// 권한 요청 실패
				isPermission = false;

			}
		};

		TedPermission.with(this)
				.setPermissionListener(permissionListener)
				.setRationaleMessage("퍼미션2")
				.setDeniedMessage("퍼미션1")
				.setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
				.check();

	}

}