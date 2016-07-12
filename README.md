
# Android-Universal-Camera-Loader #

## Features ##

- the camera is avilable
- image angle incorrect
- single utils class

## Usage ##

**Using as a library project Including In Your Project**

1.startActivity
``` android 
Intent intent = new Intent(this, CameraActivity.class);
startActivityForResult(intent, CameraActivity.Camera_Request_Code);
```

2.receiving return data
``` android
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CameraActivity.Camera_Request_Code && resultCode == RESULT_OK) {
        // return pic path
        String path = data.getStringExtra("filepath");
    } else
        super.onActivityResult(requestCode, resultCode, data);
}
```