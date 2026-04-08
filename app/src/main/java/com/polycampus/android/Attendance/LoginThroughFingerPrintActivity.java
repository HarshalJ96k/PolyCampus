package com.polycampus.android.Attendance;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.polycampus.android.HomeActivity;
import com.polycampus.android.R;
import com.polycampus.android.common.FingerprintHandler;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class LoginThroughFingerPrintActivity extends AppCompatActivity {


    private TextView txt_heading_title;
    private ImageView img_fingerprint_scanner;
    private TextView txt_description;

    private FingerprintManager fingerprintManager;  // fingerprintmanager check device have the fingerprint scanner or not
    private KeyguardManager keyguardManager; // keyguardmanager check lock is securely enable or not

    //The Android Keystore system lets you store cryptographic keys in a container to make it more difficult to extract from the device.
    // Once keys are in the keystore, they can be used for cryptographic operations with the key material remaining non-exportable.
    // Moreover, it offers facilities to restrict when and how keys can be used, such as requiring user authentication for key use or restricting keys to be used only in certain cryptographic modes.
    private KeyStore keyStore;
    //In cryptography, a cipher (or cypher) is an algorithm for performing encryption or decryption—a series of
    // well-defined steps that can be followed as a procedure. An alternative, less common term is encipherment.
    // To encipher or encode is to convert information into cipher or code.
    // In common parlance, "cipher" is synonymous with "code", as they are both a set of steps that encrypt a message;
    // however, the concepts are distinct in cryptography, especially classical cryptography.
    private Cipher cipher;
    private String KEY_NAME = "nikdroid";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_through_finger_print);
        txt_heading_title = findViewById(R.id.txt_heading_title);
        img_fingerprint_scanner = findViewById(R.id.img_fingerprint_scanner);
        txt_description = findViewById(R.id.txt_description);

        //TODO Check 1 : Android Device Version should be greater or equal to marshmallow
        //TODO Check 2 : Device has a fingerprint scanner
        //TODO Check 3 : have a permission to use fingerprint scanner in the app
        //TODO Check 4:  lock screen is secured with atleast 1 type of  lock
        //TODO Check 5 : atleast 1 fingerprint is register

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) // first todo  condition is satisfed or check
        {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (!fingerprintManager.isHardwareDetected()) // second todo is check
            {
                txt_description.setText("Fingerprint scanner is not detect in device");

            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) // third todo is check
            {

                txt_description.setText("Permission is not granted to used the fingerprint scanner");

            } else if (!keyguardManager.isKeyguardSecure()) // fourth todo is check
            {
                txt_description.setText("Add lock to your phone from settings");
            } else if (!fingerprintManager.hasEnrolledFingerprints()) // fifth condition is check
            {
                txt_description.setText("You should add atleast 1 fingerprint to use this features");
            } else {
                txt_description.setText("Place your fingur on scanner to accesss this app");

                generateKey();

                if (cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler fIngerprintHandler = new FingerprintHandler(this);
                    fIngerprintHandler.startAuth(fingerprintManager, cryptoObject);
                }

            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() {
        try {
            //Below code will give you access to gain keystore instance which allows you to store cryptographic keys.
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();

        } catch (KeyStoreException | IOException | CertificateException
                 | NoSuchAlgorithmException | InvalidAlgorithmParameterException
                 | NoSuchProviderException e) {

            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {

            keyStore.load(null);

            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);

            cipher.init(Cipher.ENCRYPT_MODE, key);
//            Toast.makeText(this, ""+key, Toast.LENGTH_SHORT).show();

            return true;

        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException |
                 IOException | NoSuchAlgorithmException |
                 InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginThroughFingerPrintActivity.this, HomeActivity.class));
        finish();
    }
}
