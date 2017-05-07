package com.example.anishdalal.finalapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalPayment;
import com.paypal.android.MEP.PayPalResultDelegate;
//import com.example.anishdalal.finalapp.DependencyHandler;



public class Settings extends AppCompatActivity {
    private Context mContext;

    //private DependencyHandler mDependencyHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        this.setTitle("Payment Settings");

        PayPal ppObj = PayPal.initWithAppID(this.getBaseContext(),
                "APP-80W284485P519543T", PayPal.ENV_SANDBOX);
        CheckoutButton launchPayPalButton =
                ppObj.getCheckoutButton(this, PayPal.BUTTON_278x43,
                        CheckoutButton.TEXT_PAY);
        LinearLayout ll = (LinearLayout) findViewById(R.id.settings_layout);
        ll.addView(launchPayPalButton);
        launchPayPalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch(v);
            }
        });

        //this.mContext = context;

        /*
        final String[] settingsOptions = {"payment"};

        final DrivingLogAdapter drivingLogAdapter = new DrivingLogAdapter(this, R.layout.list_item_settings,
                settingsOptions);
        Button button1 = (Button) findViewById(R.id.submitButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardInputWidget mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
                Card card = mCardInputWidget.getCard();
                if (card == null) {
                    Toast.makeText(Settings.this, "Invalid Card Data!", Toast.LENGTH_SHORT).show();
                } else {
                    Stripe stripe = new Stripe(Settings.this, "pk_live_dYlfh5ZYsWD5kO2En038Lbb7");
                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server - do I need to do this?
                                    //stripe.apiKey = "sk_test_Xuazta2hZm4MBaMPdpekQ8QG";

                                    //followed guide for next steps
                                    token.toString();

                                    // Create a Customer:
                                    Map<String, Object> customerParams = new HashMap<String, Object>();
                                    customerParams.put("email", "paying.user@example.com"); //put in info from user log
                                    customerParams.put("source", token);
                                    Customer customer = new Customer();
                                    try {
                                        customer = Customer.create(customerParams);
                                    } catch(Exception e) {
                                        System.out.println(e.getStackTrace());
                                    }

                                    String customerID = customer.getId(); //store this in the database

                                    // Charge the Customer instead of the card:
                                    Map<String, Object> chargeParams = new HashMap<String, Object>();
                                    chargeParams.put("amount", 1000);
                                    chargeParams.put("currency", "usd");
                                    chargeParams.put("customer", customer.getId());
                                    try {
                                        Charge charge = Charge.create(chargeParams);
                                    } catch(Exception e) {
                                        System.out.println(e.getStackTrace());
                                    }

                                }
                                public void onError(Exception error) {

                                }
                            }
                    );
                }
            }});
            */
    }

    private void launch(View v) {
        PayPalPayment newPayment = new PayPalPayment();
        newPayment.setSubtotal(new java.math.BigDecimal(10.0));
        newPayment.setRecipient("my@email.com");
        newPayment.setCurrencyType("USD");
        newPayment.setMerchantName("My Company");
        Intent paypalIntent = PayPal.getInstance().checkout(newPayment, this);
        startActivityForResult(paypalIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        PayPalResultDelegate p = new PayPalResultDelegate() {
            @Override
            public void onPaymentSucceeded(String s, String s1) {
                Toast.makeText(getApplicationContext(),
                        "Payment successful",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPaymentFailed(String s, String s1, String s2, String s3, String s4) {
                Toast.makeText(getApplicationContext(),
                        "Payment failed",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPaymentCanceled(String s) {
                Toast.makeText(getApplicationContext(),
                        "Payment canceled",
                        Toast.LENGTH_LONG).show();
            }
        };

    }

    public class DrivingLogAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public DrivingLogAdapter(Context context, int layoutId, String[] values)
        {
            super(context, layoutId, values);
            this.context = context;
            this.values = values;
        }
        @Override
        public int getCount() {
            return super.getCount();
        }
    }

    //Code to take token and post to server
//    public class StripeCharge extends AsyncTask<String, Void, String> {
//        String token;
//
//        public StripeCharge(String token) {
//            this.token = token;
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            new Thread() {
//                @Override
//                public void run() {
//                    postData(name,token,""+ amount);
//                }
//            }.start();
//            return "Done";
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            Log.e("Result",s);
//        }
//
//        public void postData(String description, String token,String amount) {
//            // Create a new HttpClient and Post Header
//            try {
//                URL url = new URL("[YOUR_SERVER_CHARGE_SCRIPT_URL]");
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setReadTimeout(10000);
//                conn.setConnectTimeout(15000);
//                conn.setRequestMethod("POST");
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new NameValuePair("method", "charge"));
//                params.add(new NameValuePair("description", description));
//                params.add(new NameValuePair("source", token));
//                params.add(new NameValuePair("amount", amount));
//
//                OutputStream os = null;
//
//                os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//                writer.write(getQuery(params));
//                writer.flush();
//                writer.close();
//                os.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
