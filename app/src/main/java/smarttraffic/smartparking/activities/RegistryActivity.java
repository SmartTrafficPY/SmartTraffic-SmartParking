package smarttraffic.smartparking.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.receivers.RegistrationReceiver;
import smarttraffic.smartparking.services.RegistrationService;

public class RegistryActivity extends AppCompatActivity {

    /**
     * The user register and get a profile on the system...
     * **/

    private static final String TAG = "RegistryActivity";

    @BindView(R.id.aliasSignUp)
    EditText aliasInput;
    @BindView(R.id.passwordSignUp)
    EditText passwordInput;
    @BindView(R.id.ageSignUp)
    EditText ageInput;
    @BindView(R.id.maleRadButton)
    RadioButton maleRadButton;
    @BindView(R.id.femaleRadButton)
    RadioButton femaleRadButton;
    @BindView(R.id.sexRadioGroup)
    RadioGroup sexSelectRadioGroup;
    @BindView(R.id.acceptTermsCheckBox)
    CheckBox termsAndConditions;
    @BindView(R.id.textInTermsAndCond)
    TextView textInTermsAndCond;
    @BindView(R.id.signUpButton)
    Button signInButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registry_activity);
        ButterKnife.bind(this);

//        signInButton.setEnabled(false);
            //TODO:make EULAActivity...
//        textInTermsAndCond.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RegistryActivity.this, EulaActivity.class);
//                startActivity(intent);
//            }
//        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(RegistrationService.REGISTRATION_ACTION);
        filter.addAction(RegistrationService.BAD_REGISTRATION_ACTION);
        RegistrationReceiver registrationReceiver = new RegistrationReceiver();
        registerReceiver(registrationReceiver, filter);


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRegister();
            }
        });

    }

    private void createRegister() {
        Log.d(TAG, "User trying to registry");

        signInButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegistryActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creando el registro...");
        progressDialog.show();
        sendRegistrationPetition();

        if(dataIsCorrectlyComplete()){
            progressDialog.show();
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            /**Here the service get the request of registration...**/
                            signInButton.setEnabled(true);
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }

    }

    private boolean dataIsCorrectlyComplete() {
        if(termsAndConditions.isChecked()){
            if(aliasInput.getText().toString().length() > 5){
                if(!passwordInput.getText().toString().isEmpty()){
                    if(maleRadButton.isChecked() || femaleRadButton.isChecked()){
                        if(!ageInput.getText().toString().isEmpty()){
                            return true;
                        }else{
                            showToast("Favor ponga su EDAD(en años)!");
                            return false;
                        }
                    }else{
                        showToast("Es necesario elegir alguna opcion de SEXO!");
                        return false;
                    }
                }else{
                    showToast("La CONTRASEÑA no puede estar vacia!");
                    return false;
                }
            }else{
                showToast("El ALIAS debe tener al menos 6 caracteres!");
                return false;
            }
        }else{
            showToast("Tienes que aceptar los TERMINOS y CONDICIONES!");
            return false;
        }
    }

    private void sendRegistrationPetition() {
        Intent registryIntent = new Intent(RegistryActivity.this, RegistrationService.class);
        registryIntent.putExtra("alias", aliasInput.getText().toString());
        registryIntent.putExtra("password", passwordInput.getText().toString());
        registryIntent.putExtra("age", Integer.parseInt(ageInput.getText().toString()));
        if(onRadioButtonClicked() != null){
            registryIntent.putExtra("sex", onRadioButtonClicked());
        }
        startService(registryIntent);
    }

    public String onRadioButtonClicked() {
        if(femaleRadButton.isChecked()){
            return "F";
        }else if(maleRadButton.isChecked()){
            return "M";
        }else{
            return null;
        }
    }

    // Show images in Toast prompt.
    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContentView = (LinearLayout) toast.getView();
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.mipmap.toast_smartparking_round);
        toastContentView.addView(imageView, 0);
        toast.show();
    }

}
